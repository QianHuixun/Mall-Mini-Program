ALTER TABLE `mkt_tag` ADD COLUMN `type` tinyint COMMENT '类型' AFTER `pkey`;
ALTER TABLE `mkt_tag` ADD COLUMN `id_del` tinyint COMMENT '是否已删除' AFTER `description`;
UPDATE `mkt_tag` SET `type` = 1, `id_del` = 0;

ALTER TABLE `mkt_recharge_card` ADD COLUMN `type` tinyint COMMENT '卡类型' AFTER `pkey`;
ALTER TABLE `mkt_recharge_card` ADD COLUMN `tag` int(11) COMMENT '标签' AFTER `card_password`;
UPDATE `mkt_recharge_card` SET `type` = 1;
