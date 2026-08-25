ALTER TABLE `mkt_vendor_order` ADD COLUMN `market_commission_rate` decimal(16,2) comment '市场佣金费率' AFTER `commissions`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `market_commissions` decimal(16,2) comment '市场交易佣金' AFTER `market_commission_rate`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `pay_comm` decimal(16,2) comment '支付渠道手续费' AFTER `market_commissions`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `commission_type` tinyint(4) comment '手续费承担' AFTER `pay_comm`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `sys_commission_rate` decimal(16,2) comment '集团方抽佣比例' AFTER `commission_type`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `sys_commissions` decimal(16,2) comment '活动用户抽佣比例' AFTER `sys_commission_rate`;


ALTER TABLE `mkt_vendor_order` ADD COLUMN `discount_refund_amt`  decimal(16,2) comment '优惠退款金额' AFTER `discount_amt`;
ALTER TABLE `mkt_vendor_order` ADD COLUMN `file_pkey` int(11) comment '清分文件对应的pkey' AFTER `end_date`;


ALTER TABLE `mkt_member_activity` ADD COLUMN `settlement_type` tinyint(4) comment '结算状态' AFTER `status`;
ALTER TABLE `mkt_order` ADD COLUMN `settlement_type` tinyint(4) comment '结算状态' AFTER `purchase_status`;

ALTER TABLE `mkt_member_activity` ADD COLUMN `file_pkey` int(11) comment '清分文件对应的pkey' AFTER `pay_time`;
ALTER TABLE `mkt_order` ADD COLUMN `file_pkey` int(11) comment '清分文件对应的pkey' AFTER `xasz_consumption`;
ALTER TABLE `mkt_member_activity` ADD COLUMN `commission_type` tinyint(4) comment '手续费承担' AFTER `settlement_type`;
ALTER TABLE `mkt_order` ADD COLUMN `commission_type` tinyint(4) comment '手续费承担' AFTER `file_pkey`;





ALTER TABLE `mkt_supply` ADD COLUMN `commission_rate3`  decimal(16,2) comment '集团方佣金费率' AFTER `commission_rate2`;


ALTER TABLE `sys_farmer_config` ADD COLUMN `is_enterprise`  tinyint(1) comment '是否是民营企业,true:是' AFTER `is_packing_charge`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `commission_type` tinyint(4) comment '手续费承担' AFTER `is_enterprise`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `commission_rate`  decimal(16,2) comment '集团方抽佣比例' AFTER `commission_type`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `member_commission_rate`  decimal(16,2) comment '活动用户抽佣比例' AFTER `commission_rate`;

ALTER TABLE `mkt_member` ADD COLUMN `is_activity`  tinyint(1) comment '是否是活动(比如 工会用户), true:是' AFTER `source`;


