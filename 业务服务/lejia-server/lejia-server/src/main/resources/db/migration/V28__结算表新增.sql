/*
Navicat MySQL Data Transfer

Source Server         : 192.168.128.91
Source Server Version : 50735
Source Host           : 192.168.128.91:3306
Source Database       : zyyscyw

Target Server Type    : MYSQL
Target Server Version : 50735
File Encoding         : 65001

Date: 2021-12-29 21:55:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mkt_settlement_line_day
-- ----------------------------
DROP TABLE IF EXISTS `mkt_settlement_line_day`;
CREATE TABLE `mkt_settlement_line_day` (
  `pkey` bigint(20) NOT NULL,
  `settlement_pkey` int(11) DEFAULT NULL,
  `line_pkey` int(11) DEFAULT NULL,
  `vendor` int(11) DEFAULT NULL,
  `settlement_date` date DEFAULT NULL,
  `zx_user_id` varchar(30) DEFAULT NULL,
  `vendor_name` varchar(100) DEFAULT NULL,
  `bankname` varchar(200) DEFAULT NULL,
  `bankuser` varchar(40) DEFAULT NULL,
  `bankcard` varchar(40) DEFAULT NULL,
  `bank_branch_name` varchar(40) DEFAULT NULL,
  `bank_no` varchar(40) DEFAULT NULL,
  `bankuser_identity` varchar(50) DEFAULT NULL,
  `bankuser_moblie` varchar(50) DEFAULT NULL,
  `order_count` int(11) DEFAULT NULL,
  `order_amt` decimal(11,2) DEFAULT NULL,
  `commission` decimal(5,2) DEFAULT NULL,
  `order_commission` decimal(11,2) DEFAULT NULL,
  `discount_amt` decimal(16,2) DEFAULT NULL COMMENT '优惠金额',
  `postage` decimal(16,2) DEFAULT NULL COMMENT '邮费',
  `difference` decimal(16,2) DEFAULT NULL COMMENT '差额',
  `amt` decimal(11,2) DEFAULT NULL,
  `type` tinyint(4) NOT NULL,
  `self_mention` bit(1) DEFAULT NULL,
  `rem` varchar(100) DEFAULT NULL,
  `farmer` varchar(40) DEFAULT NULL,
  `company` varchar(40) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mkt_settlement_total
-- ----------------------------
DROP TABLE IF EXISTS `mkt_settlement_total`;
CREATE TABLE `mkt_settlement_total` (
  `pkey` bigint(20) NOT NULL,
  `settlement_date` date DEFAULT NULL COMMENT '结算时间',
  `discount_amt` decimal(16,2) DEFAULT NULL COMMENT '优惠金额',
  `postage` decimal(16,2) DEFAULT NULL COMMENT '邮费',
  `clearing_amt` decimal(16,2) DEFAULT NULL COMMENT '清分金额',
  `difference` decimal(16,2) DEFAULT NULL COMMENT '商品和采购 差额',
  `handling_fee` decimal(16,2) DEFAULT NULL COMMENT '手续费',
  `pay_amt` decimal(16,2) DEFAULT NULL COMMENT '用户支付金额',
  `platform_amt` decimal(16,2) DEFAULT NULL COMMENT '平台金额',
  `type` tinyint(4) DEFAULT NULL COMMENT '结算状态',
  `created_time` datetime DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录每天清分数据 ';

