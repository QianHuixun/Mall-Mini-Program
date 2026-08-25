// 交易管理相关路由
// 异步加载

const OrderMall = () =>
  import("@/views/order/Mall"); //商城订单
const OrderRefund = () =>
  import("@/views/order/Refund"); //退款管理    
const OrderMarket = () =>
  import("@/views/order/Market"); //市场订单(市场管理端)
const OrderRider = () =>
  import("@/views/order/Rider"); //骑手订单(市场管理端)
const OrderCollage = () =>
  import("@/views/order/Collage"); //团购订单(市场管理端)
const OrderReport = () =>
  import("@/views/order/Report"); //财务报表
const OrderDetail = () =>
  import("@/views/order/Detail"); //交易明细
const OrderTixian = () =>
  import("@/views/order/Tixian"); //提现管理

export default [

  {
    path: "/order/mall",
    name: "OrderMall",
    component: OrderMall,
    meta: {
      notKeepAlive: true
    }
  },
  {
    path: "/order/market",
    name: "OrderMarket",
    component: OrderMarket,
    meta: {
      notKeepAlive: true
    }
  },
  {
    path: "/order/refund",
    name: "OrderRefund",
    component: OrderRefund,
    meta: {
      notKeepAlive: true
    }

  },
  {
    path: "/order/rider",
    name: "OrderRider",
    component: OrderRider,
    meta: {
      notKeepAlive: true
    }

  },
  {
    path: "/order/collage",
    name: "OrderCollage",
    component: OrderCollage,
    meta: {
      notKeepAlive: true
    }

  },
  {
    path: "/order/report",
    name: "OrderReport",
    component: OrderReport,
    meta: {
      notKeepAlive: true
    }

  }, {
    path: "/order/detail",
    name: "OrderDetail",
    component: OrderDetail,
    meta: {
      notKeepAlive: true
    }

  },
  {
    path: "/order/tixian",
    name: "OrderTixian",
    component: OrderTixian,
    meta: {
      notKeepAlive: true
    }

  },

];