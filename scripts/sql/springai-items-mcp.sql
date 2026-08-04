/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : localhost:3306
 Source Schema         : springai-items-mcp

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 04/08/2026 16:09:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品编号',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `brand` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '品牌',
  `price` int NOT NULL COMMENT '销售价格(单位:分)',
  `stock` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品简介',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态(0-下架 1-上架 2-预售)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('3S3Igvor0OH6', '热风拖鞋', 'Hot Window', 55, 10000, NULL, 2, '2026-08-04 15:37:06', '2026-08-04 15:37:06');
INSERT INTO `product` VALUES ('Cl1O2vR8ojH8', '初代暴龙机', '万代', 99, 99, '初代机型,值得收藏', 2, '2026-08-03 16:40:10', '2026-08-04 16:07:14');
INSERT INTO `product` VALUES ('gLU5IpGQhrf1', '阿迪达斯T恤', 'Adidas', 105, 10000, NULL, 2, '2026-08-04 15:38:03', '2026-08-04 15:38:03');

SET FOREIGN_KEY_CHECKS = 1;
