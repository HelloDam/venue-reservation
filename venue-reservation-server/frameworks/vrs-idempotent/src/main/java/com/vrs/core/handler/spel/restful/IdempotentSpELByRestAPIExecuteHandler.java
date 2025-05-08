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

package com.vrs.core.handler.spel.restful;

import com.vrs.annotation.Idempotent;
import com.vrs.constant.RedisKeyConstants;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ServiceException;
import com.vrs.core.IdempotentContext;
import com.vrs.core.aop.IdempotentAspect;
import com.vrs.core.aop.IdempotentParam;
import com.vrs.core.handler.IdempotentExecuteHandler;
import com.vrs.toolkit.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 RestAPI(Restful) 场景
 * 基于分布式锁实现
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@RequiredArgsConstructor
public final class IdempotentSpELByRestAPIExecuteHandler implements IdempotentExecuteHandler {

    /**
     * Redisson 客户端，用于操作分布式锁
     */
    private final RedissonClient redissonClient;

    /**
     * 分布式锁的基础键名，用于存储全局唯一标识的锁
     */
    private final static String LOCK = RedisKeyConstants.IDEMPOTENT_PREFIX + "lock:spEL:restAPI";

    /**
     * 构建幂等参数包装器，通过解析 SpEL 表达式生成请求的唯一标识（锁键）
     *
     * @param joinPoint 切点对象，包含目标方法信息及参数
     * @return 构建好的幂等参数包装器
     */
    @SneakyThrows
    @Override
    public IdempotentParam buildParam(ProceedingJoinPoint joinPoint) {
        // 从切点对象中获取方法上的 @Idempotent 注解
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        // 使用 SpEL 工具解析注解中的 key 属性表达式，生成请求的唯一标识（锁键）
        String lockKey = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
//        System.out.println("lockKey:" + lockKey);
        return IdempotentParam.builder().lockKey(lockKey).joinPoint(joinPoint).build();
    }

    /**
     * 幂等性逻辑处理。尝试获取分布式锁，如果获取失败（即锁已被其他请求持有），说明有相同方法和参数的请求正在执行，
     * 此时抛出异常，拒绝当前请求。
     *
     * @param wrapper 幂等参数包装器
     */
    @Override
    public void handler(IdempotentParam wrapper) {
//        System.out.println("wrapper.getLockKey():" + wrapper.getLockKey());
        String uniqueKey = RedisKeyConstants.IDEMPOTENT_PREFIX + wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        RLock lock = redissonClient.getLock(uniqueKey);
        // 尝试获取锁，如果无法立即获取（即锁已被其他请求持有），抛出异常，表示请求重复
        if (!lock.tryLock()) {
            System.out.println("SPEL幂等性错误，wrapper.getIdempotent().message():" + wrapper.getIdempotent().message());
            throw new ServiceException(wrapper.getIdempotent().message(), BaseErrorCode.IDEMPOTENT_ERROR);
        }
        // 上下文用来传递分布式锁，便于请求处理完成之后进行解锁
        IdempotentContext.put(LOCK, lock);
    }

    /**
     * 后处理，对分布式锁进行解锁
     */
    @Override
    public void postProcessing() {
        // 从 IdempotentContext 中获取之前存储的锁实例
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            // 如果锁实例不为空，进行解锁操作
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /**
     * 请求的方法执行过程中发生了异常，也对分布式锁进行解锁
     */
    @Override
    public void exceptionProcessing() {
        // 从 IdempotentContext 中获取之前存储的锁实例
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            // 如果锁实例不为空，进行解锁操作
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
