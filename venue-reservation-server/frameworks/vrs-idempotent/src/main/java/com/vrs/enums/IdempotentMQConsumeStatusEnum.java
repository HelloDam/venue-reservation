package com.vrs.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 幂等 MQ 消费状态枚举
 */
@RequiredArgsConstructor
public enum IdempotentMQConsumeStatusEnum {

    /**
     * 消费中
     */
    CONSUMING("0"),

    /**
     * 已消费
     */
    CONSUMED("1");

    @Getter
    private final String code;

    /**
     * 判断消费状态是否等于消费中
     *
     * @param consumeStatus 消费状态
     * @return 是否消费中
     */
    public static boolean isConsuming(String consumeStatus) {
        return CONSUMING.code.equals(consumeStatus);
    }
}
