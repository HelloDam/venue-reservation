package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/11/16 14:49
 */
public class TimePeriodModelTableShardingTest {
    public static final String SQL1 = "    DROP TABLE IF EXISTS `time_period_model_%d`;\n" +
            "    CREATE TABLE `time_period_model_%d`(\n" +
            "            `id` bigint NOT NULL COMMENT 'ID',\n" +
            "            `create_time` datetime,\n" +
            "            `update_time` datetime,\n" +
            "            `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "            `price` decimal(10,2) NOT NULL COMMENT '该时间段预订使用价格（元）',\n" +
            "            `venue_id` bigint NOT NULL COMMENT '场馆ID',\n" +
            "            `partition_id` bigint NOT NULL COMMENT '场区id',\n" +
            "            `begin_time` time NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',\n" +
            "            `end_time` time NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',\n" +
            "            `effective_start_date` date NOT NULL COMMENT '生效开始日期',\n" +
            "            `effective_end_date` date NOT NULL COMMENT '生效结束日期',\n" +
            "            `last_generated_date` date COMMENT '已生成到的日期',\n" +
            "            `status` tinyint default 0 COMMENT '0：启用；1：停用',\n" +
            "    PRIMARY KEY (`id`) USING BTREE,\n" +
            "    INDEX `idx_venue_id_partition_id` (`venue_id`,`partition_id`)\n" +
            "            );";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i);
        }
    }
}
