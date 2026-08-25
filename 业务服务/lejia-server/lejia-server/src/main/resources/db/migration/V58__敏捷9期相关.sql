DROP TABLE IF EXISTS `mkt_desktop`;
CREATE TABLE `mkt_desktop`  (
  `pkey` int(11) NOT NULL,
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '名称',
  `qr_code` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二维码',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司',
  `update_time` datetime(0) NOT NULL COMMENT '最后更新时间',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `mkt_goods_box`;
CREATE TABLE `mkt_goods_box`  (
  `pkey` int(11) NOT NULL,
  `goods` int(11) DEFAULT NULL,
  `desktop` int(11) DEFAULT NULL,
  `desktop_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '公司',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `mkt_goods_process`;
CREATE TABLE `mkt_goods_process`  (
  `pkey` int(11) NOT NULL,
  `goods` int(11) DEFAULT NULL,
  `process` int(11) DEFAULT NULL,
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '公司',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


ALTER TABLE `mkt_goods` ADD COLUMN `is_process` tinyint(1) DEFAULT NULL COMMENT '是否可加工'   AFTER `m_type`;

ALTER TABLE `mkt_order_line` ADD COLUMN `association` int(11) DEFAULT NULL COMMENT '关联主键'   AFTER `goods_name`;
ALTER TABLE `mkt_order_line` ADD COLUMN `association_name` varchar(200) DEFAULT NULL COMMENT '关联名称'   AFTER `association`;

ALTER TABLE `mkt_gwc` ADD COLUMN `association` int(11) DEFAULT NULL COMMENT '关联主键'   AFTER `num`;
ALTER TABLE `mkt_gwc` ADD COLUMN `association_name` varchar(200) DEFAULT NULL COMMENT '关联名称'   AFTER `association`;