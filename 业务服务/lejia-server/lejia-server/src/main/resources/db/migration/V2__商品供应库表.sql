/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.128.91-3306
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 192.168.128.91:3306
 Source Schema         : zyysc

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 17/09/2021 22:50:45
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mkt_supply
-- ----------------------------
DROP TABLE IF EXISTS `mkt_supply`;
CREATE TABLE `mkt_supply`  (
                               `pkey` int(11) NOT NULL COMMENT '主键',
                               `good` int(11) NULL DEFAULT NULL COMMENT '商品id',
                               `space` varchar(40) NOT NULL COMMENT '规格',
                               `vendor` int(11) NOT NULL COMMENT '供应商pkey',
                               `purchasing_price` decimal(19, 2) NOT NULL COMMENT '采购价',
                               `sort` int(11) NOT NULL COMMENT '派送顺序',
                               `enabled` tinyint(4) NOT NULL COMMENT '是否启用',
                               `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市场pkey',
                               `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司pkey',
                               PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品供应库' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
