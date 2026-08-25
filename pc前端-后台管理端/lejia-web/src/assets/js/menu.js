/*
 * @Author: 沙晓
 * @Date: 2025-06-20 09:56:02
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-07-21 14:36:04
 * @Description: file content
 * @FilePath: /lejia-web/src/assets/js/menu.js
 */
export const MenuData = {
  //首页
  zy_boss_index: "/index",
  zy_market_index: "/index",

  //系统管理 sys
  zy_boss_sys_manager: "/sys/user",
  zy_boss_sys_user: "/sys/user", //用户
  zy_boss_sys_role: "/sys/role", //角色
  zy_sys_log: "/sys/log", //日志
  zy_sys_information: "/sys/gzh",
  zy_sys_shield_version: "/sys/version",

  zy_market_sys_manager: "/sys/user",
  zy_market_sys_user: "/sys/user", //用户
  zy_market_sys_role: "/sys/role", //角色

  zy_sys_user: "/sys/user", //用户
  zy_sys_role: "/sys/role", //角色
  zy_app_manager: "/sys/mallAdmin", //商城管理员

  //system 运营商管理端
  //基础设置 base
  zy_boss_basisinfo_manager: "/base/info",
  zy_basis_mall: "/base/info", //商城配置
  zy_basis_postage_config: "/base/postage", //运费配置
  zy_basis_goods_classification: "/base/classic", //商品分类
  zy_basis_goods_house: "/base/goods", //商品库
  zy_boss_goods_supply: "/base/supply", //商品供应库
  zy_basis_search_keyword: "/base/searchCode", //搜索词管理
  zy_basis_market_dispatch: "/base/dispatch", //派单配置
  zy_boss_promote_manager: "/base/promote", // 推广管理
  zy_boss_third_payment: "/base/thirdPayment", // 第三方支付渠道
  //财务管理
  sys_finance_manage: "/finance/account",
  sys_account_manage: "/finance/account",//账户管理
  sys_fund_details: "/finance/details",//资金明细
  sys_settlement_bill: "/finance/bill",//结算账单
  // 京东优选
  zy_boss_jd_goods: "/jd/goods",
  zy_boss_jd_order: "/jd/order",
  zy_boss_jd_order_refund: "/jd/refundOrder",
  zy_boss_jd_goods_upd_notice: "/jd/updNotice",
  zy_boss_jd_category_rel: "/jd/CategoryRelation",
  // 商城管理 mall
  zy_boss_mall_manager: "/mall/goods/INTEGRAL_GOODS",
  zy_mall_goods_point: "/mall/goods/INTEGRAL_GOODS", //商品管理 积分商品
  zy_mall_goods_point_bnyp: "/mall/goods/INTEGRAL_BNYP_GOODS",  //商品管理 滨农优品商品
  zy_mall_goods_point_msd: "/mall/goods/INTEGRAL_MSD_GOODS",  //商品管理 热力豆商品
  zy_mall_goods_presale: "/mall/goods/INTEGRAL_PRESALE_GOODS",    // 商品管理 预售商品管理
  zy_mall_gift: "/mall/goods/GIFT_GOODS", //商品管理  礼品券商品
  zy_mall_coupon: "/mall/goods/COUPON_GOODS", //商品管理  优惠券商品
  zy_mall_goods_comment: "/mall/comment",//菜品评价管理


  zx_mall_goods_recommend: "/mall/recommend",    //推荐商品管理
  zy_mall_feedback: "/mall/feedback", //意见反馈
  zy_mall_feedback_2: "/mall/feedback", //意见反馈(仅浏览)
  zy_mall_problem: "/mall/problem", //常见问题
  //广告管理
  zy_adver_manager: "/ads/ads",
  zy_boss_index_advert: "/ads/ads", //弹窗广告
  zy_boss_special_area_advert: "/ads/areaAds", //专区广告
  zy_mall_adver: "/ads/pointAds", //积分商城广告管理
  zy_boss_mall_gtype_adver: "/ads/gtypeAds", //分类页广告

  //会员管理 member
  zy_boss_member_manager: "/member/member",
  zy_member_center: "/member/member", //会员中心
  zy_member_center_2: "/member/member", //会员中心(仅浏览)
  zy_member_recharge_record: "/member/deposit", //充值记录
  zy_member_point: "/member/points", //积分查询
  zy_tag_manage: "/member/tagManage", //标签管理
  zy_member_comm: "/member/purseManage", //钱包查询
  zy_member_msd: "/member/MSD", //热力豆管理
  zy_member_recharge_card: "/member/rechargeCard", //热力豆管理

  //活动管理 activity
  zy_boss_activity_manager: "/activity/lottery",
  zy_activity_prize: "/activity/lottery", //奖品配置
  zy_activity_inquire: "/activity/winning", //中奖查询
  zy_activity_inquire_2: "/activity/winning", //中奖查询(仅浏览)

  //卡券管理 coupon
  zy_boss_coupon_manager: "/coupon/coupon",
  zy_coupon_manager: "/coupon/coupon", //卡券管理
  zy_coupon_grant: "/coupon/grant", //卡券发放
  zy_coupon_inquire: "/coupon/use", //卡券使用查询

  //交易管理 order
  zy_boss_order_manager: "/order/mall",
  zy_order_mall: "/order/mall", //商城订单
  zy_order_market_refund: "/order/refund", //退款管理
  zy_order_refund_2: "/order/refund", //退款管理(仅浏览)
  zy_refund_order_mall: "/order/refund",  //商城退款订单
  zy_order_draw: "/order/tixian", //提现管理
  zy_order_draw_2: "/order/tixian", //提现管理(仅浏览)
  zy_bill_manager: "/order/report", //对账中心
  zy_bill_detail: "/order/detail", //账单明细

  //市场运营 operation
  zy_boss_boss_operation: "/operation/company",
  zy_boss_company: "/operation/company", //公司市场
  zy_boss_company_2: "/operation/company", //公司市场(仅浏览)

  //合作商户 vendor
  zy_boss_vendor: "/vendor/merchant",
  zy_vendor_manager: "/vendor/merchant", //积分商户商户
  zy_vendor_manager_2: "/vendor/merchant", //积分商户商户(仅浏览)
  zy_vendor_point_order: "/vendor/order", //积分订单
  zy_boss_market_vendor_manager: "/vendor/marketMerchant", //市场商城商户
  zy_market_vendor_revoke: "/vendor/cancel", //市场商城商户
  zy_boss_vendor_settlement: "/vendor/comsettle", //商户结算-佣金
  zy_boss_vendor_bill: "/vendor/combill", //商户对账-佣金
  zy_boss_vendor_settlement_report: "/vendor/settlereport", //结算报表
  zy_market_packing_charge: "/vendor/packingCharge", // 打包物料费明细
  zy_supplier_manager: "/vendor/supplierManager", // 供应商管理

  //客如云 kry
  zy_boss_kry: "/kry/kry",
  zy_kry_vendor: "/kry/kry", //客如云商户
  zy_kry_vendor_2: "/kry/kry", //客如云商户(仅浏览)
  zy_kry_order: "/kry/order", //客如云订单
  zy_kry_order_2: "/kry/order", //客如云订单(仅浏览)

  //数据中心
  zy_boss_data_center: "/data/table/ZONEBUSINESS",
  zy_data_01: "/data/table/ZONEBUSINESS", //专区营业报表
  zy_data_02: "/data/table/GOODSSALE", //商品销售统计
  zy_data_03: "/data/chart/GOODSSALE", //商品销售分析
  zy_data_04: "/data/table/GIFTS", //奖品统计
  zy_data_05: "/data/chart/TIMESALE", //时间段销售额
  zy_data_06: "/data/chart/PAYMEMBER", //付费会员办理
  zy_data_07: "/data/members", //付费会员明细
  zy_data_08: "/data/table/INTEGRALCHANGE", //积分兑换统计
  zy_data_09: "/data/table/MEMBERCONSUME", //付费会员消费
  zy_data_10: "/data/chart/ACCESS", //用户访问报表
  zy_data_11: "/data/chart/NEWUSERS", //新增用户报表
  zy_data_12: "/data/table/COUPONUSE", //卡券使用统计
  zy_data_13: "/data/table/COOKFDSALE", //菜品销售统计
  zy_data_14: "/data/table/INTEGRALSALE", //"商户积分统计
  zy_data_15: "/data/table/COMPANYSALE", //公司销售统计
  zy_data_16: "/data/table/MARKETSALE", //市场销售统计
  zy_data_17: "/data/table/FREIGHT", //"运费报表
  zy_data_18: "/bigData", //大数据屏
  zy_data_19: "/data/operating/operate", // 经营数据统计
  zy_data_supplier_sale: "/data/supplierSale", // 供应商销售统计
  zy_data_goods_summary: "/data/goodsSummary",  // 商品明细统计
  zy_market_data_goods_summary: '/data/goodsSummary', // 商品明细统计
  zy_data_jd_sale: "/data/JDSale", // 京东销售统计

  //end system

  //market 市场管理端
  //基础设置 base
  zy_market_basisinfo_manager: "/base/market",
  zy_basis_market_maintenance: "/base/market", //市场维护
  zy_basis_market_postage_config: "/base/market/postage", //运费配置
  zy_basis_market_gtype: "/base/market/gtype", // 商品分类
  zy_basis_market_search_keyword: "/base/searchCode", //搜索词管理
  zy_basis_market_device: '/base/market/device', // 设备管理


  //财务管理
  market_finance_manage: "/finance/marketAccount",
  market_account_manage: "/finance/marketAccount",//账户管理
  market_fund_details: "/finance/marketDetails",//资金明细
  market_settlement_bill: "/finance/marketBill",//结算账单

  //食安公示 publicity
  zy_market_publicity: "/publicity/detection",
  zy_market_maintenance_detection_info: "/publicity/detection", //检测信息
  zy_market_maintenance_retroactive_info: "/publicity/retroactive", //追溯信息

  //市场管理 market
  zy_market_manager: "/market/ads",
  zy_market_adver: "/market/ads", //广告管理
  zy_market_gtype_adver: "/market/gtypeAds",//分类页广告
  zy_market_funmenu: "/market/funmenuConfig",//分类页广告
  zy_order_rider_manager: "/market/rider", //骑手管理
  zy_market_customer_feedback: "/market/feedback", //意见反馈
  zy_market_desktop_manager: "/market/desktop", //桌位管理
  zy_market_goods_comment: "/market/comment",//菜品评价管理

  //商品管理 goods
  zy_market_goods_manager: "/goods/MARKET_GOODS",
  zy_market_goods_ordinary_manager: "/goods/MARKET_GOODS", //市场商品管理
  zy_market_goods_member_manager: "/goods/MEMBER_GOODS", //会员商品管理
  zy_market_goods_share_manager: "/goods/SHARE_GOODS", //分享商品管理
  zy_market_goods_special_manager: "/goods/SPECIAL_GOODS", //特价商品管理
  zy_market_goods_presale_manager: "/goods/PRESALE_GOODS", //预售商品管理
  zy_market_goods_poverty_manager: "/goods/POVERTY_ALLEVIATION_GOODS", //扶贫商品管理
  zy_market_goods_collage_manager: "/goods/COLLAGE_GOODS", //团购商品管理
  zy_market_goods_cut_manager: "/goods/CUT_GOODS", //砍价商品管理
  zy_market_goods_supply: "/goods/supply/supply", //商品供应库

  //菜谱管理
  zy_market_cookfd_manager: "/cookfd/cookfd",
  zy_cookfd_manager: "/cookfd/cookfd", //菜谱管理
  zy_cookfd_type: "/cookfd/cooktype", //菜谱分类

  //卡券管理
  zy_market_coupon_manager: "/coupon/coupon", //卡券管理
  zy_market_coupon_grant: "/coupon/grant", //卡券发放
  zy_market_coupon_inquire: "/coupon/use", //卡券使用查询
  zy_market_gift_manager: '/coupon/gift', //礼品券管理
  zy_market_activity_manager: "/coupon/events", //卡券活动
  zy_market_gift_usage_query: "/coupon/giftUse", //礼品券使用查询

  //交易管理
  zy_market_order_manager: "/order/market",
  zy_order_market_offline: "/order/market", //市场订单
  zy_order_rider_offline: "/order/rider", //骑手订单
  zy_order_collage_offline: "/order/collage", //团购订单

  //合作商户
  zy_market_vendor: "/vendor/merchant",
  zy_market_vendor_manager: "/vendor/merchant", //商户管理
  zy_market_boutique_vendor: "/vendor/PreferredMerchant", //商户管理
  zy_market_vendor_settlement: "/vendor/settle", //商户结算
  zy_market_vendor_settlements: "/vendor/comsettle", //商户结算-佣金
  zy_market_vendor_bill: "/vendor/bill", //商户对账
  zy_market_vendor_bills: "/vendor/combill", //商户对账-佣金
  zy_market_vendor_settlement_report: "/vendor/settlereport", //结算报表
  zy_market_vendor_wallet: "/vendor/wallet", //商户钱包
  zy_market_vendor_withdrawal: "/vendor/withdrawal", //提现打款

  //数据中心
  zy_market_data_center: "/data/table/ZONEBUSINESS",
  zy_market_data_01: "/data/table/ZONEBUSINESS", //专区营业报表
  zy_market_data_02: "/data/table/GOODSSALE", //商品销售统计
  zy_market_data_03: "/data/chart/GOODSSALE", //商品销售分析
  zy_market_data_04: "/data/Abnormal", //异常货物分析
  zy_market_data_05: "/data/chart/TIMESALE", //时间段销售额
  zy_market_data_06: "/data/table/COUPONUSE", //优惠券使用统计
  zy_market_data_07: "/data/table/COOKFDSALE", //菜品销售统计
  zy_market_data_08: "/data/table/MARKETSALE", //市场销售统计
  zy_market_data_09: "/data/table/TOPCOMMISSION", //佣金达人
  zy_market_data_10: "/data/table/COMMISSIONEARN", //佣金收入明细
  zy_market_data_11: "/data/table/COURIER", //配送员绩效表
  zy_market_data_12: "/data/purchase", //商户采购报表
  zy_market_data_13: "/data/operating/market", //经营数据统计
  //end market

  //公司管理端
  //首页
  zy_company_index: "/index", //公司首页

  //公司管理 company
  zy_company_manager: "/company/info", //公司管理
  zy_company_info: "/company/info", //公司信息

  //报表查询
  zy_report_inquire: "/data/table/ZONEBUSINESS", //报表查询
  // "zy_report_order_inquire": "", //订单查询
  // "zy_report_marketing_statistics": "", //市场销售统计
  // "zy_report_dishes_statistics": "", //菜品大类销售统计
  // "zy_report_dish_sales_statistics": "", //菜品单品销售统计
  // "zy_report_member_goods_sales_statistics": "", //会员商品销售统计
  zy_company_data_01: "/data/table/ZONEBUSINESS", //专区营业报表
  zy_company_data_02: "/data/table/GOODSSALE", //商品销售统计
  zy_company_data_03: "/data/chart/GOODSSALE", //商品销售分析
  zy_company_data_04: "/data/chart/TIMESALE", //时间段销售额
  zy_company_data_05: "/data/table/COUPONUSE", //卡券使用统计
  zy_company_data_06: "/data/table/COOKFDSALE", //菜品销售统计
  zy_company_data_07: "/data/table/MARKETSALE" //"市场销售统计

  // end 公司管理端
};

