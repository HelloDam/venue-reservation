package com.vrs.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author dam
 * @create 2024/12/31 14:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlipayPayReqDTO {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 主题
     */
    private String subject;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 支付完成的回调地址
     */
    private String returnUrl;
}
