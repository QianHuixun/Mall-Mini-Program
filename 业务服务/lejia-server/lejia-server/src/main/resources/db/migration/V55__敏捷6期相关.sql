CREATE TABLE `mkt_operating_statistics`  (
  `pkey` bigint(20) NOT NULL,
  `farmer` varchar(40) NULL DEFAULT NULL COMMENT '市场主键',
  `yester_time` varchar(10) NULL DEFAULT NULL COMMENT '日期',
  `acc_count` int(11) NOT NULL COMMENT '访问人数',
  `member_pay_num` int(11) NOT NULL COMMENT '支付人数',
  `order_count` int(11) NOT NULL COMMENT '成交订单',
  `amto` decimal(11,2) NULL DEFAULT NULL COMMENT '商品金额',
  `postage` decimal(11,2) NULL DEFAULT NULL COMMENT '配送费',
  `card_amt` decimal(11,2) NULL DEFAULT NULL COMMENT '优惠金额',
  `refund_amt` decimal(11,2) NULL DEFAULT NULL COMMENT '退款金额',
  `revenue_amt` decimal(11,2) NULL DEFAULT NULL COMMENT '营收金额',
  `ascription` int(11) NULL DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) ,
  KEY `idx_time` (`yester_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 市场核销人
CREATE TABLE `mkt_verifier` (
    `pkey` int(11) NOT NULL COMMENT '主键',
    `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
    `name` varchar(30) DEFAULT NULL COMMENT '名称',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '是否启用',
    `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
    `company` varchar(40) DEFAULT NULL COMMENT '公司',
    `created_time` datetime NOT NULL COMMENT '建档时间',
    `ascription` int(11) NOT NULL COMMENT '归属主键',
    PRIMARY KEY (`pkey`),
    KEY `idx_farmer` (`farmer`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT = '市场核销人';

-- 卡券活动
CREATE TABLE `mkt_activity` (
    `pkey` int(11) NOT NULL COMMENT '主键',
    `name` varchar(50) DEFAULT NULL COMMENT '名称',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `photo` varchar(200) DEFAULT NULL COMMENT '图片',
    `price` decimal(11,2) DEFAULT NULL COMMENT '价格',
    `coupon_num` int(11) DEFAULT NULL COMMENT '卡券数量',
    `num` int(11) DEFAULT NULL COMMENT '套餐总数',
    `issued_num` int(11) DEFAULT NULL COMMENT '已发放数量',
    `limit_member_times` int(11) DEFAULT NULL COMMENT '限制用户参与次数',
    `limit_daily_num` int(11) DEFAULT NULL COMMENT '每日限量',
    `limit_daily_card_num` int(11) DEFAULT NULL COMMENT '限制优惠券每日张数',
    `limit_daily_gift_num` int(11) DEFAULT NULL COMMENT '限制礼品券每日张数',
    `enabled` tinyint(4) DEFAULT NULL COMMENT '是否启用',
    `farmer` varchar(40) NOT NULL COMMENT '市场',
    `company` varchar(40) NOT NULL COMMENT '公司',
    `updated_time` datetime NOT NULL COMMENT '修改时间',
    `updated_by` int(11) DEFAULT NULL COMMENT '修改人',
    `created_time` datetime NOT NULL COMMENT '建档时间',
    `created_by` int(11) DEFAULT NULL COMMENT '建档人',
    `ascription` int(11) NOT NULL COMMENT '归属主键',
    PRIMARY KEY (`pkey`),
    KEY `idx_farmer` (`farmer`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT = '卡券活动表';

CREATE TABLE `mkt_activity_coupon` (
    `activity` int(11) NOT NULL COMMENT '卡券活动',
    `coupon_type` tinyint(4) NOT NULL COMMENT '卡券类型',
    `coupon` int(11) NOT NULL COMMENT '卡券主键',
    `num` int(11) DEFAULT NULL COMMENT '张数',
    PRIMARY KEY (`activity`,`coupon_type`,`coupon`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT = '卡券活动与卡券关联表';

CREATE TABLE `mkt_member_activity` (
    `pkey` int(11) NOT NULL COMMENT '主键',
    `member` int(11) DEFAULT NULL COMMENT '会员',
    `activity` int(11) DEFAULT NULL COMMENT '卡券活动',
    `kc_code` varchar(30) DEFAULT NULL COMMENT '订单号',
    `amt` decimal(11,2) DEFAULT NULL COMMENT '金额',
    `status` tinyint(4) NOT NULL COMMENT '状态',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `farmer` varchar(40) NOT NULL COMMENT '市场',
    `company` varchar(40) NOT NULL COMMENT '公司',
    `created_time` datetime NOT NULL COMMENT '建档时间',
    `ascription` int(11) NOT NULL COMMENT '归属主键',
    PRIMARY KEY (`pkey`),
    KEY `idx_farmer` (`farmer`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT = '会员卡券活动参与表';

ALTER TABLE `mkt_member_card` ADD COLUMN `activity` int(11) DEFAULT NULL COMMENT '卡券活动' AFTER `card`;
ALTER TABLE `mkt_member_gift` ADD COLUMN `activity` int(11) DEFAULT NULL COMMENT '卡券活动' AFTER `gift`;
