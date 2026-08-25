ALTER TABLE `sys_app_menu`
ADD COLUMN `pub`  bit NULL COMMENT '是否公开接口' AFTER `default_show`;
