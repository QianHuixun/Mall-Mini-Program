ALTER TABLE mkt_order_refund_line MODIFY COLUMN goods BIGINT NULL COMMENT '商品';
ALTER TABLE mkt_order_refund_line MODIFY COLUMN space BIGINT NULL COMMENT '规格';

ALTER TABLE mkt_order_refund ADD `jd_type` tinyint(4) DEFAULT NULL COMMENT '类型,退货 换货' AFTER type;
ALTER TABLE mkt_order_refund ADD `is_jd` tinyint(1) DEFAULT NULL COMMENT '是否是京东退款订单' AFTER jd_type;


ALTER TABLE mkt_tag_visible MODIFY COLUMN target BIGINT NULL COMMENT '对象主键"';

ALTER TABLE mkt_order_refund ADD refund_jd_postage decimal(11,2) DEFAULT NULL COMMENT '京东运费退款' AFTER refund_point;

ALTER TABLE mkt_app_config ADD money_rate int(11) DEFAULT NULL COMMENT '价格比' AFTER points_rate;

ALTER TABLE `mkt_order_refund_line` ADD COLUMN `remark` varchar(200) DEFAULT NULL COMMENT '备注' AFTER `refund_jd`;

ALTER TABLE `mkt_order_refund` ADD COLUMN `out_processing` tinyint DEFAULT NULL COMMENT '外部系统处理中' AFTER `re_time`;

DROP TABLE IF EXISTS `mkt_order_refund_extend`;
CREATE TABLE `mkt_order_refund_extend`  (
  `pkey` int(11) NOT NULL,
  `refund_pkey` int(11) NULL DEFAULT NULL COMMENT '退款表主键',
  `order_line_pkey` int(11) NULL DEFAULT NULL,
  `goods` bigint(20) NULL DEFAULT NULL COMMENT '商品',
  `space` bigint(20) NULL DEFAULT NULL COMMENT '规格',
  `refund_num` int(11) NULL DEFAULT NULL COMMENT '退款数量',
  `photo` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `goods_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `space_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `return_method` tinyint(4) NULL DEFAULT NULL COMMENT '退货/换货方式 上门取件或自己寄出',
  `appointment_pickup_time` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预约取件时间',
  `pickup_time` datetime NULL DEFAULT NULL COMMENT '取件时间',
  `addr` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '取件地址',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '寄件人名称',
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '寄件人电话',
  `courier_company` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递公司',
  `courier_number` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递单号',
  `postage` decimal(16, 2) NULL DEFAULT NULL COMMENT '运费',
  `refuse_courier_company` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拒绝后快递公司',
  `refuse_courier_number` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拒绝后快递单号',
  `receipt_addr` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货地址',
  `receipt_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货名称',
  `receipt_mobile` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货电话',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '市场',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '退款拓展表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;