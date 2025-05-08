/*
 Navicat Premium Data Transfer

 Source Server         : tencent2c4g
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : 159.75.240.15:3308
 Source Schema         : venue-reservation

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 08/05/2025 21:03:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for local_message
-- ----------------------------
DROP TABLE IF EXISTS `local_message`;
CREATE TABLE `local_message`  (
  `id` bigint(0) NOT NULL COMMENT '主键ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `msg_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '唯一消息ID',
  `topic` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息Topic',
  `tag` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '消息Tag',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容(JSON格式)',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '消息状态 0:待发送 1:已发送 2:消费失败',
  `fail_reason` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  `retry_count` int(0) NOT NULL DEFAULT 0 COMMENT '已重试次数',
  `next_retry_time` bigint(0) NOT NULL DEFAULT 0 COMMENT '下次重试时间戳(毫秒)',
  `max_retry_count` int(0) NOT NULL DEFAULT 3 COMMENT '最大重试次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_msg_id`(`msg_id`) USING BTREE,
  INDEX `idx_status_retry`(`status`, `next_retry_time`) USING BTREE,
  INDEX `idx_topic_tag`(`topic`, `tag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mt_biz_log
-- ----------------------------
DROP TABLE IF EXISTS `mt_biz_log`;
CREATE TABLE `mt_biz_log`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `tenant` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
  `sub_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '子类型',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方法名称',
  `method_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方法名称',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人员',
  `action` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作',
  `extra` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他补充',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '操作状态 (0正常 1异常)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '机构名称',
  `mark` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '机构唯一标识',
  `logo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '机构logo',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_mark`(`mark`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture
-- ----------------------------
DROP TABLE IF EXISTS `picture`;
CREATE TABLE `picture`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_0
-- ----------------------------
DROP TABLE IF EXISTS `picture_0`;
CREATE TABLE `picture_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_1
-- ----------------------------
DROP TABLE IF EXISTS `picture_1`;
CREATE TABLE `picture_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_10
-- ----------------------------
DROP TABLE IF EXISTS `picture_10`;
CREATE TABLE `picture_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_11
-- ----------------------------
DROP TABLE IF EXISTS `picture_11`;
CREATE TABLE `picture_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_12
-- ----------------------------
DROP TABLE IF EXISTS `picture_12`;
CREATE TABLE `picture_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_13
-- ----------------------------
DROP TABLE IF EXISTS `picture_13`;
CREATE TABLE `picture_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_14
-- ----------------------------
DROP TABLE IF EXISTS `picture_14`;
CREATE TABLE `picture_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_15
-- ----------------------------
DROP TABLE IF EXISTS `picture_15`;
CREATE TABLE `picture_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_2
-- ----------------------------
DROP TABLE IF EXISTS `picture_2`;
CREATE TABLE `picture_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_3
-- ----------------------------
DROP TABLE IF EXISTS `picture_3`;
CREATE TABLE `picture_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_4
-- ----------------------------
DROP TABLE IF EXISTS `picture_4`;
CREATE TABLE `picture_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_5
-- ----------------------------
DROP TABLE IF EXISTS `picture_5`;
CREATE TABLE `picture_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_6
-- ----------------------------
DROP TABLE IF EXISTS `picture_6`;
CREATE TABLE `picture_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_7
-- ----------------------------
DROP TABLE IF EXISTS `picture_7`;
CREATE TABLE `picture_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_8
-- ----------------------------
DROP TABLE IF EXISTS `picture_8`;
CREATE TABLE `picture_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for picture_9
-- ----------------------------
DROP TABLE IF EXISTS `picture_9`;
CREATE TABLE `picture_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `item_id` bigint(0) NOT NULL COMMENT '项目ID',
  `picture` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图片',
  `item_type` tinyint(0) NOT NULL COMMENT '项目类型 0：场馆图片 1：分区图片 2：评论图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period
-- ----------------------------
DROP TABLE IF EXISTS `time_period`;
CREATE TABLE `time_period`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_0
-- ----------------------------
DROP TABLE IF EXISTS `time_period_0`;
CREATE TABLE `time_period_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_1
-- ----------------------------
DROP TABLE IF EXISTS `time_period_1`;
CREATE TABLE `time_period_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_10
-- ----------------------------
DROP TABLE IF EXISTS `time_period_10`;
CREATE TABLE `time_period_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_11
-- ----------------------------
DROP TABLE IF EXISTS `time_period_11`;
CREATE TABLE `time_period_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_12
-- ----------------------------
DROP TABLE IF EXISTS `time_period_12`;
CREATE TABLE `time_period_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_13
-- ----------------------------
DROP TABLE IF EXISTS `time_period_13`;
CREATE TABLE `time_period_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_14
-- ----------------------------
DROP TABLE IF EXISTS `time_period_14`;
CREATE TABLE `time_period_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_15
-- ----------------------------
DROP TABLE IF EXISTS `time_period_15`;
CREATE TABLE `time_period_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_2
-- ----------------------------
DROP TABLE IF EXISTS `time_period_2`;
CREATE TABLE `time_period_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_3
-- ----------------------------
DROP TABLE IF EXISTS `time_period_3`;
CREATE TABLE `time_period_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_4
-- ----------------------------
DROP TABLE IF EXISTS `time_period_4`;
CREATE TABLE `time_period_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_5
-- ----------------------------
DROP TABLE IF EXISTS `time_period_5`;
CREATE TABLE `time_period_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_6
-- ----------------------------
DROP TABLE IF EXISTS `time_period_6`;
CREATE TABLE `time_period_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_7
-- ----------------------------
DROP TABLE IF EXISTS `time_period_7`;
CREATE TABLE `time_period_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_8
-- ----------------------------
DROP TABLE IF EXISTS `time_period_8`;
CREATE TABLE `time_period_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_9
-- ----------------------------
DROP TABLE IF EXISTS `time_period_9`;
CREATE TABLE `time_period_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `stock` int(0) NOT NULL COMMENT '库存',
  `booked_slots` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '已预订的场地（位图表示）',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique_partition_period_time`(`partition_id`, `period_date`, `begin_time`, `end_time`) USING BTREE,
  INDEX `idx_partition_id`(`partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model`;
CREATE TABLE `time_period_model`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_0
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_0`;
CREATE TABLE `time_period_model_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_1
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_1`;
CREATE TABLE `time_period_model_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_10
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_10`;
CREATE TABLE `time_period_model_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_11
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_11`;
CREATE TABLE `time_period_model_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_12
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_12`;
CREATE TABLE `time_period_model_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_13
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_13`;
CREATE TABLE `time_period_model_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_14
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_14`;
CREATE TABLE `time_period_model_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_15
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_15`;
CREATE TABLE `time_period_model_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_2
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_2`;
CREATE TABLE `time_period_model_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_3
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_3`;
CREATE TABLE `time_period_model_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_4
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_4`;
CREATE TABLE `time_period_model_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_5
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_5`;
CREATE TABLE `time_period_model_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_6
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_6`;
CREATE TABLE `time_period_model_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_7
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_7`;
CREATE TABLE `time_period_model_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_8
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_8`;
CREATE TABLE `time_period_model_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_model_9
-- ----------------------------
DROP TABLE IF EXISTS `time_period_model_9`;
CREATE TABLE `time_period_model_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `price` decimal(10, 2) NOT NULL COMMENT '该时间段预订使用价格（元）',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `effective_start_date` date NOT NULL COMMENT '生效开始日期',
  `effective_end_date` date NOT NULL COMMENT '生效结束日期',
  `last_generated_date` date NULL DEFAULT NULL COMMENT '已生成到的日期',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '0：启用；1：停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_venue_id_partition_id`(`venue_id`, `partition_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order`;
CREATE TABLE `time_period_order`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_0
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_0`;
CREATE TABLE `time_period_order_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_1
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_1`;
CREATE TABLE `time_period_order_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_10
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_10`;
CREATE TABLE `time_period_order_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_11
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_11`;
CREATE TABLE `time_period_order_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_12
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_12`;
CREATE TABLE `time_period_order_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_13
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_13`;
CREATE TABLE `time_period_order_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_14
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_14`;
CREATE TABLE `time_period_order_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_15
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_15`;
CREATE TABLE `time_period_order_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_2
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_2`;
CREATE TABLE `time_period_order_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_3
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_3`;
CREATE TABLE `time_period_order_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_4
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_4`;
CREATE TABLE `time_period_order_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_5
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_5`;
CREATE TABLE `time_period_order_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_6
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_6`;
CREATE TABLE `time_period_order_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_7
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_7`;
CREATE TABLE `time_period_order_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_8
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_8`;
CREATE TABLE `time_period_order_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_order_9
-- ----------------------------
DROP TABLE IF EXISTS `time_period_order_9`;
CREATE TABLE `time_period_order_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `partition_id` bigint(0) NOT NULL COMMENT '场区id',
  `court_index` int(0) NOT NULL COMMENT '第几个场',
  `time_period_id` bigint(0) NOT NULL COMMENT '时间段id',
  `period_date` date NOT NULL COMMENT '预定日期',
  `begin_time` time(0) NOT NULL COMMENT '时间段开始时间HH:mm（不用填日期）',
  `end_time` time(0) NOT NULL COMMENT '时间段结束时间HH:mm（不用填日期）',
  `user_id` bigint(0) NOT NULL COMMENT '下单用户id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `order_status` tinyint(0) NOT NULL COMMENT '订单状态 0:未支付 1：已支付，待使用 2：取消 3：退款 4：已核销 5：已过期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay`;
CREATE TABLE `time_period_pay`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `out_order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_0
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_0`;
CREATE TABLE `time_period_pay_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_1
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_1`;
CREATE TABLE `time_period_pay_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_10
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_10`;
CREATE TABLE `time_period_pay_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_11
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_11`;
CREATE TABLE `time_period_pay_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_12
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_12`;
CREATE TABLE `time_period_pay_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_13
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_13`;
CREATE TABLE `time_period_pay_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_14
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_14`;
CREATE TABLE `time_period_pay_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_15
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_15`;
CREATE TABLE `time_period_pay_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_2
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_2`;
CREATE TABLE `time_period_pay_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_3
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_3`;
CREATE TABLE `time_period_pay_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_4
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_4`;
CREATE TABLE `time_period_pay_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_5
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_5`;
CREATE TABLE `time_period_pay_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_6
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_6`;
CREATE TABLE `time_period_pay_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_7
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_7`;
CREATE TABLE `time_period_pay_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_8
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_8`;
CREATE TABLE `time_period_pay_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for time_period_pay_9
-- ----------------------------
DROP TABLE IF EXISTS `time_period_pay_9`;
CREATE TABLE `time_period_pay_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `order_sn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `payment_method` tinyint(0) NULL DEFAULT NULL COMMENT '支付方式，0:信用卡、1:支付宝、2:微信',
  `subject` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `transaction_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易编号',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付金额',
  `refund_status` tinyint(0) NULL DEFAULT NULL COMMENT '退款状态 0: 未退款 1: 部分退款 2: 全额退款',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idx_order_sn`(`order_sn`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_0
-- ----------------------------
DROP TABLE IF EXISTS `user_0`;
CREATE TABLE `user_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_1
-- ----------------------------
DROP TABLE IF EXISTS `user_1`;
CREATE TABLE `user_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_10
-- ----------------------------
DROP TABLE IF EXISTS `user_10`;
CREATE TABLE `user_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_11
-- ----------------------------
DROP TABLE IF EXISTS `user_11`;
CREATE TABLE `user_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_12
-- ----------------------------
DROP TABLE IF EXISTS `user_12`;
CREATE TABLE `user_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_13
-- ----------------------------
DROP TABLE IF EXISTS `user_13`;
CREATE TABLE `user_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_14
-- ----------------------------
DROP TABLE IF EXISTS `user_14`;
CREATE TABLE `user_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_15
-- ----------------------------
DROP TABLE IF EXISTS `user_15`;
CREATE TABLE `user_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_2
-- ----------------------------
DROP TABLE IF EXISTS `user_2`;
CREATE TABLE `user_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_3
-- ----------------------------
DROP TABLE IF EXISTS `user_3`;
CREATE TABLE `user_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_4
-- ----------------------------
DROP TABLE IF EXISTS `user_4`;
CREATE TABLE `user_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_5
-- ----------------------------
DROP TABLE IF EXISTS `user_5`;
CREATE TABLE `user_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_6
-- ----------------------------
DROP TABLE IF EXISTS `user_6`;
CREATE TABLE `user_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_7
-- ----------------------------
DROP TABLE IF EXISTS `user_7`;
CREATE TABLE `user_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_8
-- ----------------------------
DROP TABLE IF EXISTS `user_8`;
CREATE TABLE `user_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_9
-- ----------------------------
DROP TABLE IF EXISTS `user_9`;
CREATE TABLE `user_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` tinyint(0) NULL DEFAULT 2 COMMENT '用户类型 0：系统管理员 1：机构管理员 2：普通用户',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `gender` tinyint(0) NULL DEFAULT 2 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` tinyint(0) NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `point` int(0) NULL DEFAULT NULL COMMENT '积分',
  `organization_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写',
  `avatar_type` tinyint(0) NULL DEFAULT 0 COMMENT '头像类型（0本地头像 1远程头像）',
  `profile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '个人简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_openid
-- ----------------------------
DROP TABLE IF EXISTS `user_openid`;
CREATE TABLE `user_openid`  (
  `open_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'open_id',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  PRIMARY KEY (`open_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'openid-username路由表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue
-- ----------------------------
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `organization_id` bigint(0) NOT NULL COMMENT '所属机构ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场馆名称',
  `type` int(0) NOT NULL COMMENT '场馆类型 1:篮球馆（场） 2:足球场 3：羽毛球馆（场） 4:排球馆（场）100：体育馆 1000:其他',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场馆地址',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '场馆描述，也可以说是否提供器材等等',
  `open_time` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场馆营业时间',
  `phone_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '联系电话',
  `status` tinyint(0) NOT NULL COMMENT '场馆状态 0：关闭 1：开放 2：维护中',
  `is_open` tinyint(0) NOT NULL COMMENT '是否对外开放 0：否 1：是 如果不对外开放，需要相同机构的用户才可以预定',
  `advance_booking_day` int(0) NOT NULL COMMENT '提前可预定天数，例如设置为1，即今天可预订明天的场',
  `start_booking_time` time(0) NOT NULL COMMENT '开放预订时间',
  `latitude` decimal(9, 6) NOT NULL COMMENT '纬度',
  `longitude` decimal(9, 6) NOT NULL COMMENT '经度',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_0
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_0`;
CREATE TABLE `venue_partition_0`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_1
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_1`;
CREATE TABLE `venue_partition_1`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_10
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_10`;
CREATE TABLE `venue_partition_10`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_11
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_11`;
CREATE TABLE `venue_partition_11`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_12
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_12`;
CREATE TABLE `venue_partition_12`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_13
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_13`;
CREATE TABLE `venue_partition_13`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_14
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_14`;
CREATE TABLE `venue_partition_14`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_15
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_15`;
CREATE TABLE `venue_partition_15`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_2
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_2`;
CREATE TABLE `venue_partition_2`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_3
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_3`;
CREATE TABLE `venue_partition_3`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_4
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_4`;
CREATE TABLE `venue_partition_4`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_5
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_5`;
CREATE TABLE `venue_partition_5`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_6
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_6`;
CREATE TABLE `venue_partition_6`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_7
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_7`;
CREATE TABLE `venue_partition_7`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_8
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_8`;
CREATE TABLE `venue_partition_8`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for venue_partition_9
-- ----------------------------
DROP TABLE IF EXISTS `venue_partition_9`;
CREATE TABLE `venue_partition_9`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除 0：没删除 1：已删除',
  `venue_id` bigint(0) NOT NULL COMMENT '场馆ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分区名称名称',
  `type` int(0) NOT NULL COMMENT '分区类型 1:篮球 2:足球 3：羽毛球 4:排球',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '描述，如是否提供器材等等',
  `num` int(0) NOT NULL COMMENT '场区拥有的场数量',
  `status` int(0) NOT NULL COMMENT '场区状态 0：关闭 1：开放 2：维护中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
