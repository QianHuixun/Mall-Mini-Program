
REPLACE INTO `sys_domain` (`pkey`, `name`) VALUES ('lejia','乐加');
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('lejia-server','lejia', '农批服务', 2, 2, 'JgiUYddp97U', NULL, 0);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('lejia-web','lejia', '乐加前端', 3, 3, 'bu7rhGjvkb', NULL, 0);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('lejia-wechat','lejia', '乐加微信', 3, 3, '516HGKmbg', NULL, 0);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('lejia-android','lejia', '乐加安卓', 3, 3, 'bha8rlhFRlo', NULL, 0);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('lejia-ios','lejia', '乐加IOS', 3, 3, '7jljhs9k4G', NULL, 0);
REPLACE INTO `sys_application` (`pkey`, `domainid`, `appname`, `apptype`, `grant_type`, `secret`, `uri`, `need_captcha`) VALUES ('lejia-mp','lejia', '乐加小程序', 3, 3, 'RJgjT86JF', NULL, 0);

