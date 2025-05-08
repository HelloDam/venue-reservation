package com.vrs.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 场馆类型枚举
 */
@RequiredArgsConstructor
public enum LocalMessageStatusEnum {

    INIT(0, "待发送"),
    SEND_FAIL(1, "消费失败"),
    SEND_SUCCESS(2, "消费成功"),
    ARRIVE_MAX_RETRY_COUNT(3, "超过重试次数"),;

    @Getter
    private final int status;

    @Getter
    private final String msg;

}
