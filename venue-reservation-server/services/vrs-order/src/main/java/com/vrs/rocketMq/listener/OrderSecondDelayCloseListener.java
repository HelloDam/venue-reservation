package com.vrs.rocketMq.listener;

import com.vrs.annotation.Idempotent;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.OrderDelayCloseMqDTO;
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
 * @Author dam
 * @create 2024/9/20 21:30
 */
@Slf4j(topic = RocketMqConstant.ORDER_TOPIC)
@Component
@RocketMQMessageListener(topic = RocketMqConstant.ORDER_TOPIC,
        consumerGroup = RocketMqConstant.ORDER_CONSUMER_GROUP + "-" + RocketMqConstant.ORDER_SECOND_DELAY_CLOSE_TAG,
        messageModel = MessageModel.CLUSTERING,
        // 监听tag
        selectorType = SelectorType.TAG,
        selectorExpression = RocketMqConstant.ORDER_SECOND_DELAY_CLOSE_TAG
)
@RequiredArgsConstructor
public class OrderSecondDelayCloseListener implements RocketMQListener<MessageWrapper<OrderDelayCloseMqDTO>> {

    private final OrderService orderService;

    /**
     * 消费消息的方法
     * 方法报错就会拒收消息
     *
     * @param messageWrapper 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @Idempotent(
            uniqueKeyPrefix = "order_second_delay_close:",
            key = "#messageWrapper.getMessage().getOrderSn()",
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 3600L
    )
    @SneakyThrows
    @Override
    public void onMessage(MessageWrapper<OrderDelayCloseMqDTO> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 关闭订单：{}", messageWrapper.getMessage().getOrderSn());
        String orderSn = messageWrapper.getMessage().getOrderSn();
        orderService.secondCloseOrder(orderSn);
    }
}
