alter table mkt_addr modify addr VARCHAR(200) null;
alter table mkt_addr modify addr_code VARCHAR(100) null;
alter table mkt_addr modify name VARCHAR(100) null;
alter table mkt_addr modify mobile VARCHAR(20) null;
alter table mkt_addr modify longitude decimal(11,6) null;
alter table mkt_addr modify latitude decimal(11,6) null;

ALTER TABLE `mkt_promote` ADD COLUMN `farmer` VARCHAR(40) DEFAULT NULL COMMENT '市场'  AFTER `enabled`;


DROP TABLE IF EXISTS `mkt_vendor_wallet`;
CREATE TABLE `mkt_vendor_wallet`  (
  `pkey` int(11) NOT NULL COMMENT '对应mkt_vendor的pkey',
  `amount` decimal(16, 2) DEFAULT NULL COMMENT '金额',
  `lock_amount` decimal(16, 2) DEFAULT NULL COMMENT '锁定金额,不能使用(待结算)',
  `update_time` datetime(0) DEFAULT NULL COMMENT '最后更新时间',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商户钱包账户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mkt_vendor_wallet_line
-- ----------------------------
DROP TABLE IF EXISTS `mkt_vendor_wallet_line`;
CREATE TABLE `mkt_vendor_wallet_line`  (
  `pkey` int(11) NOT NULL,
  `vendor_key` int(11) DEFAULT NULL COMMENT '商户主键',
  `direct` tinyint(4) DEFAULT NULL COMMENT '借贷标志 借(-)/贷(+)',
  `amount` decimal(16, 2) DEFAULT NULL COMMENT '金额',
  `balance` decimal(16, 2) DEFAULT NULL COMMENT '余额',
  `lock_balance` decimal(16, 2) DEFAULT NULL COMMENT '锁定余额',
  `source` tinyint(4) DEFAULT NULL COMMENT '积分来源  购买+/消费-/手动+-',
  `status` tinyint(4) DEFAULT NULL COMMENT '结算状态',
  `settlement_time` datetime(0) DEFAULT NULL COMMENT '结算时间',
  `form_id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源单据',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商户钱包账户明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mkt_vendor_withdrawal
-- ----------------------------
DROP TABLE IF EXISTS `mkt_vendor_withdrawal`;
CREATE TABLE `mkt_vendor_withdrawal`  (
  `pkey` int(11) NOT NULL,
  `line_key` int(11) DEFAULT NULL COMMENT '对应钱包账户明细主键',
  `vendor_key` int(11) DEFAULT NULL COMMENT '商户主键',
  `status` tinyint(4) DEFAULT NULL COMMENT '打款状态',
  `amount` decimal(16, 2) DEFAULT NULL COMMENT '金额',
  `balance` decimal(11, 2) DEFAULT NULL COMMENT '余额',
  `bankname` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行',
  `bankuser` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '持卡人',
  `bankcard` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行卡号',
  `bank_branch_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '开户支行名称',
  `created_time` datetime(0) DEFAULT NULL COMMENT '申请时间',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商户提现记录' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `mkt_three_gtype_sort`;
CREATE TABLE `mkt_three_gtype_sort`  (
  `pkey` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `three_gtype` int(11) DEFAULT NULL,
  `three_gtype_enable` tinyint(1) DEFAULT NULL,
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sort_type` tinyint(4) DEFAULT NULL,
  `sort_value` decimal(16, 2) DEFAULT NULL,
  `gtype` int(11) DEFAULT NULL,
  `gtype_enable` tinyint(1) DEFAULT NULL,
  `gtype_sort` int(11) DEFAULT NULL,
  `goods_main` int(11) DEFAULT NULL,
  `goods_main_enable` tinyint(1) DEFAULT NULL,
  `goods_main_sort` int(11) DEFAULT NULL,
  `goods` int(11) DEFAULT NULL,
  `space` int(11) DEFAULT NULL,
  `vendor` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkey`) USING BTREE,
  INDEX `idx_goodsMain`(`goods_main`) USING BTREE,
  INDEX `idx_threeGtype`(`three_gtype`) USING BTREE,
  INDEX `idx_groupByGtype`(`farmer`, `gtype_enable`, `goods_main_enable`, `three_gtype_enable`, `sort_type`, `gtype_sort`, `goods_main_sort`, `sort_value`) USING BTREE,
  INDEX `idx_groupByGoodsMain`(`farmer`, `gtype_enable`, `goods_main_enable`, `three_gtype_enable`, `sort_type`, `gtype`, `goods_main_sort`, `sort_value`) USING BTREE,
  INDEX `idx_gtype`(`gtype`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

