ALTER TABLE mkt_order ADD purchase_status TINYINT(4) NULL COMMENT '采购状态';
ALTER TABLE mkt_order CHANGE purchase_status purchase_status TINYINT(4) NULL COMMENT '采购状态' AFTER status;

ALTER TABLE mkt_order ADD purchase_amt decimal(11,2) NULL COMMENT '采购金额';
ALTER TABLE mkt_order CHANGE purchase_amt purchase_amt decimal(11,2) NULL COMMENT '采购金额' AFTER cut_amt;

ALTER TABLE mkt_vendor_order ADD goods_name varchar(100) NULL COMMENT '商品名称';
ALTER TABLE mkt_vendor_order CHANGE goods_name goods_name varchar(100) NULL COMMENT '商品名称' AFTER goods;

ALTER TABLE mkt_vendor_order ADD goods_price decimal(11,2) NULL COMMENT '商品原价';
ALTER TABLE mkt_vendor_order CHANGE goods_price goods_price decimal(11,2) NULL COMMENT '商品原价' AFTER goods_name;

ALTER TABLE mkt_vendor_order ADD type TINYINT(4) NULL COMMENT '类型';
ALTER TABLE mkt_vendor_order CHANGE type type TINYINT(4) NULL COMMENT '类型' AFTER goods_price;

ALTER TABLE mkt_vendor_order ADD status TINYINT(4) NULL COMMENT '结算状态';
ALTER TABLE mkt_vendor_order CHANGE status status TINYINT(4) NULL COMMENT '结算状态' AFTER type;

ALTER TABLE mkt_vendor_order ADD purchase_status TINYINT(4) NULL COMMENT '采购状态';
ALTER TABLE mkt_vendor_order CHANGE purchase_status purchase_status TINYINT(4) NULL COMMENT '采购状态' AFTER status;

ALTER TABLE mkt_vendor_order ADD space_name varchar(40) NULL COMMENT '规格';
ALTER TABLE mkt_vendor_order CHANGE space_name space_name varchar(40) NULL COMMENT '规格' AFTER space;

ALTER TABLE mkt_vendor_order ADD weight decimal(11,2) NULL COMMENT '毛重';
ALTER TABLE mkt_vendor_order CHANGE weight weight decimal(11,2) NULL COMMENT '毛重' AFTER space_name;

ALTER TABLE mkt_vendor_order ADD revoke_time datetime NULL COMMENT '撤销时间';
ALTER TABLE mkt_vendor_order CHANGE revoke_time revoke_time datetime NULL COMMENT '撤销时间' AFTER company;

ALTER TABLE sys_farmer_config ADD automatic_courier TINYINT(1) NULL COMMENT '系统自动派单';
ALTER TABLE sys_farmer_config ADD automatic_purchase TINYINT(1) NULL COMMENT '系统自动采购';


DROP TABLE IF EXISTS `mkt_market_courier`;
CREATE TABLE `mkt_market_courier` (
  `id` int(11) NOT NULL COMMENT '主键',
  `market` varchar(40) NOT NULL COMMENT '市场主键',
  `courier_key` int(11) DEFAULT NULL COMMENT '骑手主键',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `flag` tinyint(1) NOT NULL COMMENT '是否轮到',
  `num` int(11) DEFAULT NULL COMMENT '已派单数量',
  `now_date` date DEFAULT NULL COMMENT '当前时间',
  `updated_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`,`market`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='市场骑手派单顺序';


