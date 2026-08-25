// 市场营销相关路由
// 异步加载
const MarketingLottery = () =>
    import ("@/views/marketing/Lottery"); //抽奖活动配置
const MarketingWinning = () =>
    import ("@/views/marketing/Winning"); //中奖清单
const MarketingCoupon = () =>
    import ("@/views/marketing/Coupon"); // 卡券管理
const MarketingCouponGrant = () =>
    import ("@/views/marketing/CouponGrant"); // 卡券发放
const MarketingCouponUse = () =>
    import ("@/views/marketing/CouponUse"); // 卡券使用


export default [{
        path: '/marketing/lottery', // 抽奖活动配置
        name: "MarketingLottery",
        component: MarketingLottery,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/marketing/winning', // 中奖清单
        name: "MarketingWinning",
        component: MarketingWinning,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/marketing/coupon', // 卡券管理
        name: "MarketingCoupon",
        component: MarketingCoupon,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/marketing/coupon/grant', // 卡券发放
        name: "MarketingCouponGrant",
        component: MarketingCouponGrant,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/marketing/coupon/use', // 卡券使用
        name: "MarketingCouponUse",
        component: MarketingCouponUse,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/marketing/member', // 会员管理
        name: "MarketingMember",
        component: MarketingMember,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/marketing/member/points",
        name: "MarketingMemberPoints",
        component: MarketingMemberPoints,
        meta: {
            notKeepAlive: true
        }
    }
]