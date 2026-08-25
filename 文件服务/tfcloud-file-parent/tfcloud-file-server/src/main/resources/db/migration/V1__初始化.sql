DROP TABLE IF EXISTS `file_record`;
CREATE TABLE `file_record` (
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5',
  `size` bigint(20) NOT NULL COMMENT '文件大小',
  `content_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`md5`, `size`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `file_record_thumb`;
CREATE TABLE `file_record_thumb` (
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5',
  `size` bigint(20) NOT NULL COMMENT '文件大小',
  `thumb` int(11) NOT NULL COMMENT '缩略图最长边像素大小',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`md5`, `size`, `thumb`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `file_ref`;
CREATE TABLE `file_ref` (
  `pkey` bigint(20) NOT NULL COMMENT '主键',
  `md5` VARCHAR(32) NOT NULL COMMENT '文件MD5',
  `size` bigint(20) NOT NULL COMMENT '文件大小',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '原文件名',
  `ext_name` VARCHAR(40) DEFAULT NULL COMMENT '扩展名',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `appid` VARCHAR(40) DEFAULT NULL COMMENT '来源应用',
  `userkey` bigint(20) DEFAULT NULL COMMENT '来源用户',
  `ref_url` VARCHAR(255) DEFAULT NULL COMMENT '来源引用地址',
  `created_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `file_ref_access`;
CREATE TABLE `file_ref_access` (
  `pkey` bigint(20) NOT NULL COMMENT '主键',
  `count` bigint(20) NOT NULL COMMENT '访问次数',
  `last_access` DATETIME DEFAULT NULL COMMENT '最近访问时间',
  PRIMARY KEY (`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
