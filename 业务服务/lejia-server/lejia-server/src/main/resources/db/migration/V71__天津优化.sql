ALTER TABLE `sys_farmer_extend` ADD COLUMN `content` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '小票内容' AFTER `print_code`;
ALTER TABLE `sys_farmer_extend` ADD COLUMN `photo1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '图1' AFTER `content`;
ALTER TABLE `sys_farmer_extend` ADD COLUMN `photo1_text` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '图1文字' AFTER `photo1`;
ALTER TABLE `sys_farmer_extend` ADD COLUMN `photo2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '图2' AFTER `photo1_text`;
ALTER TABLE `sys_farmer_extend` ADD COLUMN `photo2_text` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '图2文字' AFTER `photo2`;

ALTER TABLE `mkt_card` ADD COLUMN `user_goods_list` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '使用商品' AFTER `user_goods`;
ALTER TABLE `mkt_card` ADD COLUMN `user_mtype` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '使用专区' AFTER `user_goods_list`;
ALTER TABLE `mkt_member_card` ADD COLUMN `user_goods_list` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '使用商品' AFTER `user_goods`;
ALTER TABLE `mkt_member_card` ADD COLUMN `user_mtype` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL comment '使用专区' AFTER `user_goods_list`;

ALTER TABLE `mkt_order_line` ADD COLUMN `point` int(11) DEFAULT NULL comment '积分' AFTER `num`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_point` int(11) DEFAULT NULL comment '退款积分' AFTER `amtre`;
ALTER TABLE `mkt_order_refund_line` ADD COLUMN `refund_point` int(11) DEFAULT NULL comment '退款积分' AFTER `refund_amt`;
ALTER TABLE `mkt_order` ADD COLUMN `refund_point` int(11) DEFAULT NULL comment '退款积分' AFTER `refund_amt`;

DROP TABLE IF EXISTS `mkt_search_keyword`;
CREATE TABLE `mkt_search_keyword` (
    `pkey` int(11) NOT NULL COMMENT '主键',
    `module` tinyint(4) DEFAULT NULL COMMENT '模块',
    `keyword` varchar(50) DEFAULT NULL COMMENT '关键词',
    `sort` int(11) DEFAULT NULL COMMENT '排序',
    `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
    `company` varchar(40) DEFAULT NULL COMMENT '公司',
    `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
    `created_time` datetime DEFAULT NULL COMMENT '建档时间',
    `created_by` int(11) DEFAULT NULL COMMENT '建档员',
    `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
    PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索关键词';

