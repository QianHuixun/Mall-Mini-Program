//基础设置相关路由
// 异步加载
const BasicInfo = () =>
    import ("@/views/base/BasicInfo"); //基本信息配置
const Classic = () =>
    import ("@/views/base/Classic"); //商品分类
const MallGoods = () =>
    import ("@/views/base/Goods"); //商品库中心
const MallPostage = () =>
    import ("@/views/base/Postage"); //运费配置
const BaseMarketPostage = () =>
    import ("@/views/base/Postage_mkt"); //运费配置 => 市场管理端
const BaseMarketInfo = () =>
    import ("@/views/base/Market"); //市场信息
const Dispatch = () =>
    import ("@/views/base/Dispatch"); //派单配置
const Supply = () =>
    import ("@/views/goods/Supply"); //商品供应库
const Promote = () =>
    import ("@/views/base/Promote")
const ThirdPayment = () =>
    import ("@/views/base/ThirdPayment") // 第三方支付渠道
const Gtype = () =>
    import ("@/views/base/Gtype") // 商品分类
const Device = () => import("@/views/base/Device")  // 设备管理
const SearchCode = () => import("@/views/base/SearchCode")  // 搜索词管理

export default [{
    path: "/base/info",
    name: "BasicInfo",
    component: BasicInfo,
    meta: {
        notKeepAlive: true
    }
}, {
    path: "/base/classic",
    name: "Classic",
    component: Classic,
    meta: {
        notKeepAlive: true
    }
}, {
    path: "/base/goods",
    name: "MallGoods",
    component: MallGoods,
    meta: {
        notKeepAlive: true
    }
}, {
    path: "/base/postage",
    name: "MallPostage",
    component: MallPostage,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/market/postage', // 运费配置
    name: "BaseMarketPostage",
    component: BaseMarketPostage,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/market', // 市场信息
    name: "BaseMarketInfo",
    component: BaseMarketInfo,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/dispatch', // 派单配置
    name: "Dispatch",
    component: Dispatch,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/supply', // 商品供应库
    name: "Supply",
    component: Supply,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/promote', // 推广管理
    name: 'Promote',
    component: Promote,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/thirdPayment', // 第三方支付渠道
    name: 'ThirdPayment',
    component: ThirdPayment,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/market/gtype', // 商品分类
    name: 'Gtype',
    component: Gtype,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/market/device', // 商品分类
    name: 'Device',
    component: Device,
    meta: {
        notKeepAlive: true
    }
}, {
    path: '/base/searchCode', // 商品分类
    name: 'searchCode',
    component: SearchCode,
    meta: {
        notKeepAlive: true
    }
}]