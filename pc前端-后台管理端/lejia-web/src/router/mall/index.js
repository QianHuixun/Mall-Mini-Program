/*
 * @Author: 沙晓
 * @Date: 2025-07-18 15:58:26
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-07-21 14:46:25
 * @Description: file content
 * @FilePath: /lejia-web/src/router/mall/index.js
 */
// 商城管理相关路由
// 异步加载

const Ads = () => import("@/views/market/Ads"); //广告
const MallFeedback = () => import("@/views/mall/Feedback"); //客户回馈
const Goods = () => import("@/views/goods/Goods"); //商品维护
const Problem = () => import("@/views/mall/Problem")
const Recommend = () => import("@/views/mall/Recommend");	 // 推荐商品管理
const Comment = () => import("@/views/market/GoodsComment");	 // 菜品评价管理

export default [{
  path: "/mall/goods/:pkey",
  name: "Goods",
  component: Goods,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/mall/ads",
  name: "Ads",
  component: Ads,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/mall/feedback",
  name: "MallFeedback",
  component: MallFeedback,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/mall/problem",
  name: "Problem",
  component: Problem,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/mall/recommend",
  name: "recommend",
  component: Recommend,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/mall/comment",
  name: "Comment",
  component: Comment,
  meta: {
    notKeepAlive: true
  }
}]