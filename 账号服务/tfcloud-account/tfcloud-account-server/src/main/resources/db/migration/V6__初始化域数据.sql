INSERT INTO `sys_domain` (`pkey`, `name`) VALUES
('tfcloud', '致一云'),
('util', '工具'),
('wechat', '微信开放平台'),
('farm', '农贸'),
('foot', '足语');

REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('accessMonitor','util', '门禁', 2, 0, 'accessMonitor', NULL);

REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('account','tfcloud', '账号服务', 0, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('appAdmin','tfcloud', '应用管理', 0, 3, '123456', '[\"http://localhost:20002/login\", \"http://192.168.128.91:20002/login\", \"http://192.168.128.94:20002/login\",\"http://172.30.57.24:20002/login\"]');
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('file', 'tfcloud','文件服务', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('logger', 'tfcloud','日志服务', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('term-server', 'tfcloud','长连接服务', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('iot-manager','tfcloud', '物联网管理模块', 0, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('iot-server','tfcloud', '物联网连接接口模块', 0, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('lattic','tfcloud', '格子机', 1, 1, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('netty-tfiot','tfcloud', '物联网模块', 0, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('notify','tfcloud', '通知服务', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('pay','tfcloud', '支付网关', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('pay-ab-demo','tfcloud', '支付网关测试农行', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('pay-ab-wz','tfcloud', '支付网关温州农行', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('tfManager','tfcloud', '管理平台', 3, 3, '123456', '[\"http://localhost:8091/#/login\", \"http://192.168.128.91:80/#/login\", \"http://192.168.128.94:8091/#/login\", \"http://iot.z1.xyz/tfManager/#/login\"]');
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('monitor','tfcloud', '监控服务', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('monitor-client','tfcloud', '监控服务客户端', 0, 2, '123456', NULL);

REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('test','tfcloud', '测试服务', 2, 3, '123456', '[\"http://localhost:21000/login\", \"http://192.168.128.91:21000/login\", \"http://192.168.128.94:21000/login\"]');
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('testpay','tfcloud', '支付网关测试', 1, 1, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('simulation-payclient','tfcloud', '第三方微服务', 2, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('demoproject','tfcloud', '演示项目', 2, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('otherapp','tfcloud', '第三方应用', 1, 1, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wxpay','tfcloud', '微信项目支付', 1, 1, 'wx123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('xasz','tfcloud', '心安食足', 1, 1, '562343', NULL);

REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmBoss','farm', '心安食足Boss', 2, 0, 'farmBoss', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmCust','farm', '心安食足', 2, 0, 'farmCust', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmDevice','farm', '心安食足设备平台', 2, 0, 'farmDevice', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmIot','farm', '监视平台', 2, 0, 'farmIot', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmMall','farm', '心安食足商城', 2, 0, 'farmMall', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmPay','farm', '心安食足支付接口', 2, 0, 'farmPay', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmProxy','farm', '心安食足代理', 2, 0, 'farmProxy', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmPub','farm', '心安食足公共接口', 2, 0, 'farmPub', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmScale','farm', '心安食足称服务', 1, 1, 'farmScale', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('farmSupervise','farm', '心安食足监管平台', 2, 0, 'farmSupervise', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('jidong','farm', '心安食足极东', 1, 1, 'xf5jd853kh', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('yuehuMarket','farm', '心安食足月湖', 1, 1, 'jj57vjw3e7uc', NULL);

REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('mp','wechat', '微信开放平台', 0, 2, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('mpweb','wechat', 'mpweb', 0, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('mptest','wechat', '微信公众号测试前端', 0, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wxmpCust','wechat', '微信公众号开放平台cust端后台服务', 2, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wxmpCustWeb','wechat', '微信公众号开放平台cust端前端应用', 1, 0, '123456@2020', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wxmpMobile','wechat', '微信公众号开放平台mobile端后台服务', 2, 0, '123456', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wxmpMobileWeb','wechat', '微信公众号开放平台mobile端前端应用', 1, 0, '123456@2020', NULL);

update sys_user u,sys_application a set u.regist_from_domain = a.domainid where u.regist_from_app = a.pkey;
update sys_app_function u,sys_application a set u.domainid = a.domainid where u.appid = a.pkey;
update sys_app_role u,sys_application a set u.domainid = a.domainid where u.appid = a.pkey;
update sys_org u,sys_application a set u.domainid = a.domainid where u.appid = a.pkey;
update sys_org_dept u,sys_application a set u.domainid = a.domainid where u.appid = a.pkey;
update sys_user_app u,sys_application a set u.domainid = a.domainid where u.appid = a.pkey;
