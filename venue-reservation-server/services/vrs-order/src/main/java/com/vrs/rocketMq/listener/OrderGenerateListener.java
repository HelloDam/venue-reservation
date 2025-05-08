package com.vrs.rocketMq.listener;

import com.vrs.annotation.Idempotent;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.ExecuteReserveMqDTO;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.OrderService;
import com.vrs.templateMethod.MessageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 执行预订流程 消费者
 * @Author dam
 * @create 2024/9/20 21:30
 */
@Slf4j(topic = RocketMqConstant.VENUE_TOPIC)
@Component
@RocketMQMessageListener(topic = RocketMqConstant.VENUE_TOPIC,
        consumerGroup = RocketMqConstant.VENUE_CONSUMER_GROUP + "-" + RocketMqConstant.TIME_PERIOD_EXECUTE_RESERVE_TAG,
        messageModel = MessageModel.CLUSTERING,
        // 监听tag
        selectorType = SelectorType.TAG,
        selectorExpression = RocketMqConstant.TIME_PERIOD_EXECUTE_RESERVE_TAG
)
@RequiredArgsConstructor
public class OrderGenerateListener implements RocketMQListener<MessageWrapper<ExecuteReserveMqDTO>> {

    private final OrderService orderService;

    /**
     * 消费消息的方法
     * 方法报错就会拒收消息
     *
     * @param messageWrapper 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @Idempotent(
            uniqueKeyPrefix = "time_period_execute_reserve:",
            key = "#messageWrapper.getMessage().getTimePeriodId()+'_'+#messageWrapper.getMessage().getUserName()",
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 3600L
    )
    @SneakyThrows
    @Override
    public void onMessage(MessageWrapper<ExecuteReserveMqDTO> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 执行时间段预定_时间段生成，时间段ID：{}", messageWrapper.getMessage().getTimePeriodId());
        orderService.generateOrder(messageWrapper.getMessage());
    }
}
