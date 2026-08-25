ALTER TABLE mkt_express ADD courier_name varchar(200) DEFAULT NULL COMMENT '快递员姓名';
ALTER TABLE mkt_express CHANGE courier_name courier_name varchar(200)  DEFAULT NULL COMMENT '快递员姓名'  AFTER courier;

ALTER TABLE mkt_express ADD courier_mobile varchar(200) DEFAULT NULL COMMENT '快递员电话';
ALTER TABLE mkt_express CHANGE courier_mobile courier_mobile varchar(200)  DEFAULT NULL COMMENT '快递员姓名'  AFTER courier_name;


ALTER TABLE sys_farmer_config ADD store_id varchar(200) DEFAULT NULL COMMENT '第三运力门店id';
ALTER TABLE sys_farmer_config CHANGE store_id store_id varchar(200)  DEFAULT NULL COMMENT '第三运力门店id'  AFTER delivery_date;

ALTER TABLE sys_farmer_config ADD shop_id varchar(200) DEFAULT NULL COMMENT '第三运力店铺id';
ALTER TABLE sys_farmer_config CHANGE shop_id shop_id varchar(200)  DEFAULT NULL COMMENT '第三运力店铺id'  AFTER store_id;