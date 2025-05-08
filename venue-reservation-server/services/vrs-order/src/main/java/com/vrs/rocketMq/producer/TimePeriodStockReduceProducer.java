package com.vrs.rocketMq.producer;

import cn.hutool.core.util.StrUtil;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.TimePeriodStockReduceMqDTO;
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
public class TimePeriodStockReduceProducer extends AbstractCommonSendProduceTemplate<TimePeriodStockReduceMqDTO> {

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(TimePeriodStockReduceMqDTO messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("扣减时间段库存 标记已预订场号")
                .keys(messageSendEvent.getOrderSn())
                .topic(RocketMqConstant.VENUE_TOPIC)
                .tag(RocketMqConstant.TIME_PERIOD_STOCK_REDUCE_TAG)
                .sentTimeout(2000L)
                .build();
    }

    @Override
    protected Message<?> buildMessage(TimePeriodStockReduceMqDTO messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}
