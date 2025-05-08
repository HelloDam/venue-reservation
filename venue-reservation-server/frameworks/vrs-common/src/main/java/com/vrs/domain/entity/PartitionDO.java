package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import com.vrs.domain.validate.AddGroup;
import com.vrs.domain.validate.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

;

/**
 * 场区
 *
 * @TableName partition
 */
@TableName(value = "venue_partition")
@Data
public class PartitionDO extends BaseEntity implements Serializable {

    /**
     * 场馆ID
     */
    @NotNull(message = "场馆Id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long venueId;

    /**
     * 分区名称
     */
    @NotBlank(message = "分区名称不能为空", groups = {AddGroup.class})
    private String name;

    /**
     * 分区类型 1:篮球 2:足球 3：羽毛球 4:排球
     */
    @NotNull(message = "分区类型不能为空", groups = {AddGroup.class})
    private Integer type;

    /**
     * 描述，如是否提供器材等等
     */
    private String description;

    /**
     * 场区拥有的场数量
     */
    @Range(min = 1, max = 63, message = "数量必须在 [1,63] 之间", groups = {AddGroup.class})
    private Integer num;

    /**
     * 场区状态 0：关闭 1：开放 2：维护中
     */
    @NotNull(message = "场区状态不能为空", groups = {AddGroup.class})
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}