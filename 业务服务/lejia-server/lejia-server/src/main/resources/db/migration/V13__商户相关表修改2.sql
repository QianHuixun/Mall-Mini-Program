# 取消外键限制
SET FOREIGN_KEY_CHECKS=0;

# 修改表注释
ALTER TABLE mkt_vendor_bigdata COMMENT '风采展示详情内容';

ALTER TABLE mkt_vendor ADD `short_content` VARCHAR(50) NULL DEFAULT NULL COMMENT '商户简介';
ALTER TABLE mkt_vendor CHANGE `short_content` `short_content` VARCHAR(50) NULL DEFAULT NULL COMMENT '商户简介' AFTER `remark`;

# 恢复外键限制
SET FOREIGN_KEY_CHECKS=1;
