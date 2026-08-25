
ALTER TABLE `mkt_card` ADD COLUMN `user_order_type` tinyint(4) DEFAULT NULL COMMENT '使用订单类型' AFTER `user_goods`;
ALTER TABLE `mkt_member_card` ADD COLUMN `user_order_type` tinyint(4) DEFAULT NULL COMMENT '使用订单类型' AFTER `user_goods`;

ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_goods_amt` decimal(11,2) DEFAULT NULL COMMENT '退款商品金额' AFTER `amtall`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_postage` decimal(11,2) DEFAULT NULL COMMENT '退款配送费' AFTER `refund_goods_amt`;
ALTER TABLE `mkt_order_refund` ADD COLUMN `refund_card` int(11) DEFAULT NULL COMMENT '退还优惠券' AFTER `refund_postage`;
UPDATE `mkt_order_refund` SET `refund_goods_amt` = `amtre`, `refund_postage` = 0;

ALTER TABLE `mkt_order_line` ADD COLUMN `coupon_amt` decimal(11,2) DEFAULT NULL COMMENT '使用优惠券后的金额' AFTER `coupon_price`;
UPDATE `mkt_order_line` SET `coupon_amt` = `coupon_price` * `num`;

ALTER TABLE `mkt_gtype` ADD COLUMN `farmer` varchar(40) DEFAULT NULL COMMENT '市场'  AFTER `id_del`;
ALTER TABLE `mkt_goods_main` ADD COLUMN `farmer` varchar(40) DEFAULT NULL COMMENT '市场'  AFTER `id_del`;
ALTER TABLE `mkt_goods_main_three` ADD COLUMN `farmer` varchar(40) DEFAULT NULL COMMENT '市场'  AFTER `id_del`;

ALTER TABLE `mkt_three_gtype_sort` ADD COLUMN `three_gtype_sort` int(11) DEFAULT NULL COMMENT '三级分类排序'  AFTER `farmer`;