/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vrs.core.handler.spel.mq;

import com.vrs.annotation.Idempotent;
import com.vrs.core.IdempotentContext;
import com.vrs.core.aop.IdempotentAspect;
import com.vrs.core.aop.IdempotentParam;
import com.vrs.core.exception.RepeatConsumptionException;
import com.vrs.core.handler.IdempotentExecuteHandler;
import com.vrs.enums.IdempotentMQConsumeStatusEnum;
import com.vrs.toolkit.LogUtil;
import com.vrs.toolkit.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 MQ 场景
 * 这个类主要用于处理消息队列（MQ）中的重复消息问题，确保每条消息只被处理一次。
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@RequiredArgsConstructor
public final class IdempotentSpELByMQExecuteHandler implements IdempotentExecuteHandler {

    private final StringRedisTemplate redisTemplate;
    /**
     * 设置一个常量表示 Redis 中存储的状态的超时时间，这里设置为10分钟
     */
    private final static int TIMEOUT = 600;
    /**
     * 用于在上下文中保存包装对象的键
     */
    private final static String WRAPPER = "wrapper:spEL:MQ";

    /**
     * 构建 IdempotentParam 对象，该对象包含了用于幂等性检查所需的信息。
     *
     * @param joinPoint AOP 中的连接点，代表了被通知的方法调用。
     * @return 返回构建好的 IdempotentParam 对象。
     */
    @SneakyThrows
    @Override
    public IdempotentParam buildParam(ProceedingJoinPoint joinPoint) {
        // 获取方法上的 Idempotent 注解
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        // 解析出幂等性的 key，基于方法参数和注解中的表达式
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        // 构建并返回 IdempotentParam 对象
        return IdempotentParam.builder().lockKey(key).joinPoint(joinPoint).build();
    }

    /**
     * 幂等逻辑判断，
     *
     * @param wrapper 包含了幂等性检查所需的参数的包装对象。
     */
    @Override
    public void handler(IdempotentParam wrapper) {
        // 生成唯一的幂等性键
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        // 尝试将唯一键设置为正在消费的状态，并设置过期时间
        Boolean setIfAbsent = redisTemplate
                .opsForValue()
                // 将key设置为正在消费状态
                .setIfAbsent(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMING.getCode(), TIMEOUT, TimeUnit.SECONDS);
        if (setIfAbsent != null && !setIfAbsent) {
            // --if--如果键已存在，则获取其状态并判断是否需要抛出异常
            String consumeStatus = redisTemplate.opsForValue().get(uniqueKey);
            // 判断是否消费失败
            boolean isConsuming = IdempotentMQConsumeStatusEnum.isConsuming(consumeStatus);
            if (isConsuming) {
                // 记录警告日志
                LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ 重复消费, {}.", uniqueKey, "正在消费中");
                // 抛出异常，删除幂等性键，待消息队列重新投递来消费
                throw new RepeatConsumptionException(isConsuming);
            }
            // 记录警告日志
            LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ 重复消费, {}.", uniqueKey, "消费已完成");

        }
        // 将包装对象存入上下文
        IdempotentContext.put(WRAPPER, wrapper);
    }

    /**
     * 在客户端消费过程中发生异常时，需要清理幂等标识以便下次消息重试时能正常处理
     */
    @Override
    public void exceptionProcessing() {
        // 从上下文中获取包装对象
        IdempotentParam wrapper = (IdempotentParam) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            // 获取幂等性注解
            Idempotent idempotent = wrapper.getIdempotent();
            // 生成唯一的幂等性键
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                // 删除 Redis 中的幂等性键
                redisTemplate.delete(uniqueKey);
            } catch (Throwable ex) {
                // 记录错误日志
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] 删除MQ防重令牌失败。", uniqueKey);
            }
        }
    }

    /**
     * 消息消费完成的后处理
     */
    @Override
    public void postProcessing() {
        // 从上下文中获取包装对象
        IdempotentParam wrapper = (IdempotentParam) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            // 获取幂等性注解
            Idempotent idempotent = wrapper.getIdempotent();
            // 生成唯一的幂等性键
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                // 更新幂等性键的状态为消费完成，并设置过期时间。在过期之前，无法再次消费
                redisTemplate.opsForValue().set(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), idempotent.keyTimeout(), TimeUnit.SECONDS);
            } catch (Throwable ex) {
                // 记录错误日志
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}]  删除MQ防重令牌失败。", uniqueKey);
            }
        }
    }
}
