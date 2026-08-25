-- ----------------------------
-- Table structure for mkt_goods_gift
-- ----------------------------
DROP TABLE IF EXISTS `mkt_goods_gift`;
CREATE TABLE `mkt_goods_gift` (
  `pkey` int(11) NOT NULL,
  `expire_choose` tinyint(4) DEFAULT NULL COMMENT '是否有效',
  `user_farmer` varchar(40) DEFAULT NULL COMMENT '使用市场',
  `user_vendor` int(11) DEFAULT NULL COMMENT '使用商户',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '到期日期',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='礼品券商品扩展数据';


ALTER TABLE mkt_member_gift MODIFY COLUMN end_date date NULL;

ALTER TABLE mkt_member_gift ADD expire_choose tinyint(4) NULL COMMENT '是否有效';
ALTER TABLE mkt_member_gift CHANGE expire_choose expire_choose tinyint(4) NULL COMMENT '是否有效' AFTER space;

ALTER TABLE mkt_member_gift ADD start_date date NULL COMMENT '开始日期';
ALTER TABLE mkt_member_gift CHANGE start_date start_date date NULL COMMENT '开始日期' AFTER expire_choose;

ALTER TABLE mkt_member_gift ADD user_farmer varchar(40) NULL COMMENT '使用市场';
ALTER TABLE mkt_member_gift CHANGE user_farmer user_farmer varchar(40) NULL COMMENT '使用市场' AFTER end_date;


ALTER TABLE mkt_app_config ADD newcomer_card varchar(1000) NULL COMMENT '新人优惠券';
ALTER TABLE mkt_app_config CHANGE newcomer_card newcomer_card varchar(1000) NULL COMMENT '新人优惠券' AFTER member_card;


ALTER TABLE mkt_order_line ADD card int(11) NULL COMMENT '卡券主键';
ALTER TABLE mkt_order_line CHANGE card card int(11) NULL COMMENT '卡券主键' AFTER space;

ALTER TABLE mkt_access_log ADD member int(11) NULL COMMENT '会员主键';
ALTER TABLE mkt_access_log CHANGE member member int(11) NULL COMMENT '会员主键' AFTER pkey;