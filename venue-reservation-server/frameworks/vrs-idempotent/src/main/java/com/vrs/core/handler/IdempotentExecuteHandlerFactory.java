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


import com.vrs.ApplicationContextHolder;
import com.vrs.core.handler.spel.mq.IdempotentSpELByMQExecuteHandler;
import com.vrs.core.handler.spel.restful.IdempotentSpELByRestAPIExecuteHandler;
import com.vrs.enums.IdempotentSceneEnum;

/**
 * 幂等执行处理器工厂
 * 简单工厂模式
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
public final class IdempotentExecuteHandlerFactory {

    /**
     * 根据枚举参数获取对应的幂等执行处理器handler
     *
     * @param scene 指定幂等验证场景类型
     * @return 幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene) {
        IdempotentExecuteHandler result = null;
        switch (scene) {
            case RESTAPI: {
                result = ApplicationContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                break;
            }
            case MQ:
                result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
                break;
            default: {
            }
        }
        return result;
    }
}
