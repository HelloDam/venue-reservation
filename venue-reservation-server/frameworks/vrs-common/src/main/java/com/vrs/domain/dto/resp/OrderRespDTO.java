package com.vrs.domain.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 
 * @TableName order
 */
@TableName(value ="time_period_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRespDTO implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String orderSn;

    /**
     * 逻辑删除 0：没删除 1：已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;

    /**
     * 场馆ID
     */
    private Long venueId;

    /**
     * 场馆名称
     */
    private String venueName;

    /**
     * 场区id
     */
    private Long partitionId;

    /**
     * 场区名称
     */
    private String partitionName;

    /**
     * 第几个场
     */
    private Long courtIndex;

    /**
     * 时间段id
     */
    private Long timePeriodId;

    /**
     * 预定日期
     */
    private LocalDate periodDate;

    /**
     * 时间段开始时间HH:mm（不用填日期）
     */
    private LocalTime beginTime;

    /**
     * 时间段结束时间HH:mm（不用填日期）
     */
    private LocalTime endTime;

    /**
     * 下单用户id
     */
    private Long userId;

    /**
     * 下单用户名
     */
    private String userName;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单状态 0:未支付 1：已支付 2：取消 3：退款
     */
    private Integer orderStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}