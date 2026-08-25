// 广告管理相关路由
// 异步加载
const PopupAds = () =>
  import("@/views/ads/Ads"); //弹窗广告
const AreaAds = () =>
  import("@/views/ads/AreaAds"); //专区广告
const PointAds = () =>
  import("@/views/ads/mallAds"); //积分商城广告管理
const GtypeAds = () =>
  import("@/views/market/GtypeAds"); //分类页广告
export default [{
  path: "/ads/ads",
  name: "PopupAds",
  component: PopupAds,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/ads/areAads",
  name: "AreaAds",
  component: AreaAds,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/ads/pointAds",
  name: "PointAds",
  component: PointAds,
  meta: {
    notKeepAlive: true
  }
}, {
  path: "/ads/gtypeAds",
  name: "gtypeAds",
  component: GtypeAds,
  meta: {
    notKeepAlive: true
  }
},];