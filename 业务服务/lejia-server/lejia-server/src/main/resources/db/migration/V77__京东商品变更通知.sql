DROP TABLE IF EXISTS `jd_goods_upd_notice`;
CREATE TABLE `jd_goods_upd_notice` (
    `pkey` bigint comment '主键',
    `type` tinyint comment '变更类型',
    `jd_goods` bigint comment '京东skuid',
    `description` varchar(200) comment '说明',
    `farmer` varchar(255) comment '市场',
    `created_time` datetime comment '创建时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='京东商品变更通知记录';
