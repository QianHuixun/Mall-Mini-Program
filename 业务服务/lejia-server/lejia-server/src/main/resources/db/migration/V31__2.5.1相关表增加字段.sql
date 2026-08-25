/*
Navicat MySQL Data Transfer

Source Server         : 192.168.128.91
Source Server Version : 50735
Source Host           : 192.168.128.91:3306
Source Database       : zyysc

Target Server Type    : MYSQL
Target Server Version : 50735
File Encoding         : 65001

Date: 2022-01-29 09:55:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mkt_space_kc
-- ----------------------------
DROP TABLE IF EXISTS `mkt_space_kc`;
CREATE TABLE `mkt_space_kc` (
  `pkey` int(11) NOT NULL,
  `kc_num` int(11) NOT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_farmer_mtype
-- ----------------------------
DROP TABLE IF EXISTS `sys_farmer_mtype`;
CREATE TABLE `sys_farmer_mtype` (
  `pkey` int(11) NOT NULL,
  `farmer` varchar(40) CHARACTER SET utf8mb4 NOT NULL COMMENT '市场主键',
  `m_type` tinyint(4) NOT NULL COMMENT '积分/市场/会员/特价/分享/砍价/团购/预售',
  `delivery` tinyint(1) NOT NULL COMMENT '是否开启配送',
  `pickup` tinyint(1) NOT NULL COMMENT '是否开启自提',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mkt_vendor_staff`;
CREATE TABLE `mkt_vendor_staff` (
  `pkey` int(11) NOT NULL,
  `vendor` int(11) DEFAULT NULL COMMENT '商户主键',
  `vendor_name` varchar(100) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '姓名',
  `mobile` varchar(20) CHARACTER SET utf8mb4 NOT NULL COMMENT '手机号码',
  `openid1` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL,
  `openid2` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `farmer` varchar(40) CHARACTER SET utf8mb4 NOT NULL COMMENT '市场',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户店员';


ALTER TABLE mkt_member_card ADD limit_cost decimal(11,2) DEFAULT 0 NOT NULL COMMENT '最低消费';
ALTER TABLE mkt_member_card CHANGE limit_cost limit_cost decimal(11,2) DEFAULT 0 NOT NULL COMMENT '最低消费' AFTER cost;

ALTER TABLE mkt_member_card ADD user_type int(11) DEFAULT NULL COMMENT '限制分类';
ALTER TABLE   mkt_member_card CHANGE user_type user_type int(11) DEFAULT  NULL COMMENT '限制分类' AFTER user_farmer;

ALTER TABLE mkt_member_card ADD user_goods int(11) DEFAULT NULL COMMENT '限制商品';
ALTER TABLE   mkt_member_card CHANGE user_goods user_goods int(11) DEFAULT  NULL COMMENT '限制商品' AFTER user_type;

ALTER TABLE mkt_goods_space ADD photo1 varchar(1000) DEFAULT NULL COMMENT '图片';
ALTER TABLE   mkt_goods_space CHANGE  photo1 photo1 varchar(1000) DEFAULT NULL COMMENT '图片' AFTER space;

ALTER TABLE mkt_supply ADD m_type tinyint(4) DEFAULT 1 NOT NULL COMMENT '商品属性';
ALTER TABLE  mkt_supply CHANGE m_type m_type tinyint(4) DEFAULT 1 NOT NULL COMMENT '商品属性' AFTER good;

ALTER TABLE mkt_vendor_order ADD price_status tinyint(4) DEFAULT NULL COMMENT '价格异常';
ALTER TABLE  mkt_vendor_order CHANGE price_status price_status tinyint(4) DEFAULT NULL COMMENT '价格异常' AFTER purchase_status;

ALTER TABLE mkt_vendor_order ADD recommend_price decimal(11,2) DEFAULT NULL COMMENT '推荐采购价格';
ALTER TABLE  mkt_vendor_order CHANGE recommend_price recommend_price decimal(11,2) DEFAULT NULL COMMENT '推荐采购价格' AFTER price_status;

ALTER TABLE mkt_vendor ADD zx_status tinyint(4) DEFAULT NULL COMMENT '中信银行审核结果';
ALTER TABLE  mkt_vendor CHANGE zx_status zx_status tinyint(4) DEFAULT NULL COMMENT '中信银行审核结果' AFTER is_clear;

ALTER TABLE mkt_order ADD pickup_time varchar(50) DEFAULT NULL COMMENT '核销时间';
ALTER TABLE mkt_order CHANGE pickup_time pickup_time varchar(50) DEFAULT NULL COMMENT '核销时间' AFTER pickup_code;

ALTER TABLE mkt_order MODIFY COLUMN pstime varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '配送时间';




