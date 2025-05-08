package com.vrs.domain.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author dam
 * @create 2024/12/31 14:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlipayInfoRespDTO {
    /**
     * 卖家账号
     */
    private String buyerLogonId;
    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 状态码
     */
    private String code;
    /**
     * 消息
     */
    private String msg;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 支付方式，0:信用卡、1:支付宝、2:微信
     */
    private Integer paymentMethod;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 交易编号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 退款状态 0: 未退款 1: 部分退款 2: 全额退款
     */
    private Integer refundStatus;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refundTime;
}
