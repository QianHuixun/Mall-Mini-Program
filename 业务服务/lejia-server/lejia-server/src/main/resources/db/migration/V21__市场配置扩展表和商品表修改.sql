ALTER TABLE sys_farmer_config MODIFY `delivery_range` DECIMAL(11,1) NOT NULL COMMENT '配送范围';
ALTER TABLE mkt_goods ADD `guess_like` TINYINT(4) NOT NULL COMMENT '是否猜我喜欢';