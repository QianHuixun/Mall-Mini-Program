CREATE TABLE `file_ref2` (
   `pkey` bigint not null COMMENT '',
   `md5` varchar(32) not null COMMENT '文件内容的md5',
   `size` bigint not null default 0 COMMENT '文件大小',
   `type` tinyint COMMENT '类型',
   `file_name` varchar(255) COMMENT '原文件名',
   `ext_name` varchar(40) COMMENT '扩展名',
   `title` varchar(255) COMMENT '标题',
   `memo` varchar(255) COMMENT '备注',
   `appid` varchar(40) COMMENT '来源应用',
   `userkey` bigint COMMENT '来源用户',
   `created_time` datetime COMMENT '创建时间',
  KEY `idx_time` (`created_time`),
  KEY `idx_md5` (`md5`,`size`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='';
CREATE TABLE `file_ref_link` (
   `pkey` bigint not null COMMENT '',
   `domain` varchar(40) not null COMMENT '域',
   `db` varchar(100) not null COMMENT '数据库',
   `table_name` varchar(100) not null COMMENT '表',
   `data_pkey` varchar(100) not null COMMENT '主键或hash',
   `file_pkey` bigint not null default 0 COMMENT '文件Pkey',
   `size` bigint not null default 0 COMMENT '文件大小',
   `org` varchar(40) COMMENT '机构/公司',
   `dept` varchar(40) COMMENT '部门/市场',
   `created_time` datetime COMMENT '创建时间',
  KEY `idx_table` (`domain`,`db`,`table_name`),
  KEY `idx_org` (`domain`,`org`),
  KEY `idx_dept` (`domain`,`dept`),
  KEY `idx_time` (`created_time`),
  KEY `idx_file` (`file_pkey`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='';

ALTER TABLE `file_record` ADD INDEX `idx_created_time` (`created_time`) USING BTREE;
