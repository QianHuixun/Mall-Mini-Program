// 市场营销相关路由
// 异步加载
const  Cookfd = () => import("@/views/cookfd/Cookfd"); //菜谱管理
const  CookType = () => import("@/views/cookfd/CookType"); //菜谱分类

export default [
  {
    path: '/cookfd/cookfd', // 菜谱管理
    name: "Cookfd",
    component: Cookfd,
    meta: {
      notKeepAlive: true
    }
  },
  {
    path: '/cookfd/cooktype', // 菜谱分类
    name: "CookType",
    component: CookType,
    meta: {
      notKeepAlive: true
    }
  }
]