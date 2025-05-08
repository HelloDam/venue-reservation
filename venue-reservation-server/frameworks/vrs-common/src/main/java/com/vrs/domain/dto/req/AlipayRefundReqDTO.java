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
public class AlipayRefundReqDTO {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 退款状态 0: 未退款 1: 部分退款 2: 全额退款
     */
    private Integer refundStatus;
}
