package com.vrs.service.impl;

import cn.hutool.core.lang.Singleton;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.vrs.StringRedisTemplateProxy;
import com.vrs.chain_of_responsibility.ChainContext;
import com.vrs.common.context.UserContext;
import com.vrs.constant.ChainConstant;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.exception.ServiceException;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.page.PageUtil;
import com.vrs.convention.result.Result;
import com.vrs.domain.dto.mq.ExecuteReserveMqDTO;
import com.vrs.domain.dto.mq.TimePeriodStockReduceMqDTO;
import com.vrs.domain.dto.req.*;
import com.vrs.domain.dto.resp.TimePeriodRespDTO;
import com.vrs.domain.entity.OrderDO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.feign.OrderFeignService;
import com.vrs.mapper.TimePeriodMapper;
import com.vrs.rocketMq.producer.ExecuteReserveProducer;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodService;
import com.vrs.utils.DateUtil;
import com.vrs.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dam
 * @description 针对表【time_period_0】的数据库操作Service实现
 * @createDate 2024-11-17 16:35:42
 */
@Service
@RequiredArgsConstructor
public class TimePeriodServiceImpl extends ServiceImpl<TimePeriodMapper, TimePeriodDO>
        implements TimePeriodService {

    private final StringRedisTemplate stringRedisTemplate;

    private final OrderFeignService orderFeignService;

    @Qualifier("distributedCache")
    private final StringRedisTemplateProxy distributedCache;

    private final ChainContext chainContext;

    private final RedissonClient redissonClient;

    private final PartitionService partitionService;

    private final TransactionTemplate transactionTemplate;

    private final ExecuteReserveProducer executeReserveProducer;

    private final ScheduledExecutorService tokenRefreshExecutor = Executors.newScheduledThreadPool(1);

    @Value("${vrs.binlog.isUse}")
    private boolean isUseBinlog;

    /**
     * 传统秒杀架构，使用缓存存储具体的剩余库存，使用 位图 来存储空闲场号
     * 下单时首先尝试扣减库存和分配空闲场号，如果可以扣减成功，再执行下单等逻辑
     * 存在问题：
     * 1、如果缓存扣减成功之后，应用宕机了，没有执行数据库库存扣减和生成订单逻辑，那就会出现缓存、数据库不一致的情况。应用重启之后，需要重新同步缓存和数据库，需要人工管理
     * 2、使用同步扣减数据库库存和下单，接口吞吐量不够高
     *
     * @param timePeriodId
     * @param courtIndex
     * @return
     */
    @Override
    public OrderDO reserve1(Long timePeriodId, Integer courtIndex) {
        //// 参数校验：使用责任链模式校验数据是否正确
        TimePeriodReserveReqDTO timePeriodReserveReqDTO = new TimePeriodReserveReqDTO(timePeriodId, courtIndex);
        chainContext.handler(ChainConstant.RESERVE_CHAIN_NAME, timePeriodReserveReqDTO);
        TimePeriodDO timePeriodDO = timePeriodReserveReqDTO.getTimePeriodDO();
        Long venueId = timePeriodReserveReqDTO.getVenueId();
        VenueDO venueDO = timePeriodReserveReqDTO.getVenueDO();
        PartitionDO partitionDO = timePeriodReserveReqDTO.getPartitionDO();

        //// 使用lua脚本获取一个空场地对应的索引，并扣除相应的库存，同时在里面进行用户的查重
        // 首先检测空闲场号缓存有没有加载好，没有的话进行加载
        this.checkBitMapCache(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodReserveReqDTO.getTimePeriodId()),
                timePeriodId,
                partitionDO.getNum());
        // 其次检测时间段库存有没有加载好，没有的话进行加载
        this.getStockByTimePeriodId(timePeriodReserveReqDTO.getTimePeriodId());
        // 执行lua脚本
        Long freeCourtIndex = executeStockReduceByLua(
                timePeriodReserveReqDTO,
                venueDO,
                courtIndex,
                RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY,
                RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY);
        if (freeCourtIndex == -3L) {
            // --if-- 用户已经购买过该时间段
            throw new ClientException(BaseErrorCode.TIME_PERIOD_COURT_HAVE_BEEN_BOUGHT_ERROR);
        }
        if (freeCourtIndex == -2L) {
            // --if-- 用户已经购买过该时间段
            throw new ClientException(BaseErrorCode.TIME_PERIOD_HAVE_BOUGHT_ERROR);
        } else if (freeCourtIndex == -1L) {
            // --if-- 没有空闲的场号
            throw new ServiceException(BaseErrorCode.TIME_PERIOD_SELL_OUT_ERROR);
        }

        //// 修改数据库中时间段的库存和已经选定的场号，并生成订单
        // 为了保证事务原子性，将修改数据库库存操作和创建订单放在了一起，而且是同步执行，如果想要接口吞吐量更高，这里肯定是需要优化成异步的
        return this.executePreserveV1(
                timePeriodDO,
                freeCourtIndex,
                venueId,
                RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY,
                RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY);
    }

    /**
     * 执行下单和数据库库存扣减操作
     *
     * @param timePeriodDO
     * @param courtIndex
     * @param venueId
     * @return
     */
    @Override
    public OrderDO executePreserveV1(TimePeriodDO timePeriodDO,
                                     Long courtIndex, Long venueId,
                                     String stockKey, String freeIndexBitMapKey) {
        // 编程式开启事务，减少事务粒度，避免长事务的发生
        return transactionTemplate.execute(status -> {
            try {
                // 扣减当前时间段的库存，修改空闲场信息
                baseMapper.updateStockAndBookedSlots(timePeriodDO.getId(), timePeriodDO.getPartitionId(), courtIndex);

                // 调用远程服务创建订单
                OrderGenerateReqDTO orderGenerateReqDTO = OrderGenerateReqDTO.builder()
                        .timePeriodId(timePeriodDO.getId())
                        .partitionId(timePeriodDO.getPartitionId())
                        .periodDate(timePeriodDO.getPeriodDate())
                        .beginTime(timePeriodDO.getBeginTime())
                        .endTime(timePeriodDO.getEndTime())
                        .courtIndex(courtIndex)
                        .userId(UserContext.getUserId())
                        .userName(UserContext.getUsername())
                        .venueId(venueId)
                        .payAmount(timePeriodDO.getPrice())
                        .build();

                Result<OrderDO> result;
                try {
                    result = orderFeignService.generateOrder(orderGenerateReqDTO);
                    if (result == null || !result.isSuccess()) {
                        // --if-- 订单生成失败，抛出异常，上面的库存扣减也会回退
                        throw new ServiceException(BaseErrorCode.ORDER_GENERATE_ERROR);
                    }
                } catch (Exception e) {
                    // --if-- 订单生成服务调用失败
                    // 恢复缓存中的信息
                    this.restoreStockAndBookedSlotsCache(
                            timePeriodDO.getId(),
                            UserContext.getUserId(),
                            courtIndex,
                            stockKey,
                            freeIndexBitMapKey);
                    // todo 如果说由于网络原因，实际上订单已经创建成功了，但是因为超时访问失败，这里库存却回滚了，此时需要将订单置为废弃状态（即删除）
                    // 发送一个短暂的延时消息（时间过长，用户可能已经支付），去检查订单是否生成，如果生成，将其删除
                    // 打印错误堆栈信息
                    e.printStackTrace();
                    // 把错误返回到前端
                    throw new ServiceException(e.getMessage());
                }
                return result.getData();
            } catch (Exception ex) {
                status.setRollbackOnly();
                throw ex;
            }
        });
    }

    /**
     * 尝试获取令牌，令牌获取成功之后，发送消息，异步执行库存扣减和订单生成
     * 注意：令牌在极端情况下，如扣减令牌之后，服务宕机了，此时令牌的库存是小于真实库存的
     * 如果查询令牌发现库存为0，尝试去数据库中加载数据，加载之后库存还是0，说明时间段确实售罄了
     * 使用消息队列异步 扣减库存，更新缓存，生成订单
     *
     * @param timePeriodId
     * @param courtIndex
     */
    @Override
    public String reserve2(Long timePeriodId, Integer courtIndex) {
        //// 参数校验：使用责任链模式校验数据是否正确
        TimePeriodReserveReqDTO timePeriodReserveReqDTO = new TimePeriodReserveReqDTO(timePeriodId, courtIndex);
        chainContext.handler(ChainConstant.RESERVE_CHAIN_NAME, timePeriodReserveReqDTO);
        Long venueId = timePeriodReserveReqDTO.getVenueId();
        VenueDO venueDO = timePeriodReserveReqDTO.getVenueDO();
        PartitionDO partitionDO = timePeriodReserveReqDTO.getPartitionDO();
        TimePeriodDO timePeriodDO = timePeriodReserveReqDTO.getTimePeriodDO();

        //// 使用lua脚本获取一个空场地对应的索引，并扣除相应的库存，同时在里面进行用户的查重
        // 首先检测空闲场号缓存有没有加载好，没有的话进行加载
        this.checkBitMapCache(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_TOKEN_KEY, timePeriodReserveReqDTO.getTimePeriodId()),
                timePeriodId,
                partitionDO.getNum());
        // 其次检测时间段库存有没有加载好，没有的话进行加载
        this.getStockByTimePeriodId(RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_TOKEN_KEY, timePeriodReserveReqDTO.getTimePeriodId());
        // todo 判断是否还有令牌，没有的话，重新加载（注意要分布式锁）
        // 执行lua脚本
        Long freeCourtIndex = executeStockReduceByLua(
                timePeriodReserveReqDTO,
                venueDO,
                courtIndex, RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_TOKEN_KEY,
                RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_TOKEN_KEY);
        if (freeCourtIndex == -2L) {
            // --if-- 用户已经购买过该时间段
            throw new ClientException(BaseErrorCode.TIME_PERIOD_HAVE_BOUGHT_ERROR);
        } else if (freeCourtIndex == -1L) {
            // --if-- 没有空闲的场号，查询数据库，如果数据库中有库存，删除缓存，下一个用户预定时重新加载令牌
            this.refreshTokenByCheckDataBase(timePeriodId);
            throw new ServiceException(BaseErrorCode.TIME_PERIOD_SELL_OUT_ERROR);
        }

        //// 发送消息，异步更新库存并生成订单
        String orderSn = SnowflakeIdUtil.nextId() + String.valueOf(UserContext.getUserId() % 1000000);
        SendResult sendResult = executeReserveProducer.sendMessage(ExecuteReserveMqDTO.builder()
                .orderSn(orderSn)
                .timePeriodId(timePeriodId)
                .courtIndex(freeCourtIndex)
                .venueId(venueId)
                .userId(UserContext.getUserId())
                .userName(UserContext.getUsername())
                .partitionId(partitionDO.getId())
                .price(timePeriodDO.getPrice())
                .periodDate(timePeriodDO.getPeriodDate())
                .beginTime(timePeriodDO.getBeginTime())
                .endTime(timePeriodDO.getEndTime())
                .build());
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            log.error("消息发送失败: " + sendResult.getSendStatus());
            // 恢复令牌缓存
            this.restoreStockAndBookedSlotsCache(
                    timePeriodId,
                    UserContext.getUserId(),
                    freeCourtIndex,
                    RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_TOKEN_KEY,
                    RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_TOKEN_KEY);
            throw new ServiceException(BaseErrorCode.MQ_SEND_ERROR);
        }
        return orderSn;
    }

    /**
     * 查询数据库是否还有库存，如果还有的话，删除令牌，让下一个用户重新加载令牌缓存
     *
     * @param timePeriodId
     */
    private void refreshTokenByCheckDataBase(Long timePeriodId) {
        RLock lock = redissonClient.getLock(String.format(RedisCacheConstant.VENUE_LOCK_TIME_PERIOD_REFRESH_TOKEN_KEY, timePeriodId));
        // 尝试获取分布式锁，获取不成功直接返回
        if (!lock.tryLock()) {
            return;
        }
        // 延迟 10 秒之后去检查数据库和令牌是否一致
        // 为啥要延迟？如果不延迟的话，可能高峰期时，大量请求过来，数据库还没来得及更新，就触发令牌刷新，导致超卖
        tokenRefreshExecutor.schedule(() -> {
            try {
                TimePeriodDO timePeriodDO = this.getById(timePeriodId);
                if (timePeriodDO.getStock() > 0) {
                    // --if-- 数据库中还有库存，说明数据库中的库存和令牌中的库存不一致，删除缓存，让下一个用户重新获取
                    stringRedisTemplate.delete(RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_TOKEN_KEY);
                    stringRedisTemplate.delete(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_TOKEN_KEY);
                }
            } finally {
                lock.unlock();
            }
        }, 10, TimeUnit.SECONDS);
    }

    /**
     * 使用lua脚本，进行缓存中的库存扣减，并分配空闲场号
     *
     * @param timePeriodReserveReqDTO
     * @param venueDO
     * @param courtIndex
     * @param stockKey
     * @param freeIndexBitMapKey
     * @return
     */
    private Long executeStockReduceByLua(TimePeriodReserveReqDTO timePeriodReserveReqDTO, VenueDO venueDO,
                                         Integer courtIndex, String stockKey, String freeIndexBitMapKey) {
        // 使用 Hutool 的单例管理容器 管理lua脚本的加载，保证其只被加载一次
        String luaScriptPath = "lua/free_court_index_allocate_by_bitmap.lua";
        DefaultRedisScript<Long> luaScript = Singleton.get(luaScriptPath, () -> {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(luaScriptPath)));
            redisScript.setResultType(Long.class);
            return redisScript;
        });
        // 执行用户重复预定校验、库存扣减、场号分配
        Long freeCourtIndex = stringRedisTemplate.execute(
                luaScript,
                Lists.newArrayList(
                        String.format(stockKey, timePeriodReserveReqDTO.getTimePeriodId()),
                        String.format(freeIndexBitMapKey, timePeriodReserveReqDTO.getTimePeriodId()),
                        String.format(RedisCacheConstant.VENUE_IS_USER_BOUGHT_TIME_PERIOD_KEY, timePeriodReserveReqDTO.getTimePeriodId())
                ),
                UserContext.getUserId().toString(),
                String.valueOf(venueDO.getAdvanceBookingDay() * 86400),
                courtIndex.toString()
        );
        return freeCourtIndex;
    }

    /**
     * 使用管道来批量将数据存储到Redis中
     *
     * @param timePeriodDOList
     * @param isCache          是否缓存数据
     */
    @Override
    public void batchPublishTimePeriodOptimize(List<TimePeriodDO> timePeriodDOList, boolean isCache) {
        if (timePeriodDOList == null || timePeriodDOList.size() == 0) {
            return;
        }
        /// 将时间段存放到数据库中
//        this.saveBatch(timePeriodDOList);
        baseMapper.insertBatchIgnore(timePeriodDOList);

        if (!isCache) return;

        /// 将时间段信息放到缓存中
        // 创建一个管道回调
        RedisCallback<Void> pipelineCallback = connection -> {
            // 开始管道
            connection.openPipeline();
            for (TimePeriodDO timePeriodDO : timePeriodDOList) {
                // 时间段开始时间
                long timePeriodStartMill = DateUtil.combineLocalDateAndLocalTimeToDateTimeMill(timePeriodDO.getPeriodDate(), timePeriodDO.getBeginTime());
                // 计算从现在到时间段开始还有多少毫秒 + 余量（86400000表示一天）
                //todo 待确认 cacheTimeSecond 是否一定为正数
                long cacheTimeSecond = (timePeriodStartMill - System.currentTimeMillis() + 86400000) / 1000;
                /// 时间段信息
                connection.setEx(
                        String.format(RedisCacheConstant.VENUE_TIME_PERIOD_KEY, timePeriodDO.getId()).getBytes(),
                        cacheTimeSecond,
                        JSON.toJSONString(timePeriodDO).getBytes()
                );
                /// 库存
                connection.setEx(
                        String.format(RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY, timePeriodDO.getId()).getBytes(),
                        cacheTimeSecond,
                        JSON.toJSONString(timePeriodDO.getStock()).getBytes()
                );

                /// 空闲场号
                // 根据场次数计算位图所需的字节数，每8位用1个字节表示
                byte[] freeFieldBitmap = new byte[(timePeriodDO.getStock() + 7) / 8];
                // 初始化位图为全0，代表所有场次初始状态为空闲
                Arrays.fill(freeFieldBitmap, (byte) 0);
                // 将位图存入Redis，键是VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY
                connection.setEx(
                        String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodDO.getId()).getBytes(),
                        cacheTimeSecond,
                        freeFieldBitmap
                );
            }
            // 执行管道中的所有命令
            connection.closePipeline();
            return null;
        };
        // 使用StringRedisTemplate执行管道回调
        stringRedisTemplate.execute(pipelineCallback);
    }

    @Override
    public void batchPublishTimePeriod(List<TimePeriodDO> timePeriodDOList, boolean isCache) {
        if (timePeriodDOList == null || timePeriodDOList.size() == 0) {
            return;
        }
        /// 将时间段存放到数据库中
        //        this.saveBatch(timePeriodDOList);
        baseMapper.insertBatchIgnore(timePeriodDOList);

        if (!isCache) return;

        /// 将时间段信息放到缓存中
        for (TimePeriodDO timePeriodDO : timePeriodDOList) {
            // 时间段开始时间
            long timePeriodStartMill = DateUtil.combineLocalDateAndLocalTimeToDateTimeMill(timePeriodDO.getPeriodDate(), timePeriodDO.getBeginTime());
            // 计算从现在到时间段开始还有多少毫秒 + 余量（86400000表示一天）
            long cacheTimeSecond = (timePeriodStartMill - System.currentTimeMillis() + 86400000) / 1000;

            // 确保缓存时间不为负数
            if (cacheTimeSecond <= 0) {
                continue;
            }

            /// 时间段信息
            stringRedisTemplate.opsForValue().set(
                    String.format(RedisCacheConstant.VENUE_TIME_PERIOD_KEY, timePeriodDO.getId()),
                    JSON.toJSONString(timePeriodDO),
                    cacheTimeSecond,
                    TimeUnit.SECONDS
            );

            /// 库存
            stringRedisTemplate.opsForValue().set(
                    String.format(RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY, timePeriodDO.getId()),
                    JSON.toJSONString(timePeriodDO.getStock()),
                    cacheTimeSecond,
                    TimeUnit.SECONDS
            );

            /// 空闲场号
            // 根据场次数计算位图所需的字节数，每8位用1个字节表示
            byte[] freeFieldBitmap = new byte[(timePeriodDO.getStock() + 7) / 8];
            // 初始化位图为全0，代表所有场次初始状态为空闲
            Arrays.fill(freeFieldBitmap, (byte) 0);
            // 将位图存入Redis
            stringRedisTemplate.opsForValue().set(
                    String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodDO.getId()),
                    freeFieldBitmap.toString(),
                    cacheTimeSecond,
                    TimeUnit.SECONDS
            );
        }
    }

    /**
     * 扣减库存
     *
     * @param timePeriodStockReduceMqDTO
     */
    @Override
    public void reduceStockAndBookedSlots(TimePeriodStockReduceMqDTO timePeriodStockReduceMqDTO) {
        baseMapper.updateStockAndBookedSlots(timePeriodStockReduceMqDTO.getTimePeriodId(), timePeriodStockReduceMqDTO.getPartitionId(), timePeriodStockReduceMqDTO.getCourtIndex());
    }

    /**
     * 库存、空闲场号回退
     *
     * @param timePeriodStockRestoreReqDTO
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void restoreStockAndBookedSlots(TimePeriodStockRestoreReqDTO timePeriodStockRestoreReqDTO) {
        // 释放数据库中的库存
        this.restoreStockAndBookedSlotsDatabase(timePeriodStockRestoreReqDTO);
        // 释放缓存中的库存（如果缓存更新出错，数据库库存也会回退）
        // 极端情况，缓存更新成功了，但是应用出现宕机，数据库的库存没有释放，咋办？
        // 因为这个方法实际上是订单关闭消费canal的消息时调用，如果说这个方法执行失败的话，后面会再次消费。
        // 虽然缓存的库存已经提前释放了，但是再次消费的时候，由于缓存释放操作的幂等性，再调用一次，问题也不大
        this.restoreStockAndBookedSlotsCache(
                timePeriodStockRestoreReqDTO.getTimePeriodId(),
                timePeriodStockRestoreReqDTO.getUserId(),
                timePeriodStockRestoreReqDTO.getCourtIndex(),
                RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY,
                RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY);
        // todo 这里需要辨别是令牌方式还是直接库存方式
    }

    /**
     * 库存、空闲场号数据库回退
     */
    @Override
    public void restoreStockAndBookedSlotsDatabase(TimePeriodStockRestoreReqDTO timePeriodStockRestoreReqDTO) {
        // 恢复数据库中的库存
        baseMapper.restoreStockAndBookedSlots(timePeriodStockRestoreReqDTO.getTimePeriodId(), timePeriodStockRestoreReqDTO.getPartitionId(), timePeriodStockRestoreReqDTO.getCourtIndex());
    }

    /**
     * 库存、空闲场号、已购买用户缓存回退
     */
    @Override
    public void restoreStockAndBookedSlotsCache(Long timePeriodId, Long userId, Long courtIndex,
                                                String stockKey, String freeIndexBitMapKey) {
        //// 使用lua脚本获取一个空场地对应的索引，并扣除相应的库存
        // 使用 Hutool 的单例管理容器 管理lua脚本的加载，保证其只被加载一次
        String luaScriptPath = "lua/free_court_index_release_by_bitmap.lua";
        DefaultRedisScript<Long> luaScript = Singleton.get(luaScriptPath, () -> {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(luaScriptPath)));
            redisScript.setResultType(Long.class);
            return redisScript;
        });
        Long status = stringRedisTemplate.execute(
                luaScript,
                Lists.newArrayList(
                        String.format(stockKey, timePeriodId),
                        String.format(freeIndexBitMapKey, timePeriodId),
                        String.format(RedisCacheConstant.VENUE_IS_USER_BOUGHT_TIME_PERIOD_KEY, timePeriodId)
                ),
                userId.toString(),
                courtIndex.toString()
        );
        if (status == -3) {
            // --if-- 该场号本身就是空闲的，无需释放库存（说明库存已经被释放过了，这不要抛异常出去，否则库存释放方法会反复失败）
        } else if (status == -2) {
            // --if-- 用户没有购买该时间段
            throw new ServiceException(BaseErrorCode.TIME_PERIOD_HAVE_NOT_BOUGHT_ERROR);
        } else if (status == -1) {
            // --if-- 场号不合法
            throw new ServiceException(BaseErrorCode.TIME_PERIOD_FREE_COURT_INDEX_ERROR);
        }

    }

    /**
     * 根据ID查询时间段
     *
     * @param timePeriodId
     * @return
     */
    @Override
    public TimePeriodDO getTimePeriodDOById(Long timePeriodId) {
        return (TimePeriodDO) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_KEY, timePeriodId),
                new TypeReference<TimePeriodDO>() {
                },
                () -> {
                    return this.getById(timePeriodId);
                },
                1,
                TimeUnit.DAYS);
    }

    /**
     * 获取指定时间段的库存
     *
     * @param timePeriodId
     * @return
     */
    @Override
    public Integer getStockByTimePeriodId(Long timePeriodId) {
        return (Integer) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY, timePeriodId),
                new TypeReference<Integer>() {
                },
                () -> {
                    TimePeriodDO timePeriodDO = this.getById(timePeriodId);
                    return timePeriodDO.getStock();
                },
                1,
                TimeUnit.DAYS);
    }

    @Override
    public Integer getStockByTimePeriodId(String keyName, Long timePeriodId) {
        return (Integer) distributedCache.safeGet(
                String.format(keyName, timePeriodId),
                new TypeReference<Integer>() {
                },
                () -> {
                    TimePeriodDO timePeriodDO = this.getById(timePeriodId);
                    return timePeriodDO.getStock();
                },
                1,
                TimeUnit.DAYS);
    }

    /**
     * 根据位图获取时间段预定情况
     * 首先检测是否存在相应的key，没有的话，使用双重判定锁。读取数据库的位图，存储到缓存中
     *
     * @param timePeriodId
     * @param partitionId
     * @return
     */
    @Override
    public List<Integer> getBookedListByTimePeriodId(Long timePeriodId, Long partitionId) {
        String cacheKey = String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodId);
        PartitionDO partitionDO = partitionService.getPartitionDOById(partitionId);
        List<Integer> bitArrayFromCache = getBitListFromCache(cacheKey, timePeriodId, partitionDO.getNum());

        // 调整位图集合
        if (bitArrayFromCache.size() <= partitionDO.getNum()) {
            // --if-- 位不够，补位
            int diff = partitionDO.getNum() - bitArrayFromCache.size();
            for (int i = 0; i < diff; i++) {
                bitArrayFromCache.add(0);
            }
        } else {
            // --if-- 位超了，删减后面的元素
            int diff = bitArrayFromCache.size() - partitionDO.getNum();
            for (int i = 0; i < diff; i++) {
                bitArrayFromCache.remove(bitArrayFromCache.size() - 1);
            }
        }
        return bitArrayFromCache;
    }

    /**
     * 初始化Redis中的位图，并设置key的过期时间
     *
     * @param freeIndexBitmapKey 位图的键名
     * @param longValue          用于初始化位图的 long 类型数据
     * @param expireSecond       key的过期时间（秒）
     */
    @Override
    public void initializeFreeIndexBitmap(String freeIndexBitmapKey, int initStock, long longValue, long expireSecond) {
        // 将 long 转换为64位的二进制字符串
        String binaryString = Long.toBinaryString(longValue);
        // 确保字符串长度为64位，不足的部分用0补齐
        binaryString = String.format("%64s", binaryString).replace(' ', '0');

        // 从低位到高位遍历二进制字符串，设置位图中的对应位
        for (int i = 0; i < 64 && initStock-- >= 0; i++) {
            // 注意：long的最低位对应位图的第0位
            if (binaryString.charAt(63 - i) == '1') {
                stringRedisTemplate.opsForValue().setBit(freeIndexBitmapKey, i, true).booleanValue();
            } else {
                stringRedisTemplate.opsForValue().setBit(freeIndexBitmapKey, i, false).booleanValue();
            }
        }

        // 设置过期时间，仅当expireTime大于0时进行设置
        if (expireSecond > 0) {
            stringRedisTemplate.expire(freeIndexBitmapKey, expireSecond, TimeUnit.SECONDS);
        }
    }

    /**
     * 从 Redis 位图中获取 01 数组
     *
     * @param freeIndexBitmapKey 位图的键
     * @return 01 数组
     */
    public List<Integer> getBitListFromCache(String freeIndexBitmapKey, Long timePeriodId, int initStock) {
        byte[] bitmapBytes = getBitArrayFromCahe(freeIndexBitmapKey, timePeriodId, initStock);
        if (bitmapBytes == null || bitmapBytes.length == 0) {
            // 如果位图为空，返回空列表
            return new ArrayList<>();
        }

        List<Integer> bitArray = new ArrayList<>();
        for (byte b : bitmapBytes) {
            for (int i = 7; i >= 0; i--) {
                // --for-- 每个字节有8个位
                // 计算该位是否为1
                int bit = (b >> i) & 1;
                bitArray.add(bit);
            }
        }
        return bitArray;
    }

    /**
     * 获取位图字节数组，如果位图还没有初始化的话，进行初始化
     *
     * @param freeIndexBitmapKey
     * @param timePeriodId
     * @param initStock
     * @return
     */
    @Override
    public byte[] getBitArrayFromCahe(String freeIndexBitmapKey, Long timePeriodId, int initStock) {
        this.checkBitMapCache(freeIndexBitmapKey, timePeriodId, initStock);
        // 获取位图的字节数组
        byte[] bitmapBytes = stringRedisTemplate.execute((RedisCallback<byte[]>) connection ->
                connection.get(freeIndexBitmapKey.getBytes())
        );

        return bitmapBytes;
    }

    /**
     * 检测位图缓存是否加载好，没有的话，执行加载操作
     *
     * @param freeIndexBitmapKey
     * @param timePeriodId
     * @param initStock
     */
    @Override
    public void checkBitMapCache(String freeIndexBitmapKey, Long timePeriodId, int initStock) {
        String cache = stringRedisTemplate.opsForValue().get(freeIndexBitmapKey);
        if (StringUtils.isBlank(cache)) {
            // --if-- 如果缓存中的位图为空
            RLock lock = redissonClient.getLock(String.format(RedisCacheConstant.VENUE_LOCK_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodId));
            lock.lock();
            try {
                // 双重判定一下，避免其他线程已经加载数据到缓存中了
                cache = stringRedisTemplate.opsForValue().get(freeIndexBitmapKey);
                if (StringUtils.isBlank(cache)) {
                    // --if-- 如果缓存中的位图还是空，到数据库中加载位图
                    TimePeriodDO timePeriodDO = this.getById(timePeriodId);
                    if (timePeriodDO == null) {
                        throw new ServiceException(timePeriodId + "对应的时间段为null", BaseErrorCode.SERVICE_ERROR);
                    }
                    // 将位图信息设置到缓存中
                    this.initializeFreeIndexBitmap(freeIndexBitmapKey, initStock, timePeriodDO.getBookedSlots(), 24 * 3600);
                }
            } finally {
                // 解锁
                lock.unlock();
            }
        }
    }

    @Override
    public List<TimePeriodDO> listTimePeriodWithIdList(List<Long> timePeriodIdList, List<Long> partitionIdList) {
        if (timePeriodIdList.size() == 0) {
            return new ArrayList<>();
        } else {
            QueryWrapper<TimePeriodDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", timePeriodIdList);
            queryWrapper.in("partition_id", partitionIdList);
            return baseMapper.selectList(queryWrapper);
        }
    }

    @Override
    public PageResponse<TimePeriodDO> pageTimePeriodDO(TimePeriodListReqDTO request) {
        QueryWrapper<TimePeriodDO> queryWrapper = new QueryWrapper<>();
        if (request.getPartitionId() != null) {
            queryWrapper.eq("partition_id", request.getPartitionId());
        }
        // 按照开始时间升序排序
        queryWrapper.orderByAsc("begin_time");
        IPage<TimePeriodDO> page = baseMapper.selectPage(new Page(request.getCurrent(), request.getSize()), queryWrapper);
        return PageUtil.convert(page);
    }

    /**
     * 列举不同日期的可预订时间段
     *
     * @param periodDateAndTimePeriodMapRepDTO
     * @return
     */
    @Override
    public LinkedHashMap<String, List<TimePeriodRespDTO>> getPeriodDateAndTimePeriodMap(PeriodDateAndTimePeriodMapRepDTO periodDateAndTimePeriodMapRepDTO) {

        PartitionDO partitionDOById = partitionService.getPartitionDOById(periodDateAndTimePeriodMapRepDTO.getPartitionId());
        if (partitionDOById == null) {
            throw new ClientException(BaseErrorCode.PARTITION_NULL_ERROR);
        }

        LinkedHashMap<String, List<TimePeriodRespDTO>> periodDateAndTimePeriodMap = (LinkedHashMap<String, List<TimePeriodRespDTO>>) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_BY_PARTITION_ID_KEY, periodDateAndTimePeriodMapRepDTO.getPartitionId()),
                new TypeReference<LinkedHashMap<String, List<TimePeriodRespDTO>>>() {
                },
                () -> {
                    QueryWrapper<TimePeriodDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("partition_id", periodDateAndTimePeriodMapRepDTO.getPartitionId());
                    // 只查询在今天和今天之后的可预订时间段
                    queryWrapper.ge("period_date", LocalDate.now());
                    List<TimePeriodDO> timePeriodDOList = baseMapper.selectList(queryWrapper);
                    // 封装成，日期 对应 当天时间段 的形式
                    LinkedHashMap<String, List<TimePeriodRespDTO>> map = new LinkedHashMap<>();
                    for (TimePeriodDO timePeriodDO : timePeriodDOList) {
                        TimePeriodRespDTO timePeriodRespDTO = new TimePeriodRespDTO();
                        BeanUtils.copyProperties(timePeriodDO, timePeriodRespDTO);
                        if (!map.containsKey(timePeriodDO.getPeriodDate().toString())) {
                            List<TimePeriodRespDTO> newTimePeriodDOList = new ArrayList<>();
                            newTimePeriodDOList.add(timePeriodRespDTO);
                            map.put(timePeriodDO.getPeriodDate().toString(), newTimePeriodDOList);
                        } else {
                            map.get(timePeriodDO.getPeriodDate().toString()).add(timePeriodRespDTO);
                        }
                    }
                    return map;
                },
                minutesUntilMidnight(),
                TimeUnit.MINUTES);

        // 数据处理，替换为缓存中的库存和预订情况
        for (Map.Entry<String, List<TimePeriodRespDTO>> entry : periodDateAndTimePeriodMap.entrySet()) {
            for (TimePeriodRespDTO timePeriodRespDTO : entry.getValue()) {
                timePeriodRespDTO.setStock(getStockByTimePeriodId(timePeriodRespDTO.getId()));
                timePeriodRespDTO.setBookedList(getBookedListByTimePeriodId(timePeriodRespDTO.getId(), timePeriodRespDTO.getPartitionId()));
            }
        }

        return periodDateAndTimePeriodMap;
    }

    @Override
    public List<TimePeriodRespDTO> listTimePeriodByDate(ListTimePeriodByDateRepDTO listTimePeriodByDateRepDTO) {
        List<TimePeriodRespDTO> timePeriodRespDTOList = (List<TimePeriodRespDTO>) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_TIME_PERIOD_BY_PARTITION_ID_AND_DATE_KEY, listTimePeriodByDateRepDTO.getPartitionId(), listTimePeriodByDateRepDTO.getDate().toString()),
                new TypeReference<List<TimePeriodRespDTO>>() {
                },
                () -> {
                    QueryWrapper<TimePeriodDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("partition_id", listTimePeriodByDateRepDTO.getPartitionId());
                    // 只查询在今天和今天之后的可预订时间段
                    queryWrapper.eq("period_date", listTimePeriodByDateRepDTO.getDate());
                    List<TimePeriodDO> timePeriodDOList = baseMapper.selectList(queryWrapper);
                    return timePeriodDOList.stream().map(item -> {
                                TimePeriodRespDTO timePeriodRespDTO = new TimePeriodRespDTO();
                                BeanUtils.copyProperties(item, timePeriodRespDTO);
                                return timePeriodRespDTO;
                            }
                    ).collect(Collectors.toList());
                },
                minutesUntilMidnight(),
                TimeUnit.MINUTES);
        for (TimePeriodRespDTO timePeriodRespDTO : timePeriodRespDTOList) {
            timePeriodRespDTO.setStock(getStockByTimePeriodId(timePeriodRespDTO.getId()));
            timePeriodRespDTO.setBookedList(getBookedListByTimePeriodId(timePeriodRespDTO.getId(), timePeriodRespDTO.getPartitionId()));
        }
        return timePeriodRespDTOList;
    }

    @Override
    public TimePeriodRespDTO infoById(Long id) {
        TimePeriodDO timePeriodDO = baseMapper.selectById(id);
        TimePeriodRespDTO timePeriodRespDTO = new TimePeriodRespDTO();
        BeanUtils.copyProperties(timePeriodDO, timePeriodRespDTO);
        timePeriodRespDTO.setBookedList(getBookedListByTimePeriodId(id, timePeriodDO.getPartitionId()));
        return timePeriodRespDTO;
    }


    /**
     * 计算从现在到凌晨12点还有多少分钟。
     *
     * @return 从当前时间到次日零点的分钟数。
     */
    public static long minutesUntilMidnight() {
        // 获取当前的本地日期时间
        LocalDateTime now = LocalDateTime.now();

        // 获取当天的最后一秒（23:59:59）
        LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX);

        // 如果当前时间已经超过了当天的最后一秒，则获取明天的零点
        LocalDateTime midnight = now.isAfter(endOfDay) ?
                now.plusDays(1).toLocalDate().atStartOfDay() :
                now.toLocalDate().plusDays(1).atStartOfDay();

        // 计算从当前时间到第二天凌晨的时间差
        Duration duration = Duration.between(now, midnight);

        // 返回相差的分钟数
        return duration.toMinutes();
    }
}

