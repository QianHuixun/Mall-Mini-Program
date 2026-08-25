-- ----------------------------
-- Table structure for sys_farmer_station
-- ----------------------------
DROP TABLE IF EXISTS `sys_farmer_station`;
CREATE TABLE `sys_farmer_station` (
  `pkey` int(11) NOT NULL COMMENT '主键',
  `market` varchar(50) DEFAULT NULL COMMENT '市场主键',
  `y_status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `yytb` varchar(40) DEFAULT NULL  COMMENT  '营业开始时间',
  `yyte` varchar(40) DEFAULT NULL  COMMENT'营业结束时间',
  `prov` varchar(40) DEFAULT NULL COMMENT  '省',
  `city` varchar(40) DEFAULT NULL  COMMENT  '市',
  `area` varchar(40) DEFAULT NULL  COMMENT  '区',
  `address` varchar(200) DEFAULT NULL  COMMENT  '地址' ,
  `longitude` decimal(11,6) DEFAULT NULL COMMENT  '经度',
  `latitude` decimal(11,6) DEFAULT NULL  COMMENT  '维度',
  `delivery_range` int(11) DEFAULT NULL COMMENT  '配送范围',
  `phour` int(11) DEFAULT NULL COMMENT  '配送时',
  `pminute` int(11) DEFAULT NULL COMMENT  '配送分',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

---------------------------------------------------
-----------------修改市场配置表---------------
--------------------------------------

---------------------------------------------------
-----------------修改市描述表---------------
--------------------------------------

ALTER TABLE mkt_order_desc ADD yytb  varchar(40)  NULL COMMENT '预约时间起始' AFTER  end_time;


ALTER TABLE mkt_order_desc ADD yyte  varchar(40)  NULL COMMENT '预约时间结束' AFTER  yytb;


