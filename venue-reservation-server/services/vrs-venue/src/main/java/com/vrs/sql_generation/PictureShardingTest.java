package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/12/24 17:32
 */
public class PictureShardingTest {

    public static final String SQL1 = "DROP TABLE IF EXISTS `picture_%d`;\n" +
            "CREATE TABLE `picture_%d`(\n" +
            "    `id` bigint NOT NULL COMMENT 'ID',\n" +
            "    `create_time` datetime,\n" +
            "    `update_time` datetime,\n" +
            "    `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "    `item_id` bigint NOT NULL COMMENT '项目ID',\n" +
            "    `picture` varchar(100) DEFAULT '' COMMENT '图片',\n" +
            "    `item_type` tinyint NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',\n" +
            "     PRIMARY KEY (`id`) USING BTREE\n" +
            ");";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i);
        }
    }
}
