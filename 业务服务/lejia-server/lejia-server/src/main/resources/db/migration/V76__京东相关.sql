ALTER TABLE mkt_addr ADD `town` VARCHAR(40) DEFAULT NULL COMMENT '街道' AFTER area;
ALTER TABLE mkt_gwc MODIFY COLUMN goods INT(11) NULL COMMENT '商品';
ALTER TABLE mkt_gwc MODIFY COLUMN space INT(11) NULL COMMENT '规格';
ALTER TABLE mkt_gwc ADD `is_jd` tinyint(1) DEFAULT NULL COMMENT '是京东商品' AFTER association_name;
ALTER TABLE mkt_gwc ADD `sku_id` bigint DEFAULT NULL COMMENT '商品主键' AFTER is_jd;
ALTER TABLE mkt_gwc ADD `spu_id` bigint DEFAULT NULL COMMENT '商品spu' AFTER sku_id;
ALTER TABLE mkt_order_desc ADD `town` VARCHAR(40) DEFAULT NULL COMMENT '街道' AFTER area;
ALTER TABLE mkt_order_line MODIFY COLUMN goods BIGINT NULL COMMENT '商品';
ALTER TABLE mkt_order_line MODIFY COLUMN space BIGINT NULL COMMENT '规格';
ALTER TABLE mkt_order ADD pay_detail_money decimal(11,2) DEFAULT NULL COMMENT '京东支付金额' AFTER confirm_time;
ALTER TABLE mkt_order ADD refund_jd decimal(11,2) DEFAULT NULL COMMENT '京东退款金额' AFTER pay_detail_money;
ALTER TABLE mkt_order_refund_line ADD refund_jd decimal(11,2) DEFAULT NULL COMMENT '京东退款金额' AFTER refund_point;



