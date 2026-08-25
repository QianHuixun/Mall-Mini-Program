//活动管理相关路由
// 异步加载
const ActivityLottery = () =>
  import("@/views/activity/Lottery"); //奖品配置
const ActivityWinning = () =>
  import("@/views/activity/Winning"); //中奖查询

export default [{
    path: '/activity/lottery', // 奖品配置
    name: "ActivityLottery",
    component: ActivityLottery,
    meta: {
      notKeepAlive: true
    }
  },
  {
    path: '/activity/winning', // 中奖查询
    name: "ActivityWinning",
    component: ActivityWinning,
    meta: {
      notKeepAlive: true
    }
  }
]