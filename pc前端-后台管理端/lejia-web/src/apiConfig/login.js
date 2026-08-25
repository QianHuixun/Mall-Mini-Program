let baseDomain = "",
  baseUrl = "",
  fileUrl = "",
  codeUrl = "";
switch (process.env.VUE_APP_TITLE) {
  case "development":
    baseDomain = "https://cloudtest.xinanshizu.com";
    baseUrl = "https://cloudtest.xinanshizu.com/zyysc";
    fileUrl = "https://cloudtest.xinanshizu.com/file";
    codeUrl = "https://cloudtest.xinanshizu.com/auth";
    break;
  case "test":
    baseDomain = "https://cloudtest.xinanshizu.com";
    baseUrl = "https://cloudtest.xinanshizu.com/zyysc";
    fileUrl = "https://cloudtest.xinanshizu.com/file";
    codeUrl = "https://cloudtest.xinanshizu.com/auth";
    break;
  default:
    baseDomain = "https://small.xinanshizu.com";
    baseUrl = "https://small.xinanshizu.com/zyysc";
    fileUrl = "https://small.xinanshizu.com/file";
    codeUrl = "https://small.xinanshizu.com/auth";
}

/**公共接口*/
export const common = {
  uploadImage: `${fileUrl}/v3/image/upload`, //图片上传
  uploadFile: `${fileUrl}/v2/uploadFile`, //文件上传
  getIdentity: `${baseUrl}/v1/get/ideninfo`, //获取用户角色，1 运营商 2 市场 3公司
  queryConfig: `${baseUrl}/v1/sys/config/get`, //获取广告配置；合作商户；是否自动采购 的配置
  updConfig: `${baseUrl}/v1/sys/config/upd`, //修改广告配置；合作商户；是否自动采购配置
  querySettlementMethod: `${baseUrl}/v1/market/vendorOrder/settlementMethod`, //市场端-采购方式
  marketGoodsDrop: `${baseUrl}/v2/market/goods/market/drop`, //卡券限制商品
  editorHost: `${baseDomain}` // 富文本地址
};

/**下拉列表相关接口*/
export const dropdown = {
  // userList: `${baseUrl}/v1/sys/user/get`,//获取用户列表
  roleList: `${baseUrl}/v1/sys/role/queryForUser`, //读取可用角色
  marketList: `${baseUrl}/v1/sys/market/query`, //读取市场列表
  marketDrop: `${baseUrl}/v2/sys/market/drop`, //读取市场列表  运营端：所有市场，公司端：公司下所有市场，市场端：自己市场
  newMarketList: `${baseUrl}/v1/sys/market/drop`, //读取市场列表
  supplyMarketList: `${baseUrl}/v1/sys/market/drop/supply`, //读取市场列表
  goodsList: `${baseUrl}/v1/market/goods/main/query`, //读取 卡券中的商品列表
  courierList: `${baseUrl}/v1/market/courier/query`, //读取快递员列表
  couponList: `${baseUrl}/v1/market/card/query/drop`, //读取可发放优惠券
  giftVendorList: `${baseUrl}/v1/market/vendor/drop/gift`, //获取市场的商户列表
  thridMarketList: `${baseUrl}/v3/thrid/payment/list/market`, //获取心安食足市场名称
  tagsList: `${baseUrl}/v1/market/tag/list/drop`, //获取标签列表
  supplierList: `${baseUrl}/v1/market/supplier/options` //获取标签列表
};

/**登录导航相关接口*/
export const login = {
  login: `${baseUrl}/v1/login`, //登录
  codeImg: `${baseUrl}/captcha/image`, //获取验证码图片
  loginCaptcha: `${baseUrl}/account/v2/user/loginCaptcha`, //获取登录验证码
  resetpwd: `${baseUrl}/wxmp-cust/v1/sys/user/resetpwd/confirm`, //修改密码
  resetpwdCaptcha: `${baseUrl}/account/v2/user/resetpwd/setp1`, //获取修改密码验证码
  removeToken: `${codeUrl}/removeToken`, //退回登录
  modeifypwd: `${baseUrl}/v1/sys/user/modeify`, //修改密码
  getMenu: `${baseUrl}/v1/sys/config/getMenu` //修改密码
};

