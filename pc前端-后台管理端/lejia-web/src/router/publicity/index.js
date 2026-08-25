// 食安公示相关路由(市场运营端)
// 异步加载

const PublicityDetection = () =>
  import("@/views/publicity/Detection");//检测信息
const PublicityRetroactive = () =>
  import("@/views/publicity/Retroactive"); //追溯信息


export default [{
    path: '/publicity/detection', //检测信息
    name: "PublicityDetection",
    component: PublicityDetection,
    meta: {
      notKeepAlive: true
    }
  },
  {
    path: '/publicity/retroactive', //追溯信息
    name: "PublicityRetroactive",
    component: PublicityRetroactive,
    meta: {
      notKeepAlive: true
    }
  }

]