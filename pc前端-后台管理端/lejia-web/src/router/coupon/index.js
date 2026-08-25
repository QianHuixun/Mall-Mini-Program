/*
 * @Author: 沙晓
 * @Date: 2022-05-09 11:13:38
 * @LastEditors: 沙晓
 * @LastEditTime: 2024-04-17 14:41:45
 * @Description: 卡券管理相关路由
 * @FilePath: /lejia-web/src/router/coupon/index.js
 */
// 卡券管理相关路由
// 异步加载
const CouponCoupon = () =>
    import ("@/views/coupon/Coupon"); // 卡券管理
const CouponGrant = () =>
    import ("@/views/coupon/CouponGrant"); // 卡券发放
const CouponUse = () =>
    import ("@/views/coupon/CouponUse"); // 卡券使用查询
const CouponGift = () =>
    import ("@/views/coupon/CouponGift"); // 礼品券管理
const CouponEvents = () =>
    import ("@/views/coupon/CouponEvents"); // 卡券活动
const CouponGiftUse = () =>
    import ("@/views/coupon/CouponGiftUse"); // 礼品券使用查询

export default [{
        path: '/coupon/gift', // 礼品券管理
        name: "CouponGift",
        component: CouponGift,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/coupon/coupon', // 卡券管理
        name: "CouponCoupon",
        component: CouponCoupon,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/coupon/grant', // 卡券发放
        name: "CouponGrant",
        component: CouponGrant,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/coupon/use', // 卡券使用
        name: "CouponUse",
        component: CouponUse,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/coupon/events', // 卡券活动
        name: "CouponEvents",
        component: CouponEvents,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: '/coupon/giftUse', // 礼品券使用查询
        name: "CouponGiftUse",
        component: CouponGiftUse,
        meta: {
            notKeepAlive: true
        }
    }
]