DROP TABLE IF EXISTS `mkt_tag`;
CREATE TABLE `mkt_tag` (
    `pkey` int comment '主键',
    `name` varchar(50) comment '名称',
    `description` varchar(200) comment '描述',
    `created_time` datetime comment '建档时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='标签表';


DROP TABLE IF EXISTS `mkt_member_tag`;
CREATE TABLE `mkt_member_tag` (
    `pkey` varchar(50) comment '主键',
    `member` int comment '会员主键',
    `tag` int comment '标签主键',
    `created_time` datetime comment '建档时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='会员标签关联表';


DROP TABLE IF EXISTS `mkt_tag_visible`;
CREATE TABLE `mkt_tag_visible` (
    `pkey` varchar(50) comment '主键',
    `type` tinyint comment '类型',
    `target` int comment '对象主键',
    `tag` int comment '标签主键',
    `created_time` datetime comment '建档时间',
    `ascription` int comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='标签可见关联表';


ALTER TABLE `mkt_card` ADD COLUMN `visible_range` tinyint comment '用户可见范围' AFTER `card_type`;
UPDATE `mkt_card` SET `visible_range` = 1;

ALTER TABLE `mkt_activity` ADD COLUMN `visible_range` tinyint comment '用户可见范围' AFTER `distribute_type`;
UPDATE `mkt_activity` SET `visible_range` = 1;

ALTER TABLE `mkt_goods` ADD COLUMN `visible_range` tinyint comment '用户可见范围' AFTER `is_postage`;
UPDATE `mkt_goods` SET `visible_range` = 1;


ALTER TABLE `mkt_activity` ADD COLUMN `welfare_photo` varchar(200) comment '会员福利展示图' AFTER `visible_range`;


ALTER TABLE `mkt_verifier` RENAME `mkt_manager`;

DROP TABLE IF EXISTS `mkt_manager_role`;
CREATE TABLE `mkt_manager_role` (
    `pkey` varchar(255) comment '主键',
    `manager` int comment '管理员主键',
    `role` tinyint comment '角色',
    `farmer` varchar(40) not null comment '市场',
    `company` varchar(40) not null comment '公司',
    `created_time` datetime not null comment '建档时间',
    `ascription` int not null comment '归属主键',
    PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='管理员角色表';
