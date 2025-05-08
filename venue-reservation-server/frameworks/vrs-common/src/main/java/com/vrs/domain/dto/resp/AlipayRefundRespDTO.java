package com.vrs.domain.dto.resp;

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
public class AlipayRefundRespDTO {
    private String code;
    private String msg;
    private String subCode;
    private String subMsg;
    private String body;
    /**
     * 退款金额
     */
    private BigDecimal refundFee;
    private boolean isSuccess;
}
