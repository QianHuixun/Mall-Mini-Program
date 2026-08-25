ALTER TABLE mkt_member ADD status tinyint(4) DEFAULT 0 NOT NULL COMMENT '状态';
ALTER TABLE mkt_member CHANGE status status tinyint(4) DEFAULT 0 NOT NULL COMMENT '状态'  AFTER name;

ALTER TABLE mkt_member ADD log_out_time datetime(0) DEFAULT NULL COMMENT '提交注销时间';
ALTER TABLE mkt_member CHANGE log_out_time log_out_time datetime(0) DEFAULT NULL COMMENT '提交注销时间'  AFTER status;