DROP TABLE IF EXISTS `zx_user_info`;
CREATE TABLE `zx_user_info`  (
  `pkey` int(11) NOT NULL,
  `type` tinyint(4) DEFAULT NULL COMMENT '账户类型',
  `value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账户对应的主键',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账户名称',
  `comms` decimal(16, 2) DEFAULT NULL COMMENT '云商城账户余额,金额进来的时候 已经转到平台的担保登记簿',
  `trade_union_comms` decimal(16, 2) DEFAULT NULL COMMENT '工会用户可以划分给消费者钱包的金额',
  `market_auto` tinyint(1) DEFAULT NULL COMMENT '市场自动提现,true:自动提现',
  `vendor_auto` tinyint(1) DEFAULT NULL COMMENT '商户自动提现true:自动提现',
  `card_status` tinyint(4) DEFAULT NULL COMMENT '中信银行绑卡结果',
  `zx_user_id` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '中信银行主键',
  `zx_register_time` date DEFAULT NULL COMMENT '中信注册时间',
  `zx_remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '中信-备注(注册和绑卡的异常存储)',
  `user_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户类型',
  `user_nm` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户姓名',
  `user_id_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '证件类型',
  `user_id_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '证件号码',
  `user_phone` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户手机号',
  `corp_nm` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '企业法人姓名',
  `corp_id_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '企业法人证件号码',
  `corp_id_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '企业法人证件类型',
  `pan_num` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '开户银行联行号',
  `acct_nm` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账户名称',
  `bank_card_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行证件类型',
  `bank_card_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行证件号码',
  `pan` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行账号',
  `acct_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账户类型',
  `bank_phone` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行预留手机号',
  `auth_protocol_version` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户授权协议版本号',
  `auth_protocol_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户授权协议流水号',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标志，true：已删除',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '市场在中信清分中需要的资料' ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS `zx_withdraw`;
CREATE TABLE `zx_withdraw`  (
  `pkey` int(11) NOT NULL,
  `file_pkey` int(11) DEFAULT NULL COMMENT '文件的主键',
  `type` tinyint(4) DEFAULT NULL COMMENT '账户类型,区分是否是平台商户',
  `value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '云商城主键',
  `zx_user_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '中信银行主键',
  `bill_date` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账单日期,格式yyyy-MM-dd',
  `comms` decimal(16, 2) DEFAULT NULL COMMENT '需要打款的金额',
  `balance` decimal(16, 2) DEFAULT NULL COMMENT '余额',
  `status` tinyint(4) DEFAULT NULL COMMENT '打款状态',
  `withdraw_time` datetime(0) DEFAULT NULL COMMENT '打款时间',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `ascription` int(11) DEFAULT 1 COMMENT '归属主键',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '需要提现的数据' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `third_pay_line`;
CREATE TABLE `third_pay_line`  (
  `pkey` int(11) NOT NULL,
  `pay_time` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付时间 格式yyyy-MM-dd HH:mm:ss	',
  `mid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户号',
  `tid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '终端号',
  `inst_mid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '业务类型 MINIDEFAULT',
  `attached_data` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '附加数据',
  `bank_card_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付银行信息',
  `bill_funds` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资金渠道',
  `bill_funds_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资金渠道说明',
  `buyer_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '买家ID',
  `buyer_username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '买家用户名',
  `buyer_pay_amount` int(11) DEFAULT NULL COMMENT '实付金额',
  `total_amount` int(11) DEFAULT NULL COMMENT '订单金额，单位分',
  `invoice_amount` int(11) DEFAULT NULL COMMENT '开票金额',
  `mer_order_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户订单号',
  `receipt_amount` int(11) DEFAULT NULL COMMENT '实收金额',
  `ref_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付银行卡参考号',
  `refund_amount` int(11) DEFAULT NULL COMMENT '退款金额 退货交易',
  `refund_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退款说明 退货交易',
  `seq_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '系统交易流水号',
  `settle_date` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '结算日期 格式yyyy-MM-dd',
  `status` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '交易状态',
  `sub_buyer_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '买家子ID',
  `target_order_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '渠道订单号',
  `target_sys` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '支付渠道',
  `coupon_merchant_contribute` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户出资优惠金额',
  `coupon_other_contribute` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '其他出资优惠金额',
  `activity_ids` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '微信活动ID',
  `refund_target_order_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退货渠道订单号',
  `refund_pay_time` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退货时间',
  `refund_settle_date` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '结算日期',
  `order_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '订单详情',
  `create_time` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '订单创建时间',
  `mchnt_uuid` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户UUID',
  `connect_sys` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '转接系统',
  `sub_inst` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商户所属分支机构代码',
  `yxlm_amount` int(11) DEFAULT NULL COMMENT '联盟优惠金额',
  `refund_ext_order_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退货外部订单号',
  `goods_trade_no` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商品交易单号',
  `ext_order_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部订单号',
  `secure_status` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '担保交易状态',
  `complete_amount` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '担保完成金额',
  `refund_order_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '退货订单号',
  `coupon_amount` int(11) DEFAULT NULL COMMENT '渠道优惠金额 单位：分',
  `bank_info` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '银行信息',
  PRIMARY KEY (`pkey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '第三方支付渠道回调' ROW_FORMAT = Dynamic;

ALTER TABLE `sys_farmer_config` ADD COLUMN `wanli_app_id` varchar(50) DEFAULT NULL COMMENT '第三运力应用id' after `delivery_date`;
ALTER TABLE `sys_farmer_config` ADD COLUMN `wanli_secret` varchar(50) DEFAULT NULL COMMENT '第三运力应用密钥' after `wanli_app_id`;

ALTER TABLE `mkt_vendor_wallet_line` ADD COLUMN `order_time` datetime DEFAULT NULL COMMENT '订单时间' after `status`;


ALTER TABLE `sys_ascription` ADD COLUMN `zx_qf_sys`  int(11) comment '运营端清分时间' AFTER `market_num`;
ALTER TABLE `sys_ascription` ADD COLUMN `zx_qf`  int(11) comment '市场清分时间' AFTER `zx_qf_sys`;