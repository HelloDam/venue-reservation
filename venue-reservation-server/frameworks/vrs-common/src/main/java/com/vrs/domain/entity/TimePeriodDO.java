package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import com.vrs.domain.validate.AddGroup;
import com.vrs.domain.validate.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @TableName time_period_0
 */
@TableName(value = "time_period")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodDO extends BaseEntity implements Serializable {

    /**
     * 场区id
     */
    @NotNull(message = "场区id不能为空", groups = {AddGroup.class, UpdateGroup.class})
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