export const market = {
  delCom: `${baseUrl}/v1/sys/company/del`, //删除公司
  startCom: `${baseUrl}/v1/sys/company/enable/start`, //公司启用
  stopCom: `${baseUrl}/v1/sys/company/enable/stop`, //公司停用
  getComInfo: `${baseUrl}/v1/sys/company/get`, //获取公司
  insCom: `${baseUrl}/v1/sys/company/ins`, //新增公司
  queryCom: `${baseUrl}/v1/sys/company/query`, //获取公司列表
  updCom: `${baseUrl}/v1/sys/company/upd`, //修改公司

  delMarket: `${baseUrl}/v1/sys/market/del`, //删除市场
  startMarket: `${baseUrl}/v1/sys/market/enable/start`, //市场启用
  stopMarket: `${baseUrl}/v1/sys/market/enable/stop`, //市场停用
  getMarketInfo: `${baseUrl}/v1/sys/market/get`, //获取市场
  insMarket: `${baseUrl}/v1/sys/market/ins`, //新增市场
  // queryMarket: `${baseUrl}/v1/sys/market/query`, //获取市场列表
  updMarket: `${baseUrl}/v1/sys/market/upd`, //修改市场

  //检测信息
  delDetection: `${baseUrl}/v1/market/oritest/del`, //删除检测信息
  getDetection: `${baseUrl}/v1/market/oritest/get`, //获取检测信息
  importDetection: `${baseUrl}/v1/market/oritest/importexcel`, //导入检测信息
  insDetection: `${baseUrl}/v1/market/oritest/ins`, //新增检测信息
  queryDetection: `${baseUrl}/v1/market/oritest/query`, //获取检测信息列表
  updDetection: `${baseUrl}/v1/market/oritest/upd`, //修改检测信息
  downDetection: `${baseUrl}/v1/market/oritest/down/template`, //溯源信息模板下载

  //溯源信息
  delRetroactive: `${baseUrl}/v1/market/oriven/del`, //删除溯源信息
  getRetroactive: `${baseUrl}/v1/market/oriven/get`, //获取溯源信息
  importRetroactive: `${baseUrl}/v1/market/oriven/importexcel`, //删除溯源信息
  insRetroactive: `${baseUrl}/v1/market/oriven/ins`, //新增溯源信息
  queryRetroactive: `${baseUrl}/v1/market/oriven/query`, //获取溯源信息列表
  updRetroactive: `${baseUrl}/v1/market/oriven/upd`, //修改溯源信息
  downRetroactive: `${baseUrl}/v1/market/oriven/down/template`, //溯源信息模板下载
  //积分商户管理
  delMerchant: `${baseUrl}/v1/market/vendor/del`, //删除合作商户
  downMerchantCode: `${baseUrl}/v2/market/vendor/down/code`, //二维码下载
  downMerchantAllCode: `${baseUrl}/v2/market/vendor/export/zip`, //批量二维码下载
  startMerchant: `${baseUrl}/v1/market/vendor/enable/start`, //合作商户启用
  stopMerchant: `${baseUrl}/v1/market/vendor/enable/stop`, //合作商户停用
  insMerchant: `${baseUrl}/v1/market/vendor/ins`, //新增合作商户
  queryMerchant: `${baseUrl}/v1/market/vendor/query`, //获取合作商户列表

  queryClerk: `${baseUrl}/v1/vendor/staff/query`, //获取店员
  insClerk: `${baseUrl}/v1/vendor/staff/add`, //新增店员
  delClerk: `${baseUrl}/v1/vendor/staff/del`, //删除店员
  updClerk: `${baseUrl}/v1/vendor/staff/upd`, //编辑店员
  stopClerk: `${baseUrl}/v1/vendor/staff/enabled`, //启停店员

  insPonitMerchant: `${baseUrl}/v1/market/vendor/ins/point`, //新增积分合作商户
  updPonitMerchant: `${baseUrl}/v1/market/vendor/upd/point`, //修改积分合作商户

  updMerchant: `${baseUrl}/v1/market/vendor/upd`, //修改合作商户
  queryGtype: `${baseUrl}/v1/market/vendor/gtypeList`, //获取经营范围
  queryVendorDetail: `${baseUrl}/v1/market/vendor/get`, //获取合作商户详情

  //商户流水订单
  queryMerchantOrder: `${baseUrl}/v1/market/vendor/point/query`, //获取积分明细列表
  //客如云
  delKru: `${baseUrl}/v1/market/kry/vendor/del`, //删除客如云商户
  startKru: `${baseUrl}/v1/market/kry/vendor/enable/start`, //客如云启用商户
  stopKru: `${baseUrl}/v1/market/kry/vendor/enable/stop`, //客如云停用商户
  insKru: `${baseUrl}/v1/market/kry/vendor/ins`, //新增客如云商户
  queryKru: `${baseUrl}/v1/market/kry/vendor/query`, //获取客如云商户列表
  updKru: `${baseUrl}/v1/market/kry/vendor/upd`, //修改客如云商户

  //骑手管理
  queryRider: `${baseUrl}/v1/market/courier/query`, //骑手列表
  startRider: `${baseUrl}/v1/market/courier/enable/start`, //骑手启用
  stopRider: `${baseUrl}/v1/market/courier/enable/stop`, //骑手停用
  delRider: `${baseUrl}/v1/market/courier/del`, //删除骑手
  insRider: `${baseUrl}/v1/market/courier/ins`, //新增骑手
  updRider: `${baseUrl}/v1/market/courier/upd`, //修改骑手

  //运费配置
  updPostage: `${baseUrl}/v1/market/postage/upd/market`, //运费配置

  // 获取市场结算方式
  settlementMethod: `${baseUrl}/v1/sys/market/get/settlementMethod`,

  //桌位码管理
  delDesktop: `${baseUrl}/v1/market/desktop/del`, //删除桌位
  queryDesktop: `${baseUrl}/v1/market/desktop/query`, //获取桌位管理列表
  downDesktop: `${baseUrl}/v1/market/desktop/download/qrCode`, //下载二维码
  insDesktop: `${baseUrl}/v1/market/desktop/put`, //新增-编辑桌位

  //基础设置-设备管理
  getPrintCode: `${baseUrl}/v1/sys/market/config/tech/get`, //获取小票打印机设备编码
  updPrintCode: `${baseUrl}/v1/sys/market/config/tech/upd`, //修改小票打印机设备编码
  //财务管理
  financeQuery: `${baseUrl}/v1/market/finance/query`,//获取结算账单列表
  financeExport: `${baseUrl}/v1/market/finance/export/bill`,//导出结算账单EXCEL
  financeEDetailsExport: `${baseUrl}/v1/market/finance/export/details/query`,//资金明细-导出账户明细EXCEL
  financeEDetailsWithdraw: `${baseUrl}/v1/market/finance/details/withdraw`,//资金明细-提现
  financeEDetailsSum: `${baseUrl}/v1/market/finance/details/sum`,//资金明细-获取账户金额
  financeEDetailsQuery: `${baseUrl}/v1/market/finance/details/query`,//资金明细-获取账户明细
  financeUserQuery: `${baseUrl}/v1/zxUser/query`,//查询账户列表
  financeMarketEnable: `${baseUrl}/v1/zxUser/marketAuto/enable`,//启停市场自动提现
  financeVendorEnable: `${baseUrl}/v1/zxUser/vendorAuto/enable`,//启停商户自动提现
  financeUserInfoGet: `${baseUrl}/v1/zxUser/userInfo/get`,//获取账户信息
  financeUserInfoUpd: `${baseUrl}/v1/zxUser/userInfo/upd`,//编辑账户信息
  financeBankInfoGet: `${baseUrl}/v1/zxUser/userBank/get`,//获取账户信息
  financeBankInfoUpd: `${baseUrl}/v1/zxUser/userBank/upd`,//编辑账户信息
  financeUserDrop: `${baseUrl}/v1/zxUser/drop`,//账户管理-划账下拉
  financeUserAllocation: `${baseUrl}/v1/zxUser/allocation`,//账户管理-划账

  financeVendorInfoUpd: `${baseUrl}/v1/zxUser/vendor/userInfo/upd`,//编辑商户账户信息
  financeVendorInfoGet: `${baseUrl}/v1/zxUser/vendor/userInfo/get`,//获取商户账户信息
  financeVendorBankUpd: `${baseUrl}/v1/zxUser/vendor/userBank/upd`,//编辑商户银行卡信息
  financeVendorBankGet: `${baseUrl}/v1/zxUser/vendor/userBank/get`,//获取商户银行卡信息

  // 商品评价
  CommentConfigGet: `${baseUrl}/v1/market/order/comment/config/get`,//获取评价配置
  CommentConfigSet: `${baseUrl}/v1/market/order/comment/config/set`,//设置评价配置
  CommentQuery: `${baseUrl}/v1/market/order/comment/query`,//查询交易明细评价
  CommentApply: `${baseUrl}/v1/market/order/comment/batchApply`,//批量审核交易明细评价
  CommentGet: `${baseUrl}/v1/market/order/comment/get`,//批量审核交易明细评价
  CommentReply: `${baseUrl}/v1/market/order/comment/reply`,//回复交易明细评价
};

export const popup = {
  queryPopAds: `${baseUrl}/v1/market/index/img/query`, //获取弹窗广告列表
  insPopAds: `${baseUrl}/v1/market/index/img/ins`, //新增弹窗广告
  delPopAds: `${baseUrl}/v1/market/index/img/del`, //删除弹窗广告
  updPopAds: `${baseUrl}/v1/market/index/img/upd`, //更新弹窗广告
  deliveredOrder: `${baseUrl}/v1/sys/index/data/center/get/deliveredOrder`, // 待发货订单提示
  newOdrderVoice: `${baseUrl}/v1/market/order/voice`, // 新订单提示
  newRefundVoice: `${baseUrl}/v1/sys/index/data/center/get/refundOrder`, // 退款订单提示
};

