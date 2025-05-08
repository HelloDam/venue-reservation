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

package com.vrs.toolkit;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 缓存工具类
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
public final class CacheUtil {

    /**
     * 定义一个常量，用作拼接缓存键时的分隔符
     */
    private static final String SPLICING_OPERATOR = "_";

    /**
     * 构建缓存标识。
     * 此方法接受可变数量的字符串参数，并将它们拼接起来形成一个完整的缓存键。
     * 如果任何一个传入的字符串参数为空或仅包含空白字符，则抛出运行时异常。
     *
     * @param keys 可变长度的字符串数组，每个字符串代表键的一部分
     * @return 拼接后的完整缓存键
     */
    public static String buildKey(String... keys) {
        // 遍历每个字符串参数，确保它们都不是空或仅包含空白字符
        Stream.of(keys).forEach(each -> Optional.ofNullable(Strings.emptyToNull(each))
                .orElseThrow(() -> new RuntimeException("构建缓存 key 不允许为空")));
        // 使用定义的分隔符拼接所有字符串参数，形成最终的缓存键
        return Joiner.on(SPLICING_OPERATOR).join(keys);
    }

    /**
     * 判断结果是否为空或空的字符串。
     * 此方法用于检查一个对象是否为null，或者如果它是字符串类型的话，是否为空或仅包含空白字符。
     *
     * @param cacheVal 要检查的对象
     * @return 如果对象为null，或者它是字符串且为空或仅包含空白字符，则返回true；否则返回false
     */
    public static boolean isNullOrBlank(Object cacheVal) {
        return cacheVal == null || (cacheVal instanceof String && Strings.isNullOrEmpty((String) cacheVal));
    }
}
