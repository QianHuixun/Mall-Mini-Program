// 商品管理相关路由
const Goods = () => import("@/views/goods/Goods"); //商品维护
const Supply = () => import("@/views/goods/Supply"); //商品供应库
const Recommend = () => import("@/views/goods/sub/Recommend/Index");	 // 推荐商品管理


export default [
  // {
  //   path: "/goods/goods/:pkey",
  //   name: "GoodsGoods",
  //   component: GoodsGoods,
  //   mata: {
  //     notKeepAlive: true
  //   }
  // },

  {
    path: "/goods/:pkey",
    name: "Goods",
    component: Goods,
    meta: {
      notKeepAlive: true
    }
  }, {
    path: "/goods/supply/supply",
    name: "Supply",
    component: Supply,
    meta: {
      notKeepAlive: false
    }
  }, {
    path: "/goods/Recommend/:pkey",
    name: "Recommend",
    component: Recommend,
    meta: {
      notKeepAlive: false
    }
  }
]