ALTER TABLE mkt_vendor ADD bankuser_moblie varchar(50) NULL COMMENT '银行卡绑定手机';
ALTER TABLE mkt_vendor CHANGE bankuser_moblie bankuser_moblie varchar(50) NULL COMMENT '银行卡绑定手机' AFTER bank_no;

ALTER TABLE mkt_vendor_order ADD vendor_time datetime NULL COMMENT '商户确认时间';
ALTER TABLE mkt_vendor_order CHANGE vendor_time vendor_time datetime NULL COMMENT '商户确认时间' AFTER revoke_time;

ALTER TABLE mkt_vendor_order ADD farmer_time datetime NULL COMMENT '市场确认时间';
ALTER TABLE mkt_vendor_order CHANGE farmer_time farmer_time datetime NULL COMMENT '市场确认时间' AFTER vendor_time;

-- ----------------------------
-- Table structure for zx_file_record
-- ----------------------------
DROP TABLE IF EXISTS `zx_file_record`;
CREATE TABLE `zx_file_record` (
  `pkey` int(11) NOT NULL,
  `settlement_key` varchar(100) DEFAULT NULL COMMENT '结算主键',
  `name` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `save_path` varchar(200) DEFAULT NULL COMMENT '文件路径',
  `type` tinyint(4) DEFAULT NULL COMMENT '文件类型',
  `content` text COMMENT '文件内容',
  `status` tinyint(4) DEFAULT NULL COMMENT '文件状态',
  `abnormal_content` varchar(200) DEFAULT NULL COMMENT '异常内容',
  `upload_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '文件上传时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='中信文件生成';

-- ----------------------------
-- Table structure for zx_post_record
-- ----------------------------
DROP TABLE IF EXISTS `zx_post_record`;
CREATE TABLE `zx_post_record` (
  `pkey` int(11) NOT NULL,
  `req_interface` varchar(100) DEFAULT NULL COMMENT '请求接口',
  `req_content` text COMMENT '请求内容',
  `content` text COMMENT '返回结果',
  `time` datetime DEFAULT NULL COMMENT '返回时间',
  `created_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` int(11) DEFAULT NULL COMMENT '建档员',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='向中信请求记录';