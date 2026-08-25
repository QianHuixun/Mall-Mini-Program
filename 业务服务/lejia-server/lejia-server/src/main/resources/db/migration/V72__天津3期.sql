ALTER TABLE `mkt_supplier` ADD COLUMN `allowed_delivery` tinyint(4) DEFAULT NULL comment '是否支持配送' AFTER `allowed_pickup`;


DROP TABLE IF EXISTS `mkt_order_tag`;
CREATE TABLE `mkt_order_tag`  (
  `pkey` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_pkey` int(11) NULL DEFAULT NULL,
  `tag` int(11) NULL DEFAULT NULL,
  `tag_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_time` datetime NULL DEFAULT NULL COMMENT '建档时间',
  `ascription` int(11) NULL DEFAULT NULL COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mkt_recharge_card`;
CREATE TABLE `mkt_recharge_card`  (
  `pkey` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '状态',
  `cost` decimal(11, 2) NULL DEFAULT NULL COMMENT '价值',
  `mobile` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用人手机号码',
  `card_number` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卡号',
  `card_password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卡密',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止日期',
  `created_time` datetime NULL DEFAULT NULL COMMENT '建档时间',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  UNIQUE INDEX `unique_card_number`(`card_number`),
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '充值卡' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `mkt_member_msd`;
CREATE TABLE `mkt_member_msd` (
    `pkey` int comment '会员主键',
    `tag` int comment '标签主键',
    `balance` decimal(11,2) comment '余额',
    `updated_time` datetime comment '最后更新时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='会员民生豆账户';

DROP TABLE IF EXISTS `mkt_member_msd_line`;
CREATE TABLE `mkt_member_msd_line` (
    `pkey` bigint comment '主键',
    `member_key` int comment '会员',
    `tag` int comment '标签主键',
    `direct` tinyint(4) comment '加减标志',
    `amt` decimal(11,2) comment '操作金额',
    `balance` decimal(11,2) comment '余额',
    `operation_type` tinyint(4) comment '操作类型',
    `remark` varchar(100) comment '备注',
    `form_id` varchar(40) comment '来源单据',
    `created_time` datetime comment '建档时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='会员民生豆账户明细';



SET FOREIGN_KEY_CHECKS = 1;