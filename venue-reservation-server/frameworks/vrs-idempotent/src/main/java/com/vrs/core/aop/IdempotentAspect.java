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

package com.vrs.core.aop;

import com.vrs.annotation.Idempotent;
import com.vrs.core.IdempotentContext;
import com.vrs.core.exception.RepeatConsumptionException;
import com.vrs.core.handler.IdempotentExecuteHandler;
import com.vrs.core.handler.IdempotentExecuteHandlerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 幂等注解 AOP 拦截器
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@Aspect
public final class IdempotentAspect {

    /**
     * 使用Around来对Idempotent注解标记的方法进行环绕增强
     * @param joinPoint 使用 @Around ，自定义的切入点
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.vrs.annotation.Idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        Idempotent idempotent = getIdempotent(joinPoint);
        // 根据注解来获取相应的处理器
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene());
        // 存储实际方法的执行结果
        Object resultObj;
        try {
            // 当不满足幂等时，execute会报错，后面的代码不会再执行
            instance.execute(joinPoint, idempotent);
            // 请求的具体方法执行
            resultObj = joinPoint.proceed();
            // 处理器进行后处理，如解除分布式锁
            instance.postProcessing();
        } catch (RepeatConsumptionException ex) {
            if (!ex.getIsConsuming()) {
                // --if-- 消息消费成功
                return null;
            }
            // 消息还在处理，抛出这个异常，表明正在重复消费
            throw ex;
        } catch (Throwable ex) {
            // 客户端消费存在异常，需要删除幂等标识方便下次 RocketMQ 再次通过重试队列投递
            instance.exceptionProcessing();
            throw ex;
        } finally {
            IdempotentContext.clean();
        }
        return resultObj;
    }

    /**
     * 从给定的ProceedingJoinPoint中获取目标方法上的Idempotent注解实例
     *
     * @param joinPoint 切点对象，包含方法签名等信息
     * @return 目标方法上的Idempotent注解实例
     * @throws NoSuchMethodException 如果找不到对应方法时抛出此异常
     */
    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法签名对象，包含方法名、参数类型等信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 通过反射获取目标类（切面所拦截的对象）上与当前方法签名匹配的Method对象
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        // 从Method对象上获取Idempotent注解实例
        return targetMethod.getAnnotation(Idempotent.class);
    }
}
