DROP TABLE IF EXISTS `account_site`;
DROP TABLE IF EXISTS `account_site_apps`;
DROP TABLE IF EXISTS `account_test`;
DROP TABLE IF EXISTS `test_entity`;
DROP TABLE IF EXISTS `test`;
DROP TABLE IF EXISTS `sys_tenant`;
DROP TABLE IF EXISTS `sys_industry`;
DROP TABLE IF EXISTS `sys_host`;
DROP TABLE IF EXISTS `sys_app_instance`;
DROP TABLE IF EXISTS `sys_user_app`;
DROP TABLE IF EXISTS `sys_app_funcgroup_item`;
DROP TABLE IF EXISTS `sys_app_funcgroup`;

ALTER TABLE `sys_app_function` ADD COLUMN `group_name` varchar(40) comment '分组标识';

ALTER TABLE `sys_user` 
DROP COLUMN `regist_from_app`,
MODIFY COLUMN `actived` bit(1) NULL DEFAULT 1 COMMENT '用户是否激活',
DROP INDEX `idx_userid`,
DROP INDEX `idx_email`,
ADD UNIQUE INDEX `idx_userid`(`userid`) USING BTREE,
ADD INDEX `idx_domain_phone`(`regist_from_domain`, `bind_phone`);

ALTER TABLE `sys_org`
DROP COLUMN `enable`,
DROP COLUMN `shortname`,
DROP COLUMN `type`,
DROP COLUMN `appid`;

ALTER TABLE `sys_org_dept`
DROP COLUMN `enable`,
DROP COLUMN `remark`,
DROP COLUMN `type`,
DROP COLUMN `appid`;

ALTER TABLE `sys_app_role`
DROP COLUMN `org_type`;

