//市场运营相关路由
// 异步加载
const OperationCompany = () => import("@/views/operation/Company"); //市场公司


export default [
  {
    path: '/operation/company', // 商场公司管理
    name: "OperationCompany",
    component: OperationCompany,
    meta: {
      notKeepAlive: true
    }
  }
  ]