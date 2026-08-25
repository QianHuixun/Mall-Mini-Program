/*
Navicat MySQL Data Transfer

Source Server         : 192.168.128.91
Source Server Version : 50718
Source Host           : 192.168.128.91:3306
Source Database       : zyysc

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2021-11-18 09:32:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mkt_zx_pay_line
-- ----------------------------
DROP TABLE IF EXISTS `mkt_zx_pay_line`;
CREATE TABLE `mkt_zx_pay_line` (
  `pkey` int(11) DEFAULT NULL,
  `return_code` varchar(16) DEFAULT NULL COMMENT '返回状态码',
  `return_msg` varchar(128) DEFAULT NULL COMMENT '返回信息',
  `mch_id` varchar(32) DEFAULT NULL COMMENT '商户号',
  `device_info` varchar(8) DEFAULT NULL COMMENT '设备号',
  `err_code` varchar(32) DEFAULT NULL COMMENT '错误代码',
  `err_msg` varchar(128) DEFAULT NULL COMMENT '错误代码描述',
  `trade_type` varchar(32) DEFAULT NULL COMMENT '交易类型',
  `transaction_id` varchar(32) DEFAULT NULL COMMENT '第三方订单号',
  `out_trade_no` varchar(32) DEFAULT NULL COMMENT '商户订单号',
  `total_fee` int(11) DEFAULT NULL COMMENT '总金额',
  `fee_type` varchar(8) DEFAULT NULL COMMENT '货币种类',
  `time_end` varchar(14) DEFAULT NULL COMMENT '支付完成时间',
  `openid` varchar(128) DEFAULT NULL COMMENT '用户标识 用户在服务商 appid 下的唯一标识',
  `bank_type` varchar(32) DEFAULT NULL COMMENT '付款银行',
  `sub_appid` varchar(128) DEFAULT NULL COMMENT '商户appid',
  `sub_openid` varchar(128) DEFAULT NULL COMMENT '用户openid',
  `cash_fee` int(11) DEFAULT NULL COMMENT '现金支付金额',
  `coupon_fee` int(11) DEFAULT NULL COMMENT '现金券金额',
  `attach` varchar(128) DEFAULT NULL COMMENT '附加信息',
  `settleDate` varchar(16) DEFAULT NULL COMMENT '清算日期【银联二维码】',
  `cardAttr` varchar(4) DEFAULT NULL COMMENT '卡属性【银联二维码】',
  `created_time` datetime DEFAULT NULL COMMENT '建档时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='中信支付回调记录';
