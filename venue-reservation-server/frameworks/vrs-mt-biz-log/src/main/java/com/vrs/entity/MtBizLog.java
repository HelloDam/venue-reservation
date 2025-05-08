package com.vrs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 操作日志表
 * @TableName mt_biz_log
 */
@TableName(value ="mt_biz_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MtBizLog extends BaseEntity implements Serializable {

    /**
     * 租户
     */
    private String tenant;

    /**
     * 类型
     */
    private String type;

    /**
     * 子类型
     */
    private String subType;

    /**
     * 方法名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 操作人员
     */
    private String operator;

    /**
     * 操作
     */
    private String action;

    /**
     * 其他补充
     */
    private String extra;

    /**
     * 操作状态 (0正常 1异常)
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}