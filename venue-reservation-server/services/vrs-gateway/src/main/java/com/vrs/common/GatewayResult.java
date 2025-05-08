package com.vrs.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/11/16 18:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayResult {
    /**
     * HTTP 状态码
     */
    private Integer status;

    /**
     * 返回信息
     */
    private String message;
}
