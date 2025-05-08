package com.vrs.rocketMq.listener;

import com.vrs.constant.RocketMqConstant;
import com.vrs.controller.WebSocketServer;
import com.vrs.domain.dto.mq.WebsocketMqDTO;
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
 *
 * @Author dam
 * @create 2024/9/20 21:30
 */
@Slf4j(topic = RocketMqConstant.VENUE_TOPIC)
@Component
@RocketMQMessageListener(topic = RocketMqConstant.VENUE_TOPIC,
        consumerGroup = RocketMqConstant.VENUE_CONSUMER_GROUP + "-" + RocketMqConstant.WEBSOCKET_SEND_MESSAGE_TAG,
        // 需要使用广播模式
        messageModel = MessageModel.BROADCASTING,
        // 监听tag
        selectorType = SelectorType.TAG,
        selectorExpression = RocketMqConstant.WEBSOCKET_SEND_MESSAGE_TAG
)
@RequiredArgsConstructor
public class WebSocketSendMessageListener implements RocketMQListener<MessageWrapper<WebsocketMqDTO>> {

    private final WebSocketServer webSocketServer;

    /**
     * 消费消息的方法
     * 方法报错就会拒收消息
     *
     * @param messageWrapper 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @SneakyThrows
    @Override
    public void onMessage(MessageWrapper<WebsocketMqDTO> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] websocket发生消息给{}", messageWrapper.getMessage().getToUsername());
        webSocketServer.sendMessage(messageWrapper.getMessage().getToUsername(), messageWrapper.getMessage().getMessage());
    }
}
