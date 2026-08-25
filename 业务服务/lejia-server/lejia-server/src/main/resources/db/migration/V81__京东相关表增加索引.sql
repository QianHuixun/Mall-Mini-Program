ALTER TABLE `mkt_tag_visible` ADD INDEX `idx_type_target` (`type`, `target`, `ascription`) USING BTREE;
ALTER TABLE `jd_goods` ADD INDEX `idx_del_enable_spu` (`id_del`, `enabled`, `spu_id`, `ascription`) USING BTREE;
ALTER TABLE `jd_goods` ADD INDEX `idx_del_enable_state_spu` (`id_del`, `enabled`, `sku_state`, `spu_id`, `ascription`) USING BTREE;
