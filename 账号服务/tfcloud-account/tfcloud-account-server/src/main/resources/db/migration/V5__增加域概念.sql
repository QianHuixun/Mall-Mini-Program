
ALTER TABLE `sys_user`
ADD `regist_from_domain` varchar(40) COMMENT '来源域' after `regist_from_app`;

ALTER TABLE `sys_app_function`
MODIFY COLUMN `appid`  varchar(40) NULL COMMENT '应用ID' AFTER `pkey`,
ADD `domainid` varchar(40) COMMENT '域ID' after `appid`;

ALTER TABLE `sys_app_role`
MODIFY COLUMN `appid`  varchar(40) NULL COMMENT '应用ID' AFTER `pkey`,
ADD `domainid` varchar(40) COMMENT '域ID' after `appid`;

ALTER TABLE `sys_org`
MODIFY COLUMN `appid`  varchar(40) NULL COMMENT '应用ID',
ADD `domainid` varchar(40) COMMENT '域ID' after `appid`;

ALTER TABLE `sys_org_dept`
MODIFY COLUMN `appid`  varchar(40) NULL COMMENT '应用ID',
ADD `domainid` varchar(40) COMMENT '域ID' after `appid`;

ALTER TABLE `sys_application`
ADD `domainid` varchar(40) COMMENT '域ID' after `pkey`;

DROP TABLE IF EXISTS `sys_domain`;
CREATE TABLE `sys_domain` (
  `pkey` VARCHAR(40) NOT NULL COMMENT '主键',
  `name` VARCHAR(40) NOT NULL COMMENT '名称',
  PRIMARY KEY (`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sys_user_app`;
CREATE TABLE `sys_user_app` (
  `pkey` bigint(20) NOT NULL COMMENT '主键',
  `user_key` bigint(20) NOT NULL COMMENT '用户',
  `domainid` VARCHAR(40) DEFAULT NULL COMMENT '域',
  `appid` VARCHAR(40) DEFAULT NULL COMMENT '应用',
  PRIMARY KEY (`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;



