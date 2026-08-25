SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for h5_goods
-- ----------------------------
DROP TABLE IF EXISTS `h5_goods`;
CREATE TABLE `h5_goods`  (
  `pkey` int(11) NOT NULL,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标题',
  `photo1` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '照片1',
  `photo2` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '照片2',
  `photo3` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '照片2',
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `content` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '正文',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `xs_num` int(11) DEFAULT NULL COMMENT '销售数量',
  `price_old` decimal(11, 2) DEFAULT NULL COMMENT '原价',
  `noon_price` decimal(11, 2) DEFAULT NULL COMMENT '中午场价格',
  `night_price` decimal(11, 2) DEFAULT NULL COMMENT '晚上场价格',
  `lock_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '包厢门锁ID',
  `sort` int(11) DEFAULT 0,
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `levela` tinyint(4) DEFAULT NULL,
  `levelb` tinyint(4) DEFAULT NULL,
  `levelc` tinyint(4) DEFAULT NULL,
  `correlation` int(11) DEFAULT NULL COMMENT '关联主键',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '公司',
  `enabled` tinyint(1) DEFAULT NULL COMMENT '启用标志',
  `id_del` tinyint(1) DEFAULT NULL COMMENT '是否已删除',
  `update_time` datetime(0) DEFAULT NULL COMMENT '最后更新时间',
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `created_by` int(11) DEFAULT NULL COMMENT '建档员',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for h5_goods_space
-- ----------------------------
DROP TABLE IF EXISTS `h5_goods_space`;
CREATE TABLE `h5_goods_space`  (
  `pkey` int(11) NOT NULL,
  `goods` int(11) DEFAULT NULL COMMENT '商品',
  `space` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规格',
  `photo1` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '图片',
  `weight` decimal(11, 2) DEFAULT NULL COMMENT '毛重',
  `price_old` decimal(11, 2) DEFAULT NULL COMMENT '原价',
  `price` decimal(11, 2) DEFAULT NULL COMMENT '价格',
  `kc_num` int(11) DEFAULT NULL COMMENT '库存数量',
  `xs_num` int(11) DEFAULT NULL COMMENT '销售数量',
  `box_sd` datetime(0) DEFAULT NULL COMMENT '门锁密码时间-开始',
  `box_ed` datetime(0) DEFAULT NULL COMMENT '门锁密码时间-结束',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for h5_order
-- ----------------------------
DROP TABLE IF EXISTS `h5_order`;
CREATE TABLE `h5_order`  (
  `pkey` int(11) NOT NULL,
  `kc_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单号',
  `user_key` int(11) NOT NULL COMMENT '用户',
  `status` tinyint(4) NOT NULL COMMENT '状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废',
  `pay_type` tinyint(4) NOT NULL,
  `amto` decimal(11, 2) NOT NULL COMMENT '订单价格',
  `amtn` decimal(11, 2) DEFAULT NULL COMMENT '支付金额',
  `box_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '包厢名称',
  `box_time` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '包厢时间',
  `box_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '包厢门锁密码',
  `lock_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '包厢门锁ID',
  `box_sd` datetime(0) DEFAULT NULL COMMENT '门锁密码时间-开始',
  `box_ed` datetime(0) DEFAULT NULL COMMENT '门锁密码时间-结束',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `goods` int(11) DEFAULT NULL COMMENT '商品',
  `space` int(11) DEFAULT NULL COMMENT '规格',
  `photo1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商品图片',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '市场',
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司',
  `created_time` datetime(0) NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for h5_user
-- ----------------------------
DROP TABLE IF EXISTS `h5_user`;
CREATE TABLE `h5_user`  (
  `pkey` int(11) NOT NULL,
  `userid` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '手机',
  `money` decimal(16, 2) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `created_time` datetime(0) DEFAULT NULL COMMENT '建档时间',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `company` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '公司',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
