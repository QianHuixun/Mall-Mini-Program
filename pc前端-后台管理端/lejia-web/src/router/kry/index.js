// 客如云相关路由
// 异步加载
const KryKry = () => import("@/views/kry/Kry"); //客如云商户
const KryOrder = () =>  import ("@/views/kry/Order"); //客如云订单

export default [{
    path: '/kry/kry', // 客如云商户
    name: "KryKry",
    component: KryKry,
    meta: {
      notKeepAlive: true
    }
  },
  {
    path: "/kry/order",
    name: "KryOrder",
    component: KryOrder,
    meta: {
      notKeepAlive: true
    }
  }
]