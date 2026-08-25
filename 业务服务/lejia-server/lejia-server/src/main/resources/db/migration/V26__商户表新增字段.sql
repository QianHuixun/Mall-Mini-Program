ALTER TABLE mkt_vendor ADD zx_register_time date NULL COMMENT '中信注册时间';
ALTER TABLE mkt_vendor CHANGE zx_register_time zx_register_time date NULL COMMENT '中信注册时间' AFTER zx_user_id;
