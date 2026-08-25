ALTER TABLE `mkt_order` ADD COLUMN `weixin_amt` decimal(11,2) DEFAULT NULL COMMENT '微信支付金额' AFTER `amtn`;
ALTER TABLE `mkt_order` ADD COLUMN `other_amt` decimal(11,2) DEFAULT NULL COMMENT '其他支付金额' AFTER `weixin_amt`;
ALTER TABLE `mkt_order` ADD COLUMN `refund_weixin_amt` decimal(11,2) DEFAULT NULL COMMENT '微信支付退款金额' AFTER `refund_amt`;
ALTER TABLE `mkt_order` ADD COLUMN `refund_other_amt` decimal(11,2) DEFAULT NULL COMMENT '其他支付退款金额' AFTER `refund_weixin_amt`;
ALTER TABLE `mkt_order` ADD COLUMN `again_refund` TINYINT(1) DEFAULT NULL COMMENT '重新退款' AFTER `refund_jd`;

ALTER TABLE `mkt_member_msd` ADD COLUMN `lock_msd` decimal(11,2) DEFAULT NULL COMMENT '锁定民生豆' AFTER `balance`;

ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_weixin_amt` decimal(11,2) DEFAULT NULL COMMENT '微信支付退款金额' AFTER `amtre`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_other_amt` decimal(11,2) DEFAULT NULL COMMENT '其他支付退款金额' AFTER `refund_weixin_amt`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `again_refund` TINYINT(1) DEFAULT NULL COMMENT '重新退款' AFTER `out_processing`;

ALTER TABLE `jd_order_correlation` ADD INDEX `idx_order_code` (`order_code`) USING BTREE;
