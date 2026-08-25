DROP TABLE IF EXISTS `mkt_order_code`;
CREATE TABLE `mkt_order_code`  (
  `pkey` int(11) NOT NULL,
  `order_pkey` int(11) NOT NULL,
  `kc_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '璁㈠崟鍙·',
  `created_time` datetime(0) NOT NULL COMMENT '寤烘。鏃堕棿',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '鑰佺殑code' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;