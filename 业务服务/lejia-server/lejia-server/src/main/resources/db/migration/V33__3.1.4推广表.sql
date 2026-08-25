
-- ----------------------------
-- Table structure for mkt_promote
-- ----------------------------
DROP TABLE IF EXISTS `mkt_promote`;
CREATE TABLE `mkt_promote`  (
  `pkey` int(11) NOT NULL,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标题',
  `content` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内容',
  `photo` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `updated_time` datetime(0) NOT NULL COMMENT '更新时间',
  `created_time` datetime(0) NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '推广' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for mkt_ns_pay_line
-- ----------------------------
DROP TABLE IF EXISTS `mkt_ns_pay_line`;
CREATE TABLE `mkt_ns_pay_line`  (
  `pkey` int(11) NOT NULL,
  `return_code` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '返回状态码',
  `return_msg` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '返回信息',
  `err_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '错误代码',
  `err_msg` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '错误代码描述',
  `notice_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '交易类型',
  `transaction_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '第三方订单号',
  `out_trade_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户订单号',
  `total_fee` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '总金额',
  `fee_type` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '货币种类',
  `time_end` varchar(14) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付完成时间',
  `bank_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '付款银行',
  `sub_appid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户appid',
  `sub_openid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户openid',
  `cash_fee` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '现金支付金额',
  `coupon_fee` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '现金券金额',
  `settlement_total_fee` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL COMMENT '应结订单金额=订单金额-非充值 代金券金额，应结订单金额<=订单 金额。',
  `attach` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '附加信息',
  `rout` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付通道',
  `bill_date` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '对账日期',
  `bill` tinyint(4) DEFAULT NULL COMMENT '是否已对账',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '农商支付回调记录' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
