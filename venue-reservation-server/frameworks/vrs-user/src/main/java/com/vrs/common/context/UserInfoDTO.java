package com.vrs.common.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/11/16 16:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户类型 0：系统管理员 1：机构管理员 2：普通用户
     */
    private Integer userType;

    /**
     * 机构ID
     */
    private Long organizationId;
}
