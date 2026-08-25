ALTER TABLE mkt_settlement_line ADD discount_amt decimal(16,2) NULL COMMENT '优惠金额';
ALTER TABLE mkt_settlement_line CHANGE discount_amt discount_amt decimal(16,2) NULL COMMENT '优惠金额' AFTER order_commission;

ALTER TABLE mkt_settlement_line ADD postage decimal(16,2) NULL COMMENT '邮费';
ALTER TABLE mkt_settlement_line CHANGE postage postage decimal(16,2) NULL COMMENT '邮费' AFTER discount_amt;

ALTER TABLE mkt_settlement_line ADD difference decimal(16,2) NULL COMMENT '差额';
ALTER TABLE mkt_settlement_line CHANGE difference difference decimal(16,2) NULL COMMENT '差额' AFTER postage;


ALTER TABLE mkt_settlement ADD discount_amt decimal(16,2) NULL COMMENT '优惠金额';
ALTER TABLE mkt_settlement CHANGE discount_amt discount_amt decimal(16,2) NULL COMMENT '优惠金额' AFTER amt;

ALTER TABLE mkt_settlement ADD postage decimal(16,2) NULL COMMENT '邮费';
ALTER TABLE mkt_settlement CHANGE postage postage decimal(16,2) NULL COMMENT '邮费' AFTER discount_amt;

ALTER TABLE mkt_settlement ADD difference decimal(16,2) NULL COMMENT '差额';
ALTER TABLE mkt_settlement CHANGE difference difference decimal(16,2) NULL COMMENT '差额' AFTER postage;


ALTER TABLE mkt_vendor_order ADD discount_amt decimal(16,2) NULL COMMENT '优惠金额';
ALTER TABLE mkt_vendor_order CHANGE discount_amt discount_amt decimal(16,2) NULL COMMENT '优惠金额' AFTER amt;

ALTER TABLE mkt_vendor_order ADD postage decimal(16,2) NULL COMMENT '邮费';
ALTER TABLE mkt_vendor_order CHANGE postage postage decimal(16,2) NULL COMMENT '邮费' AFTER discount_amt;

ALTER TABLE mkt_vendor_order ADD difference decimal(16,2) NULL COMMENT '差额';
ALTER TABLE mkt_vendor_order CHANGE difference difference decimal(16,2) NULL COMMENT '差额' AFTER postage;