export const order = {
  queryKruOrder: `${baseUrl}/v1/market/kry/order/query`, //获取客如云订单列表
  queryOrder: `${baseUrl}/v1/market/order/query`, //获取订单列表
  queryOrderCount: `${baseUrl}/v1/market/order/query/sum`, //获取订单信息统计金额和笔数
  sendOrder: `${baseUrl}/v1/market/order/send`, //订单发货
  sendOrderSF: `${baseUrl}/v1/market/order/deliver/sf`, //顺丰发货
  sendOrderSFcancel: `${baseUrl}/v1/market/order/deliver/cancel`, //取消快递单
  queryCourier: `${baseUrl}/v1/market/order/queryCourier`, //读取跑脚员列表
  paidanCourier: `${baseUrl}/v1/market/order/paidan`, //派单
  detailOrder: `${baseUrl}/v1/market/order/loadOrder`, //获取订单详情
  vendorDetailOrder: `${baseUrl}/v1/market/order/vendor/loadOrder`, //获取订单详情
  riderOrder: `${baseUrl}/v1/market/express/query`, //获取跑腿单列表
  // collageDetail:`${baseUrl}/v1/market/order/group/list`, //获取团购订单详情列表
  collageOrder: `${baseUrl}/v1/market/order/group/query`, //获取团购订单列表
  // orderExcel: `${baseUrl}/v1/market/order/down/orderexcel`, //导出商城订单Excel
  exportOrderExcel: `${baseUrl}/v1/market/order/export/orderexcel`, //导出商城订单Excel
  exportOrderLine: `${baseUrl}/v1/market/order/export/orderLine`, //导出商城订单Excel
  printOrder: `${baseUrl}/v1/market/order/printOrder`, //小票打印
  confirmPurchase: `${baseUrl}/v1/market/vendorOrder/checkOrder`, //提交采购
  queryOrderPurchase: `${baseUrl}/v1/market/vendorOrder/loadOrder`, //读取订单采购信息
  queryPurchaseDetail: `${baseUrl}/v1/market/vendorOrder/purchase/list`, //读取订单采购详情
  confirmPurchaseComp: `${baseUrl}/v1/market/vendorOrder/purchase/confirm`, //采购完成确认
  againPurchase: `${baseUrl}/v1/market/vendorOrder/purchase/again`, //重新采购
  vendorList: `${baseUrl}/v1/market/vendorOrder/loadVendorV2`, //人工采购供应商
  pickcodeUpd: `${baseUrl}/v1/market/order/pickcode/upd`, // 自提核销码核销
  arrived: `${baseUrl}/v1/market/order/upd/arrived`, // 自提核销码核销
  pickupLocalList: `${baseUrl}/v1/market/order/pickupLocation/list`, // 获取订单可选自提点
  pickupLocalUpd: `${baseUrl}/v1/market/order/pickupLocation/upd`, // 修改自提点
  pickupWaitWriteoff: `${baseUrl}/v1/market/order/pickup/waitWriteoff`,//批量自提到货
  pickupWaitArrival: `${baseUrl}/v1/market/order/pickup/waitArrival`,//批量自提出货

  refundLoadOrder: `${baseUrl}/v1/market/order/refund/loadOrder`, // 读取订单退款信息
  getRefundReason: `${baseUrl}/v1/market/order/refund/list/reason/drop`, //获取退款原因
  refund_agree: `${baseUrl}/v1/market/order/refund/agree`, // 订单退款

  thirdDeliveryBilling: `${baseUrl}/v1/wanli/order/billing`, //获取第三方派单计价
  thirdDeliveryCreate: `${baseUrl}/v1/wanli/order/create`, //第三方派单
  thirdDeliveryStatus: `${baseUrl}/v1/market/order/third/party/status/get`, //获取第三方派单状态
  thirdDeliveryCancel: `${baseUrl}/v1/wanli/order/cancel`, //第三方订单取消
  thirdDeliveryCancelType: `${baseUrl}/v1/wanli/cancelType/list`, //第三方订单取消原因
  thirdDeliveryReach: `${baseUrl}/v1/wanli/order/reach`, //第三方订单取消原因

  //商户对账
  queryVendorPurchase: `${baseUrl}/v1/market/vendorOrder/loadVendor`, //读取采购商户信息
  queryPurchase: `${baseUrl}/v1/market/vendorOrder/query`, //获取采购订单信息列表
  exportPurchase: `${baseUrl}/v1/market/vendorOrder/export`, //商户对账-导出
  queryComPurchase: `${baseUrl}/v1/vendor/settlement/report/check`, //商户对账列表-佣金
  exportComPurchase: `${baseUrl}/v1/vendor/settlement/vendorBill/export`, //商户对账-佣金-导出
  queryComReportList: `${baseUrl}/v1/vendor/settlement/report/settlementList`, //商户对账-佣金-报表下拉
  queryBankInfo: `${baseUrl}/v1/vendor/settlement/vendorBill/bankInfo`, //商户对账-佣金-查看银行账户

  //撤销记录
  queryCancelRecord: `${baseUrl}/v1/market/vendorOrder/revokeList`, //撤销记录-列表
  exportCancelRecord: `${baseUrl}/v1/market/vendorOrder/revoke/export`, //撤销记录 -导出
  //商户结算
  queryMerSettle: `${baseUrl}/v1/market/vendorOrder/settlementList`, //商户结算 -列表-采购价结算
  exportMerSettle: `${baseUrl}/v1/market/vendorOrder/settlement/export`, //商户结算 -导出-采购价结算
  querySettleDetail: `${baseUrl}/v1/market/vendorOrder/settlementDetail`, //商户结算 -详情-采购价结算
  updMerSettle: `${baseUrl}/v1/market/vendorOrder/settlement`, //商户结算 -结算-采购价结算
  queryMerSettleCom: `${baseUrl}/v1/vendor/settlement/report/query`, //商户结算-列表-佣金
  querySettleDate: `${baseUrl}/v1/vendor/settlement/report/date`, //商户结算-佣金-生成报表已结算时间区间
  updComMerSettle: `${baseUrl}/v1/vendor/settlement/report/add`, //商户结算-佣金-结算
  exportComMerSettle: `${baseUrl}/v1/vendor/settlement/export`, //商户结算-佣金-导出
  //结算报表
  querySettleReport: `${baseUrl}/v1/vendor/settlement/report/queryLine`, //结算报表
  querySettleProcess: `${baseUrl}/v1/vendor/settlement/report/process`, //采购流程
  exportSettleProcess: `${baseUrl}/v1/vendor/settlement/export/line`, //结算报表-导出
  updSettleProcess: `${baseUrl}/v1/vendor/settlement/report/upd`, //结算报表-结算

  /**提现管理相关 */
  queryTixian: `${baseUrl}/v1/market/comm/draw/query`, //获取提现列表
  agreeTixian: `${baseUrl}/v1/market/comm/draw/agree`, //同意提现
  refuseTixian: `${baseUrl}/v1/market/comm/draw/refuse`, //拒绝提现
  paymentTixian: `${baseUrl}/v1/market/comm/draw/paid`, //已打款
  updTixianRemark: `${baseUrl}/v1/market/comm/draw/upd`, //编辑提现备注

  /** end 提现管理相关 */
  /**对账中心相关 */
  queryBillByDate: `${baseUrl}/v1/market/pay/line/bill/day/query`, //对账中心-日汇总
  queryBillByMonth: `${baseUrl}/v1/market/pay/line/bill/month/query`, //对账中心-月汇总
  downloadBillByDate: `${baseUrl}/v1/market/pay/line/down/month`, //对账中心日汇总报表下载
  downloadBillByMonth: `${baseUrl}/v1/market/pay/line/down/year`, //对账中心月汇总报表下载
  queryBillDetail: `${baseUrl}/v1/market/pay/line/bill/query`, //对账中心-明细
  queryDetailCount: `${baseUrl}/v1/market/pay/line/bill/query/count` //对账中心-明细-总笔数

  /** end 对账中心相关 */
};

