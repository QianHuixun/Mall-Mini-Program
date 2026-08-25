ALTER TABLE `sys_farmer_config` ADD COLUMN `reach_one` decimal(11, 2) comment '满减运费1'  AFTER `is_free`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `reach_two` decimal(11, 2) comment '满减运费2'  AFTER `reach_one`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `reduction_delivery_one` decimal(11, 2) comment '减少运费1'  AFTER `reach_two`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `reduction_delivery_two` decimal(11, 2) comment '减少运费2'  AFTER `reduction_delivery_one`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `is_reduction_one` tinyint(1) comment '减少运费1'  AFTER `reduction_delivery_two`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `is_reduction_two` tinyint(1) comment '减少运费1'  AFTER `is_reduction_one`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `is_packing_charge` tinyint(1) comment '是否收打包费,true:收'  AFTER `sunday`;

ALTER TABLE `mkt_vendor_order` ADD COLUMN `packing_charge` decimal(11, 2) comment '打包费用'  AFTER `commissions`;

DROP TABLE IF EXISTS `mkt_vendor_order_packing_charge`;
CREATE TABLE `mkt_vendor_order_packing_charge`  (
  `pkey` int(11) NOT NULL,
  `order_pkey` int(11) DEFAULT NULL,
  `kc_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '订单号',
  `vendor` int(11) DEFAULT NULL COMMENT '商户',
  `display_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '展示名称',
  `booth` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '摊位号',
  `order_amt` decimal(11, 2) DEFAULT NULL COMMENT '订单金额',
  `packing_charge` decimal(11, 2) DEFAULT NULL COMMENT '打包费',
  `amt` decimal(11, 2) DEFAULT NULL COMMENT '结算金额',
  `payment_time` datetime(0) DEFAULT NULL COMMENT '付款时间',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市场',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商户打包费订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mkt_vendor_packing_charge
-- ----------------------------
DROP TABLE IF EXISTS `mkt_vendor_packing_charge`;
CREATE TABLE `mkt_vendor_packing_charge`  (
  `pkey` int(11) NOT NULL,
  `vendor` int(11) DEFAULT NULL COMMENT '商户主键',
  `grade` int(11) DEFAULT NULL COMMENT '等级',
  `order_amt` decimal(11, 2) DEFAULT NULL COMMENT '订单金额',
  `packing_charge` decimal(11, 2) DEFAULT NULL COMMENT '打包费用',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商户打包费配置' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;