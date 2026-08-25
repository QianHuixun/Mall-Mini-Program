DROP TABLE IF EXISTS `mkt_member_index_advert`;
CREATE TABLE `mkt_member_index_advert`  (
  `pkey` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `member_key` int(11) DEFAULT NULL,
  `index_advert` int(11) DEFAULT NULL,
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '会员不再弹窗记录' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;