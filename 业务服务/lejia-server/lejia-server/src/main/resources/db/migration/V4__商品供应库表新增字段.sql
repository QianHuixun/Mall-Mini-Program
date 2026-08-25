ALTER TABLE `mkt_supply` MODIFY `good` int(11) NOT NULL COMMENT '商品id';
ALTER TABLE `mkt_supply` ADD `flag` tinyint(4) NOT NULL COMMENT '是否轮到自动采购' AFTER `enabled`;