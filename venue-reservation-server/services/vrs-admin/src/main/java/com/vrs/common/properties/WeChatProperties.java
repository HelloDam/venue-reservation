package com.vrs.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2025/1/9 11:04
 */
@Component
@ConfigurationProperties(prefix = "vrs.wechat")
@Data
public class WeChatProperties {
    /**
     * 小程序的appid
     */
    private String appId;
    /**
     * 小程序的秘钥
     */
    private String secret;
}
