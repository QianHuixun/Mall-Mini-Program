// 义乌市场获取公众号openid页面
const Goods = () => import("@/views/jd/goods");
const Order = () => import("@/views/jd/order");
const RefundOrder = () => import("@/views/jd/refundOrder");
const JDSale = () => import("@/views/data/JDSale")
const UpdNotice = () => import("@/views/jd/updNotice")
const CategoryRelation = () => import("@/views/jd/CategoryRelation")

export default [
  {
    path: "/jd/goods",
    name: "Goods",
    component: Goods,
    mata: {
      notKeepAlive: true,
    },
  },
  {
    path: "/jd/order",
    name: "Order",
    component: Order,
    mata: {
      notKeepAlive: true,
    },
  },
  {
    path: "/jd/refundOrder",
    name: "refundOrder",
    component: RefundOrder,
    mata: {
      notKeepAlive: true,
    },
  },
  {
    path: "/data/JDSale",
    name: "JDSale",
    component: JDSale,
    mata: {
      notKeepAlive: true,
    },
  },
  {
    path: "/jd/updNotice",
    name: "updNotice",
    component: UpdNotice,
    mata: {
      notKeepAlive: true,
    },
  },
  {
    path: "/jd/categoryRelation",
    name: "categoryRelation",
    component: CategoryRelation,
    mata: {
      notKeepAlive: true,
    },
  },
];
