// 公司管理相关路由
// 异步加载
const VendorMerchant = () =>
    import ("@/views/vendor/Merchant"); //商户管理
const VendorOrder = () =>
    import ("@/views/vendor/MerchantOrder"); //积分订单
const VendorBill = () =>
    import ("@/views/vendor/MerchantBill"); //商户对账
const VendorComBill = () =>
    import ("@/views/vendor/MerchantComBill"); //商户对账-佣金
const MerchantAdd = () =>
    import ("@/views/vendor/sub/merchant/MerchantAdd"); //商户新增
const MerchantEdit = () =>
    import ("@/views/vendor/sub/merchant/MerchantEdit"); //商户编辑
const CancelRecord = () =>
    import ("@/views/vendor/CancelRecord"); //撤销记录
const VendorSettle = () =>
    import ("@/views/vendor/MerchantSettle"); //商户结算
const VendorWallet = () =>
    import ("@/views/vendor/Wallet"); //商户钱包
const VendorWithdrawal = () =>
    import ("@/views/vendor/Withdrawal"); //提现打款

const VendorComSettle = () =>
    import ("@/views/vendor/MerchantComSettle"); //商户结算-佣金
const VendorSettleReport = () =>
    import ("@/views/vendor/MerchantSettleReport"); //商户结算-佣金
const PreferredMerchant = () =>
    import ("@/views/vendor/PreferredMerchant"); //精选商户管理

const PackingCharge = () =>
    import ("@/views/vendor/PackingCharge"); //打包费

const SupplierManager = () =>
    import ("@/views/vendor/SupplierManager"); //供应商管理
const SupplierManagerAdd = () =>
    import ("@/views/vendor/sub/supplierManager/supplierManagerAdd"); //供应商管理新增
const SupplierManagerEdit = () =>
    import ("@/views/vendor/sub/supplierManager/supplierManagerEdit"); //供应商管理编辑


MerchantEdit;
export default [{
        path: "/vendor/merchant", // 商户管理 --运营端是积分商户 市场端是商户管理
        name: "VendorMerchant",
        component: VendorMerchant,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/order", // 积分订单
        name: "VendorOrder",
        component: VendorOrder,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/bill", // 商户对账
        name: "VendorBill",
        component: VendorBill,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/combill", // 商户对账-佣金
        name: "VendorComBill",
        component: VendorComBill,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/wallet", // 商户钱包
        name: "VendorWallet",
        component: VendorWallet,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/withdrawal", // 提现打款
        name: "VendorWithdrawal",
        component: VendorWithdrawal,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/marketMerchant", // 运营端-市场商户管理
        name: "VendorMerchant",
        component: VendorMerchant,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/merchant/add", // 新增商户
        name: "MerchantAdd",
        component: MerchantAdd,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/merchant/edit", // 新增商户
        name: "MerchantEdit",
        component: MerchantEdit,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/cancel", // 撤销记录
        name: "CancelRecord",
        component: CancelRecord,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/settle", // 商户结算
        name: "VendorSettle",
        component: VendorSettle,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/comsettle", // 商户结算-佣金
        name: "VendorComSettle",
        component: VendorComSettle,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/settlereport", // 结算报表
        name: "VendorSettleReport",
        component: VendorSettleReport,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/PreferredMerchant", // 精选商户管理
        name: "VendorPreferredMerchant",
        component: PreferredMerchant,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/packingCharge", // 精选商户管理
        name: "PackingCharge",
        component: PackingCharge,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/supplierManager", // 供应商
        name: "SupplierManager",
        component: SupplierManager,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/supplierManager/add", // 供应商新增
        name: "SupplierManagerAdd",
        component: SupplierManagerAdd,
        meta: {
            notKeepAlive: true
        }
    },
    {
        path: "/vendor/supplierManager/edit", // 供应商编辑
        name: "SupplierManagerEdit",
        component: SupplierManagerEdit,
        meta: {
            notKeepAlive: true
        }
    }
];