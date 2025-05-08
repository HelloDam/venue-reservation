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

package com.vrs;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.vrs.config.RedisDistributedProperties;
import com.vrs.core.CacheGetFilter;
import com.vrs.core.CacheGetIfAbsent;
import com.vrs.core.CacheLoader;
import com.vrs.design_pattern.RedisScriptSingleton;
import com.vrs.toolkit.CacheUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 分布式缓存之操作 Redis 模版代理
 * 底层通过 {@link RedissonClient}、{@link StringRedisTemplate} 完成外观接口行为
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
@RequiredArgsConstructor
public class StringRedisTemplateProxy implements DistributedCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisDistributedProperties redisProperties;
    private final RedissonClient redissonClient;

    private static final String SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX = "safe_get_distributed_lock_get:";

    /**
     * 从Redis缓存中获取指定类型的对象。
     *
     * @param key           缓存键名，用于标识缓存条目
     * @param typeReference 指定的类类型，用于将字符串形式的缓存转换为目标对象类型
     * @return 转换后的对象，如果缓存中没有对应键的值，则返回null
     */
    public Object get(String key, TypeReference typeReference) {
        String value = stringRedisTemplate.opsForValue().get(key);

        Class rawType = typeReference.getRawType();
        // 如果目标类型是String，直接返回获取到的字符串值
        if (String.class.isAssignableFrom(rawType)) {
            return value;
        }

        // 否则，使用Fastjson2将JSON格式的字符串解析为目标类型对象
        return JSON.parseObject(value, typeReference);
    }

    /**
     * 当给定的所有键在Redis中都不存在时执行插入操作
     *
     * @param keys
     * @return
     */

    public Boolean putIfAllAbsent(@NotNull Collection<String> keys) {
        // 创建或获取一个单例的Lua脚本实例，该脚本用于在Redis中执行特定的操作
        // 如果单例中还没有这个脚本，则创建一个新的脚本实例
        DefaultRedisScript<Boolean> actual = RedisScriptSingleton.getInstance();
        // 使用String类型的RedisTemplate执行Lua脚本
        // 第一个参数是脚本实例
        // 第二个参数是键的列表
        // 第三个参数是Redis配置中的值超时时间（可能用于设置键值对的有效期）
        Boolean result = stringRedisTemplate.execute(actual, Lists.newArrayList(keys), redisProperties.getValueTimeout().toString());
        // 检查执行结果是否非空且为true，然后返回
        return result != null && result;
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Boolean deleteByPrefix(String keyPrefix) {
        // 根据给定的前缀构建匹配模式，* 是一个通配符，匹配任意数量的字符
        String pattern = keyPrefix + "*";

        // 使用 RedisCallback 执行操作
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            // 定义扫描选项，设置匹配模式
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    // 每次返回的最大键数量
                    .count(100000)
                    .build();

            // 尝试使用 Cursor 迭代器遍历所有匹配的键
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                // 创建一个列表来存储要删除的所有键
                List<byte[]> keysToDelete = new ArrayList<>();
                // 遍历 Cursor 中的所有键，存储到列表中
                while (cursor.hasNext()) {
                    keysToDelete.add(cursor.next());
                }

                // 如果有键需要删除，则打开一个管道并批量执行删除操作
                if (!keysToDelete.isEmpty()) {
                    connection.openPipeline();
                    // 遍历列表中的每一个键，并执行删除操作
                    keysToDelete.forEach(key -> connection.del(key));
                    // 关闭管道，执行所有之前发送到 Redis 服务器的命令
                    connection.closePipeline();
                }
            }
            // 返回 null，因为 RedisCallback 的泛型指定为 Void
            return null;
        });
        return true;
    }

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 定义一个泛型方法，该方法用于从缓存中获取数据，如果缓存中没有数据，则通过提供的加载器加载数据
     *
     * @param key
     * @param typeReference
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param <T>
     * @return
     */
    public <T> Object get(@NotBlank String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {

        // 尝试从缓存中获取与给定键 'key' 相关的对象，并将它转换为类型 'clazz'
        Object result = get(key, typeReference);

        // 检查获取到的结果是否非空或非空白字符串（如果 'result' 是字符串的话）
        if (!CacheUtil.isNullOrBlank(result)) {
            // 如果缓存中有数据，则直接返回该数据
            return result;
        }

        // 如果缓存中没有数据，则调用 loadAndSet 方法加载数据并将其设置到缓存中
        // 这里 'false' 参数可能是表示是否强制刷新缓存
        // 'null' 参数可能是额外的上下文信息或者用于其他目的
        return loadAndSet(key, cacheLoader, timeout, timeUnit, false, null);
    }

    /**
     * 安全地从缓存中获取数据，防止缓存穿透，并使用布隆过滤器减少无效查询。
     *
     * @param <T>              泛型类型，指定返回值的数据类型
     * @param key              缓存键名
     * @param typeReference    返回值的类类型，用于反序列化缓存值
     * @param cacheLoader      缓存加载器，当缓存中没有数据时用于加载数据
     * @param timeout          设置缓存项的有效期
     * @param timeUnit         指定有效期的时间单位
     * @param bloomFilter      布隆过滤器，用于判断某元素是否可能存在于集合中
     * @param cacheGetFilter   自定义过滤器，用于决定是否返回空
     * @param cacheGetIfAbsent 当缓存及数据库均无数据时，执行的操作
     * @return 缓存中的数据或者通过缓存加载器加载的数据
     */
    public <T> Object safeGet(String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit,
                              RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheGetFilter, CacheGetIfAbsent<String> cacheGetIfAbsent) {
        // 尝试从缓存中获取与给定键 'key' 相关联的对象，并尝试将其转换为 'clazz' 类型
        Object result = get(key, typeReference);

        // 缓存结果不等于空或空字符串直接返回；
        // 通过函数判断是否返回空，为了适配布隆过滤器无法删除的场景；
        // 两者都不成立，判断布隆过滤器是否存在，检查键是否不在布隆过滤器中（即键可能不存在于缓存中），不存在返回空
        if (!CacheUtil.isNullOrBlank(result)
                || Optional.ofNullable(cacheGetFilter).map(each -> each.filter(key)).orElse(false)
                || Optional.ofNullable(bloomFilter).map(each -> !each.contains(key)).orElse(false)) {
            return result;
        }

        // 获取一个分布式锁实例，用于保证并发环境下的数据一致性
        RLock lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key);
        // 上锁，防止多个线程同时加载相同的数据
        lock.lock();
        try {
            // 双重判定锁，减轻获得分布式锁后线程访问数据库压力
            if (CacheUtil.isNullOrBlank(result = get(key, typeReference))) {
                // 如果访问 cacheLoader 加载数据为空，执行后续操作
                if (CacheUtil.isNullOrBlank(result = loadAndSet(key, cacheLoader, timeout, timeUnit, true, bloomFilter))) {
                    // 后续操作
                    Optional.ofNullable(cacheGetIfAbsent).ifPresent(each -> each.execute(key));
                }
            }
        } finally {
            // 无论是否出现异常，都要释放锁
            lock.unlock();
        }
        return result;
    }

    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        String actual = value instanceof String ? (String) value : JSON.toJSONString(value);
        stringRedisTemplate.opsForValue().set(key, actual, timeout, timeUnit);
    }

    /**
     * 存储数据之后，将key加入到布隆过滤器
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @param bloomFilter
     */

    public void safePut(String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
        put(key, value, timeout, timeUnit);
        if (bloomFilter != null) {
            bloomFilter.add(key);
        }
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Object getInstance() {
        return stringRedisTemplate;
    }

    /**
     * 统计存在于Redis中的键的数量
     *
     * @param keys
     * @return
     */
    public Long countExistingKeys(String... keys) {
        // countExistingKeys 方法接受一个键的列表，并返回这些键中存在于 Redis 中的数量
        return stringRedisTemplate.countExistingKeys(Lists.newArrayList(keys));
    }

    /**
     * 从外部加载数据，并存储到缓存中
     *
     * @param key
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param safeFlag
     * @param bloomFilter
     * @param <T>
     * @return
     */
    private <T> T loadAndSet(String key, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, boolean safeFlag, RBloomFilter<String> bloomFilter) {
        // 通过缓存加载器加载数据
        T result = cacheLoader.load();

        // 检查加载的数据是否为空或空白（如果是字符串类型）
        if (CacheUtil.isNullOrBlank(result)) {
            // 如果数据为空或空白，则直接返回
            return result;
        }

        if (safeFlag) {
            // 使用安全的方式将数据存储到缓存中
            safePut(key, result, timeout, timeUnit, bloomFilter);
        } else {
            // 直接将数据存储到缓存中
            put(key, result, timeout, timeUnit);
        }
        return result;
    }

    //--------------------------------------------重载方法--------------------------------------------
    @Override
    public <T> Object get(String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout) {
        return get(key, typeReference, cacheLoader, timeout, redisProperties.getValueTimeUnit());

    }

    public <T> Object safeGet(@NotBlank String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout) {
        return safeGet(key, typeReference, cacheLoader, timeout, redisProperties.getValueTimeUnit());
    }

    public <T> Object safeGet(@NotBlank String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        return safeGet(key, typeReference, cacheLoader, timeout, timeUnit, null);
    }

    public <T> Object safeGet(@NotBlank String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, RBloomFilter<String> bloomFilter) {
        return safeGet(key, typeReference, cacheLoader, timeout, bloomFilter, null, null);
    }

    public <T> Object safeGet(@NotBlank String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
        return safeGet(key, typeReference, cacheLoader, timeout, timeUnit, bloomFilter, null, null);
    }

    public <T> Object safeGet(String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter) {
        return safeGet(key, typeReference, cacheLoader, timeout, redisProperties.getValueTimeUnit(), bloomFilter, cacheCheckFilter, null);
    }

    public <T> Object safeGet(String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter) {
        return safeGet(key, typeReference, cacheLoader, timeout, timeUnit, bloomFilter, cacheCheckFilter, null);
    }

    public <T> Object safeGet(String key, TypeReference typeReference, CacheLoader<T> cacheLoader, long timeout,
                              RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheGetFilter, CacheGetIfAbsent<String> cacheGetIfAbsent) {
        return safeGet(key, typeReference, cacheLoader, timeout, redisProperties.getValueTimeUnit(), bloomFilter, cacheGetFilter, cacheGetIfAbsent);
    }

    public void safePut(String key, Object value, long timeout, RBloomFilter<String> bloomFilter) {
        safePut(key, value, timeout, redisProperties.getValueTimeUnit(), bloomFilter);
    }

    public void put(String key, Object value) {
        // 将数据存入缓存，因为没有设置过期时间，默认使用配置文件中的过期时间
        put(key, value, redisProperties.getValueTimeout());
    }

    public void put(String key, Object value, long timeout) {
        put(key, value, timeout, redisProperties.getValueTimeUnit());
    }

}
