CREATE TABLE `sys_app_function_group` (
   `pkey` varchar(40) comment '',
   `domainid` varchar(40) comment '所属域',
   `name` varchar(40) comment '名称',
   `group_name` varchar(40) comment '应用分组',
   `sort` int comment '排序',
  KEY (`domainid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='功能组';

ALTER TABLE `sys_app_function` 
ADD COLUMN `func_group` varchar(40) COMMENT '权限组' AFTER `group_name`,
ADD COLUMN `sort` int COMMENT '排序' AFTER `func_group`,
ADD INDEX `idx_domain`(`domainid`);

REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `parentid`, `type`, `sort`, `domainid`, `enable`) VALUES ('tf_func_group', '权限组管理', 'tfManager', 'tf_sys', 1, 5, 'tfcloud', 1);