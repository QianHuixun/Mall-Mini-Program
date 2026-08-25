ALTER TABLE `jd_category` ADD COLUMN mall_category int comment '商城分类' AFTER `order_sort`;
ALTER TABLE `mkt_tag_visible` ADD INDEX `idx_type_tag` (`type`, `tag`, `ascription`) USING BTREE;
