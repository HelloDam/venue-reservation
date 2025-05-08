package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value = "user")
@Data
public class UserDO extends BaseEntity implements Serializable {

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型 0：系统管理员 1：机构管理员 2：普通用户
     */
    private Integer userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private Integer gender;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 头像地址类型 0：本地头像 1：远程头像
     */
    private Integer avatarType;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写
     */
    private Long organizationId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}