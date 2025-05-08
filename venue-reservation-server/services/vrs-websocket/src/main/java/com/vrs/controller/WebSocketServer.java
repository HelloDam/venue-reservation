package com.vrs.controller;

import com.vrs.config.WebSocketConfig;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.utils.JwtUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author dam
 * @create 2024/1/24 14:32
 */
// 将WebSocketServer注册为spring的一个bean
@ServerEndpoint(value = "/websocket/{username}", configurator = WebSocketConfig.class)
@Component
@Slf4j(topic = "WebSocketServer")
public class WebSocketServer {

    /**
     * 心跳检查间隔时间（单位：秒）
     */
    private static final int HEARTBEAT_INTERVAL = 30;

    /**
     * 心跳超时时间（单位：秒）
     */
    private static final int HEARTBEAT_TIMEOUT = 60;

    /**
     * 记录当前在线连接的客户端的session
     */
    private static final Map<String, Session> usernameAndSessionMap = new ConcurrentHashMap<>();

    /**
     * 记录用户最后一次活动时间
     */
    private static final Map<String, Long> lastActivityTimeMap = new ConcurrentHashMap<>();

    /**
     * 直接通过 Autowired 注入的话，redisTemplate为null，因此使用这种引入方式
     */
    private static StringRedisTemplate redisTemplate;
    @Autowired
    public void setRabbitTemplate(StringRedisTemplate redisTemplate) {
        WebSocketServer.redisTemplate = redisTemplate;
    }

    /**
     * 定时任务线程池，用于心跳检查
     */
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 初始化心跳检查任务
    static {
        scheduler.scheduleAtFixedRate(WebSocketServer::checkHeartbeat, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 浏览器和服务端连接建立成功之后会调用这个方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, EndpointConfig config) {
        // 校验 token 是否有效
        String token = (String) config.getUserProperties().get("token");
        boolean validToken = validToken(token);
        if (!validToken) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "无效的token，请先登录"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 如果用户已存在，关闭旧连接
        if (usernameAndSessionMap.containsKey(username)) {
            Session oldSession = usernameAndSessionMap.get(username);
            if (oldSession != null && oldSession.isOpen()) {
                try {
                    oldSession.close();
                } catch (IOException e) {
                    log.error("关闭旧连接时发生错误", e);
                }
            }
        }

        // 记录新连接
        usernameAndSessionMap.put(username, session);
        // 记录用户活动时间
        lastActivityTimeMap.put(username, System.currentTimeMillis());
        log.info("有新用户加入，username={}, 当前在线人数为：{}", username, usernameAndSessionMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) throws IOException {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            log.error("关闭连接时发生错误", e);
        } finally {
            usernameAndSessionMap.remove(username);
            lastActivityTimeMap.remove(username);
            log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, usernameAndSessionMap.size());
        }
    }

    /**
     * 发生错误的时候会调用这个方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误，原因：" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 收到客户端消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        // 更新用户最后一次活动时间
        lastActivityTimeMap.put(username, System.currentTimeMillis());

        if ("PING".equals(message)) {
            log.debug("收到来自 {} 的心跳检测请求", username);
        } else {
            log.info("收到来自 {} 的消息: {}", username, message);
        }
    }

    /**
     * 服务端发送消息给客户端
     */
    public void sendMessage(String toUsername, String message) {
        try {
            Session toSession = usernameAndSessionMap.get(toUsername);
            if (toSession != null && toSession.isOpen()) {
                toSession.getBasicRemote().sendText(message);
            } else {
                log.warn("用户 {} 的会话已关闭或不存在", toUsername);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }


    /**
     * 关闭心跳检测超时的 session
     */
    private static void checkHeartbeat() {
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : lastActivityTimeMap.entrySet()) {
            String username = entry.getKey();
            long lastActivityTime = entry.getValue();
            if (currentTime - lastActivityTime > HEARTBEAT_TIMEOUT * 1000) {
                log.info("用户 {} 心跳超时，关闭连接", username);
                Session session = usernameAndSessionMap.get(username);
                if (session != null) {
                    try {
                        session.close();
                    } catch (IOException e) {
                        log.error("关闭连接时发生错误", e);
                    }
                }
                usernameAndSessionMap.remove(username);
                lastActivityTimeMap.remove(username);
            }
        }
    }

    /**
     * 校验 token 有效
     *
     * @param token
     * @return
     */
    private boolean validToken(String token) {
        String userName = "";
        try {
            // 如果从 token 中解析用户名错误，说明 token 是捏造的，或者已经失效
            userName = JwtUtil.getUsername(token);
        } catch (Exception e) {
            return false;
        }
        if (StringUtils.hasText(userName) && StringUtils.hasText(token) &&
                (redisTemplate.opsForHash().get(RedisCacheConstant.USER_LOGIN_KEY + userName, token)) != null) {
            // --if-- 如果可以通过 token 从 Redis 中获取到用户的登录信息，说明通过校验
            return true;
        }
        return false;
    }

}