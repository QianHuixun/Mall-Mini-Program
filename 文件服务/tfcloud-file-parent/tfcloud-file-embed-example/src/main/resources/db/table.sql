DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record` (
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5',
  `size` bigint NOT NULL COMMENT '文件大小',
  `content_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`md5`, `size`)
) ;

DROP TABLE IF EXISTS `file_record_thumb`;
CREATE TABLE `file_record_thumb` (
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5',
  `size` bigint NOT NULL COMMENT '文件大小',
  `thumb` int NOT NULL COMMENT '缩略图最长边像素大小',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`md5`, `size`, `thumb`)
) ;

DROP TABLE IF EXISTS `file_ref`;
CREATE TABLE `file_ref` (
  `pkey` bigint NOT NULL COMMENT '主键',
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5',
  `size` bigint NOT NULL COMMENT '文件大小',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '原文件名',
  `ext_name` VARCHAR(40) DEFAULT NULL COMMENT '扩展名',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `appid` VARCHAR(40) DEFAULT NULL COMMENT '来源应用',
  `userkey` bigint DEFAULT NULL COMMENT '来源用户',
  `ref_url` VARCHAR(255) DEFAULT NULL COMMENT '来源引用地址',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`pkey`)
) ;


DROP TABLE IF EXISTS `file_ref_access`;
CREATE TABLE `file_ref_access` (
  `pkey` bigint NOT NULL COMMENT '主键',
  `count` bigint NOT NULL COMMENT '访问次数',
  `last_access` DATETIME DEFAULT NULL COMMENT '最近访问时间',
  PRIMARY KEY (`pkey`)
) ;



ALTER TABLE `file_ref`
ADD `ref_count` int DEFAULT NULL COMMENT '引用数量' after `ref_url`;

Update `file_ref` set `ref_count` = 1, `ref_url` = '旧记录';

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
  PRIMARY KEY(`pkey`)
);
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
  PRIMARY KEY(`pkey`)
);


ALTER TABLE `file_record`
ADD `width` int DEFAULT NULL COMMENT '宽' after `content_type`;
ALTER TABLE `file_record`
ADD `height` int DEFAULT NULL COMMENT '高' after `width`;