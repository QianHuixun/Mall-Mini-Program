ALTER TABLE `mkt_order_line` ADD COLUMN `space_name` varchar(100)  DEFAULT NULL COMMENT '规格名称'  AFTER `space`;
ALTER TABLE `mkt_order` ADD COLUMN `is_box` tinyint(1)  DEFAULT NULL COMMENT '是否包厢订单,true:是'  AFTER `tjr`;
ALTER TABLE `mkt_order` ADD COLUMN `box_time` varchar(100)  DEFAULT NULL COMMENT '包厢时间'  AFTER `is_box`;
ALTER TABLE `mkt_order` ADD COLUMN `box_name` varchar(200)  DEFAULT NULL COMMENT '包厢名称'  AFTER `box_time`;
ALTER TABLE `mkt_order` ADD COLUMN `box_password` varchar(100)  DEFAULT NULL COMMENT '包厢门锁密码'  AFTER `box_name`;
ALTER TABLE `mkt_order` ADD COLUMN `lock_id` varchar(100)  DEFAULT NULL COMMENT '包厢门锁ID'  AFTER `box_password`;
ALTER TABLE `mkt_order` ADD COLUMN `box_sd` datetime  DEFAULT NULL COMMENT '门锁密码时间-开始'  AFTER `lock_id`;
ALTER TABLE `mkt_order` ADD COLUMN `box_ed` datetime  DEFAULT NULL COMMENT '门锁密码时间-结束'  AFTER `box_sd`;

ALTER TABLE `mkt_goods_box` ADD COLUMN `lock_id` varchar(100)  DEFAULT NULL COMMENT '包厢门锁ID'  AFTER `desktop_name`;
ALTER TABLE `mkt_goods_space` ADD COLUMN `box_sd` datetime  DEFAULT NULL COMMENT '门锁密码时间-开始'  AFTER `xs_num`;
ALTER TABLE `mkt_goods_space` ADD COLUMN `box_ed` datetime  DEFAULT NULL COMMENT '门锁密码时间-结束'  AFTER `box_sd`;


ALTER TABLE `mkt_activity` ADD COLUMN `receive_num` int(11)  DEFAULT NULL COMMENT '已领取卡券数'  AFTER `issued_num`;
ALTER TABLE `mkt_activity` ADD COLUMN `use_num` int(11)  DEFAULT NULL COMMENT '已使用卡券数'  AFTER `receive_num`;
ALTER TABLE `sys_ascription` ADD COLUMN `certificate_serial_no` varchar(200)  DEFAULT NULL COMMENT '证书序列号'  AFTER `config_localpath`;

DROP TABLE IF EXISTS `sys_farmer_extend`;
CREATE TABLE `sys_farmer_extend`  (
  `pkey` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `print_code` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '打印机编码',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


ALTER TABLE `mkt_goods_box` ADD COLUMN `noon_price` decimal(11,2) DEFAULT NULL COMMENT '中午场价格'  AFTER `lock_id`;
ALTER TABLE `mkt_goods_box` ADD COLUMN `night_price` decimal(11,2) DEFAULT NULL COMMENT '晚上场价格'  AFTER `noon_price`;