// 会员管理相关路由
// 异步加载
const MemberDeposit = () =>
    import ("@/views/member/Deposit"); //充值记录
const MemberMember = () =>
    import ("@/views/member/Member"); // 会员中心
const MemberPoints = () =>
    import ("@/views/member/MemberPoints"); //会员积分
const MemberTagManage = () =>
    import ("@/views/member/TagManage"); //标签管理
const PurseManage = () =>
    import ("@/views/member/PurseManage"); //钱包查询
const MSD = () =>
    import ("@/views/member/MSD"); //热力豆管理
const RechargeCard = () =>
    import ("@/views/member/RechargeCard"); //充值卡密管理

export default [{
        path: "/member/deposit",
        name: "MemberDeposit",
        component: MemberDeposit,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/member/member', // 会员中心
        name: "MemberMember",
        component: MemberMember,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/member/points",
        name: "MemberPoints",
        component: MemberPoints,
        meta: {
            notKeepAlive: true
        }

    },
    {
        path: "/member/tagManage",
        name: "MemberTagManage",
        component: MemberTagManage,
        meta: {
            notKeepAlive: true
        }

    },
    {
        path: "/member/purseManage",
        name: "PurseManage",
        component: PurseManage,
        meta: {
            notKeepAlive: true
        }

    },
    {
        path: "/member/MSD",
        name: "MSD",
        component: MSD,
        meta: {
            notKeepAlive: true
        }

    },
    {
        path: "/member/rechargeCard",
        name: "RechargeCard",
        component: RechargeCard,
        meta: {
            notKeepAlive: true
        }

    }
];