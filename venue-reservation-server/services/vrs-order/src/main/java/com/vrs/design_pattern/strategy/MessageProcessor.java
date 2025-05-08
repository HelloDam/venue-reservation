package com.vrs.design_pattern.strategy;

import com.vrs.domain.entity.LocalMessageDO;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @Author dam
 * @create 2025/5/1 21:19
 */
public interface MessageProcessor {
    SendResult process(LocalMessageDO message) throws Exception;
}