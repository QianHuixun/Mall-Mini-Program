// 义乌市场获取公众号openid页面
const Rider = () => import("@/views/gzh/rider"); //骑手
const Vendor = () => import("@/views/gzh/vendor"); //商户



export default [
{
  path: "/gzh/rider",
  name: "Rider",
  component: Rider,
  mata: {
    notKeepAlive: true
  }
},{
  path: "/gzh/vendor",
  name: "Vendor",
  component: Vendor,
  mata: {
    notKeepAlive: true
  }
}]