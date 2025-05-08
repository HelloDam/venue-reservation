package com.vrs.domain.dto.req;

import lombok.Data;

/**
 * 用户注册请求参数
 */
@Data
public class UserUpdateReqDTO {
    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String profile;

}
