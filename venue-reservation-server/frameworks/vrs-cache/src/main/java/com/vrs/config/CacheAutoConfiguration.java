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

package com.vrs.config;

import com.vrs.RedisKeySerializer;
import com.vrs.StringRedisTemplateProxy;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 缓存配置自动装配
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@Component
@AllArgsConstructor
// @EnableConfigurationProperties 注解用于激活 RedisDistributedProperties 和 BloomFilterPenetrateProperties 这两个配置类的支持。
// RedisDistributedProperties 类中定义的所有属性都可以从配置文件（如 application.properties 或 application.yml）中读取并绑定到对应的 Java 对象上
// BloomFilterPenetrateProperties 类中的属性也会被读取并绑定
// 这使得你可以在应用中轻松地访问和使用这些配置属性，而不需要手动创建和配置这些 Bean。
// 例如，在 CacheAutoConfiguration 类中，你可以直接注入 RedisDistributedProperties 和 BloomFilterPenetrateProperties 来获取配置值
@EnableConfigurationProperties({RedisDistributedProperties.class, BloomFilterPenetrateProperties.class})
public class CacheAutoConfiguration {

    private final RedisDistributedProperties redisDistributedProperties;

    /**
     * 创建一个自定义的Redis Key序列化器。
     * 根据配置属性设置Key前缀和字符集。
     * 这可以用来在所有键前加上一个统一的前缀，便于管理和组织键的空间。
     *
     * @return 自定义的RedisKeySerializer实例
     */
    @Bean
    public RedisKeySerializer redisKeySerializer() {
        String prefix = redisDistributedProperties.getPrefix();
        String prefixCharset = redisDistributedProperties.getPrefixCharset();
        return new RedisKeySerializer(prefix, prefixCharset);
    }

    /**
     * 创建一个布隆过滤器，用于防止缓存穿透攻击。
     * 仅当配置文件中指定了启用布隆过滤器时才创建。
     *
     * @param redissonClient                 Redisson客户端，用于连接Redis集群
     * @param bloomFilterPenetrateProperties 布隆过滤器的相关配置属性
     * @return 初始化好的RBloomFilter实例
     */
    @Bean
    @ConditionalOnProperty(prefix = BloomFilterPenetrateProperties.PREFIX, name = "enabled", havingValue = "true")
    public RBloomFilter<String> cachePenetrationBloomFilter(RedissonClient redissonClient, BloomFilterPenetrateProperties bloomFilterPenetrateProperties) {
        // 获取布隆过滤器实例
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter(bloomFilterPenetrateProperties.getName());
        // 尝试初始化布隆过滤器，设置预期插入的数量和允许的误判率
        cachePenetrationBloomFilter.tryInit(bloomFilterPenetrateProperties.getExpectedInsertions(),
                bloomFilterPenetrateProperties.getFalseProbability());
        return cachePenetrationBloomFilter;
    }

    /**
     * 静态代理模式：建一个StringRedisTemplate的代理类，以增强其功能。
     * 例如，可以添加额外的功能，如缓存穿透保护等。
     *
     * @param redisKeySerializer 自定义的Redis Key序列化器
     * @param stringRedisTemplate Spring Data Redis提供的StringRedisTemplate实例
     * @param redissonClient Redisson客户端，用于高级Redis操作
     * @return 增强后的StringRedisTemplate代理实例
     */
    @Bean(name = "distributedCache")
    public StringRedisTemplateProxy StringRedisTemplateDamProxy(RedisKeySerializer redisKeySerializer,
                                                                StringRedisTemplate stringRedisTemplate,
                                                                RedissonClient redissonClient) {
//        System.out.println("配置distributedCache");
        // 设置StringRedisTemplate的Key序列化器
        stringRedisTemplate.setKeySerializer(redisKeySerializer);
        // 返回StringRedisTemplate的代理对象
        return new StringRedisTemplateProxy(stringRedisTemplate, redisDistributedProperties, redissonClient);
    }
}
