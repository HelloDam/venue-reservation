package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/11/16 14:49
 */
public class TimePeriodTableShardingTest {

    public static final String SQL1 = "DROP TABLE IF EXISTS `time_period_%d`;\n" +
            "CREATE TABLE `time_period_%d`( \n" +
            "    `id` bigint NOT NULL COMMENT 'ID',\n" +
            "    `create_time` datetime,\n" +
            "    `update_time` datetime,\n" +
            "    `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "    `partition_id` bigint NOT NULL COMMENT '场区id',\n" +
            "    `price` decimal(10,2) NOT NULL COMMENT '该时间段预订使用价格（元）',\n" +
            "    `stock` int NOT NULL COMMENT '库存',\n" +
            "    `booked_slots` bigint unsigned NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',\n" +
            "    `period_date` date NOT NULL COMMENT '预定日期', \n" +
            "    `begin_time` time NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',\n" +
            "    `end_time` time NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）', \n" +
            "     PRIMARY KEY (`id`) USING BTREE,\n" +
            "     INDEX `idx_partition_id` (`partition_id`),\n" +
            "     UNIQUE INDEX `idx_unique_partition_period_time` (`partition_id`, `period_date`, `begin_time`, `end_time`)\n" +
            ");";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i);
        }
    }
}
