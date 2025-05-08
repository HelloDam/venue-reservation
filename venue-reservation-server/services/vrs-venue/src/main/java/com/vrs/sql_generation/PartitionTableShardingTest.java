package com.vrs.sql_generation;

/**
 * @Author dam
 * @create 2024/12/7 11:57
 */
public class PartitionTableShardingTest {

    public static final String SQL1 = "DROP TABLE IF EXISTS `venue_partition_%d`;\n" +
            "CREATE TABLE `venue_partition_%d`(\n" +
            "    `id` bigint NOT NULL COMMENT 'ID',\n" +
            "    `create_time` datetime,\n" +
            "    `update_time` datetime,\n" +
            "    `is_deleted` tinyint default 0 COMMENT '逻辑删除 0：没删除 1：已删除',\n" +
            "    `venue_id` bigint NOT NULL COMMENT '场馆ID',\n" +
            "    `name` varchar(30) NOT NULL COMMENT '分区名称名称',\n" +
            "    `type` char(4) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',\n" +
            "    `description` varchar(255) DEFAULT '' COMMENT '描述，如是否提供器材等等',\n" +
            "    `num` int NOT NULL COMMENT '场区拥有的场数量',\n" +
            "    `status` int NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',\n" +
            "     PRIMARY KEY (`id`) USING BTREE\n" +
            ");" ;

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL1) + "%n", i, i);
        }
    }

}
