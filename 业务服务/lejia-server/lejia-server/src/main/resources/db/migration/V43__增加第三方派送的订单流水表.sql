drop table if exists `mkt_order_delivery_msg`;
CREATE TABLE `mkt_order_delivery_msg` (
   `pkey` varchar(64) comment 'pkey',
   `order_no` varchar(64) comment '系统订单号',
   `third_party_order_no` varchar(64) comment '第三方的配送订单号',
   `shop_id` varchar(64) comment '店铺ID',
   `farmer` varchar(255) comment '市场',
   `company` varchar(255) comment '公司',
   `created_time` datetime comment '建档时间',
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='第三发配送单号流水';