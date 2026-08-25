
---------------------------------------------------
-----------------修改订单表---------------
--------------------------------------

ALTER TABLE  mkt_order ADD distribution_type  tinyint(4)  NULL COMMENT '配送方式' AFTER  created_time;

ALTER TABLE mkt_order ADD pickup_code  varchar(64)  NULL COMMENT '核销码' AFTER  distribution_type;

ALTER TABLE mkt_order ADD pickup_flag  tinyint(1)  NULL COMMENT '是否核销 false 未核销,true已经核销' AFTER  pickup_code;
