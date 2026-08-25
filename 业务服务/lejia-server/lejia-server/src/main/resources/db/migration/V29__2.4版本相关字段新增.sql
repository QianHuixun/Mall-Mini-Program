ALTER TABLE mkt_goods ADD guess_sort INT(11) NULL COMMENT '猜你喜欢排序';

ALTER TABLE mkt_gtype ADD market_sort INT(11) DEFAULT 0 NOT NULL COMMENT '市场分类排序';
ALTER TABLE mkt_gtype CHANGE market_sort market_sort INT(11) DEFAULT 0 NOT NULL COMMENT '市场分类排序' AFTER sort;
ALTER TABLE mkt_gtype ADD point_sort INT(11) DEFAULT 0 NOT NULL COMMENT '积分商城排序';
ALTER TABLE mkt_gtype CHANGE point_sort point_sort INT(11) DEFAULT 0 NOT NULL COMMENT '积分商城排序' AFTER market_sort;

ALTER TABLE mkt_card ADD invalid TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否失效,false:未失效';
ALTER TABLE mkt_card CHANGE invalid invalid TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否失效,false:未失效' AFTER enabled;

ALTER TABLE mkt_card ADD issued_num INT(11) DEFAULT 0 NOT NULL COMMENT '已发放数量';
ALTER TABLE mkt_card ADD used_num INT(11) DEFAULT 0 NOT NULL COMMENT '已使用数量';


ALTER TABLE mkt_member_card ADD invalid TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否失效,false:未失效';
ALTER TABLE mkt_member_card CHANGE invalid invalid TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否失效,false:未失效' AFTER user_time;

