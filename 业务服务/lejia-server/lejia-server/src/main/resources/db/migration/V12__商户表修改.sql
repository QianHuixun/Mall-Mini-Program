# 取消外键限制
SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE mkt_vendor ADD `rate_update_time` DATETIME NULL DEFAULT NULL COMMENT '佣金费率更新时间';
ALTER TABLE mkt_vendor CHANGE `rate_update_time` `rate_update_time` DATETIME NULL DEFAULT NULL COMMENT '佣金费率更新时间' AFTER `commission_rate`;
# 兼容老数据
UPDATE mkt_vendor SET rate_update_time = update_time WHERE rate_update_time IS NULL;

# 恢复外键限制
SET FOREIGN_KEY_CHECKS=1;
