package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import com.vrs.domain.validate.AddGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName organization
 */
@TableName(value ="organization")
@Data
public class OrganizationDO extends BaseEntity implements Serializable {
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空", groups = {AddGroup.class})
    private String name;

    /**
     * 机构唯一标识
     */
    @NotBlank(message = "机构唯一标识不能为空", groups = {AddGroup.class})
    private String mark;

    /**
     * 机构logo
     */
    @NotBlank(message = "logo不能为空", groups = {AddGroup.class})
    private String logo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}