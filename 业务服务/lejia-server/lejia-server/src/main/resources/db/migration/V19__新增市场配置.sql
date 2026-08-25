
---------------------------------------------------
-----------------修改市场配置表---------------
--------------------------------------



ALTER TABLE sys_farmer_config ADD distribution_config bit(1)  NULL COMMENT '配送配置' AFTER  settlement_method;

ALTER TABLE sys_farmer_config ADD fee  decimal(16,2)  NULL COMMENT '统一设定金额' AFTER  distribution_config;

ALTER TABLE sys_farmer_config ADD starting_price  decimal(16,2)  NULL COMMENT '起步价' AFTER  fee;

ALTER TABLE sys_farmer_config  ADD phour   int(11)  NULL COMMENT '自提时间小时' AFTER  starting_price;

ALTER TABLE sys_farmer_config  ADD pminute   int(11)  NULL COMMENT '自提时间小时' AFTER  phour;
