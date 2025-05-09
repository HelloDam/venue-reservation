package com.vrs.filter;

import com.alibaba.fastjson2.JSON;
import com.vrs.common.GatewayResult;
import com.vrs.common.WhitePathConfig;
import com.vrs.config.IpFlowControlConfiguration;
import com.vrs.utils.FlowLimitUtil;
import com.vrs.utils.IpUtils;
import com.vrs.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author dam
 * @create 2024/11/16 18:14
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<WhitePathConfig> {
    private final StringRedisTemplate stringRedisTemplate;
    private final IpFlowControlConfiguration ipFlowControlConfiguration;

    public TokenValidateGatewayFilterFactory(StringRedisTemplate stringRedisTemplate, IpFlowControlConfiguration ipFlowControlConfiguration) {
        super(WhitePathConfig.class);
        this.stringRedisTemplate = stringRedisTemplate;
        this.ipFlowControlConfiguration = ipFlowControlConfiguration;
    }

    @Override
    public GatewayFilter apply(WhitePathConfig whitePathConfig) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 获取用户ip
            String ipAddr = IpUtils.getIpAddr(request);
            // 执行IP访问频率检查，如果超过限制则直接返回错误响应，否则继续处理请求
            if (!FlowLimitUtil.ipFlowLimit(ipAddr, stringRedisTemplate, ipFlowControlConfiguration, "interface")) {
                // IP访问频率已超过限制，已返回错误响应，无需继续过滤链
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return writeResult(response, "操作频率太快，请稍后再试");
            }

            // 获取请求路径
            String requestPath = request.getPath().toString();
            if (!isPathInWhiteList(requestPath, whitePathConfig.getWhitePathList())) {
                // --if-- 当前请求路径不在白名单中
                String token = request.getHeaders().getFirst("token");
                // 用户名为空，或者不存在于Redis中，返回错误提示
                String userName = "";
                try {
                    userName = JwtUtil.getUsername(token);
                } catch (Exception e) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return writeResult(response, "没有通过登录校验，请先登录");
                }
                // 将解析出来的信息放到请求头中，避免上下文封装的时候还需要去查询一遍
                String finalUserName = userName;
                ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set("userId", JwtUtil.getUserId(token).toString());
                    httpHeaders.set("userType", JwtUtil.getUserType(token).toString());
                    httpHeaders.set("organizationId", JwtUtil.getOrganizationId(token).toString());
                    httpHeaders.set("userName", URLEncoder.encode(finalUserName, StandardCharsets.UTF_8));
                });
                return chain.filter(exchange.mutate().request(builder.build()).build());
            }
            return chain.filter(exchange);
        };
    }


    /**
     * 返回结果给前端
     *
     * @param response
     * @param e
     * @return
     */
    private static Mono<Void> writeResult(ServerHttpResponse response, String e) {
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            GatewayResult resultMessage = GatewayResult.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(e)
                    .build();
            return bufferFactory.wrap(JSON.toJSONString(resultMessage).getBytes());
        }));
    }

    /**
     * 判断请求路径是否存在于白名单中
     *
     * @param requestPath
     * @param whitePathList
     * @return
     */
    private boolean isPathInWhiteList(String requestPath, List<String> whitePathList) {
        if (whitePathList.isEmpty()) {
            return false;
        }
        for (String whitePath : whitePathList) {
            if (isPathMatched(whitePath, requestPath) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查给定的路径是否与whitePath模式匹配。
     *
     * @param whitePath 定义的白名单路径模式
     * @param testPath  要校验的具体路径
     * @return 如果testPath匹配whitePath，则返回true；否则返回false。
     */
    public boolean isPathMatched(String whitePath, String testPath) {
        // 去除路径两边的空白字符
        whitePath = whitePath.trim();
        testPath = testPath.trim();

        // 如果whitePath是以'**'结尾，则只检查前面的部分是否匹配
        if (whitePath.endsWith("/**")) {
            // 获取whitePath中除了'/**'之外的部分
            String prefix = whitePath.substring(0, whitePath.length() - 3);
            // 检查testPath是否以prefix开头
            return testPath.startsWith(prefix);
        }

        // 对于其他类型的模式，这里可以扩展更多的匹配规则
        // 但在这个例子中我们只处理'/webjars/**'这种简单的情况

        // 默认情况下，直接比较字符串是否完全相等
        return whitePath.equals(testPath);
    }

}
