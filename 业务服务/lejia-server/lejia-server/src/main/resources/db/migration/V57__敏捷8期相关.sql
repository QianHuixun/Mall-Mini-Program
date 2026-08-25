ALTER TABLE `mkt_express` ADD COLUMN `photo` varchar(2000) DEFAULT NULL COMMENT '送达照片' AFTER `qr_time`;

ALTER TABLE `mkt_order` ADD COLUMN  `express_type` tinyint(4) DEFAULT NULL COMMENT '骑手类型,可为空'   AFTER `distribution_type`;

ALTER TABLE `wx_account` ADD COLUMN  `template_id` varchar(100) DEFAULT NULL COMMENT '模板id'   AFTER `shield_allowed_pkey`;

ALTER TABLE `mkt_activity` ADD COLUMN `distribute_type` tinyint(4) DEFAULT NULL COMMENT '活动分发方式'   AFTER `limit_daily_gift_num`;