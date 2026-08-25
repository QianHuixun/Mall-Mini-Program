-- 系统管理员
REPLACE INTO `sys_user` (pkey,userid,nickname,password,actived)
     VALUES ('-1', 'admin', '管理员', '{bcrypt}$2a$10$NMwvzBykhj4ndQBE1DLUneCvauDGHB1S7d8Uk7FtJm0WPAwq6WlEC', 1);


-- 系统角色
REPLACE INTO sys_app_role (pkey,name,group_name,description,enable)
     VALUES ('sysAdmin', '系统管理员', '系统','拥有全部权限',1),
            ('oauth2Admin', '微服务管理员', '系统','拥有微服务接入和配置的权限',1),
            ('domainAdmin', '域管理员', '系统','拥有本域下所有应用的管理权限',1);
     
-- 分配系统管理员角色
REPLACE INTO `sys_user_role` (pkey,ownerid,value,scope,scope_type)
     VALUES ('auto_1', '-1', 'sysAdmin', '*', '0');
     
-- 系统权限
REPLACE INTO sys_app_function (pkey,name,description)
     VALUES ('managerApplication', '管理应用', '可以对微服务的应用进行新增修改配置'),
            ('managerRole', '角色管理', '可以对角色进行管理'),
            ('managerFunction', '权限管理', '可以对权限进行管理'),
            ('managerMenu', '菜单管理', '可以对菜单进行管理'),
            ('domainAdmin', '域管理员', '域管理员权限，默认拥有域下的所有菜单和权限'),
            ('managerUser', '用户管理', '可以对用户进行管理'),
            ('managerOrg', '机构管理', '管理公司市场等机构');

-- 重置基础域
REPLACE INTO `sys_domain` (`pkey`, `name`) 
     VALUES ('tfcloud', '致一云');

-- 重置基础APP
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('account','tfcloud', '账号服务', 0, 0, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('appAdmin','tfcloud', '应用管理', 0, 3, '123456', '[\"http://localhost:20002/login\", \"http://192.168.128.91:20002/login\", \"http://192.168.128.94:20002/login\",\"http://172.30.57.24:20002/login\"]');
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('file', 'tfcloud','文件服务', 0, 2, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('term-server', 'tfcloud','长连接服务', 0, 2, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('iot-manager','tfcloud', '物联网管理模块', 0, 0, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('iot-server','tfcloud', '物联网连接接口模块', 0, 0, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('pay','tfcloud', '支付网关', 0, 2, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('tfManager','tfcloud', '管理平台', 3, 3, '123456', '[\"http://localhost:8091/#/login\", \"http://192.168.128.91:80/#/login\", \"http://192.168.128.94:8091/#/login\", \"http://iot.z1.xyz/tfManager/#/login\"]');
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('test','tfcloud', '测试服务', 2, 3, '123456', '[\"http://localhost:21000/login\", \"http://192.168.128.91:21000/login\", \"http://192.168.128.94:21000/login\"]');
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('testpay','tfcloud', '支付网关测试', 1, 1, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('simulation-payclient','tfcloud', '第三方微服务', 2, 2, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('demoproject','tfcloud', '演示项目', 2, 2, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('otherapp','tfcloud', '第三方应用', 1, 1, '123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wxpay','tfcloud', '微信项目支付', 1, 1, 'wx123456', NULL);
INSERT IGNORE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('xasz','tfcloud', '心安食足', 1, 1, '562343', NULL);

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