export const mall = {
  //基础信息配置
  // delConfig: `${baseUrl}/v1/market/mall/app/config/del`, //删除配置
  getConfig: `${baseUrl}/v1/market/mall/app/config/get`, //获取配置
  // insConfig: `${baseUrl}/v1/market/mall/app/config/ins`, //新增配置
  // queryConfig: `${baseUrl}/v1/market/mall/app/config/query`, //获取配置列表
  updConfig: `${baseUrl}/v1/market/mall/app/config/upd`, //修改配置

  //派单配置
  updDispatchConfig: `${baseUrl}/v1/sys/market/dispatch/upd`, //修改派单配置
  updDispatchCourier: `${baseUrl}/v1/sys/market/courier/dispatch/upd`, //骑手派单配置
  queryDispatchCourier: `${baseUrl}/v1/sys/market/courier/dispatch/list`, //获取骑手已派单配置

  //商品分类
  delClassic: `${baseUrl}/v1/market/goods/gtype/del`, //删除商品分类
  startClassic: `${baseUrl}/v1/market/goods/gtype/enable/start`, //商品分类启用
  stopClassic: `${baseUrl}/v1/market/goods/gtype/enable/stop`, //商品分类停用
  getClassic: `${baseUrl}/v1/market/goods/gtype/get`, //获取商品分类
  insClassic: `${baseUrl}/v1/market/goods/gtype/ins`, //新增商品分类
  queryClassic: `${baseUrl}/v1/market/goods/gtype/query`, //获取商品分类列表
  updClassic: `${baseUrl}/v1/market/goods/gtype/upd`, //修改商品分类
  threeClassic: `${baseUrl}/v1/market/goods/gtype/three/drop`, //获取三级分类下拉列表
  twoClassicGoods: `${baseUrl}/v1/market/goods/gtype/two/withGoods/drop`, //获取两级分类下拉列表（带商品）

  //广告
  delImg: `${baseUrl}/v1/market/img/del`, //删除广告
  startImg: `${baseUrl}/v1/market/img/enable/start`, //广告启用
  stopImg: `${baseUrl}/v1/market/img/enable/stop`, //广告停用
  getImg: `${baseUrl}/v1/market/img/get`, //市场获取广告
  insImg: `${baseUrl}/v1/market/img/ins`, //市场新增广告
  queryImg: `${baseUrl}/v1/market/img/query`, //获取广告列表
  updImg: `${baseUrl}/v1/market/img/upd`, //修改广告
  activityList: `${baseUrl}/v1/market/activity/list`, //活动列表

  // 功能菜单配置
  funmenuConfigUpd: `${baseUrl}/v1/market/img/funmenu/config/upd`,       //新增或者修改功能菜单配置详情
  funmenuConfigQuery: `${baseUrl}/v1/market/img/funmenu/config/query`,   //获取功能菜单配置列表
  funmenuConfigGet: `${baseUrl}/v1/market/img/funmenu/config/get`,       //获取功能菜单配详情
  funmenuConfigEnable: `${baseUrl}/v1/market/img/funmenu/config/enable`, //功能菜单配置启动-停用
  funmenuConfigDel: `${baseUrl}/v1/market/img/funmenu/del`,              //删除功能菜单配置

  //库存管理
  queryGoodsKc: `${baseUrl}/v1/market/ware/query`, //获取库存列表
  queryKcNum: `${baseUrl}/v1/market/ware/sum`, //获取库存列表统计数据
  updGoodsKc: `${baseUrl}/v1/market/ware/ins`, //采购入库和库存盘点

  // 打印插件下载
  downPrint: `${baseUrl}/v1/market/order/down/dy`, //打印插件下载

  //商品库
  delGoods: `${baseUrl}/v1/market/goods/main/del`, //删除商品
  startGoods: `${baseUrl}/v1/market/goods/main/enable/start`, //商品启用
  stopGoods: `${baseUrl}/v1/market/goods/main/enable/stop`, //商品停用
  insGoods: `${baseUrl}/v1/market/goods/main/ins`, //新增商品
  queryGoodsList: `${baseUrl}/v1/market/goods/main/query`,
  queryGoods: `${baseUrl}/v1/market/goods/manager/img/query`, //获取商品库列表
  // queryGoods: `${baseUrl}/v1/market/goods/manager/query`, //获取商品库列表
  updGoods: `${baseUrl}/v1/market/goods/main/upd`, //修改商品
  importGoods: `${baseUrl}/v1/market/goods/main/importexcel`, //商品库excel导入
  downGoods: `${baseUrl}/v1/market/goods/main/down/template`, //商品库模板下载
  GoodsList: `${baseUrl}/v1/market/goods/manager/list/title`, //根据title搜索
  sysGoodsList: `${baseUrl}/v1/market/goods/main/sys/list`, //获取平台商品分类下拉接口

  //商品库-三级分类
  delThreeGoods: `${baseUrl}/v1/market/goods/main/three/del`, //删除商品
  startThreeGoods: `${baseUrl}/v1/market/goods/main/three/enable/start`, //商品启用
  stopThreeGoods: `${baseUrl}/v1/market/goods/main/three/enable/stop`, //商品停用
  insThreeGoods: `${baseUrl}/v1/market/goods/main/three/ins`, //新增商品
  queryThreeGoodsList: `${baseUrl}/v1/market/goods/main/three/query`, //三级分类列表
  updThreeGoods: `${baseUrl}/v1/market/goods/main/three/upd`, //修改商品
  importThreeGoods: `${baseUrl}/v1/market/goods/main/three/importexcel`, //商品库excel导入
  downThreeGoods: `${baseUrl}/v1/market/goods/main/three/down/template`, //商品库模板下载

  //运费配置
  // insPostage: `${baseUrl}/v1/market/postage/ins`, //新增运费配置
  queryPostage: `${baseUrl}/v1/market/postage/query`, //获取运费配置列表
  queryDeliveryPostage: `${baseUrl}/v1/market/postage/queryDeliveryTime`, // 获取配送时间列表
  updPostage: `${baseUrl}/v1/market/postage/upd`, //修改运费配置

  //客户回馈
  delFeedback: `${baseUrl}/v1/get/advise/del`, //删除建议反馈
  queryFeedback: `${baseUrl}/v1/market/advise/query`, //获取建议反馈列表
  //客如云
  queryKruOrder: `${baseUrl}/v1/market/kry/order/query`, //获取客如云订单列表
  delKru: `${baseUrl}/v1/market/kry/vendor/del`, //删除客如云商户
  startKru: `${baseUrl}/v1/market/kry/vendor/enable/start`, //客如云启用商户
  stopKru: `${baseUrl}/v1/market/kry/vendor/enable/stop`, //客如云停用商户
  insKru: `${baseUrl}/v1/market/kry/vendor/ins`, //新增客如云商户
  queryKru: `${baseUrl}/v1/market/kry/vendor/query`, //获取客如云商户列表
  updKru: `${baseUrl}/v1/market/kry/vendor/upd`, //修改客如云商户

  // 优惠券商品
  queryGoodsCoupon: `${baseUrl}/v3/market/goods/coupon/query`, //获取优惠券商品列表
  insGoodsCoupon: `${baseUrl}/v2/market/goods/ins/coupon`, //新增优惠券商品
  updGoodsCoupon: `${baseUrl}/v2/market/goods/upd/coupon`, //编辑优惠券商品
  queryGoodsCouponDetail: `${baseUrl}/v2/market/goods/get`, //获取优惠券详情
  invalidGoodsCoupon: `${baseUrl}/v2/market/goods/invalid/coupon`, //优惠券失效

  queryGtype: `${baseUrl}/v1/market/goods/gtype/drop`, //获取优惠券和礼券的商品库

  // 基础设置-推广管理
  promotequery: `${baseUrl}/v3/sys/promote/query`, // 获取推广列表
  promoteenabledstart: `${baseUrl}/v3/sys/promote/enabled/start`, // 开启状态
  promoteenabledstop: `${baseUrl}/v3/sys/promote/enabled/stop`, // 关闭状态
  promoteins: `${baseUrl}/v3/sys/promote/ins`, // 新增推广
  promoteupd: `${baseUrl}/v3/sys/promote/upd`, // 编辑推广
  promotedel: `${baseUrl}/v3/sys/promote/del`, // 删除推广

  // 第三方支付渠道
  thridPaymentQuery: `${baseUrl}/v3/thrid/payment/query`, // 获取第三方支付渠道列表
  thridPaymentAdd: `${baseUrl}/v3/thrid/payment/ins`, // 新增第三方支付渠道
  thridPaymentEdit: `${baseUrl}/v3/thrid/payment/upd`, // 编辑第三方支付渠道
  thridPaymentDel: `${baseUrl}/v3/thrid/payment/del`, // 获取第三方支付渠道列表

  // 常见问题
  problemQuery: `${baseUrl}/v3/sys/market/problem/query`, // 获取常见问题列表
  problemAdd: `${baseUrl}/v3/sys/market/problem/ins`, // 新增常见问题
  problemEdit: `${baseUrl}/v3/sys/market/problem/upd`, // 编辑常见问题
  problemEnabled: `${baseUrl}/v3/sys/market/problem/enabled`, // 启停常见问题
  problemDel: `${baseUrl}/v3/sys/market/problem/del`, // 删除常见问题
  // 常见问题分类
  problemTypeQuery: `${baseUrl}/v3/sys/market/problem/type/query`, // 获取常见问题分类
  problemTypeAdd: `${baseUrl}/v3/sys/market/problem/type/ins`, // 新增常见问题分类
  problemTypeEdit: `${baseUrl}/v3/sys/market/problem/type/upd`, // 编辑常见问题分类
  problemTypeDel: `${baseUrl}/v3/sys/market/problem/type/del`, // 删除常见问题分类
  problemTypeList: `${baseUrl}/v3/sys/market/problem/type/list`, // 获取常见问题分类-不带分页

  gtypeQuery: `${baseUrl}/v4/goods/gtype/list`, // 获取分类列表
  gtypeDownTemplate: `${baseUrl}/v4/goods/gtype/downTemplate`, // 模板下载
  gtypeExportExcel: `${baseUrl}/v4/goods/gtype/exportExcel`, // 导出分类
  gtypeImportexcel: `${baseUrl}/v4/goods/gtype/importexcel`, // 导入分类
  gtypeSort: `${baseUrl}/v4/goods/gtype/drag/sort/ago`, // 分类拖动排序
  gtypeAdd: `${baseUrl}/v4/goods/gtype/add`, // 新增一级分类
  gtypeTwoAdd: `${baseUrl}/v4/goods/gtype/two/add`, // 新增二级分类
  gtypeThreeAdd: `${baseUrl}/v4/goods/gtype/three/add`, // 新增三级分类
  gtypeUpd: `${baseUrl}/v4/goods/gtype/upd`, // 编辑一级分类
  gtypeTwoUpd: `${baseUrl}/v4/goods/gtype/two/upd`, // 编辑二级分类
  gtypeThreeUpd: `${baseUrl}/v4/goods/gtype/three/upd`, // 编辑三级分类

  /**关键词搜索 */
  keywordQuery: `${baseUrl}/v1/market/search/keyword/query`, // 查询搜索词
  keywordAdd: `${baseUrl}/v1/market/search/keyword/add`, // 新增搜索词
  keywordUpd: `${baseUrl}/v1/market/search/keyword/upd`, // 编辑搜索词
  keywordGet: `${baseUrl}/v1/market/search/keyword/get`, // 编辑搜索词
  keywordDel: `${baseUrl}/v1/market/search/keyword/del`, // 编辑搜索词
};

