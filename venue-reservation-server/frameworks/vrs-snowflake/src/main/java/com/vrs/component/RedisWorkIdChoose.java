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

package com.vrs.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Singleton;
import com.google.common.collect.Lists;
import com.vrs.algorithm.Snowflake;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.entity.WorkCenterInfo;
import com.vrs.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@Slf4j
@Component
public class RedisWorkIdChoose implements InitializingBean {

    @Autowired
    private RedisTemplate stringRedisTemplate;

    public WorkCenterInfo chooseWorkId() {
        String luaScriptPath = "lua/workId_generate.lua";
        DefaultRedisScript<List> luaScript = Singleton.get(luaScriptPath, () -> {
            DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(luaScriptPath)));
            redisScript.setResultType(List.class);
            return redisScript;
        });
        List<Long> luaResultList = null;
        try {
            luaResultList = (ArrayList) this.stringRedisTemplate.execute(luaScript, Lists.newArrayList(
                    RedisCacheConstant.SNOWFLAKE_WORK_ID_KEY
            ));
        } catch (Exception ex) {
            log.error("Redis Lua 脚本获取 WorkId 失败", ex);
        }
        if (CollUtil.isEmpty(luaResultList)) {
            log.error("Redis Lua 脚本获取 WorkId 失败");
        }
        return new WorkCenterInfo(luaResultList.get(0), luaResultList.get(1));
    }

    @Override
    public void afterPropertiesSet() {
        WorkCenterInfo workCenterInfo = chooseWorkId();
        long workId = workCenterInfo.getWorkId();
        long dataCenterId = workCenterInfo.getDataCenterId();
        log.info("Snowflake type: {}, workId: {}, dataCenterId: {}", this.getClass().getSimpleName(), workId, dataCenterId);
        // 生成机器标识之后，初始化工具类的雪花算法静态对象
        Snowflake snowflake = new Snowflake(workId, dataCenterId, true);
        SnowflakeIdUtil.initSnowflake(snowflake);
    }
}
