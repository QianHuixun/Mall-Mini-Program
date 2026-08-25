ALTER TABLE `mkt_addr` ADD COLUMN `type` tinyint DEFAULT NULL COMMENT '类型' AFTER `member_key`;
UPDATE `mkt_addr` SET `type`=1;

ALTER TABLE `mkt_order_desc` MODIFY COLUMN `longitude` decimal(11,6) DEFAULT NULL COMMENT '经度';
ALTER TABLE `mkt_order_desc` MODIFY COLUMN `latitude` decimal(11,6) NOT NULL COMMENT '纬度';

CREATE TABLE `mkt_delivery_time_config`  (
  `pkey` varchar(60) NOT NULL,
  `distance` decimal(11, 2) DEFAULT NULL COMMENT '距离',
  `hour` int(11) DEFAULT NULL COMMENT '小时',
  `minute` int(11) DEFAULT NULL COMMENT '分钟',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`),
  KEY `idx_farmer` (`farmer`)
) COMMENT = '预计配送时间配置';

ALTER TABLE `mkt_goods_gift` ADD COLUMN `goods` int DEFAULT NULL COMMENT '商品' AFTER `pkey`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `title` varchar(100) DEFAULT NULL COMMENT '标题' AFTER `goods`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `content` varchar(2000) DEFAULT NULL COMMENT '介绍' AFTER `title`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `picture` varchar(300) DEFAULT NULL COMMENT '图片' AFTER `content`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `gift_type` tinyint(4) DEFAULT NULL COMMENT '领取方式' AFTER `picture`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `gift_code` varchar(100) DEFAULT NULL COMMENT '领券码' AFTER `gift_type`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `enabled` tinyint(4) DEFAULT NULL COMMENT '启用标志' AFTER `gift_code`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `invalid` tinyint(4) DEFAULT NULL COMMENT '是否失效,false:未失效' AFTER `enabled`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `effective` int DEFAULT NULL COMMENT '有效期(天)' AFTER `user_vendor`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `count` int DEFAULT NULL COMMENT '优惠券数量' AFTER `end_date`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `issued_num` int DEFAULT NULL COMMENT '已发放数量' AFTER `count`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `used_num` int DEFAULT NULL COMMENT '已使用数量' AFTER `issued_num`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `farmer` varchar(40) DEFAULT NULL COMMENT '市场' AFTER `used_num`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `company` varchar(40) DEFAULT NULL COMMENT '公司' AFTER `farmer`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `updated_time` datetime DEFAULT NULL COMMENT '最后更新时间' AFTER `company`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `created_time` datetime DEFAULT NULL COMMENT '建档时间' AFTER `updated_time`;
ALTER TABLE `mkt_goods_gift` ADD COLUMN `created_by` int DEFAULT NULL COMMENT '建档员' AFTER `created_time`;
UPDATE `mkt_goods_gift` SET `goods` = `pkey`,`issued_num` = 0, `used_num` = 0;

ALTER TABLE `mkt_member_gift` MODIFY COLUMN `order_pkey` int(11) DEFAULT NULL COMMENT '订单pkey';
ALTER TABLE `mkt_member_gift` ADD COLUMN `gift` int(11) DEFAULT NULL COMMENT '礼品券' AFTER `card_number`;
ALTER TABLE `mkt_member_gift` MODIFY COLUMN `goods` int(11) DEFAULT NULL COMMENT '卡券';
ALTER TABLE `mkt_member_gift` MODIFY COLUMN `space` int(11) DEFAULT NULL COMMENT '规格';
ALTER TABLE `mkt_member_gift` MODIFY COLUMN `user_vendor` int(11) DEFAULT NULL COMMENT '使用商户';
ALTER TABLE `mkt_member_gift` ADD COLUMN `invalid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否失效,false:未失效' AFTER `user_time`;