CREATE TABLE `sys_model` (
   `pkey` varchar(40) comment '',
   `domainid` varchar(40) comment '所属域',
   `name` varchar(40) comment '名称',
   `sort` int comment '排序',
   `status` int comment '状态：OnLine，OffLine，Disabled',
   `def_enable` bit not null default 0 comment '是否默认开通',
   `def_show_menu` bit not null default 1 comment '模块下的菜单是否默认显示',
   `created_time` datetime comment '',
   `updated_time` datetime comment '',
  KEY (`domainid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='系统模块';
CREATE TABLE `sys_org_model` (
   `pkey` varchar(40) comment '主键',
   `domainid` varchar(40) comment '所属域',
   `orgid` varchar(40) comment '所属机构',
   `model_id` varchar(40) comment '模块',
   `enable` bit not null default 0 comment '是否开通',
   `created_time` datetime comment '',
   `updated_time` datetime comment '',
  KEY (`domainid`),
  KEY (`orgid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='公司模块配置';
CREATE TABLE `sys_dept_model` (
   `pkey` varchar(40) comment '主键',
   `domainid` varchar(40) comment '所属域',
   `orgid` varchar(40) comment '所属机构',
   `deptid` varchar(40) comment '所属部门',
   `model_id` varchar(40) comment '模块',
   `enable` bit not null default 0 comment '是否开通',
   `created_time` datetime comment '',
   `updated_time` datetime comment '',
  KEY (`domainid`),
  KEY (`orgid`),
  KEY (`deptid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='市场模块配置';
CREATE TABLE `sys_org_menu` (
   `pkey` varchar(40) comment '主键',
   `domainid` varchar(40) comment '所属域',
   `orgid` varchar(40) comment '所属机构',
   `menu_appid` varchar(40) comment '菜单所属应用',
   `menu_model` varchar(40) comment '菜单所属模块',
   `menu` varchar(40) comment '菜单',
   `enable` bit not null default 0 comment '是否启用',
   `created_time` datetime comment '',
   `updated_time` datetime comment '',
  KEY (`domainid`),
  KEY (`orgid`,`menu_appid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='公司菜单配置';
CREATE TABLE `sys_dept_menu` (
   `pkey` varchar(40) comment '主键',
   `domainid` varchar(40) comment '所属域',
   `orgid` varchar(40) comment '所属机构',
   `deptid` varchar(40) comment '所属部门',
   `menu_appid` varchar(40) comment '菜单所属应用',
   `menu_model` varchar(40) comment '菜单所属模块',
   `menu` varchar(40) comment '菜单',
   `enable` bit not null default 0 comment '是否启用',
   `created_time` datetime comment '',
   `updated_time` datetime comment '',
  KEY (`domainid`),
  KEY (`orgid`,`menu_appid`),
  KEY (`deptid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='市场菜单配置';
CREATE TABLE `sys_role_menu` (
   `pkey` varchar(40) comment '主键',
   `ownerid` varchar(40) comment '角色主键',
   `domainid` varchar(40) comment '所属域',
   `menu` varchar(40) comment '菜单',
   `accept` bit not null default 0 comment '允许还是禁用',
   `created_time` datetime comment '',
   `updated_time` datetime comment '',
  KEY (`domainid`),
  KEY (`ownerid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='角色菜单配置';
CREATE TABLE `sys_server_run_as_check` (
   `pkey` varchar(40) comment '应用',
   `func_key` varchar(40) comment '功能主键',
   `domainid` varchar(40) comment '所属域',
  KEY (`domainid`),
  PRIMARY KEY(`pkey`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 comment='server_run_as方式登录校验';


ALTER TABLE `sys_app_menu` ADD COLUMN `model_id` varchar(40) comment '所属模块';
ALTER TABLE `sys_app_menu` ADD COLUMN `enable` bit(1) NOT NULL DEFAULT 1 COMMENT '启用' AFTER `model_id`;
ALTER TABLE `sys_app_menu` ADD COLUMN `created_time` datetime comment '';
ALTER TABLE `sys_app_menu` ADD COLUMN `updated_time` datetime comment '';


REPLACE INTO sys_app_role (pkey,name,group_name,description,enable)
     VALUES ('sysAdmin', '系统管理员', '系统','拥有全部权限',1);
REPLACE INTO sys_app_role (pkey,name,group_name,description,enable)
     VALUES ('oauth2Admin', '微服务管理员', '系统','拥有微服务接入和配置的权限',1);
REPLACE INTO sys_app_role (pkey,name,group_name,description,enable)
     VALUES ('domainAdmin', '域管理员', '系统','拥有本域下所有应用的管理权限',1);
     

REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerApplication', '管理应用', '可以对微服务的应用进行新增修改配置');
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerRole', '角色管理', '可以对角色进行管理');
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerFunction', '权限管理', '可以对权限进行管理');
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerMenu', '菜单管理', '可以对菜单进行管理');
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('domainAdmin', '域管理员', '域管理员权限，默认拥有域下的所有菜单和权限');
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerUser', '用户管理', '可以对用户进行管理');
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerOrg', '机构管理', '管理公司市场等机构');
     
delete from sys_app_menu where domainid is null;
delete from sys_app_menu where appid = 'account';
update sys_app_menu a, sys_app_menu b  set a.parentid = b.`code` where a.parentid = b.pkey and a.parentid <> b.`code`;
update sys_app_menu a set a.pkey = a.`code`  where a.pkey <> a.`code`;

ALTER TABLE `sys_user_role` 
ADD COLUMN `domainid` varchar(40) NULL COMMENT '所属域',
ADD INDEX `idx_domainid`(`domainid`);
update sys_user_role u, sys_app_role r set u.domainid = r.domainid where u.`value` = r.pkey;


ALTER TABLE `sys_user_acl` 
ADD COLUMN `domainid` varchar(40) NULL COMMENT '所属域',
ADD INDEX `idx_domainid`(`domainid`);
update sys_user_acl u, sys_app_function r set u.domainid = r.domainid where u.`func_key` = r.pkey;


ALTER TABLE `sys_role_acl` 
ADD COLUMN `domainid` varchar(40) NULL COMMENT '所属域',
ADD INDEX `idx_domainid`(`domainid`);
update sys_role_acl u, sys_app_role r set u.domainid = r.domainid where u.`ownerid` = r.pkey;


-- 重置管理中心菜单
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_appAdmin', 'appAdmin', 'tfManager', 'tf_appAdmin', 'tf_server', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_file', '文件服务', 'tfManager', 'tf_file', NULL, 0, 5, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileAccessLogs', '访问日志', 'tfManager', 'tf_fileAccessLogs', 'tf_file', 1, 5, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileAccessReport', '访问报表', 'tfManager', 'tf_fileAccessReport', 'tf_file', 1, 6, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileBack', '备份管理', 'tfManager', 'tf_fileBack', 'tf_file', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileClear', '未引用文件清理', 'tfManager', 'tf_fileClear', 'tf_file', 1, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileLinkQuery', '关联记录查询', 'tfManager', 'tf_fileLinkQuery', 'tf_file', 1, 4, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileQuery', '文件查询', 'tfManager', 'tf_fileQuery', 'tf_file', 1, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_fileReport', '文件报表', 'tfManager', 'tf_fileReport', 'tf_file', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_home', '首页', 'tfManager', 'tf_home', NULL, 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_instances', '实例管理', 'tfManager', 'tf_instances', 'tf_server', 1, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iot', '物联网服务', 'tfManager', 'tf_iot', NULL, 0, 4, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotConnect', '设备连接管理', 'tfManager', 'tf_iotConnect', 'tf_iot', 1, 4, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotDevice', '设备管理', 'tfManager', 'tf_iotDevice', 'tf_iot', 1, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotDeviceLog', '设备日志', 'tfManager', 'tf_iotDeviceLog', 'tf_iot', 1, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotLog', 'Iot日志', 'tfManager', 'tf_iotLog', 'tf_iot', 1, 6, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotProduct', '产品类型管理', 'tfManager', 'tf_iotProduct', 'tf_iot', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotReport', 'Iot报表', 'tfManager', 'tf_iotReport', 'tf_iot', 1, 5, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotServer', '服务列表', 'tfManager', 'tf_iotServer', 'tf_iot', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_iotTrack', 'Iot链路追踪', 'tfManager', 'tf_iotTrack', 'tf_iot', 1, 7, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_pay', '支付', 'tfManager', 'tf_pay', NULL, 0, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payConfig', '支付参数配置', 'tfManager', 'tf_payConfig', 'tf_pay', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payDemo', '模拟支付', 'tfManager', 'tf_payDemo', 'tf_pay', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payNotify', '回调记录流水', 'tfManager', 'tf_payNotify', 'tf_pay', 1, 4, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payOrder', '支付订单流水', 'tfManager', 'tf_payOrder', 'tf_pay', 1, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payPipe', '交易支付队列状态', 'tfManager', 'tf_payPipe', 'tf_pay', 1, 7, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payReport', '支付报表', 'tfManager', 'tf_payReport', 'tf_pay', 1, 5, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payRequest', '支付请求流水', 'tfManager', 'tf_payRequest', 'tf_pay', 1, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_payTrack', '支付链路追踪', 'tfManager', 'tf_payTrack', 'tf_pay', 1, 6, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_pub', '公共服务', 'tfManager', 'tf_pub', NULL, 0, 7, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_pubAdmin', '授权管理', 'tfManager', 'tf_pubAdmin', 'tf_pub', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_server', '服务管理', 'tfManager', 'tf_server', NULL, 0, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_serverlogs', '服务日志', 'tfManager', 'tf_serverlogs', 'tf_server', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_serverwarns', '服务异常信息', 'tfManager', 'tf_serverwarns', 'tf_server', 1, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_site', '站点管理', 'tfManager', 'tf_site', 'tf_sys', 1, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_model', '模块管理', 'tfManager', 'tf_model', 'tf_sys', 1, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_menu', '菜单管理', 'tfManager', 'tf_menu', 'tf_sys', 1, 4, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_func', '权限管理', 'tfManager', 'tf_func', 'tf_sys', 1, 5, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_role', '系统角色管理', 'tfManager', 'tf_role', 'tf_sys', 1, 6, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_sync', '监管同步服务', 'tfManager', 'tf_sync', NULL, 0, 8, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_syncAdmin', '授权管理', 'tfManager', 'tf_syncAdmin', 'tf_sync', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_syncDumpWatch', '文件产生和同步', 'tfManager', 'tf_syncDumpWatch', 'tf_sync', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_sys', '基础管理', 'tfManager', 'tf_sys', NULL, 0, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_sysApplication', '应用管理', 'tfManager', 'tf_sysApplication', 'tf_sys', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_sysDomain', '域管理', 'tfManager', 'tf_sysDomain', 'tf_sys', 1, 0, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_trade', '秤报文', 'tfManager', 'tf_trade', NULL, 0, 6, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_tradeAbnormal', '交易报文异常', 'tfManager', 'tf_tradeAbnormal', 'tf_trade', 1, 3, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_tradeReport', '交易报文统计', 'tfManager', 'tf_tradeReport', 'tf_trade', 1, 1, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_tradeScalesAbnormal', '秤交易异常', 'tfManager', 'tf_tradeScalesAbnormal', 'tf_trade', 1, 2, 'tfcloud');
REPLACE INTO `sys_app_menu` (`pkey`, `name`, `appid`, `code`, `parentid`, `type`, `sort`, `domainid`) VALUES ('tf_tradeScalesReport', '秤报文统计', 'tfManager', 'tf_tradeScalesReport', 'tf_trade', 1, 0, 'tfcloud');
ALTER TABLE `sys_app_menu` DROP COLUMN `functions`;
ALTER TABLE `sys_app_menu` DROP COLUMN `default_show`;
ALTER TABLE `sys_app_menu` DROP COLUMN `pub`;
ALTER TABLE `sys_app_menu` DROP COLUMN `code`;
ALTER TABLE `sys_user_acl` DROP COLUMN `include_sub`;
ALTER TABLE `sys_user_role` DROP COLUMN `include_sub`;

