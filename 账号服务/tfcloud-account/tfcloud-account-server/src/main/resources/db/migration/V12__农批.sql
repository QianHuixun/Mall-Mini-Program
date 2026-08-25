ALTER TABLE `sys_app_funcgroup_item`
   ADD `related_functions` VARCHAR(2000) DEFAULT NULL COMMENT '关联权限';

REPLACE INTO `sys_domain` (`pkey`, `name`) VALUES ('wsale','农批');
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-market-server','wsale', '农批服务', 2, 2, 'Hkf883jSxf', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-scale','wsale', '农批称服务', 2, 2, 'gsx7ufjc6', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-pay','wsale', '农批支付服务', 2, 2, 'gjHhglcgkl', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-web-boss','wsale', '农批Boss前端', 3, 3, 'jid4jfJbnv', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-web-cust','wsale', '农批前端', 3, 3, 'rjHk01jufbV', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-mp-manager','wsale', '管理小程序', 3, 3, 'mnkzhjUhvkf', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-android-merchant','wsale', '商户APP', 3, 3, '7hfkz264hd', NULL);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`) VALUES ('wsale-ios-merchant','wsale', '商户APP', 3, 3, '5h2jflXgsLM', NULL);

