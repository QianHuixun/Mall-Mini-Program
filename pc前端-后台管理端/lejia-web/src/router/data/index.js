// 数据中心相关路由

const Table = () =>
    import ("@/views/data/Table"); //通用普通报表统计
const Chart = () =>
    import ("@/views/data/Chart"); //图表分析
const Abnormal = () =>
    import ("@/views/data/Abnormal"); //异常货物分析
const Members = () =>
    import ("@/views/data/Members"); //付费会员明细
const Purchase = () =>
    import ("@/views/data/Purchase"); //商户采购报表
const Operating = () =>
    import ("@/views/data/Operating"); //经营数据统计
const SupplierSale = () =>
    import ("@/views/data/SupplierSale"); //供应商销售统计
const GoodsSummary = () =>
    import ("@/views/data/goodsSummary"); //供应商销售统计
const GoodsSummaryDetail = () =>
    import ("@/views/data/sub/goodsSummary/detail"); //供应商销售统计
export default [{
    path: "/data/table/:pkey",
    name: "Table",
    component: Table,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/chart/:pkey",
    name: "Chart",
    component: Chart,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/abnormal",
    name: "Abnormal",
    component: Abnormal,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/members",
    name: "Members",
    component: Members,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/purchase",
    name: "Purchase",
    component: Purchase,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/operating/:type",
    name: "Operating",
    component: Operating,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/supplierSale",
    name: "SupplierSale",
    component: SupplierSale,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/goodsSummary",
    name: "GoodsSummary",
    component: GoodsSummary,
    mata: {
        notKeepAlive: true
    }
}, {
    path: "/data/goodsSummary/detail",
    name: "GoodsSummaryDetail",
    component: GoodsSummaryDetail,
    mata: {
        notKeepAlive: true
    }
}]