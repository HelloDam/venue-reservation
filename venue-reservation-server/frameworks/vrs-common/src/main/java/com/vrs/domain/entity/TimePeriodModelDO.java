package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrs.domain.base.BaseEntity;
import com.vrs.domain.validate.AddGroup;
import com.vrs.domain.validate.UpdateGroup;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName time_period_model_0
 */
@TableName(value = "time_period_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimePeriodModelDO extends BaseEntity implements Serializable {

    /**
     * 该时间段预订使用价格（元）
     */
    @DecimalMin(value = "0.00", inclusive = true, message = "价格必须大于等于 0", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal price;

    /**
     * 场馆id
     */
    @NotNull(message = "场馆ID不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long venueId;

    /**
     * 场区id
     */
    @NotNull(message = "场区ID不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long partitionId;

    /**
     * 时间段开始时间HH:mm（不用填日期）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "GMT+8")
    @NotNull(message = "时间段开始时间不能为空", groups = {AddGroup.class})
    private Date beginTime;

    /**
     * 时间段结束时间HH:mm（不用填日期）
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "GMT+8")
    @NotNull(message = "时间段结束时间不能为空", groups = {AddGroup.class})
    private Date endTime;

    /**
     * 生效开始日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "生效开始日期不能为空", groups = {AddGroup.class})
    private Date effectiveStartDate;

    /**
     * 生效结束日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "生效结束日期不能为空", groups = {AddGroup.class})
    private Date effectiveEndDate;

    /**
     * 已生成到的日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastGeneratedDate;


    /**
     * 0：启用；1：停用
     */
    @NotNull(message = "场区状态不能为空", groups = {AddGroup.class})
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}