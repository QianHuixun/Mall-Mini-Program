ALTER TABLE `mkt_order` ADD COLUMN `small_ticket` int(11) DEFAULT NULL COMMENT '小票码'  AFTER `pickup_code`;
