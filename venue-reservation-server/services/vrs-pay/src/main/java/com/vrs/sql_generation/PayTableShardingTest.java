package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/11/16 14:49
 */
public class PayTableShardingTest {

    public static final String SQL1 = "DROP TABLE IF EXISTS `time_period_pay_%d`;\n" +
            "CREATE TABLE `time_period_pay_%d`( \n" +
            "    `id` bigint NOT NULL COMMENT 'ID',\n" +
            "    `create_time` datetime,\n" +
            "    `update_time` datetime,\n" +
            "    `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "    `order_sn` varchar(30) NOT NULL COMMENT '订单号',\n" +
            "    `payment_method` tinyint COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',\n" +
            "    `subject` varchar(512) NULL COMMENT '订单标题',\n" +
            "    `transaction_id` varchar(255) COMMENT '交易编号',\n" +
            "    `pay_time` datetime COMMENT '支付时间',\n" +
            "    `pay_amount` decimal(10, 2) COMMENT '支付金额',\n" +
            "    `refund_status` tinyint COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',\n" +
            "    `refund_amount` decimal(10, 2) COMMENT '退款金额',\n" +
            "    `refund_time` datetime COMMENT '退款时间',\n" +
            "     PRIMARY KEY (`id`) USING BTREE\n" +
            ");\n" +
            "ALTER TABLE `time_period_pay_%d`\n" +
            "ADD UNIQUE INDEX `uniq_idx_order_sn` (`order_sn`);";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i, i);
        }
    }
}
