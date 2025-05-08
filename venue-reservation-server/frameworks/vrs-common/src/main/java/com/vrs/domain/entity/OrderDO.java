package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class OrderDO implements Serializable {

    /**
     * 使用雪花算法来生成ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "id", hidden = true)
    private Long id;

    /**
     * 逻辑删除 0：没删除 1：已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 订单号
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String orderSn;

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
     * 场区id
     */
    private Long partitionId;

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
     * 订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期
     */
    private Integer orderStatus;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}