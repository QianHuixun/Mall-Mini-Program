/*
 * @Author: 沙晓
 * @Date: 2025-06-06 13:40:31
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-06-13 09:51:09
 * @Description: 财务管理路由
 * @FilePath: /lejia-web/src/router/finance/index.js
 */
const financeAccount = () => import("@/views/finance/accountManage.vue");
const financeMarketAccount = () => import("@/views/finance/accountMarketManage.vue");
const financeDetails = () => import("@/views/finance/fundDetails.vue");
const financeBill = () => import("@/views/finance/settleBill.vue");

export default [{
  path: "/finance/account",
  name: "financeAccount",
  component: financeAccount,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/finance/details",
  name: "financeDetails",
  component: financeDetails,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/finance/bill",
  name: "financeBill",
  component: financeBill,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/finance/marketAccount",
  name: "financeMarketAccount",
  component: financeMarketAccount,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/finance/marketDetails",
  name: "financeMarketDetails",
  component: financeDetails,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/finance/marketBill",
  name: "financeMarketBill",
  component: financeBill,
  meta: {
    notKeepAlive: true
  }
}]