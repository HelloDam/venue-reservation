package com.vrs.service.scheduled;

import com.alibaba.fastjson2.JSON;
import com.vrs.constant.RocketMqConstant;
import com.vrs.design_pattern.strategy.MessageProcessor;
import com.vrs.domain.dto.mq.OrderDelayCloseMqDTO;
import com.vrs.domain.dto.mq.TimePeriodStockReduceMqDTO;
import com.vrs.domain.entity.LocalMessageDO;
import com.vrs.enums.LocalMessageStatusEnum;
import com.vrs.rocketMq.producer.OrderDelayCloseProducer;
import com.vrs.rocketMq.producer.TimePeriodStockReduceProducer;
import com.vrs.service.LocalMessageService;
import jakarta.annotation.PostConstruct;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author dam
 * @create 2024/11/17 16:44
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocalMessageScheduledScan {
    private final DataSource dataSource;
    private final LocalMessageService localMessageService;
    private final TimePeriodStockReduceProducer timePeriodStockReduceProducer;
    private final OrderDelayCloseProducer orderDelayCloseProducer;
    private final RedissonClient redissonClient;

    /**
     * 使用策略模式处理消息
     */
    // todo 可以优化策略模式的写法，方便代码扩展
    private final Map<String, MessageProcessor> messageProcessors = new HashMap<>();

    private final int BATCH_SIZE = 1000;

    /**
     * 注册 tag 和其对应的消息处理器
     */
    @PostConstruct
    public void init() {
        messageProcessors.put(RocketMqConstant.TIME_PERIOD_STOCK_REDUCE_TAG, mqDTO -> {
            TimePeriodStockReduceMqDTO dto = JSON.parseObject(mqDTO.getContent(), TimePeriodStockReduceMqDTO.class);
            return timePeriodStockReduceProducer.sendMessage(dto);
        });

        messageProcessors.put(RocketMqConstant.ORDER_DELAY_CLOSE_TAG, mqDTO -> {
            OrderDelayCloseMqDTO dto = JSON.parseObject(mqDTO.getContent(), OrderDelayCloseMqDTO.class);
            return orderDelayCloseProducer.sendMessage(dto);
        });
    }

    /**
     * 定时任务：扫描并处理本地消息
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @SneakyThrows
    public void processLocalMessage() {
        RLock lock = redissonClient.getLock("LocalMessageScan");
        boolean locked = false;
        try {
            locked = lock.tryLock(1, TimeUnit.MINUTES);
            if (!locked) {
                log.warn("获取分布式锁失败，跳过本次处理");
                return;
            }
            log.info("开始扫描本地消息表...");
            long start = System.currentTimeMillis();
            @Cleanup Connection conn = dataSource.getConnection();
            @Cleanup Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(Integer.MIN_VALUE);

            // 查询sql，只查询关键的字段
            String sql = "SELECT id,msg_id,topic,tag,content,retry_count,max_retry_count,next_retry_time FROM local_message where " +
                    "is_deleted = 0 and (status = 0 OR status = 1) and next_retry_time<" + start;
            @Cleanup ResultSet rs = stmt.executeQuery(sql);

            List<LocalMessageDO> localMessageBuffer = new ArrayList<>();

            while (rs.next()) {
                // 获取数据中的属性
                LocalMessageDO localMessageDO = new LocalMessageDO();
                localMessageDO.setId(rs.getLong("id"));
                localMessageDO.setMsgId(rs.getString("msg_id"));
                localMessageDO.setTopic(rs.getString("topic"));
                localMessageDO.setTag(rs.getString("tag"));
                localMessageDO.setContent(rs.getString("content"));
                localMessageDO.setRetryCount(rs.getInt("retry_count"));
                localMessageDO.setMaxRetryCount(rs.getInt("max_retry_count"));
                localMessageDO.setNextRetryTime(rs.getLong("next_retry_time"));
                if (localMessageDO.getRetryCount() > localMessageDO.getMaxRetryCount()) continue;
                localMessageBuffer.add(localMessageDO);
                if (localMessageBuffer.size() > BATCH_SIZE) {
                    batchProcessMessages(localMessageBuffer);
                    localMessageBuffer.clear();
                }
            }

            if (!localMessageBuffer.isEmpty()) {
                batchProcessMessages(localMessageBuffer);
            }
            log.info("结束扫描本地消息表..." + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            log.error("处理本地消息表时发生异常", e);
            throw e; // 或根据业务决定是否抛出
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 批量处理消息
     */
    private void batchProcessMessages(List<LocalMessageDO> messages) {
        // 成功和失败的消息分开处理
        List<Long> successIds = new ArrayList<>();
        List<Long> retryIds = new ArrayList<>();
        List<Long> arriveMaxRetryCountIds = new ArrayList<>();

        Map<Long, String> failureReasons = new HashMap<>();

        for (LocalMessageDO message : messages) {
            try {
                if (message.getRetryCount() > message.getMaxRetryCount()) {
                    // 已经到达最大重试次数
                    arriveMaxRetryCountIds.add(message.getId());
                    continue;
                }
                MessageProcessor processor = messageProcessors.get(message.getTag());
                SendResult sendResult = processor.process(message);
                if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                    successIds.add(message.getId());
                } else {
                    retryIds.add(message.getId());
                    failureReasons.put(message.getId(), "MQ发送状态: " + sendResult.getSendStatus());
                }
            } catch (Exception e) {
                log.error("处理消息 {} 时发生异常", message.getMsgId(), e);
                retryIds.add(message.getId());
                failureReasons.put(message.getId(), "处理异常: " + e.getMessage());
            }
        }

        // 批量更新状态
        if (!successIds.isEmpty()) {
            batchUpdateMessagesStatus(successIds, LocalMessageStatusEnum.SEND_SUCCESS);
        }

        if (!arriveMaxRetryCountIds.isEmpty()) {
            // todo 通知人工处理
            batchUpdateMessagesStatus(arriveMaxRetryCountIds, LocalMessageStatusEnum.ARRIVE_MAX_RETRY_COUNT);
        }

        if (!retryIds.isEmpty()) {
            batchUpdateRetryMessages(retryIds, failureReasons);
        }
    }

    /**
     * 批量更新消息状态
     */
    private void batchUpdateMessagesStatus(List<Long> ids, LocalMessageStatusEnum status) {
        if (ids.isEmpty()) return;

        List<LocalMessageDO> updates = ids.stream().map(id -> {
            LocalMessageDO update = new LocalMessageDO();
            update.setId(id);
            update.setStatus(status.getStatus());
            if (status == LocalMessageStatusEnum.SEND_FAIL) {
                update.setRetryCount(localMessageService.getById(id).getMaxRetryCount());
            }
            return update;
        }).collect(Collectors.toList());

        if (updates.size() > 0) {
            localMessageService.updateBatchById(updates);
        }
    }

    /**
     * 批量更新重试消息
     */
    private void batchUpdateRetryMessages(List<Long> ids, Map<Long, String> failReasons) {
        if (ids.isEmpty()) return;

        List<LocalMessageDO> messages = localMessageService.listByIds(ids);
        List<LocalMessageDO> updates = messages.stream().map(message -> {
            LocalMessageDO update = new LocalMessageDO();
            update.setId(message.getId());
            update.setStatus(LocalMessageStatusEnum.SEND_FAIL.getStatus());
            update.setRetryCount(message.getRetryCount() + 1);
            update.setNextRetryTime(getNextRetryTime(message.getRetryCount() + 1));
            update.setFailReason(failReasons.get(message.getId()));
            return update;
        }).collect(Collectors.toList());

        if (updates.size() > 0) {
            localMessageService.updateBatchById(updates);
        }
    }

    /**
     * 获取下次重试时间
     *
     * @param retryCount
     * @return
     */
    private long getNextRetryTime(int retryCount) {
        long interval = (long) Math.min(Math.pow(2, retryCount) * 1000, 3600 * 1000);
        return System.currentTimeMillis() + interval;
    }
}