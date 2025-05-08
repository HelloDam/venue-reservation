package com.vrs.rocketMq.producer;

import cn.hutool.core.util.StrUtil;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.OrderDelayCloseMqDTO;
import com.vrs.templateMethod.AbstractCommonSendProduceTemplate;
import com.vrs.templateMethod.BaseSendExtendDTO;
import com.vrs.templateMethod.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 计算数据准备 生产者
 *
 * @Author dam
 * @create 2024/9/20 16:00
 */
@Slf4j
@Component
public class OrderDelayCloseProducer extends AbstractCommonSendProduceTemplate<OrderDelayCloseMqDTO> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(OrderDelayCloseMqDTO messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("延时关闭订单")
                .keys(String.valueOf(messageSendEvent.getOrderSn()))
                .topic(RocketMqConstant.ORDER_TOPIC)
                .tag(RocketMqConstant.ORDER_DELAY_CLOSE_TAG)
                .sentTimeout(2000L)
//                .delayTime(10 * 1000L)
                // 延时十分钟，关闭未支付订单
                .delayTime(10 * 60 * 1000L)
                .build();
    }

    @Override
    protected Message<?> buildMessage(OrderDelayCloseMqDTO messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}
