# 取消外键限制
SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE mkt_vendor_order ADD `total_price` DECIMAL(10,2) NULL DEFAULT NULL COMMENT '总价';
ALTER TABLE mkt_vendor_order CHANGE `total_price` `total_price` DECIMAL(10,2) NULL DEFAULT NULL COMMENT '总价' AFTER price;
ALTER TABLE mkt_vendor_order ADD `commission_rate` DECIMAL(5,2) NULL DEFAULT NULL COMMENT '佣金费率';
ALTER TABLE mkt_vendor_order CHANGE `commission_rate` `commission_rate` DECIMAL(5,2) NULL DEFAULT NULL COMMENT '佣金费率' AFTER total_price;
ALTER TABLE mkt_vendor_order ADD `commissions` DECIMAL(20,8) NOT NULL COMMENT '交易佣金';
ALTER TABLE mkt_vendor_order CHANGE `commissions` `commissions` DECIMAL(20,8) NOT NULL COMMENT '交易佣金' AFTER commission_rate;

# 恢复外键限制
SET FOREIGN_KEY_CHECKS=1;
