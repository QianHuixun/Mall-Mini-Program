ALTER TABLE mkt_advert ADD type tinyint(4) NULL COMMENT '广告类型:专区、自有';
ALTER TABLE mkt_advert CHANGE type type tinyint(4) NULL COMMENT '广告类型:专区、自有' AFTER enabled;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mkt_special_advert
-- ----------------------------
DROP TABLE IF EXISTS `mkt_special_advert`;
CREATE TABLE `mkt_special_advert` (
  `id` int(11) NOT NULL,
  `advert_key` int(11) NOT NULL,
  `farmer` varchar(40) CHARACTER SET utf8mb4 NOT NULL COMMENT '市场主键',
  `position` tinyint(4) NOT NULL COMMENT '位置  1号/2号/3号/4号/5号',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  PRIMARY KEY (`id`,`advert_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='专区广告';