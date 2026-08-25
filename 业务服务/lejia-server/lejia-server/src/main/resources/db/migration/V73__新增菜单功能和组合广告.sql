DROP TABLE IF EXISTS `mkt_function_menu_config`;
CREATE TABLE `mkt_function_menu_config` (
   `pkey` int comment 'pkey',
   `name` varchar(64) comment '名称',
   `photos` varchar(255) comment '图片Url',
   `url_type` tinyint(4) comment '点击效果',
   `obj_key` varchar(255) comment '内容',
   `sort` int comment '排序',
   `targer_keys` varchar(255) comment '标签',
   `enabled` bit comment '状态',
   `visible_range` int comment '用户可见范围',
   `updated_time` datetime comment '修改时间',
   `created_time` datetime comment '建立时间',
   `farmer`  varchar(64) comment '市场',
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='功能菜单配置';

ALTER TABLE mkt_advert ADD `location_type`  tinyint(4) DEFAULT NULL COMMENT '广告位置';


ALTER TABLE `mkt_goods_main` ADD COLUMN `sys_two_gtype` int(11) DEFAULT NULL comment '关联运营端二级分类' AFTER `gtype`;

