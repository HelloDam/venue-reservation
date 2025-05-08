package com.vrs.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2024/3/15 19:34
 */
@Data
@Component
@ConfigurationProperties(prefix = "vrs.ip-flow-limit")
public class IpFlowControlConfiguration {
    /**
     * 是否开启用户流量风控验证
     */
    private Boolean enable;

    /**
     * 流量风控时间窗口，单位：秒
     */
    private String timeWindow;

    /**
     * 流量风控时间窗口内可访问次数
     */
    private Long maxAccessCount;
}
