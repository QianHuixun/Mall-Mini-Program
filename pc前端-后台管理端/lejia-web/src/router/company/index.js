// 公司管理相关路由(公司信息)
// 异步加载
const CompanyInfo = () => import("@/views/company/Info"); //公司信息

export default [
  {
    path: '/company/info', // 商场公司管理
    name: "CompanyInfo",
    component: CompanyInfo,
    meta: {
      notKeepAlive: true
    }
  }
]