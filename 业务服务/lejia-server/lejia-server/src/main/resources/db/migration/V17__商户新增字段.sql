ALTER TABLE mkt_vendor ADD is_clear tinyint(4) NULL COMMENT '是否可以清分';
ALTER TABLE mkt_vendor CHANGE is_clear is_clear tinyint(4) NULL COMMENT '是否可以清分' AFTER mobile;

ALTER TABLE mkt_vendor ADD zx_user_id varchar(30) NULL COMMENT '中信银行主键';
ALTER TABLE mkt_vendor CHANGE zx_user_id zx_user_id varchar(30) NULL COMMENT '中信银行主键' AFTER is_clear;

ALTER TABLE mkt_vendor ADD zx_identity varchar(50) NULL COMMENT '身份证号码';
ALTER TABLE mkt_vendor CHANGE zx_identity zx_identity varchar(50) NULL COMMENT '身份证号码' AFTER zx_user_id;

ALTER TABLE mkt_vendor ADD zx_remark varchar(200) NULL COMMENT '中信-备注(注册和绑卡的异常存储)';
ALTER TABLE mkt_vendor CHANGE zx_remark zx_remark varchar(200) NULL COMMENT '中信-备注(注册和绑卡的异常存储)' AFTER zx_identity;

# 优惠券新增字段
ALTER TABLE mkt_card ADD `count` INTEGER(11) NOT NULL COMMENT '优惠券数量';

ALTER TABLE mkt_member_card ADD `is_read` TINYINT(4) NOT NULL COMMENT '是否已读';
