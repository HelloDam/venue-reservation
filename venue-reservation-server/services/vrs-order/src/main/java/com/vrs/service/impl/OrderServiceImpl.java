package com.vrs.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.common.util.QrCodeUtil;
import com.vrs.constant.OrderStatusConstant;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.constant.RocketMqConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.exception.ServiceException;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.result.Result;
import com.vrs.domain.dto.mq.ExecuteReserveMqDTO;
import com.vrs.domain.dto.mq.OrderDelayCloseMqDTO;
import com.vrs.domain.dto.mq.TimePeriodStockReduceMqDTO;
import com.vrs.domain.dto.mq.WebsocketMqDTO;
import com.vrs.domain.dto.req.*;
import com.vrs.domain.dto.resp.*;
import com.vrs.domain.entity.*;
import com.vrs.enums.LocalMessageStatusEnum;
import com.vrs.feign.AlipayFeignService;
import com.vrs.feign.VenueFeignService;
import com.vrs.mapper.OrderMapper;
import com.vrs.rocketMq.producer.OrderDelayCloseProducer;
import com.vrs.rocketMq.producer.OrderSecondDelayCloseProducer;
import com.vrs.rocketMq.producer.WebsocketSendMessageProducer;
import com.vrs.service.LocalMessageService;
import com.vrs.service.OrderService;
import com.vrs.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dam
 * @description 针对表【order】的数据库操作Service实现
 * @createDate 2024-11-30 19:03:04
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO>
        implements OrderService {

    private final OrderDelayCloseProducer orderDelayCloseProducer;
    private final OrderSecondDelayCloseProducer orderSecondDelayCloseProducer;
    private final VenueFeignService venueFeignService;
    private final AlipayFeignService alipayFeignService;
    private final StringRedisTemplate stringRedisTemplate;
    private final LocalMessageService localMessageService;
    private final TransactionTemplate transactionTemplate;
    private final WebsocketSendMessageProducer websocketSendMessageProducer;

    @Value("${vrs.binlog.isUse}")
    private boolean isUseBinlog;

    @Override
    public OrderDO generateOrder(OrderGenerateReqDTO orderGenerateReqDTO) {
        OrderDO orderDO = OrderDO.builder()
                // 订单号使用雪花算法生成分布式ID，然后再拼接用户ID的后面六位
                .orderSn(SnowflakeIdUtil.nextId() + String.valueOf(orderGenerateReqDTO.getUserId() % 1000000))
                .orderTime(new Date())
                .venueId(orderGenerateReqDTO.getVenueId())
                .partitionId(orderGenerateReqDTO.getPartitionId())
                .courtIndex(orderGenerateReqDTO.getCourtIndex())
                .timePeriodId(orderGenerateReqDTO.getTimePeriodId())
                .periodDate(orderGenerateReqDTO.getPeriodDate())
                .beginTime(orderGenerateReqDTO.getBeginTime())
                .endTime(orderGenerateReqDTO.getEndTime())
                .userId(orderGenerateReqDTO.getUserId())
                .userName(orderGenerateReqDTO.getUserName())
                .payAmount(orderGenerateReqDTO.getPayAmount())
                .orderStatus(OrderStatusConstant.UN_PAID)
                .build();
        int insert = baseMapper.insert(orderDO);
        if (insert > 0) {
            // 发送延时消息来关闭未支付的订单
            orderDelayCloseProducer.sendMessage(OrderDelayCloseMqDTO.builder()
                    .orderSn(orderDO.getOrderSn())
                    .build());
        }
        return orderDO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void closeOrder(String orderSn) {
        String orderPayLock = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.ORDER_PAY_LOCK_KEY, orderSn));
        if ("0".equals(orderPayLock)) {
            OrderDO orderDO = baseMapper.selectByOrderSn(orderSn);
            // --if-- 订单已经被锁定，说明订单正处于支付状态，先不要关闭订单，等等再看看是否支付成功了
            if (orderDO.getOrderStatus().equals(OrderStatusConstant.UN_PAID)) {
                // --if-- 当前订单还没有支付成功，发一个延时消息，如果等等订单还没有被支付，就关闭订单
                orderSecondDelayCloseProducer.sendMessage(OrderDelayCloseMqDTO.builder()
                        .orderSn(orderDO.getOrderSn())
                        .build());
                // 将订单支付状态设置为1，拒绝后面的支付调用
                stringRedisTemplate.opsForValue().set(String.format(RedisCacheConstant.ORDER_PAY_LOCK_KEY, orderSn), "1", 5, TimeUnit.MINUTES);
            }
        } else {
            // --if-- 订单不在支付中，直接关闭订单
            secondCloseOrder(orderSn);
        }
    }

    @Override
    public void secondCloseOrder(String orderSn) {
        OrderDO orderDO = baseMapper.selectByOrderSn(orderSn);
        if (orderDO != null && orderDO.getOrderStatus().equals(OrderStatusConstant.UN_PAID)) {
            // --if-- 到时间了，订单还没有支付，取消该订单
            orderDO.setOrderStatus(OrderStatusConstant.CANCEL);
            // 分片键不能更新
            orderDO.setVenueId(null);
            baseMapper.updateByOrderSn(orderDO);

            if (!isUseBinlog) {
                // --if-- 如果不启用binlog的话，需要自己手动调用方法来释放库存
                // 极端情况，如果说远程已经还原了库存，但是因为网络问题，返回了错误，导致订单没有关闭，于是出现了不一致的现象。库存都还原完了，你订单还可以支付
                Result<OrderDO> result;
                try {
                    result = venueFeignService.release(TimePeriodStockRestoreReqDTO.builder()
                            .timePeriodId(orderDO.getTimePeriodId())
                            .partitionId(orderDO.getPartitionId())
                            .courtIndex(orderDO.getCourtIndex())
                            .userId(orderDO.getUserId())
                            .build());
                } catch (Exception e) {
                    // --if-- 库存恢复远程接口调用失败
                    throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
                }
                if (result == null || !result.isSuccess()) {
                    // 因为使用了Transactional，如果这里出现了异常，订单的关闭修改会回退
                    throw new ServiceException("调用远程服务释放时间段数据库库存失败", BaseErrorCode.SERVICE_ERROR);
                }

            } else {
                // --if-- 如果启用binlog的话，会自动监听数据库的订单关闭，然后恢复缓存中的库存
            }
        }
    }

    @Override
    public PageResponse<OrderRespDTO> pageOrderDO(OrderListReqDTO request) {
        QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<>();
        if (request.getUserId() != null) {
            queryWrapper.eq("user_id", request.getUserId());
        }
        if (request.getOrderStatus() != null) {
            queryWrapper.eq("order_status", request.getOrderStatus());
        }
        queryWrapper.orderByDesc("order_time");
        IPage<OrderDO> page = baseMapper.selectPage(new Page(request.getCurrent(), request.getSize()), queryWrapper);
        Set<Long> venueIdSet = new HashSet<>();
        Set<Long> partitionIdSet = new HashSet<>();

        List<OrderRespDTO> orderRespDTOList = new ArrayList<>();
        for (OrderDO orderDO : page.getRecords()) {
            venueIdSet.add(orderDO.getVenueId());
            partitionIdSet.add(orderDO.getPartitionId());
            OrderRespDTO orderRespDTO = new OrderRespDTO();
            BeanUtils.copyProperties(orderDO, orderRespDTO);
            orderRespDTOList.add(orderRespDTO);
        }

        if (partitionIdSet.size() > 0) {
            Result<OrderListDetailRespDTO> result;
            try {
                result = venueFeignService.getOrderListDetail(OrderListDetailReqDTO.builder()
                        .partitionIdList(new ArrayList<>(partitionIdSet))
                        .venueIdList(new ArrayList<>(venueIdSet))
                        .build());
            } catch (Exception e) {
                // --if-- 库存恢复远程接口调用失败
                throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
            }
            if (result == null || !result.isSuccess()) {
                throw new ServiceException("调用远程服务查询订单详细信息失败", BaseErrorCode.SERVICE_ERROR);
            }
            OrderListDetailRespDTO orderListDetailRespDTO = result.getData();
            List<PartitionDO> partitionDOList = orderListDetailRespDTO.getPartitionDOList();
            List<VenueDO> venueDOList = orderListDetailRespDTO.getVenueDOList();
            List<TimePeriodDO> timePeriodDOList = orderListDetailRespDTO.getTimePeriodDOList();
            Map<Long, PartitionDO> idAndPartitionDOMap = new HashMap<>();
            Map<Long, VenueDO> idAndVenueDOMap = new HashMap<>();

            for (PartitionDO partitionDO : partitionDOList) {
                idAndPartitionDOMap.put(partitionDO.getId(), partitionDO);
            }
            for (VenueDO venueDO : venueDOList) {
                idAndVenueDOMap.put(venueDO.getId(), venueDO);
            }
            for (OrderRespDTO orderRespDTO : orderRespDTOList) {
                orderRespDTO.setPartitionName(idAndPartitionDOMap.get(orderRespDTO.getPartitionId()).getName());
                orderRespDTO.setVenueName(idAndVenueDOMap.get(orderRespDTO.getVenueId()).getName());
            }
        }

        return new PageResponse(request.getCurrent(), request.getSize(), page.getTotal(), orderRespDTOList);
    }

    @Override
    public String generateORCode(String orderSn) {
        OrderRespDTO orderRespDTO = this.getOrderRespDTOByOrderSn(orderSn);
        if (orderRespDTO == null) {
            // --if-- 订单不存在
            throw new ClientException(BaseErrorCode.ORDER_NULL_ERROR);
        }
        if (orderRespDTO.getOrderStatus() != OrderStatusConstant.PAID) {
            // --if-- 订单状态不是已支付状态
            throw new ClientException(BaseErrorCode.ORDER_NOT_PAID_ERROR);
        }
        if (LocalDate.now().isAfter(orderRespDTO.getPeriodDate())) {
            // --if-- 该订单预定的时间已经过了
            throw new ClientException(BaseErrorCode.ORDER_EXPIRE_ERROR);
        }
//        if (LocalDate.now().isBefore(orderRespDTO.getPeriodDate())) {
//            // --if-- 还没有到该订单预定的日期
//            throw new ClientException(BaseErrorCode.ORDER_NOT_ARRIVE_DATE_ERROR);
//        }
//        if (LocalTime.now().isAfter(orderRespDTO.getEndTime())) {
//            // --if-- 该订单预定的时间已经过了
//            throw new ClientException(BaseErrorCode.ORDER_EXPIRE_ERROR);
//        }
        return QrCodeUtil.createQRCode(orderSn);
    }

    @Override
    public String pay(String orderSn, String returnUrl) {
        OrderRespDTO orderRespDTO = this.getOrderRespDTOByOrderSn(orderSn);

        if (orderRespDTO == null) {
            // --if-- 订单不存在
            throw new ClientException(BaseErrorCode.ORDER_NULL_ERROR);
        }
        if (orderRespDTO.getOrderStatus() == OrderStatusConstant.PAID) {
            // --if-- 当前订单已经被支付
            throw new ClientException(BaseErrorCode.ORDER_HAS_PAID_ERROR);
        }
        if (orderRespDTO.getOrderStatus() == OrderStatusConstant.CANCEL) {
            // --if-- 当前订单已经取消
            throw new ClientException(BaseErrorCode.ORDER_HAS_CANCELED_ERROR);
        }
        if (orderRespDTO.getOrderStatus() == OrderStatusConstant.REFUND) {
            // --if-- 当前订单已退款
            throw new ClientException(BaseErrorCode.ORDER_HAS_REFUND_ERROR);
        }

        String orderPayLock = stringRedisTemplate.opsForValue().get(String.format(RedisCacheConstant.ORDER_PAY_LOCK_KEY, orderSn));
        if ("1".equals(orderPayLock)) {
            // --if-- 订单已经过期，不允许再发起支付
            throw new ClientException(BaseErrorCode.ORDER_EXPIRE_ERROR);
        }

        // 使用 StringBuilder 进行字符串连接
        StringBuilder subjectBuilder = new StringBuilder(orderRespDTO.getVenueName())
                .append("_")
                .append(orderRespDTO.getPartitionName())
                .append("：")
                .append(orderRespDTO.getPeriodDate())
                .append(" ")
                .append(orderRespDTO.getBeginTime())
                .append("至")
                .append(orderRespDTO.getEndTime());

        // 构建 AlipayReqDTO 对象
        AlipayPayReqDTO alipayPayReqDTO = AlipayPayReqDTO.builder()
                .orderSn(orderSn)
                .payAmount(orderRespDTO.getPayAmount())
                .subject(subjectBuilder.toString())
                .returnUrl(returnUrl)
                .build();

        Result<String> result;
        try {
            result = alipayFeignService.commonPay(alipayPayReqDTO);
        } catch (Exception e) {
            // --if-- 支付远程接口调用失败
            throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
        }
        if (result == null || !result.isSuccess()) {
            throw new ServiceException("调用远程支付宝支付失败", BaseErrorCode.SERVICE_ERROR);
        }

        // 锁定订单，防止订单过期未支付被取消
        stringRedisTemplate.opsForValue().setIfAbsent(String.format(RedisCacheConstant.ORDER_PAY_LOCK_KEY, orderSn), "0", 5, TimeUnit.MINUTES);

        return result.getData();
    }

    @Override
    public AlipayInfoRespDTO info(String orderSn) {
        OrderRespDTO orderRespDTO = this.getOrderRespDTOByOrderSn(orderSn);
        if (orderRespDTO == null) {
            // --if-- 订单不存在
            throw new ClientException(BaseErrorCode.ORDER_NULL_ERROR);
        }
        // 构建 AlipayReqDTO 对象
        AlipayInfoReqDTO alipayInfoReqDTO = AlipayInfoReqDTO.builder()
                .orderSn(orderSn)
                .build();

        Result<AlipayInfoRespDTO> result;
        try {
            result = alipayFeignService.info(alipayInfoReqDTO);
        } catch (Exception e) {
            // --if-- 支付远程接口调用失败
            throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
        }
        if (result == null || !result.isSuccess()) {
            throw new ServiceException("调用远程支付宝交易查询失败", BaseErrorCode.SERVICE_ERROR);
        }
        AlipayInfoRespDTO alipayInfoRespDTO = result.getData();
        return alipayInfoRespDTO;
    }

    /**
     * 消费预订之后的消息
     * 生成订单、生成本地消息
     *
     * @param message
     */
    @Override
    public void generateOrder(ExecuteReserveMqDTO message) {

        OrderDO orderDO = OrderDO.builder()
                // 订单号使用雪花算法生成分布式ID，然后再拼接用户ID的后面六位
                .orderSn(message.getOrderSn())
                .orderTime(new Date())
                .venueId(message.getVenueId())
                .partitionId(message.getPartitionId())
                .courtIndex(message.getCourtIndex())
                .timePeriodId(message.getTimePeriodId())
                .periodDate(message.getPeriodDate())
                .beginTime(message.getBeginTime())
                .endTime(message.getEndTime())
                .userId(message.getUserId())
                .userName(message.getUserName())
                .payAmount(message.getPrice())
                .orderStatus(OrderStatusConstant.UN_PAID)
                .build();

        TimePeriodStockReduceMqDTO timePeriodStockReduceMqDTO = TimePeriodStockReduceMqDTO.builder()
                .orderSn(message.getOrderSn())
                .timePeriodId(message.getTimePeriodId())
                .partitionId(message.getPartitionId())
                .courtIndex(message.getCourtIndex())
                .build();
        LocalMessageDO stockReduceLocalMessageDO = LocalMessageDO.builder()
                .msgId(message.getOrderSn())
                .topic(RocketMqConstant.VENUE_TOPIC)
                .tag(RocketMqConstant.TIME_PERIOD_STOCK_REDUCE_TAG)
                .content(JSON.toJSONString(timePeriodStockReduceMqDTO))
                .nextRetryTime(System.currentTimeMillis())
                .maxRetryCount(5)
                .build();
        LocalMessageDO delayCloseLocalMessageD0 = LocalMessageDO.builder()
                .msgId(SnowflakeIdUtil.nextIdStr())
                .topic(RocketMqConstant.ORDER_TOPIC)
                .tag(RocketMqConstant.ORDER_DELAY_CLOSE_TAG)
                .content(JSON.toJSONString(OrderDelayCloseMqDTO.builder()
                        .orderSn(orderDO.getOrderSn())
                        .build()))
                .nextRetryTime(System.currentTimeMillis())
                .maxRetryCount(5)
                .build();
        // 使用编程式事务，保证订单创建、本地消息插入的一致性
        boolean success = transactionTemplate.execute(status -> {
            try {
                int insertCount = baseMapper.insert(orderDO);
                localMessageService.save(stockReduceLocalMessageDO);
                // 也保存一个本地消息，进行兜底。防止事务提交成功之后就宕机，延时消息没有发生成功
                localMessageService.save(delayCloseLocalMessageD0);
                return insertCount > 0;
            } catch (Exception ex) {
                status.setRollbackOnly();
                throw ex;
            }
        });

        if (success) {
            // 发送延时消息来关闭未支付的订单
            SendResult sendResult = orderDelayCloseProducer.sendMessage(OrderDelayCloseMqDTO.builder()
                    .orderSn(orderDO.getOrderSn())
                    .build());
            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                // 延迟关单已经发生成功，后面扫描的时候，无需再处理
                LocalMessageDO localMessageDO = new LocalMessageDO();
                localMessageDO.setId(delayCloseLocalMessageD0.getId());
                localMessageDO.setStatus(LocalMessageStatusEnum.INIT.getStatus());
                localMessageService.updateById(localMessageDO);
            }
            // todo 如果出现宕机，可能出现宕机，但是 websocket 消息没有消息，所以前端还要实现一个轮询来保底
            // 通过 websocket 发送消息，通知前端
            websocketSendMessageProducer.sendMessage(WebsocketMqDTO.builder()
                    .toUsername(orderDO.getUserName())
                    .message(JSON.toJSONString(orderDO))
                    .build());
        }
    }

    @Override
    public OrderRespDTO getNearestOrder(Long userId) {
        // 先查未来日期的订单（仅按 period_date 过滤，避免数据库计算 begin_time）
        List<OrderDO> futureOrders = this.list(new QueryWrapper<OrderDO>()
                .eq("user_id", userId)
                .eq("order_status", OrderStatusConstant.PAID)
                .ge("period_date", LocalDate.now()) // 只查今天及未来的日期
                .orderByAsc("period_date", "end_time") // 按日期和时间排序
        );

        // 在应用层过滤出 begin_time >= 当前时间的订单
        OrderDO orderDO = findNearestFutureOrder(futureOrders);
        if (orderDO == null) {
            return null;
        }

        Result<OrderDetailRespDTO> result;
        try {
            result = venueFeignService.getOrderDetail(OrderDetailReqDTO.builder()
                    .partitionId(orderDO.getPartitionId())
                    .venueId(orderDO.getVenueId())
                    .build());
        } catch (Exception e) {
            // --if-- 库存恢复远程接口调用失败
            throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
        }
        if (result == null || !result.isSuccess()) {
            throw new ServiceException("调用远程服务查询订单详细信息失败", BaseErrorCode.SERVICE_ERROR);
        }
        OrderDetailRespDTO orderDetailRespDTO = result.getData();
        OrderRespDTO orderRespDTO = new OrderRespDTO();
        BeanUtils.copyProperties(orderDO, orderRespDTO);
        orderRespDTO.setVenueName(orderDetailRespDTO.getVenueDO().getName());
        orderRespDTO.setPartitionName(orderDetailRespDTO.getPartitionDO().getName());
        return orderRespDTO;
    }

    /**
     * 从订单列表中找出 begin_time >= 当前时间的最近一条订单
     */
    private OrderDO findNearestFutureOrder(List<OrderDO> orders) {
        if (orders == null || orders.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return orders.stream()
                .filter(order -> {
                    // 如果订单日期 > 今天，直接通过
                    if (order.getPeriodDate().isAfter(today)) {
                        return true;
                    }
                    // 如果订单日期 = 今天，检查 begin_time >= 当前时间
                    return order.getBeginTime().compareTo(now) >= 0;
                })
                .findFirst() // 返回第一条符合条件的订单（列表已按日期+时间排序）
                .orElse(null);
    }

    @Override
    public AlipayRefundRespDTO refund(String orderSn) {
        //// 退款条件校验
        // todo 临近开场，不能退款
        OrderRespDTO orderRespDTO = this.getOrderRespDTOByOrderSn(orderSn);
        if (orderRespDTO == null) {
            // --if-- 订单不存在
            throw new ClientException(BaseErrorCode.ORDER_NULL_ERROR);
        }
        if (orderRespDTO.getOrderStatus() != OrderStatusConstant.PAID) {
            // --if-- 当前订单还没有被支付
            throw new ClientException(BaseErrorCode.ORDER_NOT_PAID_ERROR);
        }
        // 构建 AlipayReqDTO 对象
        AlipayRefundReqDTO alipayRefundReqDTO = AlipayRefundReqDTO.builder()
                .orderSn(orderSn)
                .refundAmount(orderRespDTO.getPayAmount())
                .build();

        Result<AlipayRefundRespDTO> result;
        try {
            result = alipayFeignService.commonRefund(alipayRefundReqDTO);
        } catch (Exception e) {
            // --if-- 支付远程接口调用失败
            throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
        }
        if (result == null || !result.isSuccess()) {
            throw new ServiceException("调用远程支付宝退款失败", BaseErrorCode.SERVICE_ERROR);
        }
        return result.getData();
    }

    @Override
    public void payOrder(String orderSn) {
        // 修改订单状态为已支付状态
        baseMapper.updateStatusByOrderSn(orderSn, OrderStatusConstant.PAID);
        // 删除订单支付锁定标识
        stringRedisTemplate.delete(String.format(RedisCacheConstant.ORDER_PAY_LOCK_KEY, orderSn));
    }

    @Override
    public void refundOrder(String orderSn) {
        // 修改订单状态为已退款状态
        baseMapper.updateStatusByOrderSn(orderSn, OrderStatusConstant.REFUND);
    }

    /**
     * 获取订单详细信息
     *
     * @param orderSn
     * @return
     */
    public OrderRespDTO getOrderRespDTOByOrderSn(String orderSn) {
        OrderDO orderDO = this.getOne(new QueryWrapper<OrderDO>().eq("order_sn", orderSn));
        if (orderDO == null) {
            return null;
        }
        Result<OrderDetailRespDTO> result;
        try {
            result = venueFeignService.getOrderDetail(OrderDetailReqDTO.builder()
                    .partitionId(orderDO.getPartitionId())
                    .venueId(orderDO.getVenueId())
                    .build());
        } catch (Exception e) {
            // --if-- 库存恢复远程接口调用失败
            throw new ServiceException(e.getMessage(), BaseErrorCode.REMOTE_ERROR);
        }
        if (result == null || !result.isSuccess()) {
            throw new ServiceException("调用远程服务查询订单详细信息失败", BaseErrorCode.SERVICE_ERROR);
        }
        OrderDetailRespDTO orderDetailRespDTO = result.getData();
        OrderRespDTO orderRespDTO = new OrderRespDTO();
        BeanUtils.copyProperties(orderDO, orderRespDTO);
        orderRespDTO.setVenueName(orderDetailRespDTO.getVenueDO().getName());
        orderRespDTO.setPartitionName(orderDetailRespDTO.getPartitionDO().getName());
        return orderRespDTO;
    }

}




