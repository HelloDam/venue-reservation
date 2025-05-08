package com.vrs;

import cn.hutool.core.io.FileUtil;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.util.ListUtils;
import com.github.javafaker.Faker;
import com.vrs.common.entity.ExcelUserData;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * 模拟用户数据生成
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
public class UserDataGenerateTest {
    /**
     * 用户数量
     */
    private final int userNum = 10000;
    private final Faker faker = new Faker(Locale.CHINA);
    /**
     * excel地址
     */
    private final String excelPath = Paths.get("").toAbsolutePath().getParent().getParent() + File.separator + "tmp";

    @Test
    public void generate() {
        if (!FileUtil.exist(excelPath)) {
            FileUtil.mkdir(excelPath);
        }

        // 数据生成
        List<ExcelUserData> list = ListUtils.newArrayList();
        for (int i = 0; i < userNum; i++) {
            ExcelUserData data = ExcelUserData.builder()
                    // 随机生成10位数字，并拼接成邮箱
                    .email(faker.number().digits(10) + "@qq.com")
                    // 生成一个随机的电话号码
                    .phoneNumber(faker.phoneNumber().cellPhone())
                    // 生成一个随机的用户名，使用 faker 库的 regexify 方法生成 3 到 10 个字母组成的字符串
                    .userName(faker.regexify("[a-zA-Z]{3,10}"))
                    // 生成一个随机的昵称，使用 faker 库的 name 方法生成一个名字作为昵称
                    .nickName(faker.name().firstName())
                    // 示例性别选项
                    .gender(faker.options().option(0, 1))
                    // 生成随机密码
                    .password(faker.internet().password())
                    .build();
            list.add(data);
        }

        // 输出Excel
        String fileName = excelPath + File.separator + "机构用户生成.xlsx";
        EasyExcel.write(fileName, ExcelUserData.class)
                .sheet("用户表")
                .doWrite(list);

    }
}