//商城营销
export const marketing = {
  //抽奖活动配置
  getLotteryRule: `${baseUrl}/v1/market/drawconf/get`, //获取规则设置
  updLotteryRule: `${baseUrl}/v1/market/drawconf/upd`, //修改规则设置
  queryLottery: `${baseUrl}/v1/market/drawprize/query`, //获取礼品配置
  updLottery: `${baseUrl}/v1/market/drawprize/upd`, //修改礼品配置

  //中奖清单
  queryWinning: `${baseUrl}/v1/market/drawwin/query`, //获取中奖记录
  queryWinningData: `${baseUrl}/v1/market/drawwin/query/num`, //获取中奖记录次数
  updWinning: `${baseUrl}/v1/market/drawwin/upd/status`, //设置奖品已发货

  //卡券
  delCoupon: `${baseUrl}/v1/market/card/del`, //删除卡券
  startCoupon: `${baseUrl}/v1/market/card/enable/start`, //卡券启用
  stopCoupon: `${baseUrl}/v1/market/card/enable/stop`, //卡券停用
  getCoupon: `${baseUrl}/v1/market/card/get`, //获取卡券单个
  insCoupon: `${baseUrl}/v1/market/card/ins`, //新增卡券
  updCoupon: `${baseUrl}/v1/market/card/upd`, //修改卡券
  queryCoupon: `${baseUrl}/v1/market/card/query`, //获取卡券列表
  downCoupon: `${baseUrl}/v1/market/card/down/code`, //卡券二维码下载
  downCouponCenter: `${baseUrl}/v1/market/card/down/center`, //领券中心二维码下载
  invalidCoupon: `${baseUrl}/v1/market/card/invalid`, //卡券失效
  mtypeDrop: `${baseUrl}/v1/market/card/mtype/drop`, // 新增优惠券适用专区下拉
  gtypeDrop: `${baseUrl}/v1/market/goods/gtype/card/drop`, // 获取品类下拉-用于新增有优惠券使用

  //卡券发放
  grantCoupon: `${baseUrl}/v1/market/card/member/ins/all`, //发放多个用户
  //卡券使用查询
  useCoupon: `${baseUrl}/v1/market/card/query/use`, //卡券使用查询
  useCouponSum: `${baseUrl}/v1/market/card/query/use/sum`, //卡券统计数据
  useCouponExport: `${baseUrl}/v1/market/card/export/use`, //导出已使用优惠券列表
  insCardcenter: `${baseUrl}/v1/market/card/center`, //加入卡券中心

  //礼品券使用查询
  useGiftCoupon: `${baseUrl}/v2/market/gift/query/use`, //礼品券使用查询
  //礼品券使用查询
  useGiftSum: `${baseUrl}/v2/market/gift/query/use/sum`, //礼品券统计数据
  useGiftCouponExport: `${baseUrl}/v2/market/gift/export/use`, //导出已使用礼品券列表

  //会员
  adjustMember: `${baseUrl}/v1/market/member/adjustment`, //调整积分
  queryPoint: `${baseUrl}/v1/market/member/point/query`, //获取积分明细列表
  openMember: `${baseUrl}/v1/market/member/open`, //开通年会会员
  queryMember: `${baseUrl}/v1/market/member/query`, //获取会员信息列表
  exportMember: `${baseUrl}/v1/market/member/export/memberInfo`, //导出会员信息列表
  tagsMember: `${baseUrl}/v1/market/member/tags`, //备注标签
  tagsDrop: `${baseUrl}/v1/market/tag/list/drop`, //标签下拉
  downTemplateMember: `${baseUrl}/v1/market/member/downTemplate`, //模板下载
  importexcelMember: `${baseUrl}/v1/market/member/importexcel`, //导入标签
  tagsGet: `${baseUrl}/v1/market/member/get/tag`, //获取用户标签情况
  tagsGetTrue: `${baseUrl}/v1/market/member/get/tag/true`, //获取用户已勾选的标签
  tagsMarkTag: `${baseUrl}/v1/market/member/mark/tag`, //打标签

  //钱包查询
  purseQuery: `${baseUrl}/v1/market/member/comm/query`, //钱包查询
  downTemplatepurse: `${baseUrl}/v1/market/member/comm/export`, //钱包查询导出

  //标签管理
  tagsQuery: `${baseUrl}/v1/market/tag/query`, //查询标签
  tagsDel: `${baseUrl}/v1/market/tag/del`, //删除标签
  tagsIns: `${baseUrl}/v1/market/tag/ins`, //新增标签
  tagsUpd: `${baseUrl}/v1/market/tag/upd`, //编辑标签

  // 礼品劵
  giftQuery: `${baseUrl}/v2/market/gift/query`, //获取礼品券列表
  giftUpd: `${baseUrl}/v2/market/gift/upd`, //修改礼品券
  giftInvalid: `${baseUrl}/v2/market/gift/invalid`, //礼品券失效
  giftIns: `${baseUrl}/v2/market/gift/ins`, //新增礼品券
  giftGet: `${baseUrl}/v2/market/gift/get`, //获取礼品券
  giftEnableStop: `${baseUrl}/v2/market/gift/enable/stop`, //礼品券停用
  giftEnableStart: `${baseUrl}/v2/market/gift/enable/start`, //礼品券启用

  // 卡券活动
  activityQuery: `${baseUrl}/v1/market/activity/query`, //查询卡券活动
  activityQrCodeDown: `${baseUrl}/v1/market/activity/down/qrCode`, //活动二维码下载
  activityPopUpQrCodeDown: `${baseUrl}/v1/market/activity/down/popUpQrCode`, //弹框二维码下载
  activityIssueQuery: `${baseUrl}/v1/market/activity/issue/query`, //查询卡券活动发放记录
  activityAdd: `${baseUrl}/v1/market/activity/add`, //新增卡券活动
  activityGet: `${baseUrl}/v1/market/activity/get`, //获取卡券活动
  activityUpd: `${baseUrl}/v1/market/activity/upd`, //编辑卡券活动
  activityEnable: `${baseUrl}/v1/market/activity/enable`, //启停卡券活动
  activityIssueExport: `${baseUrl}/v1/market/activity/issue/export`, //导出卡券活动发放记录

  // 热力豆
  msdQuery: `${baseUrl}/v1/market/member/msd/query`, //查询热力豆账户
  msdTagDrop: `${baseUrl}/v1/market/tag/list/drop`, //热力豆标签下拉
  msdBalanceClear: `${baseUrl}/v1/market/member/msd/balance/clear`, //清空热力豆余额
  msdBalanceAdjust: `${baseUrl}/v1/market/member/msd/balance/adjust`, //调整热力豆余额
  msdRechargeImport: `${baseUrl}/v1/market/member/msd/recharge/import`, //导入批量充值
  msdRechargeTemplate: `${baseUrl}/v1/market/member/msd/recharge/template`, //下载充值模板
  msdLineQuery: `${baseUrl}/v1/market/member/msd/line/query`, //查询热力豆明细
  msdLineExport: `${baseUrl}/v1/market/member/msd/line/export`, //导出热力豆明细
  msdExport: `${baseUrl}/v1/market/member/msd/export`, //导出热力豆总览
  MsdConfigGet: `${baseUrl}/v1/market/member/msd/get/config`,// 获取热力豆配置
  msdConfigSet: `${baseUrl}/v1/market/member/msd/set/config`, //修改热力豆配置

  // 充值卡密管理
  rechargeCardQuery: `${baseUrl}/v1/market/member/recharge/card/query`, //分页查询
  rechargeCardQuerySum: `${baseUrl}/v1/market/member/recharge/card/query/sum`, //合计
  rechargeCardImport: `${baseUrl}/v1/market/member/recharge/card/importExcel`, //导入卡密
  rechargeCardExport: `${baseUrl}/v1/market/member/recharge/card/exportexcel`, //导出
  rechargeCardDownTemplate: `${baseUrl}/v1/market/member/recharge/card/downTemplate`, //下载导入模板
  rechargeCardCancel: `${baseUrl}/v1/market/member/recharge/card/cancel`, //作废
  rechargeCardAdd: `${baseUrl}/v1/market/member/recharge/card/add`, //生成卡密
};

