package com.vrs.common.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Author dam
 * @create 2024/11/21 19:02
 */
@Deprecated
public class RequestUtil {
    public static String getDomainPort(HttpServletRequest request) {
        String scheme = request.getScheme(); // http 或 https
        String serverName = request.getServerName(); // 域名
        int serverPort = request.getServerPort(); // 端口号
        return scheme + "://" + serverName + ":" + serverPort;
    }
}
