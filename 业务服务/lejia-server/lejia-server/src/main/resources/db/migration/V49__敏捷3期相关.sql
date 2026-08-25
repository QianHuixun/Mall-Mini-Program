ALTER TABLE `mkt_order_line` ADD COLUMN `refund_num` int(11) DEFAULT NULL COMMENT '退款数量'  AFTER `num`;
ALTER TABLE `mkt_order_line` ADD COLUMN `refund_amt` decimal(11, 2) DEFAULT NULL COMMENT '退款金额'  AFTER `refund_num`;
ALTER TABLE `mkt_order_line` ADD COLUMN `coupon_price` decimal(11, 2) DEFAULT NULL COMMENT '使用优惠券后的价格'  AFTER `pricen`;

ALTER TABLE `mkt_order` ADD COLUMN `refund_amt` decimal(11, 2) DEFAULT NULL COMMENT '退款金额'  AFTER `reduce_price`;


ALTER TABLE `mkt_vendor_order` ADD COLUMN `refund_status` tinyint(4) DEFAULT NULL COMMENT '退款状态'  AFTER `price_status`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `refund_amt` decimal(11, 2) DEFAULT NULL COMMENT '退款金额'  AFTER `refund_status`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `procure_refund_amt` decimal(11, 2) DEFAULT NULL COMMENT '采购退款金额'  AFTER `refund_amt`;


DROP TABLE IF EXISTS `mkt_order_refund`;
CREATE TABLE `mkt_order_refund`  (
  `pkey` int(11) NOT NULL,
  `kc_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '单据号',
  `out_refund_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退款单号',
  `order_pkey` int(11) DEFAULT NULL COMMENT '订单主键',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态 申请中/同意/已退款/拒绝',
  `member_key` int(11) DEFAULT NULL COMMENT '用户',
  `type` tinyint(4) DEFAULT NULL COMMENT '类型,市场/用户 退款',
  `reason` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退款理由',
  `refund_describe` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `photo` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '照片',
  `goods_amt` decimal(11, 2) DEFAULT NULL COMMENT '商品价格',
  `preferential_amt` decimal(11, 2) DEFAULT NULL COMMENT '优惠金额',
  `postage` decimal(11, 2) DEFAULT NULL COMMENT '配送费',
  `amtall` decimal(11, 2) DEFAULT NULL COMMENT '订单价格\r\n',
  `amtre` decimal(11, 2) DEFAULT NULL COMMENT '退款金额',
  `del_desc` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '处理意见',
  `del_by` int(11) DEFAULT NULL COMMENT '处理员',
  `del_time` datetime(0) DEFAULT NULL COMMENT '处理时间',
  `re_time` datetime(0) DEFAULT NULL COMMENT '退款时间',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '公司',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单退款记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mkt_order_refund_line
-- ----------------------------
DROP TABLE IF EXISTS `mkt_order_refund_line`;
CREATE TABLE `mkt_order_refund_line`  (
  `pkey` int(11) NOT NULL,
  `refund_pkey` int(11) DEFAULT NULL COMMENT '退款表主键',
  `order_line_pkey` int(11) DEFAULT NULL,
  `goods` int(11) DEFAULT NULL,
  `space` int(11) DEFAULT NULL,
  `refund_num` int(11) DEFAULT NULL COMMENT '退款数量',
  `refund_amt` decimal(11, 2) DEFAULT NULL COMMENT '退款金额',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mkt_member_coupon_linshi`;
CREATE TABLE `mkt_member_coupon_linshi`  (
  `pkey` int(11) NOT NULL,
  `openid1` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `kc_code` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
   `card` int(11) NULL DEFAULT NULL COMMENT '优惠券主键',
  `activity` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动主键',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;