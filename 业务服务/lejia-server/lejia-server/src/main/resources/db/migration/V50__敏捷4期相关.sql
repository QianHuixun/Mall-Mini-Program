ALTER TABLE `mkt_member` ADD COLUMN `last_consume_time` datetime DEFAULT NULL COMMENT '最近消费时间' AFTER `end_date`;
ALTER TABLE `mkt_member` ADD COLUMN `last_consume_farmer` varchar(40) DEFAULT NULL COMMENT '最近消费市场' AFTER `last_consume_time`;

ALTER TABLE `wx_account` ADD COLUMN `shield_version` varchar(50) DEFAULT NULL COMMENT '屏蔽版本号' AFTER `access_time`;
ALTER TABLE `wx_account` ADD COLUMN `shield_allowed_pkey` varchar(100) DEFAULT NULL COMMENT '屏蔽后允许主键' AFTER `shield_version`;

ALTER TABLE `mkt_order` ADD COLUMN `qr_time` datetime DEFAULT NULL COMMENT '到货时间' AFTER `third_party_order_no`;
