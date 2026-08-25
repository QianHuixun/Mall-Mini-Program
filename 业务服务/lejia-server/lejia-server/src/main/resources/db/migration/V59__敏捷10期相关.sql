ALTER TABLE `mkt_card` ADD COLUMN `type` tinyint(4)  DEFAULT NULL COMMENT '优惠券类型'   AFTER `card_type`;
ALTER TABLE `mkt_card` ADD COLUMN `avoid_postage` tinyint(1)  DEFAULT NULL COMMENT '免邮费'  AFTER `type`;
ALTER TABLE `mkt_member_card` ADD COLUMN `type` tinyint(4)  DEFAULT NULL COMMENT '优惠券类型'   AFTER `card`;
ALTER TABLE `mkt_member_card` ADD COLUMN `avoid_postage` tinyint(1)  DEFAULT NULL COMMENT '免邮费'   AFTER `type`;
ALTER TABLE `mkt_order` ADD COLUMN `old_postage` decimal(11,2)  DEFAULT NULL COMMENT '原配送费'  AFTER `weight`;
ALTER TABLE `mkt_order` ADD COLUMN `card_postage` int(11)  DEFAULT NULL COMMENT '配送卡券主键'  AFTER `card`;
ALTER TABLE `mkt_order` ADD COLUMN `card_postage_amt` decimal(11,2)  DEFAULT NULL COMMENT '配送卡券金额'  AFTER `card_postage`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `preferential_postage_amt` decimal(11,2)  DEFAULT NULL COMMENT '配送费优惠金额'  AFTER `preferential_amt`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_card_postage` int(11)  DEFAULT NULL COMMENT '退还配送优惠券'  AFTER `refund_card`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `old_postage` decimal(11,2)  DEFAULT NULL COMMENT '原配送费'  AFTER `preferential_postage_amt`;



