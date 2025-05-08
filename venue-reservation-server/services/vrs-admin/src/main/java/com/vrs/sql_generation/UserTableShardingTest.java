package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/11/16 14:49
 */
public class UserTableShardingTest {

    public static final String SQL1 = "DROP TABLE IF EXISTS `user_%d`;\n" +
            "CREATE TABLE `user_%d` (\n" +
            "  `id` bigint NOT NULL COMMENT 'ID',\n" +
            "  `create_time` datetime,\n" +
            "  `update_time` datetime,\n" +
            "  `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "  `user_name` varchar(30) NOT NULL COMMENT '用户账号',\n" +
            "  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',\n" +
            "  `user_type` tinyint NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',\n" +
            "  `email` varchar(50) NULL DEFAULT '' COMMENT '用户邮箱',\n" +
            "  `phone_number` varchar(11) NULL COMMENT '手机号码',\n" +
            "  `gender` tinyint NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',\n" +
            "  `avatar` varchar(100) NULL DEFAULT '' COMMENT '头像地址',\n" +
            "  `password` varchar(100) NULL DEFAULT '' COMMENT '密码',\n" +
            "  `status` tinyint NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',\n" +
            "  `login_ip` varchar(128) NULL DEFAULT '' COMMENT '最后登录IP',\n" +
            "  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',\n" +
            "  `point` int NULL DEFAULT NULL COMMENT '积分',\n" +
            "  `organization_id` bigint COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',\n" +
            "  PRIMARY KEY (`id`) USING BTREE\n" +
            ");\n" +
            "\n" +
            "-- 添加唯一约束\n" +
            "ALTER TABLE `user_%d` ADD CONSTRAINT `uk_user_name` UNIQUE (`user_name`);\n" +
            "ALTER TABLE `user_%d` ADD CONSTRAINT `uk_phone_number` UNIQUE (`phone_number`);\n" +
            "ALTER TABLE `user_%d` ADD CONSTRAINT `uk_email` UNIQUE (`email`);";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i, i, i, i);
        }
    }
}
