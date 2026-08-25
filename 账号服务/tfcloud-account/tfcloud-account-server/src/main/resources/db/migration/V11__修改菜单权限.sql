DROP TABLE IF EXISTS `sys_role_menu`;

ALTER TABLE `sys_app_role`
DROP COLUMN `appid`;

ALTER TABLE `sys_app_function`
DROP COLUMN `appid`,
DROP COLUMN `group_name`;

DROP TABLE IF EXISTS `sys_app_funcgroup`;
CREATE TABLE `sys_app_funcgroup` (
  `pkey` VARCHAR(40) NOT NULL COMMENT '主键',
  `parentid` VARCHAR(40) DEFAULT NULL COMMENT '上级ID',
  `name` VARCHAR(40) DEFAULT NULL COMMENT '组名',
  `domainid` VARCHAR(40) DEFAULT NULL COMMENT '域',
  PRIMARY KEY (`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sys_app_funcgroup_item`;
CREATE TABLE `sys_app_funcgroup_item` (
  `groupid` VARCHAR(40) NOT NULL COMMENT '组主键',
  `id` int(11) NOT NULL COMMENT '顺序号',
  `func` VARCHAR(40) DEFAULT NULL COMMENT '权限',
  `func_name` VARCHAR(40) DEFAULT NULL COMMENT '权限名称',
  PRIMARY KEY (`groupid`, `id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

ALTER TABLE `sys_app_menu`
   DROP COLUMN `created_time`,
   DROP COLUMN `updated_time`,
   DROP COLUMN `created_by`,
   DROP COLUMN `updated_by`,
   ADD `domainid` VARCHAR(40) DEFAULT NULL COMMENT '域';
update sys_app_menu u,sys_application a set u.domainid = a.domainid where u.appid = a.pkey;
