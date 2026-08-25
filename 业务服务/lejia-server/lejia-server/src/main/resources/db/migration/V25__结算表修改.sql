ALTER TABLE `mkt_vendor_order` ADD `settlement_pkey` INT(11) NULL DEFAULT NULL COMMENT '结算主键';
-- ----------------------------
-- Table structure for mkt_settlement
-- ----------------------------
DROP TABLE IF EXISTS `mkt_settlement`;
CREATE TABLE `mkt_settlement` (
  `pkey` int(11) NOT NULL,
  `start_date` varchar(10) NOT NULL,
  `end_date` varchar(10) NOT NULL,
  `num_merchant` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `amt` decimal(16,2) DEFAULT NULL,
  `await_amt` decimal(16,2) DEFAULT NULL,
  `type` tinyint(4) NOT NULL,
  `farmer` varchar(40) DEFAULT NULL,
  `company` varchar(40) DEFAULT NULL,
  `updated_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mkt_settlement_line
-- ----------------------------
DROP TABLE IF EXISTS `mkt_settlement_line`;
CREATE TABLE `mkt_settlement_line` (
  `pkey` bigint(20) NOT NULL,
  `settlement_pkey` int(11) DEFAULT NULL,
  `vendor` int(11) DEFAULT NULL,
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
-- Table structure for mkt_settlement_process
-- ----------------------------
DROP TABLE IF EXISTS `mkt_settlement_process`;
CREATE TABLE `mkt_settlement_process` (
  `pkey` bigint(20) NOT NULL,
  `settlement_key` bigint(20) DEFAULT NULL,
  `process_node` tinyint(4) NOT NULL,
  `content` varchar(500) DEFAULT NULL,
  `rem` varchar(200) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;