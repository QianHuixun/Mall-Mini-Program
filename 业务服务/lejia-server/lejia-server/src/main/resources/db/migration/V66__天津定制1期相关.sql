DROP TABLE IF EXISTS `mkt_supplier`;
CREATE TABLE `mkt_supplier` (
    `pkey` int comment '主键',
    `name` varchar(100) comment '供应商名称',
    `mobile` varchar(20) comment '手机号码',
    `start_business_time` varchar(10) comment '开始营业时间',
    `end_business_time` varchar(10) comment '结束营业时间',
    `allowed_pickup` tinyint(4) comment '是否支持自提',
    `express_sender` varchar(20) comment '快递寄件人',
    `express_mobile` varchar(20) comment '快递寄件手机号',
    `express_pro` varchar(50) comment '快递寄件省',
    `express_city` varchar(50) comment '快递寄件市',
    `express_area` varchar(50) comment '快递寄件区',
    `express_address` varchar(100) comment '快递寄件地址',
    `sf_monthly_card` varchar(20) comment '顺丰月结卡号',
    `sf_app_id` varchar(20) comment '顺丰寄件appId',
    `sf_sk` varchar(50) comment '顺丰寄件sk',
    `openid1` varchar(40) comment 'openid1',
    `enabled` tinyint(4) comment '启用标志',
    `is_del` tinyint(4) comment '是否已删除',
    `farmer` varchar(40) comment '市场',
    `company` varchar(40) comment '公司',
    `update_time` datetime comment '修改时间',
    `created_time` datetime comment '建档时间',
    `update_by` int comment '建档员',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='供应商';


DROP TABLE IF EXISTS `mkt_supplier_pickup_location`;
CREATE TABLE `mkt_supplier_pickup_location` (
    `pkey` int comment '主键',
    `supplier` int comment '供应商主键',
    `address` varchar(200) comment '自提点地址',
    `longitude` decimal(11,6) comment '经度',
    `latitude` decimal(11,6) comment '纬度',
    `update_time` datetime comment '修改时间',
    `created_time` datetime comment '建档时间',
    `update_by` int comment '建档员',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='供应商自提点';


DROP TABLE IF EXISTS `sys_farmer_pickup_location`;
CREATE TABLE `sys_farmer_pickup_location` (
    `pkey` int comment '主键',
    `farmer` varchar(40) comment '市场主键',
    `address` varchar(200) comment '自提点地址',
    `longitude` decimal(11,6) comment '经度',
    `latitude` decimal(11,6) comment '纬度',
    `update_time` datetime comment '修改时间',
    `created_time` datetime comment '建档时间',
    `update_by` int comment '建档员',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='市场自提点';


DROP TABLE IF EXISTS `mkt_order_express`;
CREATE TABLE `mkt_order_express` (
    `pkey` bigint comment '主键',
    `express_no` varchar(20) comment '物流单号',
    `order_pkey` int comment '订单主键',
    `kc_code` varchar(20) comment '订单号',
    `express_company` tinyint comment '快递公司',
    `waybill_no` varchar(20) comment '快递公司运单号',
    `pickup_time` datetime comment '上门取件时间',
    `send_content` varchar(128) comment '寄托物内容',
    `send_num` int comment '寄托物数量',
    `pickup_courier_mobile` varchar(20) comment '取件快递员手机号',
    `latest_pickup_time` datetime comment '最晚上门时间',
    `sf_monthly_card` varchar(20) comment '顺丰月结卡号',
    `status` tinyint(4) comment '状态',
    `error_msg` varchar(100) comment '异常描述',
    `farmer` varchar(40) comment '市场',
    `company` varchar(40) comment '公司',
    `created_time` datetime comment '建档时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='订单物流表';


DROP TABLE IF EXISTS `mkt_order_express_route`;
CREATE TABLE `mkt_order_express_route` (
    `pkey` bigint comment '主键',
    `order_express` bigint comment '物流单主键',
    `express_no` varchar(20) comment '物流单号',
    `order_pkey` int comment '订单主键',
    `kc_code` varchar(20) comment '订单号',
    `mail_no` varchar(20) comment '快递运单号',
    `third_id` varchar(50) comment '快递公司路由节点编号',
    `time` datetime comment '路由节点产生的时间',
    `address` varchar(100) comment '路由节点发生的城市',
    `op_code` varchar(20) comment '路由节点操作码',
    `description` varchar(200) comment '路由节点具体描述',
    `created_time` datetime comment '建档时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='订单物流节点表';


ALTER TABLE `mkt_goods` ADD COLUMN `supplier` int comment '供应商主键' AFTER `vendor`;

ALTER TABLE `mkt_addr` ADD COLUMN `pro` varchar(40) comment '省' AFTER `type`;
ALTER TABLE `mkt_addr` ADD COLUMN `city` varchar(40) comment '市' AFTER `pro`;
ALTER TABLE `mkt_addr` ADD COLUMN `area` varchar(40) comment '区' AFTER `city`;

ALTER TABLE `mkt_order_desc` ADD COLUMN `pro` varchar(40) comment '省' AFTER `kd_code`;
ALTER TABLE `mkt_order_desc` ADD COLUMN `city` varchar(40) comment '市' AFTER `pro`;
ALTER TABLE `mkt_order_desc` ADD COLUMN `area` varchar(40) comment '区' AFTER `city`;

ALTER TABLE `mkt_order` ADD COLUMN `supplier` int comment '供应商' AFTER `box_ed`;
