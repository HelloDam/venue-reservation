package com.vrs.domain.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @TableName time_period_0
 */
@TableName(value = "time_period")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodRespDTO extends BaseEntity implements Serializable {

    /**
     * 场区id
     */
    private Long partitionId;

    /**
     * 该时间段预订使用价格（元）
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 已预订的场地（位图表示）
     */
    private Long bookedSlots;

    /**
     * 已预定数组
     */
    private List<Integer> bookedList;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}