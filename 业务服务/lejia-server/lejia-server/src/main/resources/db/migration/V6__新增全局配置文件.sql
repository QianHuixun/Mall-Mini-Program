/*
Navicat MySQL Data Transfer

Source Server         : 192.168.128.91
Source Server Version : 50718
Source Host           : 192.168.128.91:3306
Source Database       : zyysc

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2021-09-28 15:58:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `pkey` varchar(40) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
