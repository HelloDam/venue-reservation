package com.vrs.service.scheduled;

import com.vrs.constant.RedisCacheConstant;
import com.vrs.service.TimePeriodModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author dam
 * @create 2024/11/17 16:44
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TimePeriodScheduledTasks {

    private final TimePeriodModelService timePeriodModelService;
    private final StringRedisTemplate stringRedisTemplate;
    private int tableNum = 16;
    private final boolean isCacheTimePeriod = false;

    /**
     * 在每天凌晨1点执行
     * 扫描数据库的时间段模板，生成可预定的时间段
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void timePeriodGenerator1() {
        for (int i = 0; i < tableNum; i++) {
            timePeriodGenerate(i);
        }
    }

    /**
     * 兜底一次，保证时间段都生成成功了
     * 扫描数据库的时间段模板，生成可预定的时间段
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void timePeriodGenerator2() {
        for (int i = 0; i < tableNum; i++) {
            // 获取当前表的状态
            String status = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.VENUE_TIME_PERIOD_GENERATE_KEY, i));

            // 如果状态为 "1"，说明任务已经完成，跳过
            if ("1".equals(status)) {
                continue;
            }

            // 如果还没有完成，尝试获取锁并执行任务
            timePeriodGenerate(i);
        }
    }

    /**
     * 时间段生成
     *
     * @param tableIndex
     */
    private void timePeriodGenerate(int tableIndex) {
        // 0状态设置2小时就过期，方便没有执行完成的任务在兜底时可以重新执行
        boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_GENERATE_KEY, tableIndex),
                "0", 2, TimeUnit.HOURS
        ).booleanValue();

        if (isSuccess) {
            try {
                // --if-- 设置键成功，说明集群中的其他机器还没有扫描当前表，由当前机器执行
                // 执行时间段生成
                timePeriodModelService.generateTimePeriodByModelOptimize(tableIndex, isCacheTimePeriod);

                // 时间段生成成功，设置状态为1，过期时间段也设置长一点，方便兜底时检测
                stringRedisTemplate.opsForValue().set(
                        String.format(RedisCacheConstant.VENUE_TIME_PERIOD_GENERATE_KEY, tableIndex),
                        "1", 6, TimeUnit.HOURS
                );
            } catch (Exception e) {
                // 如果任务执行失败，删除锁，以便其他机器可以重试
                stringRedisTemplate.delete(String.format(RedisCacheConstant.VENUE_TIME_PERIOD_GENERATE_KEY, tableIndex));
                log.error("时间段生成失败，表编号：{}，错误信息：{}", tableIndex, e.getMessage(), e);
            }
        } else {
            // 如果锁已被占用，检查任务是否已完成
            String currentStatus = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.VENUE_TIME_PERIOD_GENERATE_KEY, tableIndex));
            if (!"1".equals(currentStatus)) {
                log.warn("表编号：{} 的任务未完成，但锁已被占用，可能由其他机器处理中", tableIndex);
            }
        }
    }

    // 每5秒执行一次
//    @Scheduled(fixedRate = 1000)
//    public void reportCurrentTime() {
//        for (int i = 0; i < tableNum; i++) {
//            // 执行时间段生成
//            timePeriodModelService.generateTimePeriodByModel(i);
//        }
//    }
}