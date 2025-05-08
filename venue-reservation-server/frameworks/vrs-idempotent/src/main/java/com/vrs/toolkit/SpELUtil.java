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

import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Lists;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Optional;

/**
 * SpEL 表达式解析工具
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
public class SpELUtil {

    /**
     * 校验并返回实际使用的 SpEL 表达式
     *
     * @param spEl       SpEL 表达式字符串
     * @param method     目标方法对象
     * @param contextObj 目标方法的参数数组
     * @return 如果传入的 SpEL 表达式包含特定符号（如 "#" 或 "T("），则解析并返回其实际值；否则直接返回传入的 SpEL 表达式字符串
     */
    public static Object parseKey(String spEl, Method method, Object[] contextObj) {
        // 定义一个列表，存储 SpEL 表达式可能包含的特殊标志符
        ArrayList<String> spELFlag = Lists.newArrayList("#", "T(");
        // 查找传入 SpEL 表达式是否包含这些特殊标志符中的任意一个
        Optional<String> optional = spELFlag.stream().filter(spEl::contains).findFirst();
        // 如果找到，则需要解析 SpEL 表达式
        if (optional.isPresent()) {
            // 调用 parse 方法解析 SpEL 表达式，并返回解析后的值
            Object parse = parse(spEl, method, contextObj);
            return parse;
        }
        // 如果未找到特殊标志符，直接返回传入的 SpEL 表达式字符串
        return spEl;
    }

    /**
     * 转换参数为字符串
     *
     * @param spEl       spEl 表达式
     * @param contextObj 上下文对象
     * @return 解析的字符串值
     */
    public static Object parse(String spEl, Method method, Object[] contextObj) {
        DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(spEl);
        String[] params = discoverer.getParameterNames(method);
//        System.out.println("contextObj:" + JSON.toJSONString(contextObj));
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (ArrayUtil.isNotEmpty(params)) {
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], contextObj[len]);
            }
        }
        Object value = exp.getValue(context);
        return value;
//        String md5Hex = DigestUtil.md5Hex(JSON.toJSONString(contextObj));
//        System.out.println("md5Hex:" + md5Hex);
//        return md5Hex;
    }
}
