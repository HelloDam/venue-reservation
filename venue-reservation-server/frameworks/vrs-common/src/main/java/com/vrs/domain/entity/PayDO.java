package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName time_period_pay
 */
@TableName(value ="time_period_pay")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayDO extends BaseEntity implements Serializable {

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
    private Date refundTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}