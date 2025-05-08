package com.vrs.rocketMq.listener;

import com.vrs.annotation.Idempotent;
import com.vrs.constant.RocketMqConstant;
import com.vrs.domain.dto.mq.ImportUserExcelMqDTO;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.UserService;
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
@Slf4j(topic = RocketMqConstant.VENUE_TOPIC)
@Component
@RocketMQMessageListener(topic = RocketMqConstant.ADMIN_TOPIC,
        consumerGroup = RocketMqConstant.ADMIN_CONSUMER_GROUP + "-" + RocketMqConstant.IMPORT_USER_EXCEL_TAG,
        messageModel = MessageModel.CLUSTERING,
        // 监听tag
        selectorType = SelectorType.TAG,
        selectorExpression = RocketMqConstant.IMPORT_USER_EXCEL_TAG
)
@RequiredArgsConstructor
public class ImportUserExcelListener implements RocketMQListener<MessageWrapper<ImportUserExcelMqDTO>> {

    private final UserService userService;

    /**
     * 消费消息的方法
     * 方法报错就会拒收消息
     *
     * @param messageWrapper 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @Idempotent(
            uniqueKeyPrefix = "import_user_excel:",
            key = "#messageWrapper.getMessage().getOrganizationId()+''",
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 3600L
    )
    @SneakyThrows
    @Override
    public void onMessage(MessageWrapper<ImportUserExcelMqDTO> messageWrapper) {
        // 开头打印日志，平常可 Debug 看任务参数，线上可报平安（比如消息是否消费，重新投递时获取参数等）
        log.info("[消费者] 机构用户数据导入，机构ID：{}", messageWrapper.getMessage().getOrganizationId());
        userService.handleImportUserExcel(messageWrapper.getMessage());
    }
}