//商品维护
export const goods = {
  //各类商品
  delGoods: `${baseUrl}/v1/market/goods/manager/del`, //删除商品
  startGoods: `${baseUrl}/v1/market/goods/manager/enable/start`, //商品启用
  stopGoods: `${baseUrl}/v1/market/goods/manager/enable/stop`, //商品停用
  getGoods: `${baseUrl}/v1/market/goods/manager/get`, //获取商品详情
  insGoods: `${baseUrl}/v1/market/goods/manager/ins`, //新增商品
  queryGoods: `${baseUrl}/v1/market/goods/manager/query`, //获取商品列表
  queryGoodsV3: `${baseUrl}/v3/market/goods/query`, //获取商品列表
  updGoods: `${baseUrl}/v1/market/goods/manager/upd`, //修改商品
  downGoodsExcel: `${baseUrl}/v3/market/goods/export`, //导出市场商品清单
  ImportGoodsExcel: `${baseUrl}/v1/market/goods/manager/importexcel`, //导入市场商品清单
  richTempGet: `${baseUrl}/v1/market/goods/manager/richTemp/get`, // 获取富文本模板
  richTempUpd: `${baseUrl}/v1/market/goods/manager/richTemp/upd`, // 编辑富文本模板
  enableGuessLike: `${baseUrl}/v1/market/goods/manager/enableGuessLike`, // 市场推荐切换
  checkPrice: `${baseUrl}/v1/market/goods/manager/checkPrice`, // 检查采购价
  startGoodsEnable: `${baseUrl}/v2/market/goods/enable/start`, // 批量启用
  stopGoodsEnable: `${baseUrl}/v2/market/goods/enable/stop`, // 批量停用
  batchDelete: `${baseUrl}/v1/market/goods/manager/del/list`, // 批量删除
  recommendEnable: `${baseUrl}/v1/market/goods/manager/zone/recommend/enable`, // 启停商品轮播推荐
  getDisplayName: `${baseUrl}/v1/market/goods/manager/zone/displayName/get`, //获取商品专区显示名称
  setDisplayName: `${baseUrl}/v1/market/goods/manager/zone/displayName/set`, //获取商品专区显示名称

  //商品供应库
  querySupply: `${baseUrl}/v1/market/supply/pageList`, //商品供应库-列表查询
  changeSupplyStatus: `${baseUrl}/v1/market/supply/enable/true`, //商品供应库-启停供应商
  delSupplyGoods: `${baseUrl}/v1/market/supply/delByGoods`, //商品供应库-删除商品
  delSupplySpecs: `${baseUrl}/v1/market/supply/del`, //商品供应库-删除商品规格
  exportSupply: `${baseUrl}/v1/market/supply/export`, //商品供应库-导出
  insSupply: `${baseUrl}/v1/market/supply/insert`, //商品供应库-新增
  updSupply: `${baseUrl}/v1/market/supply/update`, //商品供应库-修改
  supplyGoodsList: `${baseUrl}/v1/market/supply/goodsList`, //商品供应库--更新-商品下拉列表
  supplySpaceList: `${baseUrl}/v1/market/supply/spaceList`, //商品供应库--更新-规格下拉列表
  supplyVendorList: `${baseUrl}/v1/market/supply/vendorList`, //商品供应库--更新-供应商下拉列表
  querySupplyConfig: `${baseUrl}/v1/market/supply/getConf`, //商品供应库--获取派单配置
  updSupplyConfig: `${baseUrl}/v1/market/supply/updConf`, //商品供应库--设置派单配置
  querySupplyDetail: `${baseUrl}/v1/market/supply/detail`, //商品供应库--明细
  queryAdminSupply: `${baseUrl}/v1/market/supply/isGoodSupply`, //商品供应库--是否开启统一配置
  queryAutoConfig: `${baseUrl}/v1/market/supply/isGoodPurchaseDeploy`, //商品供应库--是否系统自动派单

  //推荐商品
  recommendQuery: `${baseUrl}/v1/market/goods/manager/recommend/query`,  //查询推荐商品
  recommendAdd: `${baseUrl}/v1/market/goods/manager/recommend/add`,      //新增推荐商品
  recommendUpd: `${baseUrl}/v1/market/goods/manager/recommend/upd`,      //编辑推荐商品
  recommendDel: `${baseUrl}/v1/market/goods/manager/recommend/del`,      //删除推荐商品
  recommendGet: `${baseUrl}/v1/market/goods/manager/recommend/get`,      //获取推荐商品
};

//市场营销
export const mkt_marketing = {
  //菜谱
  delCookfd: `${baseUrl}/v1/market/goods/cookfd/del`, //删除菜谱
  startCookfd: `${baseUrl}/v1/market/goods/cookfd/enable/start`, //菜谱启用
  stopCookfd: `${baseUrl}/v1/market/goods/cookfd/enable/stop`, //菜谱停用
  getCookfd: `${baseUrl}/v1/market/goods/cookfd/get`, //获取菜谱
  insCookfd: `${baseUrl}/v1/market/goods/cookfd/ins`, //新增菜谱
  queryCookfd: `${baseUrl}/v1/market/goods/cookfd/query`, //获取菜谱列表
  updCookfd: `${baseUrl}/v1/market/goods/cookfd/upd`, //修改菜谱
  startRecom: `${baseUrl}/v1/market/goods/cookfd/recom/start`, //加入
  stopRecom: `${baseUrl}/v1/market/goods/cookfd/recom/stop` //退出今日推荐
};
//菜谱管理-菜谱分类
export const cookfd = {
  delCookType: `${baseUrl}/v1/market/goods/cookfd/type/del`, //删除菜谱分类
  startCookType: `${baseUrl}/v1/market/goods/cookfd/type/enable/start`, //启用菜谱分类
  stopCookType: `${baseUrl}/v1/market/goods/cookfd/type/enable/stop`, //停用菜谱分类
  insCookType: `${baseUrl}/v1/market/goods/cookfd/type/ins`, //新增菜谱分类
  queryCookType: `${baseUrl}/v1/market/goods/cookfd/type/query`, //菜谱分类列表
  updCookType: `${baseUrl}/v1/market/goods/cookfd/type/upd` //修改菜谱分类
};

export const sys = {
  insRole: `${baseUrl}/v1/sys/role/ins`, //新增角色
  queryRole: `${baseUrl}/v1/sys/role/query`, //查询角色
  updRole: `${baseUrl}/v1/sys/role/upd`, //修改角色
  delRole: `${baseUrl}/v1/sys/role/del`, //删除角色
  getRole: `${baseUrl}/v1/sys/role/getFunction`, //获取角色的权限
  getAllRole: `${baseUrl}/v1/sys/role/getFunctionAll`, //读取可配置权限
  setRole: `${baseUrl}/v1/sys/role/setFunction`, //设置角色的权限

  delUser: `${baseUrl}/v1/sys/user/del`, //删除用户
  getUser: `${baseUrl}/v1/sys/user/list`, //获取用户
  insUser: `${baseUrl}/v1/sys/user/ins`, //新增用户
  updUser: `${baseUrl}/v1/sys/user/upd`, //修改用户

  queryLog: `${baseUrl}/v1/sys/log/query`, //获取日志列表

  gzhQuery: `${baseUrl}/v3/sys/market/gzh/query`, //获取日志列表
  gzhStart: `${baseUrl}/v3/sys/market/gzh/enabled/start`, //获取日志列表
  gzhStop: `${baseUrl}/v3/sys/market/gzh/enabled/stop`, //获取日志列表

  getVersion: `${baseUrl}/v1/sys/account/shieldVersion/get`, //获取版本屏蔽设置
  saveVersion: `${baseUrl}/v1/sys/account/shieldVersion/save`, // 保存版本屏蔽设置

  managerQuery: `${baseUrl}/v1/market/manager/query`, // 商城管理员-查询列表
  managerIns: `${baseUrl}/v1/market/manager/ins`, // 商城管理员-新增
  managerUpd: `${baseUrl}/v1/market/manager/upd`, // 商城管理员-编辑
  managerDel: `${baseUrl}/v1/market/manager/del`, // 商城管理员-删除
};