export const MenuIcon = {
  zy_boss_index: "iconfont iconshouye", //首页
  zy_market_index: "iconfont iconshouye", //首页
  zy_boss_sys_manager: "iconfont iconxitongshezhi", //系统管理
  zy_market_sys_manager: "iconfont iconxitongshezhi", //系统管理
  zy_boss_basisinfo_manager: "iconfont iconjichushijianfenlei", //基础设置
  sys_finance_manage: "iconfont iconwodehcplanbaobiao",// 财务管理
  market_finance_manage: "iconfont iconwodehcplanbaobiao",// 财务管理
  zy_boss_jd: "iconfont iconshangcheng",
  zy_boss_mall_manager: "iconfont iconshangcheng", //商城管理
  zy_adver_manager: "iconfont iconzujian-danchuang", //广告管理
  zy_boss_member_manager: "iconfont iconhuiyuan", //会员管理
  zy_boss_activity_manager: "iconfont iconyingxiao", //活动管理
  zy_boss_coupon_manager: "iconfont iconqiaquan", //卡券管理
  zy_boss_order_manager: "iconfont iconjiaoyizhanghuguanli", //交易管理
  zy_boss_boss_operation: "iconfont iconxingzhuangjiehe", //市场运营
  zy_boss_vendor: "iconfont iconhezuoshixin", //合作商户
  zy_market_vendor: "iconfont iconhezuoshixin", //合作商户
  zy_boss_kry: "iconfont iconheader-12", //客如云
  zy_boss_data_center: "iconfont iconwodehcplanbaobiao", //数据中心

  zy_market_basisinfo_manager: "iconfont iconjichushijianfenlei", //基础设置
  zy_market_publicity: "iconfont icongonggao", //食安公示
  zy_market_manager: "iconfont iconxingzhuangjiehe", //市场管理
  zy_market_goods_manager: "iconfont icongoods-copy", //商品管理
  zy_market_cookfd_manager: "iconfont iconcaidan", //菜谱管理
  zy_market_card_manager: "iconfont iconqiaquan", //卡券管理
  zy_market_order_manager: "iconfont iconjiaoyizhanghuguanli", //交易管理
  zy_market_data_center: "iconfont iconwodehcplanbaobiao", //数据中心

  zy_company_manager: "iconfont icongongsi", //公司管理
  zy_report_inquire: "iconfont iconwodehcplanbaobiao" //报表查询
};