package com.vrs.utils;

import com.google.common.collect.Lists;
import com.vrs.config.IpFlowControlConfiguration;
import com.vrs.constant.RedisCacheConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * IP限流工具类，提供基于Redis Lua脚本实现的IP地址访问频率限制功能。
 *
 * @Author dam
 * @create 2024/3/15 20:41
 */
@Slf4j
public class FlowLimitUtil {
    /**
     * 存储IP限流Lua脚本的路径常量。
     */
    private static final String IP_FLOW_CONTROL_LUA_SCRIPT_PATH = "lua/ip_flow_control.lua";

    /**
     * 使用单例模式存储编译后的IP限流Lua脚本实例，避免每次调用时重复加载和编译。
     */
    private static DefaultRedisScript<Long> redisScript;

    /**
     * 获取或初始化IP限流Lua脚本实例。使用synchronized保证只有一个线程初始化实例
     *
     * @return 编译后的IP限流Lua脚本实例
     */
    public static DefaultRedisScript<Long> getRedisScriptInstance() {
        if (redisScript == null) {
            synchronized (FlowLimitUtil.class) {
                redisScript = new DefaultRedisScript<>();
                // 首先获取Redis的lua脚本
                redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(IP_FLOW_CONTROL_LUA_SCRIPT_PATH)));
                // 设置脚本返回值的类型
                redisScript.setResultType(Long.class);
            }
        }
        return redisScript;
    }


    /**
     * 根据IP地址、HttpServletResponse、StringRedisTemplate、IpFlowControlConfiguration和主题（topic）执行IP限流检查。
     * 如果IP地址的访问频率超过配置的限制，将返回错误响应。
     *
     * @param ipAddr              客户端IP地址
     * @param response            HttpServletResponse对象，用于设置返回给客户端的响应
     * @param stringRedisTemplate Spring Data Redis模板，用于执行Lua脚本
     * @param ipFlowControlConfig IP限流配置对象，包含时间窗口和最大访问次数等参数
     * @param topic               请求的主题，用于区分不同类型的请求限流
     * @return true表示访问频率未超过限制，可以继续处理请求；false表示访问频率超出限制，已返回错误响应
     */
    @SneakyThrows
    public static boolean ipFlowLimit(String ipAddr, StringRedisTemplate stringRedisTemplate,
                                      IpFlowControlConfiguration ipFlowControlConfig, String topic) {
        // 执行Lua脚本
        Long result = executeIpFlowControlLuaScript(stringRedisTemplate, ipAddr, topic, ipFlowControlConfig.getTimeWindow());
        // 检查返回结果是否为空或超出最大访问次数
        if (result == null || result > ipFlowControlConfig.getMaxAccessCount()) {
            // 返回错误响应，提示访问太频繁
            return false;
        }
        return true;
    }

    /**
     * 根据IP地址、HttpServletResponse、StringRedisTemplate、时间窗口、最大访问次数和主题（topic）执行IP限流检查。
     * 如果IP地址的访问频率超过指定的限制，将返回错误响应并设置HttpServletResponse状态码和内容。
     *
     * @param ipAddr              客户端IP地址
     * @param response            HttpServletResponse对象，用于设置返回给客户端的响应
     * @param stringRedisTemplate Spring Data Redis模板，用于执行Lua脚本
     * @param timeWindow          时间窗口（单位：秒），用于计算IP地址在指定时间内的访问次数
     * @param maxAccessCount      最大访问次数限制
     * @param topic               请求的主题，用于区分不同类型的请求限流
     * @return true表示访问频率未超过限制，可以继续处理请求；false表示访问频率超出限制，已返回错误响应
     */
    @SneakyThrows
    public static boolean ipFlowLimit(String ipAddr, StringRedisTemplate stringRedisTemplate,
                                      String timeWindow, Long maxAccessCount, String topic) {
        // 执行Lua脚本
        Long result = executeIpFlowControlLuaScript(stringRedisTemplate, ipAddr, topic, timeWindow);

        // 检查返回结果是否为空或超出最大访问次数
        if (result == null || result > maxAccessCount) {
            // 返回错误响应，提示访问太频繁
            return false;
        }
        return true;
    }

    /**
     * 私有方法，封装执行IP限流Lua脚本的逻辑，简化代码重复。
     *
     * @param stringRedisTemplate Spring Data Redis模板
     * @param ipAddr              客户端IP地址
     * @param topic               请求的主题
     * @param timeWindow          时间窗口（单位：秒）
     * @return 执行Lua脚本的返回结果（访问次数）
     */
    private static Long executeIpFlowControlLuaScript(StringRedisTemplate stringRedisTemplate, String ipAddr, String topic, String timeWindow) {
        // 组织Lua脚本参数列表
        List<String> scriptArgs = Lists.newArrayList(
                RedisCacheConstant.IP_FLOW_CONTROL + RedisCacheConstant.SEPARATE + topic + RedisCacheConstant.SEPARATE + ipAddr
        );

        try {
            // 执行Lua脚本，获取返回结果
            return stringRedisTemplate.execute(getRedisScriptInstance(), scriptArgs, timeWindow);
        } catch (Throwable ex) {
            log.error("执行请求流量限制LUA脚本出错", ex);
            // 抛出异常，全局拦截器无法捕获，需要使用returnJson方法返回错误信息
            throw new RuntimeException("执行请求流量限制LUA脚本出错", ex);
        }
    }

    /**
     * 设置HttpServletResponse的响应头，并输出JSON格式的错误信息。
     *
     * @param response HttpServletResponse对象
     * @param json     JSON格式的错误信息字符串
     * @return
     * @throws Exception 可能抛出的异常
     */
    private static Mono<Void> returnJson(ServerHttpResponse response, String json) {
        if (response == null || json == null) {
            return null;
        }

        try {
            // Set response headers
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.getHeaders().set(HttpHeaders.CONTENT_ENCODING, "UTF-8");

            // Write JSON response
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Failed to write JSON response", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to write JSON response",
                    e
            );
        }
    }

}
