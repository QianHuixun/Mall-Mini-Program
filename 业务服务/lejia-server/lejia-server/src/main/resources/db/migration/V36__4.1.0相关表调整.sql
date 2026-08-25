ALTER TABLE sys_farmer_config ADD delivery_date tinyint(4) DEFAULT NULL COMMENT '配送日期';
ALTER TABLE sys_farmer_config CHANGE delivery_date delivery_date tinyint(4) DEFAULT NULL COMMENT '配送日期' AFTER pminute;

ALTER TABLE sys_farmer_station ADD delivery_date tinyint(4) DEFAULT NULL COMMENT '配送日期';
ALTER TABLE sys_farmer_station CHANGE delivery_date delivery_date tinyint(4) DEFAULT NULL COMMENT '配送日期' AFTER pminute;

ALTER TABLE mkt_vendor_order ADD start_date datetime(0) DEFAULT NULL COMMENT '配送开始日期';
ALTER TABLE mkt_vendor_order CHANGE start_date start_date datetime(0) DEFAULT NULL COMMENT '配送开始日期' AFTER settlement_pkey;

ALTER TABLE mkt_vendor_order ADD end_date datetime(0) DEFAULT NULL COMMENT '配送结束日期';
ALTER TABLE mkt_vendor_order CHANGE end_date end_date datetime(0) DEFAULT NULL COMMENT '配送结束日期' AFTER start_date;


DROP TABLE IF EXISTS `mkt_goods_presale`;
CREATE TABLE `mkt_goods_presale`  (
  `pkey` int(11) NOT NULL,
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结算日期',
  `ascription` int(11) NOT NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '预售商品扩展信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `mkt_gzh`;
CREATE TABLE `mkt_gzh`  (
  `pkey` int(11) NOT NULL,
  `mobile` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `openid` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'openid',
  `created_time` datetime(0) NOT NULL COMMENT '建档时间',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mkt_gzh_associate
-- ----------------------------
DROP TABLE IF EXISTS `mkt_gzh_associate`;
CREATE TABLE `mkt_gzh_associate`  (
  `pkey` int(11) NOT NULL,
  `gzh` int(11) NOT NULL,
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市场',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
