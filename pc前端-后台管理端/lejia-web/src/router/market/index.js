/*
 * @Author: 沙晓
 * @Date: 2025-07-10 15:09:44
 * @LastEditors: 沙晓
 * @LastEditTime: 2025-07-18 16:19:00
 * @Description: file content
 * @FilePath: /lejia-web/src/router/market/index.js
 */
// 市场管理相关路由(市场运营端)
// 异步加载

const Ads = () =>
  import("@/views/ads/AreaAds"); //广告
const GtypeAds = () =>
  import("@/views/market/GtypeAds"); //分类页广告
const funmenuConfig = () =>
  import("@/views/market/funmenuConfig"); //分类页广告
const MKT_MarketRider = () =>
  import("@/views/market/Rider"); //骑手管理
const MallFeedback = () =>
  import("@/views/mall/Feedback"); //客户回馈
const Desktop = () =>
  import("@/views/market/Desktop"); //桌位管理
const Comment = () =>
  import("@/views/market/GoodsComment"); //菜品评价管理




export default [{
  path: "/market/ads",
  name: "Ads",
  component: Ads,
  meta: {
    notKeepAlive: true
  }
},
{
  path: "/market/gtypeAds",
  name: "GtypeAds",
  component: GtypeAds,
  meta: {
    notKeepAlive: true
  }
},
{
  path: "/market/funmenuConfig",
  name: "funmenuConfig",
  component: funmenuConfig,
  meta: {
    notKeepAlive: true
  }
},
{
  path: '/market/rider', // 骑手管理
  name: "MKT_MarketRider",
  component: MKT_MarketRider,
  meta: {
    notKeepAlive: true
  }
},
{
  path: '/market/feedback', // 骑手管理
  name: "MallFeedback",
  component: MallFeedback,
  meta: {
    notKeepAlive: true
  }
},
{
  path: '/market/desktop', // 桌位管理
  name: "Desktop",
  component: Desktop,
  meta: {
    notKeepAlive: true
  }
},
{
  path: '/market/comment', // 菜品评价管理
  name: "Comment",
  component: Comment,
  meta: {
    notKeepAlive: true
  }
}

]