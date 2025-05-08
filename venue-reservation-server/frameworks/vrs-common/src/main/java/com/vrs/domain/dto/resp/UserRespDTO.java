package com.vrs.domain.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vrs.domain.dto.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

/**
 * 用户返回参数响应
 */
@Data
public class UserRespDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String nickName;

    /**
     * 手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private int gender;

    /**
     * 性别
     */
    private String avatar;

    /**
     * 头像地址类型 0：本地头像 1：远程头像
     */
    private Integer avatarType;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 个人简介
     */
    private String profile;
}
