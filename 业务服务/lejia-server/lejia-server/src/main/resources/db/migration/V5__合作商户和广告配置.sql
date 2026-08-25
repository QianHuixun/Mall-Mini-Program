ALTER TABLE mkt_express ADD status_name varchar(100) NULL COMMENT '状态名称';
ALTER TABLE mkt_express CHANGE status_name status_name varchar(100) NULL COMMENT '状态名称' AFTER status;