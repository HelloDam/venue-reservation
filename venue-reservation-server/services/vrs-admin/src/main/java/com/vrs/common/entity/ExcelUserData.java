package com.vrs.common.entity;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2025/1/12 15:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelUserData {

    // 设置相关列的宽度
    @ColumnWidth(30)
    // 设置相关列的名称
    @ExcelProperty("用户名")
    private String userName;

    @ExcelProperty("昵称")
    private String nickName;

    @ColumnWidth(20)
    @ExcelProperty("手机号")
    private String phoneNumber;

    @ColumnWidth(30)
    @ExcelProperty("邮箱")
    private String email;

    @ColumnWidth(10)
    @ExcelProperty("性别")
    private int gender;

    @ColumnWidth(20)
    @ExcelProperty("密码")
    private String password;
}
