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

package com.vrs.core.handler;

import com.vrs.annotation.Idempotent;
import com.vrs.core.aop.IdempotentParam;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等执行处理器
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
public interface IdempotentExecuteHandler {

    IdempotentParam buildParam(ProceedingJoinPoint joinPoint);

    /**
     * 幂等处理逻辑
     *
     * @param wrapper 幂等参数包装器
     */
    void handler(IdempotentParam wrapper);

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    default void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent){
        // 模板方法模式：构建幂等参数包装器
        IdempotentParam idempotentParam = buildParam(joinPoint).setIdempotent(idempotent);
        // 如果不满足幂等，handler方法会通过抛出异常来使下面的程序被中断
        handler(idempotentParam);
    };

    /**
     * 幂等异常流程处理
     */
    default void exceptionProcessing() {

    }

    /**
     * 执行目标方法成功的后置处理
     */
    default void postProcessing() {

    }
}
