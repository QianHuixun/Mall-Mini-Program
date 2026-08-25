# 取消外键限制
SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE mkt_vendor_order ADD `settlement_remark` VARCHAR(100) NULL DEFAULT NULL COMMENT '结算备注';
ALTER TABLE mkt_vendor_order CHANGE `settlement_remark` `settlement_remark` VARCHAR(100) NULL DEFAULT NULL COMMENT '结算备注' AFTER remark;
# 取消兼容老数据（原来的采购价 = 总价）
UPDATE mkt_vendor_order SET mkt_vendor_order.total_price = amt WHERE mkt_vendor_order.total_price IS NULL;

# 恢复外键限制
SET FOREIGN_KEY_CHECKS=1;
