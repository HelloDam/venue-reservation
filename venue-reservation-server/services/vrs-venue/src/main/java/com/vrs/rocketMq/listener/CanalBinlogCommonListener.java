package com.vrs.rocketMq.listener;

import com.vrs.annotation.Idempotent;
import com.vrs.chain_of_responsibility.ChainContext;
import com.vrs.constant.ChainConstant;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.CanalBinlogDTO;
import com.vrs.enums.IdempotentSceneEnum;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2024/9/20 21:30
 */
@Slf4j(topic = RocketMqConstant.CANAL_TOPIC)
@Component
@RocketMQMessageListener(topic = RocketMqConstant.CANAL_TOPIC,
        consumerGroup = RocketMqConstant.CANAL_CONSUMER_GROUP,
        messageModel = MessageModel.CLUSTERING
)
@RequiredArgsConstructor
public class CanalBinlogCommonListener implements RocketMQListener<CanalBinlogDTO> {

    private final ChainContext chainContext;

    /**
     * 消费消息的方法
     * 方法报错就会拒收消息
     *
     * @param CanalBinlogDTO 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @Idempotent(
            uniqueKeyPrefix = "canal_binlog_common:",
            key = "#canalBinlogDTO.getId()+''",
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 3600L
    )
    @SneakyThrows
    @Override
    public void onMessage(CanalBinlogDTO canalBinlogDTO) {
        if (canalBinlogDTO.getOld() == null) {
            // --if-- 如果不是修改数据，快速退出，因为我们现在的业务逻辑都是识别出数据修改才有下面的操作
            return;
        }
        // 调用责任链来消费 canal 消息
        chainContext.handler(ChainConstant.CANAL_CHAIN_NAME, canalBinlogDTO);
    }
}
