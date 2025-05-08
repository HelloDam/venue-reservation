package com.vrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author dam
 * @create 2024/11/21 20:55
 */

@Component
public class CustomGlobalFilter {
    @Value("${server.port}")
    private int serverPort;

    /**
     * 将网关服务的域名端口放到请求头中，方便其他服务使用
     * @return
     */
    @Bean(name = "customGlobalFilter1")
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String hostAndPort = getHostAndPort(request);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Gateway-Host", hostAndPort)
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
            return chain.filter(modifiedExchange);
        };
    }

    /**
     * 获取网关服务的访问域名端口
     * @param request
     * @return
     */
    private String getHostAndPort(ServerHttpRequest request) {
        try {
            // 获取请求的主机名和端口
            String host = request.getURI().getHost();
            if (host == null) {
                // 如果请求中没有主机名，使用本地主机名
                host = InetAddress.getLocalHost().getHostName();
            }
            // 获取请求的协议（HTTP或HTTPS）
            String scheme = request.getURI().getScheme();
            return scheme + "://" + host + ":" + serverPort;
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
