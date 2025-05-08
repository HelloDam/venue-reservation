package com.vrs.rocketMq.producer;

import cn.hutool.core.util.StrUtil;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.ExecuteReserveMqDTO;
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
 * 执行预订流程 生产者
 *
 * @Author dam
 * @create 2024/9/20 16:00
 */
@Slf4j
@Component
public class ExecuteReserveProducer extends AbstractCommonSendProduceTemplate<ExecuteReserveMqDTO> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(ExecuteReserveMqDTO messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("执行时间段预定")
                .keys(String.valueOf(messageSendEvent.getTimePeriodId()))
                .topic(RocketMqConstant.VENUE_TOPIC)
                .tag(RocketMqConstant.TIME_PERIOD_EXECUTE_RESERVE_TAG)
                .sentTimeout(2000L)
                .build();
    }

    @Override
    protected Message<?> buildMessage(ExecuteReserveMqDTO messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}
