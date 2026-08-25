CREATE TABLE `mkt_activity_write_off_linshi` (
  `pkey` varchar(60) NOT NULL,
  `name` varchar(200) DEFAULT NULL COMMENT '活动名称',
  `member` int(11) DEFAULT NULL COMMENT '会员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`),
  KEY `idx_name_member` (`name`,`member`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT = '预计配送时间配置';
