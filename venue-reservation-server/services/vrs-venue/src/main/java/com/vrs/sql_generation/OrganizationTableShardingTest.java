package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/11/16 14:49
 */
public class OrganizationTableShardingTest {

//    public static final String SQL1 = "DROP TABLE IF EXISTS `organization_%d`;\n" +
//            "CREATE TABLE `organization_%d`(\n" +
//            "    `id` bigint NOT NULL COMMENT 'ID',\n" +
//            "    `create_time` datetime,\n" +
//            "    `update_time` datetime,\n" +
//            "    `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
//            "    `name` varchar(30) NOT NULL COMMENT '机构名称',\n" +
//            "    `logo` varchar(100) NOT NULL COMMENT '机构logo',\n" +
//            "    PRIMARY KEY (`id`) USING BTREE\n" +
//            ");";
    public static final String SQL1 = "DROP TABLE IF EXISTS `organization_%d`;\n";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i);
        }
    }
}
