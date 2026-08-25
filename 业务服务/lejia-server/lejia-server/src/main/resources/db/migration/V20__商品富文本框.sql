ALTER TABLE mkt_goods ADD content2 TEXT NULL COMMENT '富文本框商品详情';
ALTER TABLE mkt_goods CHANGE content2 content2 TEXT NULL COMMENT '富文本框商品详情' AFTER content;

-- ----------------------------
-- Table structure for mkt_rich_template
-- ----------------------------
DROP TABLE IF EXISTS `mkt_rich_template`;
CREATE TABLE `mkt_rich_template` (
  `pkey` int(11) NOT NULL,
  `type` tinyint(4) DEFAULT NULL COMMENT '模板类型',
  `content` text COMMENT '模板内容',
  `farmer` varchar(40) NOT NULL COMMENT '市场主键',
  `updated_time` datetime NOT NULL COMMENT '建档时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='富文本模板';