DROP TABLE IF EXISTS `sys_dynamic_attribute`;
CREATE TABLE `sys_dynamic_attribute` (
    `pkey` varchar(40) NOT NULL COMMENT '主键',
    `vendor` int(11) DEFAULT NULL COMMENT '商户',
    `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
    `company` varchar(40) DEFAULT NULL COMMENT '公司',
    `config_class` varchar(100) DEFAULT NULL COMMENT '配置类',
    `property` varchar(100) DEFAULT NULL COMMENT '属性',
    `value` varchar(500) DEFAULT NULL COMMENT '值',
    `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
    PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态属性';

DROP TABLE IF EXISTS `mkt_goods_recommend`;
CREATE TABLE `mkt_goods_recommend` (
    `pkey` int(11) NOT NULL COMMENT '主键',
    `goods` int(11) DEFAULT NULL COMMENT '商品主键',
    `sort` int(11) DEFAULT NULL COMMENT '排序',
    `goods_farmer` varchar(40) DEFAULT NULL COMMENT '商品所属市场，冗余',
    `source_goods` int(11) DEFAULT NULL COMMENT '来源商品主键',
    `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
    `company` varchar(40) DEFAULT NULL COMMENT '公司',
    `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
    `created_time` datetime DEFAULT NULL COMMENT '建档时间',
    `created_by` int(11) DEFAULT NULL COMMENT '建档员',
    `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
    PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐商品';

DROP TABLE IF EXISTS `mkt_goods_recommend_zone`;
CREATE TABLE `mkt_goods_recommend_zone` (
    `pkey` varchar(40) NOT NULL COMMENT '主键',
    `goods_recommend` int(11) DEFAULT NULL COMMENT '推荐商品主键',
    `zone` tinyint(4) DEFAULT NULL COMMENT '推荐区域',
    `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
    `company` varchar(40) DEFAULT NULL COMMENT '公司',
    `created_time` datetime DEFAULT NULL COMMENT '建档时间',
    `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
    PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐商品区域关联';

ALTER TABLE `mkt_goods` ADD INDEX `idx_farmer` (`farmer`);
ALTER TABLE `mkt_goods` ADD COLUMN `zone_recommend` tinyint(4) DEFAULT NULL COMMENT '是否专区推荐' AFTER `guess_sort`;
ALTER TABLE `mkt_goods` ADD COLUMN `tag` varchar(10) DEFAULT NULL COMMENT '标签' after `title`;

DROP TABLE IF EXISTS `mkt_goods_selling_point`;
CREATE TABLE `mkt_goods_selling_point` (
    `pkey` int(11) NOT NULL COMMENT '主键',
    `goods` int(11) DEFAULT NULL COMMENT '商品主键',
    `name` varchar(10) DEFAULT NULL COMMENT '名称',
    `content` varchar(10) DEFAULT NULL COMMENT '内容',
    `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
    `created_time` datetime DEFAULT NULL COMMENT '建档时间',
    `created_by` int(11) DEFAULT NULL COMMENT '建档员',
    `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
    PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品卖点';

ALTER TABLE `mkt_advert` ADD COLUMN `position_obj` varchar(200) DEFAULT NULL COMMENT '位置关联对象主键' AFTER `position`;

DROP TABLE IF EXISTS `mkt_order_goods_comment`;
CREATE TABLE `mkt_order_goods_comment` (
    `pkey` int NOT NULL COMMENT '主键',
    `order_pkey` int DEFAULT NULL COMMENT '订单主键',
    `member` int DEFAULT NULL COMMENT '评价用户',
    `gtype` int DEFAULT NULL COMMENT '分类',
    `goods` int DEFAULT NULL COMMENT '商品pkey',
    `goods_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
    `score` int DEFAULT NULL COMMENT '评分',
    `content` varchar(300) DEFAULT NULL COMMENT '内容',
    `photo` varchar(1000) DEFAULT NULL COMMENT '图片',
    `reply_content` varchar(300) DEFAULT NULL COMMENT '回复内容',
    `reply_status` tinyint(4) DEFAULT NULL COMMENT '回复状态',
    `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
    `apply_status` tinyint(4) DEFAULT NULL COMMENT '审核状态',
    `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
    `company` varchar(40) DEFAULT NULL COMMENT '公司',
    `created_time` datetime DEFAULT NULL COMMENT '建档时间',
    `ascription` int DEFAULT NULL COMMENT '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细评价';

ALTER TABLE `mkt_order` ADD COLUMN `confirm_time` datetime DEFAULT NULL COMMENT '完成时间' AFTER `qr_time`;

INSERT INTO `sys_config` (`pkey`, `name`, `value`, `ascription`) VALUES ('template_activity_13', '天津滨海-活动通知', 'bzm5xTXYINXXkjKD8xOvF4w7BoIz6IeXW2t-UOOLh8w', 13);
INSERT INTO `sys_config` (`pkey`, `name`, `value`, `ascription`) VALUES ('template_new_card_13', '天津滨海-优惠券开抢提醒', '2cfjjpWq3FTiPK7CoJQmoGxmuHWunx7gF0qqVLCrV_Q', 13);
INSERT INTO `sys_config` (`pkey`, `name`, `value`, `ascription`) VALUES ('template_new_membercard_13', '天津滨海-优惠券到账通知', 'HhPUXnm42UW_b_smumzhZ31SLcfuacw84ULJiJXy5yE', 13);
INSERT INTO `sys_config` (`pkey`, `name`, `value`, `ascription`) VALUES ('template_special_goods_13', '天津滨海-秒杀开始提醒', 'sQhVptWg15AotV9SR_MUcrcPexfwsRm1iXC_JyMKQ-8', 13);

ALTER TABLE `sys_farmer_config` ADD COLUMN `customer_service_link` varchar(100) NULL COMMENT '客服链接' AFTER `shop_id`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `customer_service_id` varchar(32) DEFAULT NULL COMMENT '微信客服企业ID' AFTER `shop_id`;