DROP TABLE IF EXISTS `jd_address`;
CREATE TABLE `jd_address`  (
  `area_id` bigint(20) NOT NULL COMMENT '区域ID',
  `area_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域名',
  `area_level` int(11) NULL DEFAULT NULL COMMENT '区域级别',
  `parent` bigint(20) NULL DEFAULT NULL COMMENT '上级区域ID',
  `client_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端区域名称（用于匹配）',
  PRIMARY KEY (`area_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '京东四级地址' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for jd_category
-- ----------------------------
DROP TABLE IF EXISTS `jd_category`;
CREATE TABLE `jd_category`  (
  `pkey` bigint(11) NOT NULL COMMENT '当前分类ID',
  `category_level` int(11) NULL DEFAULT NULL COMMENT '0：一级分类；1：二级分类；2：三级分类',
  `parent_id` bigint(11) NULL DEFAULT NULL COMMENT '父分类ID',
  `category_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前分类名称',
  `need_show` int(11) NULL DEFAULT NULL COMMENT '1：有效；0：无效',
  `order_sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for jd_goods
-- ----------------------------
DROP TABLE IF EXISTS `jd_goods`;
CREATE TABLE `jd_goods`  (
  `pkey` bigint(20) NOT NULL,
  `category` bigint(20) NULL DEFAULT NULL COMMENT '分类',
  `two_category` bigint(20) NULL DEFAULT NULL COMMENT '二级分类',
  `three_category` bigint(20) NULL DEFAULT NULL COMMENT '三级分类',
  `category_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `two_category_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '二级分类名称',
  `three_category_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '三级分类名称',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `tag` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签',
  `photo1` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '照片1',
  `photo2` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '照片2',
  `photo3` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '照片2',
  `weight` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sale_unit` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '售卖单位',
  `seo_model` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格型号',
  `serial_number` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标准编号',
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `content` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正文',
  `content2` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '富文本框商品详情',
  `introduce` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品详情',
  `introduce_pc` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'PC商品详情',
  `introduce_app` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '移动端商品详情',
  `introduce_wechat` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '微信商品详情',
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `send_date` date NULL DEFAULT NULL COMMENT '发货日期',
  `view_count` int(11) NULL DEFAULT NULL,
  `xs_num` int(11) NULL DEFAULT NULL COMMENT '销售数量',
  `purchase_num` int(11) NULL DEFAULT NULL COMMENT '限购数量',
  `price` decimal(11, 2) NULL DEFAULT NULL COMMENT '价格',
  `tax_price` decimal(11, 2) NULL DEFAULT NULL COMMENT '税额',
  `jd_price` decimal(11, 2) NULL DEFAULT NULL COMMENT '京东价，仅供参考',
  `sale_price` decimal(11, 2) NULL DEFAULT NULL COMMENT '京东销售价，实际下单价格以此为准',
  `naked_price` decimal(11, 2) NULL DEFAULT NULL COMMENT '未税价，当此参数返回null或者返回值小于0时，表示暂无报价，建议客户前台不上架该SKU',
  `tax_rate_percentage` decimal(11, 2) NULL DEFAULT NULL COMMENT '税率',
  `has_promotion` tinyint(1) NULL DEFAULT NULL COMMENT '当前商品是否含有促销活动，当返回true时，需要配合【商品促销信息接口】查询对应的商品促销限购数量',
  `promotion_type` int(11) NULL DEFAULT NULL COMMENT '促销类型；1:到手价 2:一口价',
  `original_price` decimal(11, 2) NULL DEFAULT NULL COMMENT '促销原价',
  `limited_num` int(11) NULL DEFAULT NULL COMMENT 'cid下总可购买次数',
  `remain_num` int(11) NULL DEFAULT NULL COMMENT 'cid下剩余可购买次数',
  `stock_state` int(11) NULL DEFAULT NULL COMMENT '库存状态,33:有货',
  `sku_state` int(11) NULL DEFAULT NULL COMMENT '主站上下架状态 (1上架 0下架)',
  `spu_id` bigint(20) NULL DEFAULT NULL COMMENT '主商品ID',
  `spu_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主商品名称',
  `visible_range` tinyint(4) NULL DEFAULT NULL COMMENT '用户可见范围',
  `sort` int(11) NULL DEFAULT 0,
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `farmer` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '市场',
  `enabled` tinyint(1) NULL DEFAULT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NULL DEFAULT NULL COMMENT '是否已删除',
  `update_time` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  `space1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space3` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space4` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space5` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space6` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space7` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space8` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space9` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space10` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `biz_pool_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品池',
  PRIMARY KEY (`pkey`) USING BTREE,
  INDEX `gtype`(`category`) USING BTREE,
  INDEX `goodsMain`(`two_category`) USING BTREE,
  INDEX `title`(`title`) USING BTREE,
  INDEX `idx_farmer`(`farmer`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '京东商品' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for jd_goods_service
-- ----------------------------
DROP TABLE IF EXISTS `jd_goods_service`;
CREATE TABLE `jd_goods_service`  (
  `pkey` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务内容',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for jd_goods_space
-- ----------------------------
DROP TABLE IF EXISTS `jd_goods_space`;
CREATE TABLE `jd_goods_space`  (
  `pkey` bigint(20) NOT NULL,
  `space_value1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value3` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value4` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value5` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value6` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value7` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value8` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value9` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `space_value10` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
  `biz_pool_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品池',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for jd_order_correlation
-- ----------------------------
DROP TABLE IF EXISTS `jd_order_correlation`;
CREATE TABLE `jd_order_correlation`  (
  `pkey` int(11) NULL DEFAULT NULL COMMENT 'mkt_order的主键',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '订单状态,是否作废',
  `order_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'mkt_order的编号',
  `jd_code` bigint(20) NULL DEFAULT NULL COMMENT '京东的订单编号',
  `parent_order` bigint(20) NULL DEFAULT NULL COMMENT '京东拆掉父类主键(该值为空代表没有拆单)',
  `created_time` datetime NULL DEFAULT NULL COMMENT '建档时间'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;