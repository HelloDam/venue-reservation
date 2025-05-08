package com.vrs.domain.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 修改订单为已支付状态
 *
 * @Author dam
 * @create 2024/12/1 19:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteReserveMqDTO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 预订场号
     */
    private Long courtIndex;

    /**
     * 所预订时间段ID
     */
    private Long timePeriodId;

    /**
     * 场馆ID
     */
    private Long venueId;

    /**
     * 场区id
     */
    private Long partitionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

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
     * 该时间段预订使用价格（元）
     */
    private BigDecimal price;
}
