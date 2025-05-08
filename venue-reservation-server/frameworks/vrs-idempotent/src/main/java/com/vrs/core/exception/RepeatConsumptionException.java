package com.vrs.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 重复消费异常
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@RequiredArgsConstructor
public class RepeatConsumptionException extends RuntimeException {
    
    /**
     * 错误标识
     * <p>
     * 触发幂等逻辑时可能有两种情况：
     * 1. 消息还在处理
     * 2. 消息处理成功了，该消息直接返回成功即可
     */
    @Getter
    private final Boolean isConsuming;
}
