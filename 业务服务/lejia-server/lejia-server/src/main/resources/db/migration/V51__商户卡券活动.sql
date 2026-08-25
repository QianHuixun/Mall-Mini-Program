ALTER TABLE `mkt_card` ADD COLUMN `user_vendor` int(11) DEFAULT NULL COMMENT '限制商户'  AFTER `user_farmer`;
ALTER TABLE `mkt_member_card` ADD COLUMN `user_vendor` int(11) DEFAULT NULL COMMENT '限制商户'  AFTER `user_farmer`;


DROP TABLE IF EXISTS `mkt_activity_linshi`;
CREATE TABLE `mkt_activity_linshi`  (
  `pkey` int(11) NOT NULL,
  `vendor` int(11) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `card` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