//售后
export const sale = {
  refundList: `${baseUrl}/v2/market/refund/query`, //获取退款列表
  refundUpd: `${baseUrl}/v1/market/refund/upd`, //修改退款状态
  refundImport: `${baseUrl}/v2/market/refund/export`, //退款导出
  refundGet: `${baseUrl}/v2/market/refund/get`, //获取退款订单详情
  refuseRefund: `${baseUrl}/v2/market/refund/refuse`, // 拒绝退款
  agreeRefund: `${baseUrl}/v2/market/refund/agree`, // 同意退款
  updateRefund: `${baseUrl}/v2/market/refund/upd/line`, //修改退款
  updateRefund_Pre: `${baseUrl}/v2/market/refund/upd/line/pre`, //预计算修改退款金额
};

/**数据中心*/
export const data = {
  queryPay: `${baseUrl}/v1/market/pay/line/query`, //充值记录
  getGifts: `${baseUrl}/v1/sys/data/center/drawwin`, //奖品记录
  goodsList: `${baseUrl}/v1/sys/data/center/goods`, //各商品报表
  getAbnormal: `${baseUrl}/v1/sys/data/center/goods/abnormal`, //异常货物分析
  goodsChart: `${baseUrl}/v1/sys/data/center/goods/analysis`, //各商品分析
  timeChart: `${baseUrl}/v1/sys/data/center/goods/hour/abnormal`, //时间段明细 折线图
  timeList: `${baseUrl}/v1/sys/data/center/goods/hour/detail`, //时间段明细表
  zoneList: `${baseUrl}/v1/sys/data/center/mtype`, //各专区报表
  consumption: `${baseUrl}/v1/market/member/consumption/query`, //获取会员消费记录
  couponList: `${baseUrl}/v1/market/member/card/query`, //获取会员优惠券记录
  balanceList: `${baseUrl}/v1/market/member/comm/query`, //获取会员余额记录
  consumeList: `${baseUrl}/v1/sys/data/center/member/goods/sales`, //付费会员消费分析报表
  integralList: `${baseUrl}/v1/sys/data/center/goods/integral/sales`, //积分兑换统计
  newUserList: `${baseUrl}/v1/sys/data/center/add/member/count`, //新增用户报表
  couponsUse: `${baseUrl}/v1/sys/data/center/query/farmer/card`, //优惠券使用记录报表
  goodTypeSale: `${baseUrl}/v1/sys/data/center/goods/type/sales`, //菜品类别销售报表
  pointSale: `${baseUrl}/v1/sys/data/center/vendor/sales`, //积分商户销售报表
  farmerSale: `${baseUrl}/v1/sys/data/center/farmer/sales`, //市场销售统计报表
  companySale: `${baseUrl}/v1/sys/data/center/company/sales`, //公司销售统计报表
  freightList: `${baseUrl}/v1/sys/data/center/postage/count`, //运费报表
  queryMemPay: `${baseUrl}/v1/sys/data/center/annual/memberPay`, //付费会员办理分析
  queryCourier: `${baseUrl}/v1/sys/data/center/express/courier/count`, //配送员绩效报表
  queryAccess: `${baseUrl}/v1/sys/data/center/mall/access`, //用户访问
  queryComms: `${baseUrl}/v1/sys/data/center/comms`, //佣金达人报表
  commsDetail: `${baseUrl}/v1/sys/data/center/comms/detail`, //佣金明细报表
  queryPurchase: `${baseUrl}/v1/market/vendorOrder/purchase/report`, //商户采购报表
  exportPurchase: `${baseUrl}/v1/market/vendorOrder/export/purchase`, //商户采购报表-导出
  queryMerchant: `${baseUrl}/v1/market/vendor/drop`, //商户采购报表-商户下拉列表

  exportComms: `${baseUrl}/v1/sys/data/center/export/comms`, //佣金达人报表-导出
  exportCommsDetail: `${baseUrl}/v1/sys/data/center/export/comms/detail`, //佣金明细报表-导出
  exportCompanySale: `${baseUrl}/v1/sys/data/center/export/company/sales`, //公司销售统计报表-导出
  exportGifts: `${baseUrl}/v1/sys/data/center/export/drawwin`, //奖品统计-导出
  exportCourier: `${baseUrl}/v1/sys/data/center/export/express/courier/count`, //配送员绩效报表-导出
  exportCoupon: `${baseUrl}/v1/sys/data/center/export/farmer/card`, //优惠券记录-导出
  exportFarmerSale: `${baseUrl}/v1/sys/data/center/export/farmer/sales`, //市场销售统计报表-导出
  exportGoods: `${baseUrl}/v1/sys/data/center/export/goods`, //商品销售统计报表-导出
  exportAbnormal: `${baseUrl}/v1/sys/data/center/export/goods/abnormal`, //异常货物分析-导出
  exportTime: `${baseUrl}/v1/sys/data/center/export/goods/hour/detail`, //时间段销售额-导出
  exportIntegral: `${baseUrl}/v1/sys/data/center/export/goods/integral/sales`, //积分兑换统计-导出
  exportTypeSale: `${baseUrl}/v1/sys/data/center/export/goods/type/sales`, //品类销售统计报表-导出
  exportConsumption: `${baseUrl}/v1/sys/data/center/export/member/goods/sales`, //付费会员消费报表-导出
  exportMember: `${baseUrl}/v1/sys/data/center/export/member/paid`, //付费会员明细-导出
  exportZone: `${baseUrl}/v1/sys/data/center/export/mtype`, //专区营业报表-导出
  exportFreight: `${baseUrl}/v1/sys/data/center/export/postage/count`, //运费报表-导出
  exportPointSale: `${baseUrl}/v1/sys/data/center/export/vendor/sales`, //商户积分统计报表-导出

  queryStatistics: `${baseUrl}/v1/sys/data/statistics/query`, //查询经营数据统计列表
  countStatistics: `${baseUrl}/v1/sys/data/statistics/count`, //经营数据统计行
  exportStatistics: `${baseUrl}/v1/sys/data/statistics/export/operatingStatistics`, //导出经营数据统计

  querySupplierSales: `${baseUrl}/v1/sys/data/center/supplier/sales`, //供应商销售统计
  querySupplierSalesSum: `${baseUrl}/v1/sys/data/center/supplier/sales/sum`, //供应商销售统计合计
  querySupplierSalesExport: `${baseUrl}/v1/sys/data/center/supplier/sales/export`, //导出供应商销售统计
  querySupplierSalesline: `${baseUrl}/v1/sys/data/center/supplier/order/line/query`, //供应商交易明细查询
  querySupplierSalesSumline: `${baseUrl}/v1/sys/data/center/supplier/order/line/sum`, //供应商交易明细查询
  querySupplierSaleslineExport: `${baseUrl}/v1/sys/data/center/supplier/order/line/export`, //导出供应商交易明细查询

  goodsLineSummary: `${baseUrl}/v1/sys/data/center/goods/line/summary`, //商品明细统计
  goodsLineSum: `${baseUrl}/v1/sys/data/center/goods/line/sum`, //商品明细统计合计
  goodsLineExport: `${baseUrl}/v1/sys/data/center/goods/line/export`, //导出商品明细统计
  farmerOptions: `${baseUrl}/v1/sys/data/center/farmer/options`, //市场-运营端下拉列表

  goodsOrderLineQuery: `${baseUrl}/v1/sys/data/center/goods/order/line/query`, //商品明细统计-明细
  goodsOrderLineSum: `${baseUrl}/v1/sys/data/center/goods/order/line/sum`, //商品明细统计-明细合计
  goodsOrderLineExport: `${baseUrl}/v1/sys/data/center/goods/order/line/export`, //商品明细统计-明细合计
};
export const index = {
  queryFarmsales: `${baseUrl}/v1/sys/index/data/center/farmer/sales`, //市场销售概况
  queryZonesales: `${baseUrl}/v1/sys/index/data/center/mType/status`, //专区销售概况
  queryGoodsrank: `${baseUrl}/v1/sys/index/data/center/goods/sales`, //商品TOP10
  queryKcwarn: `${baseUrl}/v1/sys/index/data/center/kc/warning`, //库存预警
  querySales: `${baseUrl}/v1/sys/index/data/center/sales/status`, //销售概况图表
  queryRealdata: `${baseUrl}/v1/sys/index/data/center/yesterday/compared` //实时概况数据
};
export const ads = {
  queryAreaList: `${baseUrl}/v1/market/img/special/query`, //专区广告-获取列表
  insAreaList: `${baseUrl}/v1/market/img/special/ins`, //专区广告-新增
  queryCombination: `${baseUrl}/v1/market/img/combination/query`, //专区广告-获取组合广告列表
  updCombination: `${baseUrl}/v1/market/img/combination/upd`, //专区广告-新增或者修改组合广告详情
};
export const gzh = {
  getCode: `${baseUrl}/v1/wx/redirect`, //公众号-获取openid
  getOpenid: `${baseUrl}/v1/wx/redirect/code`, //公众号-获取用户信息
  bindOpenid: `${baseUrl}/v1/wx/bind/courier`, //骑手-绑定公众号openid和小程序openid
  bindVendorOpenid: `${baseUrl}/v1/wx/bind/vendor` //商户-绑定公众号openid和小程序openid
};
export const bigData = {
  queryToken: `${baseUrl}/v1/login/data`, //获取大数据屏token
  queryClassifyAmt: `${baseUrl}/v2/sys/big/screen/ranking/type/sales`, //左边 - 销量分类金额排行 TOP10
  queryGoodPrice: `${baseUrl}/v2/sys/big/screen/ranking/goods/sales`, //左边 - 销量商品金额排行榜 TOP20
  queryClassifyCount: `${baseUrl}/v2/sys/big/screen/ranking/type/num`, //左边 - 销量分类笔数排行榜 TOP10
  queryGoodCount: `${baseUrl}/v2/sys/big/screen/ranking/goods/num`, //左边 - 销量商品笔数排行榜 TOP20
  queryMarketMap: `${baseUrl}/v2/sys/big/screen/ranking/market`, //中间 -地图数据 以及右边市场销售详情
  queryTradeInfo: `${baseUrl}/v2/sys/big/screen/ranking/topRight`, //中间 -实时交易额 以及  右边-销售额
  queryCheckInfo: `${baseUrl}/v2/sys/big/screen/query/test` //右边 -检测信息
};
/**合作商户 */
export const vendor = {
  boutiqueQuery: `${baseUrl}/v1/vendor/boutique/query`, //获取精选商户列表
  boutiqueAdd: `${baseUrl}/v1/vendor/boutique/add`, //新增精选商户
  boutiqueUpd: `${baseUrl}/v1/vendor/boutique/upd`, //编辑精选商户
  boutiqueDel: `${baseUrl}/v1/vendor/boutique/del`, //删除精选商户
  boutiqueEnabled: `${baseUrl}/v1/vendor/boutique/enabled`, //启动/关闭精选商户
  boutiqueGoods: `${baseUrl}/v1/vendor/boutique/list/drop`, //展示商品下拉列表

  withdrawalQuery: `${baseUrl}/v1/vendor/wallet/withdrawal/query`, //提现打款-查询
  withdrawalExport: `${baseUrl}/v1/vendor/wallet/withdrawal/export`, // 提现打款导出
  withdrawalConfirm: `${baseUrl}/v1/vendor/wallet/withdrawal/confirm`, //提现打款-点击打款
  walletQuery: `${baseUrl}/v1/vendor/wallet/query`, //商户钱包-查询
  walletExport: `${baseUrl}/v1/vendor/wallet/export`, //商户钱包导出
  walletLineQuery: `${baseUrl}/v1/vendor/wallet/line/query`, //商户钱包-明细查询

  packingChargeQuery: `${baseUrl}/v1/vendor/packingCharge/query`, //打包物料费明细查询
  packingChargeExport: `${baseUrl}/v1/vendor/packingCharge/export`, //导出打包物料费明细

  supplierQuery: `${baseUrl}/v1/market/supplier/query`, //查询供应商
  supplierDel: `${baseUrl}/v1/market/supplier/del`, //删除供应商
  supplierEnable: `${baseUrl}/v1/market/supplier/enable`, //删除供应商
  supplierIns: `${baseUrl}/v1/market/supplier/ins`, //新增供应商
  supplierUpd: `${baseUrl}/v1/market/supplier/upd`, //编辑供应商
  supplierGet: `${baseUrl}/v1/market/supplier/get`, //获取供应商
};

