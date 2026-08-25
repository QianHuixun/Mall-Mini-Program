DROP TABLE IF EXISTS `xasz_association`;
CREATE TABLE `xasz_association`  (
  `pkey` int(11) NOT NULL,
  `farmer` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'saas市场主键',
  `market` int(11) DEFAULT NULL COMMENT '云农贸市场主键',
  `update_time` datetime(0) NOT NULL COMMENT '最后更新时间',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '心安食足关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `mkt_problem`;
CREATE TABLE `mkt_problem`  (
  `pkey` int(11) NOT NULL,
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '问题名称',
  `type` int(11) DEFAULT NULL COMMENT '问题分类',
  `answer` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回答',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '启用标志',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `is_default` tinyint(1) DEFAULT NULL COMMENT '默认问题',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '常见问题' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mkt_problem_type
-- ----------------------------
DROP TABLE IF EXISTS `mkt_problem_type`;
CREATE TABLE `mkt_problem_type`  (
  `pkey` int(11) NOT NULL,
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '常见问题分类' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
