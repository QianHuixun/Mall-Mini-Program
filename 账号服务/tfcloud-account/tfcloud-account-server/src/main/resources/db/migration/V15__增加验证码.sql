
ALTER TABLE `sys_application`
   ADD `need_captcha` bit DEFAULT NULL COMMENT '是否需要验证码';

   
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('wsale-web-boss','wsale', '农批Boss前端', 3, 3, 'jid4jfJbnv', NULL, 1);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('wsale-web-cust','wsale', '农批前端', 3, 3, 'rjHk01jufbV', NULL, 1);