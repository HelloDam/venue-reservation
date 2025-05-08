package com.vrs.config;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.List;
import java.util.Map;

/**
 * @Author dam
 * @create 2025/1/24 15:25
 */
@Configuration
public class WebSocketConfig extends ServerEndpointConfig.Configurator {

    /**
     * 这个bean会自动注册使用了@ServerEndpoint注解声明的对象
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 建立握手时，连接前的操作
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取请求参数
        Map<String, List<String>> parameterMap = request.getParameterMap();
        List<String> tokenList = parameterMap.get("token");
        if (tokenList != null && !tokenList.isEmpty()) {
            String token = tokenList.get(0);
            sec.getUserProperties().put("token", token);
        }
    }

    /**
     * 初始化端点对象,也就是被@ServerEndpoint所标注的对象
     */
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return super.getEndpointInstance(clazz);
    }
}
