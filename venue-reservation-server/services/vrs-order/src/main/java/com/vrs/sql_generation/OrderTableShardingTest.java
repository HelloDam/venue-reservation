package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/11/16 14:49
 */
public class OrderTableShardingTest {

    public static final String SQL1 = "DROP TABLE IF EXISTS `time_period_order_%d`;\n" +
            "CREATE TABLE `time_period_order_%d`( \n" +
            "    `id` bigint NOT NULL COMMENT 'ID',\n" +
            "    `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "    `order_sn` varchar(30) NOT NULL COMMENT '订单号',\n" +
            "    `order_time` datetime NOT NULL COMMENT '下单时间',\n" +
            "    `venue_id` bigint NOT NULL COMMENT '场馆ID',\n" +
            "    `partition_id` bigint NOT NULL COMMENT '场区id',\n" +
            "    `court_index` int NOT NULL COMMENT '第几个场',\n" +
            "    `time_period_id` bigint NOT NULL COMMENT '时间段id',\n" +
            "    `period_date` date NOT NULL COMMENT '预定日期', \n" +
            "    `begin_time` time NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',\n" +
            "    `end_time` time NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）', \n" +
            "    `user_id` bigint NOT NULL COMMENT '下单用户id',\n" +
            "    `user_name` varchar(30) NOT NULL COMMENT '下单用户名',\n" +
            "    `pay_amount` decimal(10, 2) COMMENT '支付金额',\n" +
            "    `order_status` tinyint NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',\n" +
            "     PRIMARY KEY (`id`) USING BTREE\n" +
            ");\n" +
            "\n" +
            "ALTER TABLE `time_period_order_%d`\n" +
            "ADD UNIQUE INDEX `uniq_idx_order_sn` (`order_sn`);";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i, i);
        }
    }
}
