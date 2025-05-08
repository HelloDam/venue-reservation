package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * openid-username路由表
 * @TableName user_openid
 */
@TableName(value ="user_openid")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOpenIdDO implements Serializable {
    /**
     * open_id
     */
    @TableId
    private String openId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 逻辑删除 0：没删除 1：已删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}