/**京东优选 */
export const jd = {
  // 商品列表
  goodsQuery: `${baseUrl}/v1/jd/goods/manager/query`, //获取商品列表
  goodsExport: `${baseUrl}/v1/jd/goods/manager/export`, //导出excel
  goodsImport: `${baseUrl}/v1/jd/goods/manager/import`, //导入excel
  goodsSpuIdEnable: `${baseUrl}/v1/jd/goods/manager/spuId/enable`, //根据spuId商品批量上下架
  getSpecialZone: `${baseUrl}/v1/jd/goods/manager/get/specialZone`, //设置专区名称
  setSpecialZone: `${baseUrl}/v1/jd/goods/manager/set/specialZone`, //设置专区名称
  getServiceContent: `${baseUrl}/v1/jd/goods/manager/get/service/content`, //获取服务内容
  setServiceContent: `${baseUrl}/v1/jd/goods/manager/set/service/content`, //设置服务内容
  getPostageConfig: `${baseUrl}/v1/jd/goods/manager/config/postage/get`, //获取京东专区运费配置
  setPostageConfig: `${baseUrl}/v1/jd/goods/manager/config/postage/set`, //保存京东专区运费配置

  // spu商品详情
  spuGoodsQuery: `${baseUrl}/v1/jd/goods/manager/spu/list`, //获取商品列表
  spuGoodsUpd: `${baseUrl}/v1/jd/goods/manager/upd`, //修改商品
  spuGoodsEnable: `${baseUrl}/v1/jd/goods/manager/enable`, //根据skuId(pkey)商品批量上下架

  // 商品分类
  categoryDrop: `${baseUrl}/v1/jd/category/manager/multi/drop`, //获取各级分类下拉
  categoryThreeDrop: `${baseUrl}/v1/jd/goods/manager/category/three/drop`, //获取三级分类下拉

  // 关联商品分类
  categoryRelationQuery: `${baseUrl}/v1/jd/category/manager/rel/query`, //查询关联分类列表
  categoryRelationUpd: `${baseUrl}/v1/jd/category/manager/rel/upd`, //更新关联分类

  // 订单管理
  orderQuery: `${baseUrl}/v1/jd/order/manager/query`, //获取订单信息列表
  orderQuerySum: `${baseUrl}/v1/jd/order/manager/sum`, //获取订单信息列表
  orderQueryExport: `${baseUrl}/v1/jd/order/manager/export`, //导出京东订单
  orderLineQueryExport: `${baseUrl}/v1/jd/order/manager/export/orderLine`, //导出京东订单明细
  loadOrder: `${baseUrl}/v1/jd/order/manager/loadOrder`, //读取订单信息
  getDeliveryInfo: `${baseUrl}/v1/jd/order/manager/deliveryInfo`, //读取订单信息


  // 销售统计
  reportByOrder: `${baseUrl}/v1/jd/order/manager/report/byOrder`, //京东销售订单统计
  reportByOrderSum: `${baseUrl}/v1/jd/order/manager/report/byOrder/sum`, //京东销售订单统计合计
  reportByOrderExport: `${baseUrl}/v1/jd/order/manager/report/byOrder/export`, //京东销售订单统计导出
  reportByGoods: `${baseUrl}/v1/jd/order/manager/report/byGoods`, //京东销售商品统计统计
  reportByGoodsSum: `${baseUrl}/v1/jd/order/manager/report/byGoods/sum`, //京东销售商品统计合计
  reportByGoodsExport: `${baseUrl}/v1/jd/order/manager/report/byGoods/export`, //京东销售订单统计导出


  // 退款订单
  refundQuery: `${baseUrl}/v1/jd/order/refund/manager/query`, //获取订单信息列表
  refundExport: `${baseUrl}/v1/jd/order/refund/manager/export`, //获取订单信息列表
  refundDetails: `${baseUrl}/v1/jd/order/refund/manager/get`, //查看退款订单详情
  refundAgree: `${baseUrl}/v1/jd/order/refund/manager/agree`, //同意退款
  refundRefuse: `${baseUrl}/v1/jd/order/refund/manager/refuse`, //拒绝退款
  refundAgainWX: `${baseUrl}/v2/market/refund/weixin/again`,// 微信退款重新退款

  // 商品变更通知
  updNoticeQuery: `${baseUrl}/v1/jd/goods/manager/updNotice/query`, //查询商品变更通知
  updNoticeTypeList: `${baseUrl}/v1/jd/goods/manager/updNotice/type/list`, //商品变更类型枚举列表
}