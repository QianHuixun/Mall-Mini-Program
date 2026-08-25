/*
Navicat MySQL Data Transfer

Source Server         : 192.168.128.91
Source Server Version : 50718
Source Host           : 192.168.128.91:3306
Source Database       : lejia

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2020-07-29 11:09:27
*/

SET FOREIGN_KEY_CHECKS=0;



-- lejia.mkt_access_log definition

CREATE TABLE `mkt_access_log` (
  `pkey` int(11) NOT NULL,
  `openid` varchar(40) DEFAULT NULL,
  `access_time` date NOT NULL COMMENT '建档时间',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='app访问记录';


-- lejia.mkt_addr definition

CREATE TABLE `mkt_addr` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `addr` varchar(200) NOT NULL COMMENT '地址',
  `addr_detail` varchar(200) NOT NULL COMMENT '详细地址',
  `addr_code` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '收货人',
  `mobile` varchar(20) NOT NULL COMMENT '收货人手机',
  `default_addr` tinyint(1) NOT NULL DEFAULT '1' COMMENT '默认地址',
  `longitude` decimal(11,6) NOT NULL COMMENT '经度',
  `latitude` decimal(11,6) NOT NULL COMMENT '纬度',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_advert definition

CREATE TABLE `mkt_advert` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `position` tinyint(4) NOT NULL COMMENT '位置  1号/2号/3号/4号/5号',
  `photo` varchar(1000) NOT NULL COMMENT '图片',
  `url_type` tinyint(4) NOT NULL COMMENT '链接类型 无/链接/积分商城/会员办理',
  `obj_key` varchar(200) DEFAULT NULL COMMENT '对象',
  `sort` int(11) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='广告位';


-- lejia.mkt_advise definition

CREATE TABLE `mkt_advise` (
  `pkey` int(11) NOT NULL,
  `content` varchar(1000) NOT NULL COMMENT '正文',
  `member_key` int(11) NOT NULL COMMENT '提交人',
  `mobile` varchar(20) NOT NULL COMMENT '提交人手机',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='建议反馈';


-- lejia.mkt_app_config definition

CREATE TABLE `mkt_app_config` (
  `pkey` int(11) NOT NULL,
  `points_rate` int(11) NOT NULL COMMENT '积分比',
  `points_date` date DEFAULT NULL COMMENT '积分清理日期',
  `points_qd` int(11) NOT NULL COMMENT '签到积分',
  `points_qd_dz` int(11) NOT NULL COMMENT '签到递增积分',
  `points_qd_sx` int(11) NOT NULL COMMENT '签到天数上限',
  `points_cj_user` int(11) NOT NULL COMMENT '抽奖消费积分',
  `points_cj_xz` int(11) NOT NULL COMMENT '抽奖限制',
  `member_price` decimal(11,2) NOT NULL COMMENT '会员原价',
  `member_price_n` decimal(11,2) NOT NULL COMMENT '会员优惠价',
  `member_points` int(11) NOT NULL COMMENT '会员赠送积分',
  `member_get_points` int(11) NOT NULL COMMENT '会员积分比例',
  `member_card` varchar(1000) DEFAULT NULL COMMENT '会员赠送卡券',
  `tel` varchar(20) NOT NULL COMMENT '联系电话',
  `addr` varchar(200) NOT NULL COMMENT '退货地址',
  `wechat_num` varchar(200) DEFAULT NULL COMMENT '微信号',
  `wechat_code` varchar(200) DEFAULT NULL COMMENT '微信二维码',
  `member_photo1` varchar(200) DEFAULT NULL COMMENT '会员办理图片',
  `member_photo2` varchar(200) DEFAULT NULL COMMENT '会员办理图片',
  `invitation_photo` varchar(200) DEFAULT NULL COMMENT '閭€璇锋湁绀煎浘鐗?',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_app_msg definition

CREATE TABLE `mkt_app_msg` (
  `pkey` int(11) NOT NULL,
  `title` varchar(100) NOT NULL COMMENT '主题',
  `bottom` varchar(100) NOT NULL COMMENT '底部标语',
  `tel` varchar(20) NOT NULL COMMENT '联系电话',
  `content` varchar(2000) DEFAULT NULL COMMENT '介绍',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_card definition

CREATE TABLE `mkt_card` (
  `pkey` int(11) NOT NULL,
  `title` varchar(100) NOT NULL COMMENT '标题',
  `cost` decimal(11,2) NOT NULL COMMENT '价值',
  `limit_cost` decimal(11,2) NOT NULL COMMENT '最低消费',
  `effective` int(11) DEFAULT NULL COMMENT '有效期(天)',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '到期日期',
  `content` varchar(2000) DEFAULT NULL COMMENT '卡券说明',
  `user_farmer` varchar(40) DEFAULT NULL COMMENT 'MktFarmer',
  `user_type` int(11) DEFAULT NULL COMMENT 'MktGtype',
  `user_goods` int(11) DEFAULT NULL COMMENT 'MktGoodsMain',
  `card_type` tinyint(4) DEFAULT NULL COMMENT '领取方式 手动发放/二维码自领/所有',
  `card_code` varchar(100) DEFAULT NULL COMMENT '领券码',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  KEY `title` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券';


-- lejia.mkt_card_market definition

CREATE TABLE `mkt_card_market` (
  `pkey` int(11) NOT NULL,
  `card` int(11) NOT NULL COMMENT '优惠券',
  `farmer` varchar(40) NOT NULL,
  PRIMARY KEY (`pkey`),
  KEY `card` (`card`) USING BTREE,
  KEY `farmer` (`farmer`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券使用范围';


-- lejia.mkt_collection definition

CREATE TABLE `mkt_collection` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `ctype` tinyint(4) NOT NULL COMMENT '类型 菜谱/商品',
  `obj_key` int(11) NOT NULL COMMENT '对象主键',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  KEY `member` (`member_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='我的收藏';


-- lejia.mkt_comm_draw definition

CREATE TABLE `mkt_comm_draw` (
  `pkey` int(11) NOT NULL,
  `order_number` varchar(100) NOT NULL COMMENT '订单号',
  `member_key` int(11) NOT NULL COMMENT '用户',
  `comms` decimal(11,2) NOT NULL COMMENT '佣金值',
  `status` tinyint(4) NOT NULL COMMENT '状态 初始/已发',
  `bank_code` varchar(100) DEFAULT NULL COMMENT '流水号',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `check_time` datetime DEFAULT NULL COMMENT '确认时间',
  `check_by` int(11) DEFAULT NULL COMMENT '确认人',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='提现表';


-- lejia.mkt_cookfd definition

CREATE TABLE `mkt_cookfd` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `recom` tinyint(1) NOT NULL COMMENT '今日推荐',
  `ctype` int(11) NOT NULL COMMENT '分类',
  `photo1` varchar(1000) NOT NULL COMMENT '照片1',
  `photo2` varchar(1000) DEFAULT NULL COMMENT '照片2',
  `photo3` varchar(1000) DEFAULT NULL COMMENT '照片2',
  `sort` int(11) NOT NULL COMMENT '排序',
  `descp` varchar(200) DEFAULT NULL COMMENT '描述',
  `content` text COMMENT '正文',
  `view_count` int(11) NOT NULL COMMENT '浏览数量',
  `coll_count` int(11) NOT NULL COMMENT '收藏数量',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  KEY `name` (`name`) USING BTREE,
  KEY `recom` (`recom`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜谱';


-- lejia.mkt_cookfd_line definition

CREATE TABLE `mkt_cookfd_line` (
  `pkey` int(11) NOT NULL,
  `cookfd` int(11) NOT NULL COMMENT '菜谱',
  `goods` int(11) NOT NULL COMMENT '商品',
  `space` int(11) NOT NULL COMMENT '规格',
  `num` int(11) NOT NULL COMMENT '数量',
  `sort` int(11) NOT NULL COMMENT '排序',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`pkey`),
  KEY `cookfd` (`cookfd`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='食材清单';


-- lejia.mkt_cookfd_type definition

CREATE TABLE `mkt_cookfd_type` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` int(11) NOT NULL COMMENT '排序',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜谱分类';


-- lejia.mkt_courier definition

CREATE TABLE `mkt_courier` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `mobile` varchar(20) NOT NULL COMMENT '手机',
  `openid1` varchar(40) DEFAULT NULL,
  `openid2` varchar(40) DEFAULT NULL,
  `unionid` varchar(40) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='快递员';


-- lejia.mkt_draw_conf definition

CREATE TABLE `mkt_draw_conf` (
  `pkey` int(11) NOT NULL,
  `point` int(11) NOT NULL COMMENT '支付积分',
  `limit_num` int(11) NOT NULL COMMENT '每日次数限制',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='抽奖配置';


-- lejia.mkt_draw_prize definition

CREATE TABLE `mkt_draw_prize` (
  `pkey` int(11) NOT NULL,
  `p_type` tinyint(4) NOT NULL COMMENT '礼品类型 积分/优惠券/礼品券/礼品/',
  `name` varchar(100) DEFAULT NULL COMMENT '奖品名称',
  `probability` int(11) NOT NULL COMMENT '中奖概率（%）',
  `photo` varchar(200) DEFAULT NULL COMMENT '图片',
  `pvalue` int(11) DEFAULT '0' COMMENT '奖品值',
  `descp` varchar(100) NOT NULL COMMENT '中奖描述',
  `sort` int(11) NOT NULL COMMENT '排序',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='礼品配置';


-- lejia.mkt_draw_win definition

CREATE TABLE `mkt_draw_win` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态 初始/已发',
  `p_type` tinyint(4) NOT NULL COMMENT '礼品类型 积分/优惠券/礼品券/礼品/',
  `prize` int(255) NOT NULL COMMENT '奖品ID',
  `descp` varchar(100) NOT NULL COMMENT '中奖描述',
  `addr` varchar(1000) DEFAULT NULL COMMENT '收货地址',
  `logistics` varchar(100) DEFAULT NULL COMMENT '快递公司',
  `express` varchar(100) DEFAULT NULL,
  `send_time` datetime DEFAULT NULL,
  `created_time` datetime NOT NULL COMMENT '中奖时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='中奖记录';


-- lejia.mkt_express definition

CREATE TABLE `mkt_express` (
  `pkey` int(11) NOT NULL,
  `kc_code` varchar(20) NOT NULL COMMENT '单据号',
  `order_id` int(11) NOT NULL COMMENT '订单',
  `status` tinyint(4) NOT NULL COMMENT '状态 初始/已派单/已拦货/已到货/拒收',
  `courier` int(11) DEFAULT NULL COMMENT '快递员',
  `pd_time` datetime DEFAULT NULL COMMENT '派单时间',
  `jd_time` datetime DEFAULT NULL COMMENT '接单时间',
  `qr_time` datetime DEFAULT NULL COMMENT '到货时间',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `code` (`kc_code`),
  UNIQUE KEY `order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='跑脚单';


-- lejia.mkt_goods definition

CREATE TABLE `mkt_goods` (
  `pkey` int(11) NOT NULL,
  `gtype` int(11) NOT NULL COMMENT '分类',
  `goods_main` int(11) NOT NULL COMMENT '商品库',
  `m_type` tinyint(4) NOT NULL COMMENT '积分/市场/会员/特价/分享/砍价/团购/预售',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `photo1` varchar(1000) DEFAULT NULL COMMENT '照片1',
  `photo2` varchar(1000) DEFAULT NULL COMMENT '照片2',
  `photo3` varchar(1000) DEFAULT NULL COMMENT '照片2',
  `serial_number` varchar(40) DEFAULT NULL COMMENT '标准编号',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `content` varchar(1000) DEFAULT NULL COMMENT '正文',
  `start_date` date NOT NULL COMMENT '起售日期',
  `end_date` date NOT NULL,
  `send_date` date DEFAULT NULL COMMENT '发货日期',
  `view_count` int(11) NOT NULL,
  `xs_num` int(11) NOT NULL COMMENT '销售数量',
  `purchase_num` int(11) DEFAULT NULL COMMENT '限购数量',
  `extend_con` varchar(1000) DEFAULT NULL COMMENT '扩展内容  砍价、团购使用',
  `price` decimal(11,2) DEFAULT NULL COMMENT '价格',
  `is_postage` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否免邮',
  `sort` int(11) NOT NULL DEFAULT '0',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  KEY `gtype` (`gtype`) USING BTREE,
  KEY `goodsMain` (`goods_main`) USING BTREE,
  KEY `title` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品(在售)';


-- lejia.mkt_goods_collage definition

CREATE TABLE `mkt_goods_collage` (
  `pkey` int(11) NOT NULL,
  `price` decimal(11,2) NOT NULL COMMENT '拼团价',
  `end_date` date DEFAULT NULL COMMENT '到期日期',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='拼团商品';


-- lejia.mkt_goods_cut definition

CREATE TABLE `mkt_goods_cut` (
  `pkey` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL COMMENT '砍价设置  按固定/按比例',
  `comm` int(11) NOT NULL COMMENT '佣金值',
  `end_date` date DEFAULT NULL COMMENT '到期日期',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='砍价商品';


-- lejia.mkt_goods_main definition

CREATE TABLE `mkt_goods_main` (
  `pkey` int(11) NOT NULL COMMENT '主键',
  `gtype` int(11) NOT NULL COMMENT '分类',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `sort` int(11) NOT NULL DEFAULT '0',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  KEY `gtype` (`gtype`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品库';


-- lejia.mkt_goods_popular definition

CREATE TABLE `mkt_goods_popular` (
  `pkey` int(11) NOT NULL,
  `sort` int(11) NOT NULL COMMENT '排序字段',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='热门商品表';


-- lejia.mkt_goods_space definition

CREATE TABLE `mkt_goods_space` (
  `pkey` int(11) NOT NULL,
  `goods` int(11) NOT NULL COMMENT '商品',
  `space` varchar(40) NOT NULL COMMENT '规格',
  `weight` decimal(11,2) DEFAULT NULL COMMENT '毛重',
  `price_old` decimal(11,2) NOT NULL COMMENT '原价',
  `price` decimal(11,2) NOT NULL COMMENT '价格',
  `price_member` decimal(11,2) NOT NULL COMMENT '会员价',
  `point` int(11) NOT NULL COMMENT '积分',
  `comm` decimal(11,2) NOT NULL COMMENT '佣金',
  `kc_num` int(11) NOT NULL COMMENT '库存数量',
  `xs_num` int(11) NOT NULL COMMENT '销售数量',
  PRIMARY KEY (`pkey`),
  KEY `goods` (`goods`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='市场商品规格';


-- lejia.mkt_goods_special definition

CREATE TABLE `mkt_goods_special` (
  `pkey` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL COMMENT '会员设置 按固定/按比例',
  `price` decimal(11,2) NOT NULL COMMENT '价格',
  `end_date` date DEFAULT NULL COMMENT '到期日期',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='特价商品';


-- lejia.mkt_gtype definition

CREATE TABLE `mkt_gtype` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `photo` varchar(1000) DEFAULT NULL COMMENT '图标',
  `sort` int(11) NOT NULL COMMENT '排序',
  `show_point` tinyint(1) NOT NULL COMMENT '积分商城',
  `show_market` tinyint(1) NOT NULL,
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分类';


-- lejia.mkt_gwc definition

CREATE TABLE `mkt_gwc` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `goods` int(11) NOT NULL COMMENT '商品',
  `space` int(11) NOT NULL COMMENT '规格',
  `num` int(11) NOT NULL,
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  KEY `member` (`member_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购物车';


-- lejia.mkt_index_advert definition

CREATE TABLE `mkt_index_advert` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `photo` varchar(1000) NOT NULL COMMENT '图片',
  `subject` tinyint(4) NOT NULL COMMENT '活动对象 全部/年费会员/活跃会员/......等',
  `url_type` tinyint(4) NOT NULL COMMENT '链接类型 无/链接/积分商城/会员办理',
  `obj_key` varchar(200) DEFAULT NULL COMMENT '对象',
  `sort` int(11) NOT NULL DEFAULT '0',
  `start_date` date NOT NULL COMMENT '启用日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `update_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='app弹窗广告';


-- lejia.mkt_kry_order definition

CREATE TABLE `mkt_kry_order` (
  `pkey` int(11) NOT NULL,
  `uuid` bigint(20) NOT NULL DEFAULT '1' COMMENT '0',
  `order_id` bigint(20) NOT NULL COMMENT '订单Id',
  `kc_code` varchar(100) NOT NULL COMMENT '订单号',
  `status` tinyint(4) NOT NULL COMMENT '订单状态 已完成/其他',
  `source` varchar(100) DEFAULT NULL COMMENT '订单来源',
  `received_amount` bigint(20) NOT NULL COMMENT '商户实收金额',
  `cust_real_pay` bigint(20) NOT NULL COMMENT '用户实付金额',
  `trade_amount` bigint(20) NOT NULL COMMENT '订单原始金额',
  `privilege_amount` bigint(20) NOT NULL COMMENT '优惠总金额',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '顾客ID',
  `custmer_name` varchar(100) DEFAULT NULL COMMENT '顾客昵称',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `mobile` varchar(20) DEFAULT NULL COMMENT '会员手机',
  `order_time` datetime NOT NULL COMMENT '订单时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `orderId` (`order_id`),
  KEY `status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客如云订单';


-- lejia.mkt_kry_vendor definition

CREATE TABLE `mkt_kry_vendor` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '店名',
  `manager` varchar(40) NOT NULL COMMENT '管理员',
  `mobile` varchar(20) NOT NULL COMMENT '手机号码',
  `token` varchar(40) NOT NULL,
  `uuid` bigint(20) NOT NULL COMMENT '客如云ID',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客如云商户';


-- lejia.mkt_logistics definition

CREATE TABLE `mkt_logistics` (
  `pkey` int(11) NOT NULL,
  `name` varchar(40) NOT NULL COMMENT '名称',
  `descp` varchar(200) DEFAULT NULL COMMENT '描述',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='快递公司';


-- lejia.mkt_member definition

CREATE TABLE `mkt_member` (
  `pkey` int(11) NOT NULL,
  `name` varchar(20) DEFAULT NULL COMMENT '名称',
  `mobile` varchar(20) NOT NULL COMMENT '手机',
  `tjr` int(11) DEFAULT NULL,
  `unionid` varchar(40) DEFAULT NULL,
  `openid1` varchar(40) DEFAULT NULL,
  `openid2` varchar(40) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `level` tinyint(4) NOT NULL COMMENT '等级',
  `photo` varchar(200) DEFAULT NULL COMMENT '头像',
  `idcard` varchar(20) DEFAULT NULL COMMENT '身份证',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别',
  `birth` varchar(20) DEFAULT NULL COMMENT '出生日期',
  `login_time` datetime NOT NULL COMMENT '上次登陆时间',
  `login_type` tinyint(4) NOT NULL COMMENT '登陆类型 小程序/公众号/app',
  `last_farmer` varchar(40) NOT NULL COMMENT '登陆市场',
  `end_date` date DEFAULT NULL COMMENT '会员到期日期',
  `area` varchar(40) DEFAULT NULL COMMENT '地区',
  `cust_card` varchar(40) DEFAULT NULL COMMENT '提现银行卡',
  `cust_name` varchar(40) DEFAULT NULL COMMENT '提现银行卡用户名',
  `account_bank` varchar(40) DEFAULT NULL COMMENT '提现银行卡 开户行',
  `remark` varchar(200) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE,
  KEY `unionid` (`unionid`),
  KEY `openid1` (`openid1`),
  KEY `openid2` (`openid2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_member_card definition

CREATE TABLE `mkt_member_card` (
  `pkey` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 初始/已使用/已过期',
  `member_key` int(11) NOT NULL COMMENT '用户',
  `card` int(11) NOT NULL COMMENT '优惠券',
  `card_number` varchar(20) NOT NULL COMMENT '卡券编号',
  `cost` decimal(11,2) NOT NULL COMMENT '卡券价值',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '到期日期',
  `order_id` int(11) DEFAULT NULL,
  `user_farmer` varchar(40) DEFAULT NULL COMMENT '菜场',
  `user_time` datetime DEFAULT NULL COMMENT '使用日期',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime DEFAULT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `cardNumber` (`card_number`) USING BTREE,
  KEY `member` (`member_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户优惠券';


-- lejia.mkt_member_comm definition

CREATE TABLE `mkt_member_comm` (
  `pkey` int(11) NOT NULL,
  `comms` decimal(11,2) NOT NULL COMMENT '佣金值',
  `lock_comms` decimal(11,2) NOT NULL COMMENT '锁定佣金',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_member_comm_line definition

CREATE TABLE `mkt_member_comm_line` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `direct` tinyint(4) NOT NULL COMMENT '借贷标志 借(-)/贷(+)',
  `comms` decimal(11,2) NOT NULL COMMENT '佣金值',
  `balance` decimal(11,2) NOT NULL COMMENT '余额',
  `source` tinyint(4) NOT NULL COMMENT '积分来源  购买+/消费-/手动+-',
  `form_id` varchar(40) DEFAULT NULL COMMENT '来源单据',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_member_gift definition

CREATE TABLE `mkt_member_gift` (
  `pkey` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 初始/已使用/已过期',
  `member_key` int(11) NOT NULL COMMENT '用户',
  `order_pkey` int(11) NOT NULL COMMENT '订单pkey',
  `card_number` varchar(20) NOT NULL COMMENT '卡券编号',
  `goods` int(11) NOT NULL COMMENT '卡券',
  `space` int(11) NOT NULL COMMENT '规格',
  `end_date` date NOT NULL,
  `user_vendor` int(11) NOT NULL COMMENT '使用商户',
  `user_time` datetime DEFAULT NULL COMMENT '使用日期',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL,
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `cardNumber` (`card_number`) USING BTREE,
  KEY `member` (`member_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='礼券明细';


-- lejia.mkt_member_pay definition

CREATE TABLE `mkt_member_pay` (
  `pkey` int(11) NOT NULL COMMENT '主键',
  `member_key` int(11) DEFAULT NULL COMMENT '用户',
  `p_type` tinyint(4) NOT NULL,
  `order_number` varchar(20) NOT NULL COMMENT '订单号',
  `status` tinyint(4) NOT NULL COMMENT '状态 初始/支付成功/支付失败',
  `pay_type` tinyint(4) NOT NULL COMMENT '支付类型 微信/支付宝/电子帐户',
  `amt` decimal(11,2) NOT NULL COMMENT '支付金额',
  `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员年费';


-- lejia.mkt_member_point definition

CREATE TABLE `mkt_member_point` (
  `pkey` int(11) NOT NULL COMMENT '同步member',
  `points` int(11) NOT NULL COMMENT '积分值',
  `lock_points` int(11) NOT NULL COMMENT '锁定积分',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_member_point_line definition

CREATE TABLE `mkt_member_point_line` (
  `pkey` int(11) NOT NULL COMMENT '同步member',
  `member_key` int(11) NOT NULL COMMENT '用户',
  `direct` tinyint(4) NOT NULL COMMENT '借贷标志 借(-)/贷(+)',
  `points` int(11) NOT NULL DEFAULT '0' COMMENT '积分值',
  `balance` int(11) NOT NULL DEFAULT '0' COMMENT '余额',
  `source` tinyint(4) NOT NULL COMMENT '积分来源  购买+/消费-/手动+-/过期',
  `form_id` varchar(40) DEFAULT NULL COMMENT '来源单据',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_member_sign definition

CREATE TABLE `mkt_member_sign` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `sign_date` date NOT NULL COMMENT '签到日期',
  `sign_num` int(11) NOT NULL COMMENT '连续签到天数',
  `points` int(11) NOT NULL COMMENT '所获积分',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='签到记录';


-- lejia.mkt_notice definition

CREATE TABLE `mkt_notice` (
  `pkey` int(11) NOT NULL,
  `title` varchar(100) NOT NULL COMMENT '标题',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `content` text NOT NULL COMMENT '正文',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公告';


-- lejia.mkt_order definition

CREATE TABLE `mkt_order` (
  `pkey` int(11) NOT NULL,
  `kc_code` varchar(20) NOT NULL COMMENT '订单号',
  `member_key` int(11) NOT NULL COMMENT '用户',
  `status` tinyint(4) NOT NULL COMMENT '状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废',
  `order_oir` tinyint(4) NOT NULL COMMENT '订单来源 自营/积分商城/市场商城',
  `order_type` tinyint(4) NOT NULL COMMENT '订单类型 砍价/团购/预售/佣金/普通',
  `pay_type` tinyint(4) NOT NULL,
  `cg_check` tinyint(4) NOT NULL DEFAULT '0' COMMENT '采购标志',
  `pstime` varchar(20) DEFAULT NULL COMMENT '配送时间',
  `weight` decimal(11,2) NOT NULL COMMENT '毛重',
  `postage` decimal(11,2) NOT NULL COMMENT '邮费',
  `amto` decimal(11,2) NOT NULL COMMENT '订单价格',
  `amtall` decimal(11,2) NOT NULL COMMENT '总价',
  `amtn` decimal(11,2) DEFAULT NULL COMMENT '支付金额',
  `pointn` int(11) DEFAULT NULL COMMENT '支付积分',
  `commn` decimal(11,2) DEFAULT NULL COMMENT '支付佣金',
  `card_amt` decimal(11,2) DEFAULT NULL COMMENT '卡券优惠',
  `card` int(11) DEFAULT NULL COMMENT '支付卡券',
  `cut_amt` decimal(11,2) DEFAULT NULL COMMENT '砍价金额',
  `reduce_price` decimal(11,2) NOT NULL COMMENT '会员优惠',
  `tjr` int(11) DEFAULT NULL COMMENT '推荐人',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `code` (`kc_code`) USING BTREE,
  KEY `member` (`member_key`),
  KEY `status` (`status`),
  KEY `orderOir` (`order_oir`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单';


-- lejia.mkt_order_cut definition

CREATE TABLE `mkt_order_cut` (
  `pkey` int(11) NOT NULL,
  `member_pkey` int(11) NOT NULL COMMENT '会员',
  `order_pkey` int(11) NOT NULL COMMENT '订单',
  `cut_amt` decimal(11,2) NOT NULL COMMENT '砍价金额',
  `end_date` date NOT NULL COMMENT '到期日期',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='砍价记录';


-- lejia.mkt_order_desc definition

CREATE TABLE `mkt_order_desc` (
  `pkey` int(11) NOT NULL,
  `logistics` varchar(100) DEFAULT NULL COMMENT '快递公司',
  `kd_code` varchar(40) DEFAULT NULL COMMENT '快递单号',
  `addr` varchar(200) DEFAULT NULL COMMENT '地址',
  `longitude` decimal(11,6) NOT NULL COMMENT '经度',
  `latitude` decimal(11,6) NOT NULL COMMENT '纬度',
  `name` varchar(20) DEFAULT NULL COMMENT '收货人',
  `mobile` varchar(40) DEFAULT NULL COMMENT '收货人手机',
  `remark` varchar(200) DEFAULT NULL COMMENT '留言',
  `fk_time` datetime DEFAULT NULL COMMENT '付款时间',
  `fh_time` datetime DEFAULT NULL COMMENT '发货时间',
  `dr_time` datetime DEFAULT NULL COMMENT '确认时间',
  `tk_time` datetime DEFAULT NULL COMMENT '退款申请时间',
  `tk_desc` varchar(200) DEFAULT NULL COMMENT '退款说明',
  `end_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单说明';


-- lejia.mkt_order_group definition

CREATE TABLE `mkt_order_group` (
  `pkey` int(11) NOT NULL,
  `goods` int(11) NOT NULL,
  `group_id` int(11) NOT NULL COMMENT '团购组',
  `status` tinyint(4) NOT NULL COMMENT '状态 未成团/已成团',
  `buy_num` int(11) NOT NULL COMMENT '当前采购数',
  `group_num` int(11) NOT NULL COMMENT '成团采购数',
  `end_date` date NOT NULL COMMENT '到期日期',
  `order_list` varchar(1000) NOT NULL COMMENT '订单号组',
  `update_time` datetime NOT NULL,
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='团购记录';


-- lejia.mkt_order_line definition

CREATE TABLE `mkt_order_line` (
  `pkey` int(11) NOT NULL,
  `order_pkey` int(11) DEFAULT NULL,
  `goods` int(11) DEFAULT NULL,
  `space` int(11) DEFAULT NULL,
  `goods_name` varchar(100) DEFAULT NULL,
  `price` decimal(11,2) DEFAULT NULL,
  `pricen` decimal(11,2) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `amt` decimal(11,2) DEFAULT NULL,
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单明细';


-- lejia.mkt_ori_test definition

CREATE TABLE `mkt_ori_test` (
  `pkey` int(11) NOT NULL,
  `merchant` varchar(100) NOT NULL COMMENT '检测商户',
  `goods` varchar(100) NOT NULL COMMENT '检测商品',
  `entry` varchar(100) NOT NULL COMMENT '检测项目',
  `test_result` tinyint(1) NOT NULL COMMENT '检测结果',
  `test_date` date NOT NULL COMMENT '检测日期',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='检测信息';


-- lejia.mkt_ori_ven definition

CREATE TABLE `mkt_ori_ven` (
  `pkey` int(11) NOT NULL,
  `merchant` varchar(100) NOT NULL COMMENT '溯源商户',
  `goods` varchar(100) NOT NULL COMMENT '溯源商品',
  `vendor` varchar(100) DEFAULT NULL COMMENT '供应商',
  `ori_date` date DEFAULT NULL,
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='溯源信息';


-- lejia.mkt_pay_line definition

CREATE TABLE `mkt_pay_line` (
  `pkey` int(11) NOT NULL COMMENT '主键',
  `order_number` varchar(40) DEFAULT NULL COMMENT '订单号',
  `kc_code` varchar(40) DEFAULT NULL COMMENT '流水号',
  `status` varchar(20) DEFAULT NULL,
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付类型 微信/支付宝/电子帐户',
  `amt` varchar(20) DEFAULT NULL COMMENT '支付金额',
  `pay_time` varchar(20) DEFAULT NULL COMMENT '支付成功时间',
  `created_time` datetime DEFAULT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `order_number` (`order_number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付流水';


-- lejia.mkt_point_pay definition

CREATE TABLE `mkt_point_pay` (
  `pkey` int(11) NOT NULL,
  `member_key` int(11) NOT NULL COMMENT '用户',
  `order_number` varchar(20) DEFAULT NULL COMMENT '订单号',
  `p_type` tinyint(4) NOT NULL COMMENT '类型 抽奖/消费',
  `points` int(11) NOT NULL COMMENT '积分值',
  `created_time` datetime DEFAULT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_postage_config definition

CREATE TABLE `mkt_postage_config` (
  `pkey` int(11) NOT NULL,
  `weight` decimal(11,2) NOT NULL COMMENT '重量',
  `postage` decimal(11,2) NOT NULL COMMENT '邮费',
  `logistics` int(11) DEFAULT NULL,
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='快递费';


-- lejia.mkt_refund definition

CREATE TABLE `mkt_refund` (
  `pkey` int(11) NOT NULL COMMENT '主键',
  `kc_code` varchar(20) NOT NULL COMMENT '单据号',
  `order_num` int(11) NOT NULL COMMENT '订单',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态 申请中/同意/已退款/拒绝',
  `member_key` int(11) NOT NULL COMMENT '用户',
  `reason` varchar(1000) NOT NULL COMMENT '退款理由',
  `photo` varchar(2000) DEFAULT NULL COMMENT '照片',
  `amtall` decimal(11,2) NOT NULL COMMENT '订单金额',
  `amtre` decimal(11,2) NOT NULL COMMENT '退款金额',
  `del_desc` varchar(1000) DEFAULT NULL COMMENT '处理意见',
  `del_by` int(11) DEFAULT NULL COMMENT '处理员',
  `del_time` datetime DEFAULT NULL COMMENT '处理时间',
  `re_time` datetime DEFAULT NULL COMMENT '退款时间',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `order` (`order_num`),
  UNIQUE KEY `code` (`kc_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='退款';


-- lejia.mkt_search definition

CREATE TABLE `mkt_search` (
  `pkey` int(11) NOT NULL,
  `stype` tinyint(4) NOT NULL COMMENT '搜索类型 商品/菜谱/积分商城',
  `descp` varchar(100) NOT NULL COMMENT '搜索内容',
  `member_key` int(11) DEFAULT NULL COMMENT '用户',
  `created_time` datetime NOT NULL COMMENT '搜索时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_search_hot definition

CREATE TABLE `mkt_search_hot` (
  `pkey` int(11) NOT NULL,
  `stype` tinyint(4) NOT NULL COMMENT '搜索类型 商品/菜谱/积分商城',
  `descp` varchar(100) NOT NULL COMMENT '搜索内容',
  `created_time` datetime NOT NULL COMMENT '搜索时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_vendor definition

CREATE TABLE `mkt_vendor` (
  `pkey` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '店名',
  `manager` varchar(40) DEFAULT NULL COMMENT '负责人',
  `addr` varchar(200) NOT NULL COMMENT '地址',
  `mobile` varchar(20) NOT NULL COMMENT '手机号码',
  `unionid` varchar(40) DEFAULT NULL,
  `openid1` varchar(40) DEFAULT NULL,
  `openid2` varchar(40) DEFAULT NULL,
  `bankname` varchar(200) DEFAULT NULL COMMENT '开户行',
  `bankuser` varchar(40) DEFAULT NULL COMMENT '开户人',
  `bankcard` varchar(40) DEFAULT NULL COMMENT '银行卡号',
  `visit_count` int(11) NOT NULL COMMENT '访问数量',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `update_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合作商户';


-- lejia.mkt_vendor_goods definition

CREATE TABLE `mkt_vendor_goods` (
  `pkey` int(11) NOT NULL,
  `vendor` int(11) NOT NULL COMMENT '商户',
  `goods` int(11) NOT NULL,
  `price` decimal(11,2) NOT NULL,
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `update_time` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_vendor_order definition

CREATE TABLE `mkt_vendor_order` (
  `pkey` int(11) NOT NULL,
  `order_pkey` int(11) NOT NULL,
  `order_line_pkey` int(11) NOT NULL,
  `vendor` int(11) NOT NULL COMMENT '商户',
  `goods` int(11) NOT NULL COMMENT '商品',
  `space` int(11) NOT NULL COMMENT '规格',
  `price` decimal(11,2) NOT NULL COMMENT '采购价格',
  `num` int(11) NOT NULL COMMENT '数量',
  `amt` decimal(20,8) NOT NULL COMMENT '结算金额',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `farmer` varchar(40) NOT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `update_by` int(11) NOT NULL COMMENT '建档员',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_vendor_point definition

CREATE TABLE `mkt_vendor_point` (
  `pkey` int(11) NOT NULL,
  `points` int(11) NOT NULL COMMENT '积分值',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合作商户积分';


-- lejia.mkt_vendor_point_line definition

CREATE TABLE `mkt_vendor_point_line` (
  `pkey` int(11) NOT NULL,
  `vendor` int(11) NOT NULL COMMENT '商户 pkey',
  `points` int(11) NOT NULL COMMENT '积分值',
  `balance` int(11) NOT NULL COMMENT '余额',
  `source` tinyint(4) NOT NULL COMMENT '积分来源 购买+/消费-/活动+/手动+-',
  `form_id` int(11) NOT NULL COMMENT '关联流水',
  `member_key` int(11) NOT NULL COMMENT '支付会员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.mkt_ware_line definition

CREATE TABLE `mkt_ware_line` (
  `pkey` int(11) NOT NULL,
  `ware_type` tinyint(4) NOT NULL,
  `goods` int(11) NOT NULL,
  `goods_name` varchar(100) NOT NULL,
  `space` int(11) NOT NULL,
  `space_name` varchar(40) NOT NULL,
  `order_number` varchar(100) DEFAULT NULL,
  `price` decimal(11,2) DEFAULT NULL,
  `num` int(11) NOT NULL,
  `supplier` varchar(200) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `actual_num` int(11) NOT NULL,
  `created_by` int(11) DEFAULT NULL COMMENT '建档员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.sys_company definition

CREATE TABLE `sys_company` (
  `pkey` varchar(40) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `manager_user` bigint(20) NOT NULL COMMENT '管理员主键',
  `manager` varchar(40) NOT NULL COMMENT '管理员',
  `mobile` varchar(20) NOT NULL COMMENT '登陆帐号',
  `addr` varchar(200) DEFAULT NULL COMMENT '地址',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`),
  UNIQUE KEY `mobile` (`mobile`) USING BTREE,
  KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.sys_farmer definition

CREATE TABLE `sys_farmer` (
  `pkey` varchar(40) NOT NULL,
  `name` varchar(40) NOT NULL COMMENT '菜场名称',
  `kc_code` varchar(40) DEFAULT NULL COMMENT '菜场编码',
  `manager_user` int(11) NOT NULL COMMENT '管理员主键',
  `manager` varchar(40) NOT NULL COMMENT '管理员',
  `mobile` varchar(20) NOT NULL COMMENT '负责人手机',
  `logo` varchar(200) DEFAULT '' COMMENT '市场Logo',
  `content` varchar(2000) DEFAULT NULL COMMENT '介绍',
  `tel` varchar(20) DEFAULT NULL COMMENT '售后电话',
  `photo1` varchar(200) DEFAULT NULL COMMENT '市场照片',
  `photo2` varchar(200) DEFAULT NULL COMMENT '市场照片',
  `photo3` varchar(200) DEFAULT NULL COMMENT '市场照片',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `enabled` tinyint(1) NOT NULL COMMENT '启用标志',
  `id_del` tinyint(1) NOT NULL COMMENT '是否已删除',
  `dept` varchar(40) NOT NULL COMMENT '部门',
  `org` varchar(40) NOT NULL COMMENT '机构',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.sys_farmer_config definition

CREATE TABLE `sys_farmer_config` (
  `pkey` varchar(40) NOT NULL,
  `y_status` tinyint(1) NOT NULL COMMENT '营业状态 营业/休息',
  `yytb` varchar(20) NOT NULL COMMENT '营业时间起始',
  `yyte` varchar(20) NOT NULL COMMENT '营业时间结束',
  `addr` varchar(200) DEFAULT NULL COMMENT '地址',
  `longitude` decimal(10,6) NOT NULL COMMENT '经度',
  `latitude` decimal(10,6) NOT NULL COMMENT '纬度',
  `yj_time` varchar(10) DEFAULT NULL COMMENT '夜间时间配置',
  `yj_pos` int(11) DEFAULT NULL COMMENT '夜间运费配置',
  `delivery_range` int(11) NOT NULL COMMENT '配送范围',
  `ps_time` varchar(1000) NOT NULL COMMENT '配送时间',
  `abnormal_num` int(11) DEFAULT NULL COMMENT '异常订单',
  `free_delivery` decimal(11,2) DEFAULT NULL COMMENT '包邮金额',
  `is_free` tinyint(1) DEFAULT NULL,
  `member_photo` varchar(200) DEFAULT NULL COMMENT '会员办理图片',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.sys_log definition

CREATE TABLE `sys_log` (
  `pkey` bigint(20) NOT NULL,
  `user_pkey` int(11) DEFAULT NULL,
  `user_id` varchar(40) DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `mobile` varchar(40) DEFAULT NULL COMMENT '手机号码',
  `app_id` varchar(40) DEFAULT NULL,
  `remote_address` varchar(20) DEFAULT NULL,
  `operation` varchar(20) DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `proc_millisecond` int(11) DEFAULT NULL,
  `success` bit(1) DEFAULT NULL,
  `content` varchar(200) DEFAULT NULL,
  `result` varchar(200) DEFAULT NULL,
  `market` varchar(40) DEFAULT NULL,
  `company` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.sys_log_yuanlai definition

CREATE TABLE `sys_log_yuanlai` (
  `pkey` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL COMMENT '类型 登陆/操作/查询',
  `description` varchar(200) NOT NULL COMMENT '描述',
  `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `created_by` int(11) NOT NULL COMMENT '建档员',
  `created_time` datetime NOT NULL COMMENT '建档时间',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.sys_user definition

CREATE TABLE `sys_user` (
  `pkey` int(11) NOT NULL,
  `mobile` varchar(20) NOT NULL COMMENT '手机号码',
  `nickname` varchar(40) NOT NULL COMMENT '昵称',
  `role_key` varchar(40) DEFAULT NULL,
  `farmer` varchar(40) DEFAULT NULL COMMENT '市场',
  `company` varchar(40) NOT NULL COMMENT '公司',
  `row_vension` smallint(6) NOT NULL COMMENT '版本',
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- lejia.wx_account definition

CREATE TABLE `wx_account` (
  `pkey` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(100) NOT NULL,
  `account_token` varchar(200) NOT NULL COMMENT '公众帐号TOKEN',
  `account_number` varchar(100) NOT NULL COMMENT '公众微信号',
  `account_id` varchar(100) NOT NULL COMMENT '原始ID',
  `account_type` tinyint(4) NOT NULL COMMENT '公众帐号类型',
  `account_email` varchar(100) DEFAULT NULL,
  `account_desc` varchar(200) DEFAULT NULL COMMENT '公众帐号描述',
  `account_appid` varchar(200) NOT NULL COMMENT '公众帐号APPID',
  `account_appsecret` varchar(200) NOT NULL COMMENT '公众帐号APPSECRET',
  `access_token` varchar(200) DEFAULT NULL,
  `access_time` datetime DEFAULT NULL COMMENT 'TOKEN获取时间',
  `row_version` smallint(6) NOT NULL,
  PRIMARY KEY (`pkey`) USING BTREE,
  UNIQUE KEY `account_appid` (`account_appid`) USING BTREE,
  UNIQUE KEY `account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;