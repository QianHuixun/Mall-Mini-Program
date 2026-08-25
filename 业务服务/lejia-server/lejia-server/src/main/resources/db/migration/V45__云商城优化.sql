ALTER TABLE `sys_farmer` ADD COLUMN `type`  tinyint(4) DEFAULT NULL COMMENT '市场类型'  AFTER  `name`;

ALTER TABLE `sys_farmer_config` ADD COLUMN `monday`  tinyint(1) DEFAULT NULL COMMENT '星期一'  AFTER  `shop_id`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `tuesday`  tinyint(1) DEFAULT NULL COMMENT '星期二'  AFTER  `monday`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `wednesday`  tinyint(1) DEFAULT NULL COMMENT '星期三'  AFTER  `tuesday`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `thursday`  tinyint(1) DEFAULT NULL COMMENT '星期四'  AFTER  `wednesday`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `friday`  tinyint(1) DEFAULT NULL COMMENT '星期五'  AFTER  `thursday`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `saturday`  tinyint(1) DEFAULT NULL COMMENT '星期六'  AFTER  `friday`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `sunday`  tinyint(1) DEFAULT NULL COMMENT '星期日'  AFTER  `saturday`;

ALTER TABLE `mkt_goods` ADD COLUMN `three_gtype`  int(11) DEFAULT NULL COMMENT '三级分类'  AFTER  `goods_main`;
ALTER TABLE `mkt_goods` ADD COLUMN `vendor`  int(11) DEFAULT NULL COMMENT '商户主键'  AFTER  `three_gtype`;

ALTER TABLE `mkt_vendor` ADD COLUMN `display_name` varchar(200) DEFAULT NULL COMMENT '展示名称'  AFTER  `name`;
ALTER TABLE `mkt_vendor` ADD COLUMN `booth`  varchar(200) DEFAULT NULL COMMENT '摊位号'  AFTER  `display_name`;
ALTER TABLE `mkt_vendor` ADD COLUMN `merchant`  int(11) DEFAULT NULL COMMENT 'cust商户主键'  AFTER  `id_del`;
ALTER TABLE `mkt_vendor` ADD COLUMN `display_flag`  tinyint(1) DEFAULT NULL COMMENT '商户展示名称'  AFTER  `display_name`;


ALTER TABLE `mkt_member` ADD COLUMN `tjv`  int(11) DEFAULT NULL COMMENT '推荐商户'  AFTER  `tjr`;
ALTER TABLE `mkt_member` ADD COLUMN `tjv_time` datetime DEFAULT NULL COMMENT '推荐时间'  AFTER  `tjv`;

ALTER TABLE `mkt_order_desc` ADD COLUMN `distance` decimal(11,2) DEFAULT NULL COMMENT '距离'  AFTER  `latitude`;


alter table mkt_goods modify start_date date null;
alter table mkt_goods modify end_date date null;
alter table mkt_vendor modify business_scope VARCHAR(200) null;
alter table mkt_vendor modify update_by int(11) null;

alter table sys_farmer_config modify yytb varchar(20) null;
alter table sys_farmer_config modify yyte varchar(20) null;


DROP TABLE IF EXISTS `sys_farmer_time`;
CREATE TABLE `sys_farmer_time`  (
  `pkey` int(11) NOT NULL,
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市场',
  `start_hour` int(11) DEFAULT NULL COMMENT '开始小时',
  `start_minute` int(11) DEFAULT NULL COMMENT '开始分钟',
  `end_hour` int(11) DEFAULT NULL COMMENT '结束小时',
  `end_minute` int(11) DEFAULT NULL COMMENT '结束时间',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE,
  INDEX `farmer`(`farmer`, `ascription`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '市场营业时间' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `mkt_goods_main_three`;
CREATE TABLE `mkt_goods_main_three`  (
  `pkey` int(11) NOT NULL,
  `gtype` int(11) DEFAULT NULL COMMENT '一级分类',
  `two_gtype` int(11) DEFAULT NULL COMMENT '二级分类',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '名称',
  `sort` int(11) DEFAULT 0,
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '启用标志',
  `id_del` tinyint(1) DEFAULT NULL COMMENT '是否已删除',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `created_by` int(11) DEFAULT NULL COMMENT '建档员',
  `row_vension` smallint(6) DEFAULT NULL COMMENT '版本',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `mkt_vendor_boutique`;
CREATE TABLE `mkt_vendor_boutique`  (
  `pkey` int(11) NOT NULL,
  `vendor` int(11) DEFAULT NULL COMMENT '商户主键',
  `label` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标签',
  `show_type1` tinyint(4) DEFAULT NULL COMMENT '展示类型1',
  `show_content1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '展示内容1',
  `show_type2` tinyint(4) DEFAULT NULL COMMENT '展示类型2',
  `show_content2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '展示内容2',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '启停',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '公司',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '精选商户' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

