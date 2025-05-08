package com.vrs.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 订单生成
 * @Author dam
 * @create 2024/12/1 15:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderGenerateReqDTO {
    /**
     * 场馆ID
     */
    private Long venueId;
    /**
     * 场区ID
     */
    private Long partitionId;
    /**
     * 时间段ID
     */
    private Long timePeriodId;
    /**
     * 第几个场
     */
    private Long courtIndex;

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
     * 用户ID
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
}
