
ALTER TABLE `sys_app_funcgroup`
   ADD `sort` int(11) DEFAULT NULL COMMENT '顺序';
   
ALTER TABLE `sys_app_funcgroup_item`
   CHANGE COLUMN `id` `sort`  int(11) NOT NULL COMMENT '顺序号';
   
   