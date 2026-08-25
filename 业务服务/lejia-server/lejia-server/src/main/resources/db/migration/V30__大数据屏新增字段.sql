ALTER TABLE mkt_order_line ADD status tinyint(4) DEFAULT 0 NOT NULL COMMENT '状态';
ALTER TABLE mkt_order_line CHANGE status status tinyint(4) DEFAULT 0 NOT NULL COMMENT '分类' AFTER order_pkey;

ALTER TABLE mkt_order_line ADD gtype INT(11) DEFAULT NULL COMMENT '分类';
ALTER TABLE mkt_order_line CHANGE gtype gtype INT(11) DEFAULT NULL COMMENT '分类' AFTER status;


ALTER TABLE mkt_order_line ADD created_time datetime DEFAULT NULL COMMENT '建档时间';
