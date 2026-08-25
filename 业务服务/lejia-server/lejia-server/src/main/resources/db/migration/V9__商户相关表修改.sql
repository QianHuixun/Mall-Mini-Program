# 取消外键限制
SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE mkt_vendor ADD `bank_branch_name` VARCHAR(40) NULL DEFAULT NULL COMMENT '开户支行名称';
ALTER TABLE mkt_vendor CHANGE `bank_branch_name` `bank_branch_name` VARCHAR(40) NULL DEFAULT NULL COMMENT '开户支行名称' AFTER bankcard;
ALTER TABLE mkt_vendor ADD `bank_no` VARCHAR(40) NULL DEFAULT NULL COMMENT '开户行大额行号';
ALTER TABLE mkt_vendor CHANGE `bank_no` `bank_no` VARCHAR(40) NULL DEFAULT NULL COMMENT '开户行大额行号' AFTER bank_branch_name;
ALTER TABLE mkt_vendor ADD `business_scope` VARCHAR(200) NOT NULL COMMENT '经营范围';
ALTER TABLE mkt_vendor CHANGE `business_scope` `business_scope` VARCHAR(200) NOT NULL COMMENT '经营范围' AFTER visit_count;
ALTER TABLE mkt_vendor ADD `commission_rate` DECIMAL(5,2) NULL DEFAULT NULL COMMENT '佣金费率配置';
ALTER TABLE mkt_vendor CHANGE `commission_rate` `commission_rate` DECIMAL(5,2) NULL DEFAULT NULL COMMENT '佣金费率配置' AFTER business_scope;

-- ----------------------------
-- Table structure for mkt_vendor_file
-- ----------------------------
DROP TABLE IF EXISTS `mkt_vendor_file`;
CREATE TABLE `mkt_vendor_file` (
  `pkey` INT(11) NOT NULL COMMENT '主键',
  `vendor_pkey` INT(11) NOT NULL COMMENT '商户表主键',
  `remark` VARCHAR(50) NULL DEFAULT NULL COMMENT '备注',
  `url` VARCHAR(300) NOT NULL COMMENT '文件地址',
  `type` TINYINT(4) NOT NULL COMMENT '类型',
  `enabled` TINYINT(1) NOT NULL COMMENT '启用标志',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户文件表';

-- ----------------------------
-- Table structure for mkt_vendor_bigdata
-- ----------------------------
DROP TABLE IF EXISTS `mkt_vendor_bigdata`;
CREATE TABLE `mkt_vendor_bigdata` (
    `pkey` INT(11) NOT NULL COMMENT '主键',
    `content` text NULL DEFAULT NULL COMMENT '说明',
    PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户大数据表';

# 回复外键限制
SET FOREIGN_KEY_CHECKS=1;
