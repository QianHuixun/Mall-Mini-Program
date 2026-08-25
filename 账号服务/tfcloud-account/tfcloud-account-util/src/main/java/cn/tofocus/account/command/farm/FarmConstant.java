package cn.tofocus.account.command.farm;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FarmConstant
{
    public static final String DOMAIN = "farm";
    
    public static final String root = "farm_root";
    
    public static final String boss = "farm_boss";
    
    public static final String cust = "farm_cust";
    
    public static final String USERID_PREFIX = "xasz.";
    
    @UtilityClass
    public static class C
    {
        public static final String CUST = "farmCust";
        
        public static final String DEVICE = "farmDevice";
        
        public static final String PAY = "farmPay";
        
        public static final String PROXY = "farmProxy";
        
        public static final String PUB = "farmPub";
        
        public static final String SYNC = "farmSync";
        
        public static final String SCALE = "farmScale";
        
        public static final String CUST_WEB = "farmCust-Web";
        
        public static final String BOSS_WEB = "farmBoss-Web";
        
        public static final String MARKET_APP = "farmMarketApp";
        
        public static final String MER_APP = "farmMerchantApp";
        
        public static final String DEVICE_MANAGER_APP = "farmDeviceManagerApp"; //密码 ：j8gjWDfjHFgdgd35
        
        public static final String MEMBER = "farmMember";
    }
    
    @UtilityClass
    public static class M
    {
        public static final String BASE = "farm_base";
        
        public static final String WARNING = "farm_warning";
        
        public static final String WATCH = "farm_watch";
        
        public static final String MEMBER = "farm_member";
        
        public static final String SHOPPINGCART = "farm_shoppingCart";
        
        public static final String OLDMALL = "farm_oldmall";
        
        public static final String LEGACY = "farm_legacy";
        
        public static final String MEASUREMENT = "farm_measurement";
        
        public static final String ZYYSC = "farm_zyysc";
        
        public static final String TRUCK = "farm_truck";
        
        public static final String INVENTORY = "farm_inventory";
        
        public static final String LIQUIDATION = "farm_liquidation";
        
        public static final String HYDROPOWER = "farm_hydropower";
        
        public static final String CUSTOM = "farm_custom";
        
    }
    
    @UtilityClass
    public static class R
    {
        /**
         * 超级管理员
         */
        public static final String ADMIN = "farm_admin";
        
        /**
         * 实施总管
         */
        public static final String IMPLEMENTOR_HEAD = "farm_implementor_head";
        
        /**
         * 实施人员
         */
        public static final String IMPLEMENTOR = "farm_implementor";
        
        /**
         * 运营总管
         */
        public static final String OPERATOR_HEAD = "farm_operator_head";
        
        /**
         * 运营人员
         */
        public static final String OPERATOR = "farm_operator";
        
        /**
         * 公司负责人
         */
        public static final String COMPANY_HEAD = "farm_company_head";
        
        /**
         * 市场负责人
         */
        public static final String MARKET_HEAD = "farm_market_head";
        
        /**
         * 市场管理员
         */
        public static final String MARKET_MANAGER = "farm_market_manager";
        
        /**
         * 检测员
         */
        public static final String DETECTOR = "farm_detector";
        
        /**
         * 财务
         */
        public static final String FINANCIAL_STAFF = "farm_financial_staff";
        
        /**
         * 巡检员
         */
        public static final String PATROL = "farm_patrol";
        
        /**
         * 大屏数据管理员
         */
        public static final String LARGE_SCREEN_DATA_MANAGER = "farm_large_screen_data_manager";
        
        /**
         * 市场员工
         */
        public static final String MARKET_EMPLOYEE = "farm_market_employee";
        
    }
    
    @UtilityClass
    public static class SysF
    {
        public static final String MANAGER_ROLE = "managerRole";
        
        public static final String MANAGER_USER = "managerUser";
        
        public static final String MANAGER_ORG = "managerOrg";
        
        public static final String DOMAIN_ADMIN = "domainAdmin";
    }
    
    @UtilityClass
    public static class F
    {
        /**
         * 登陆Boss
         */
        public static final String BOSS_LOGIN = "farm_boss_login";
        
        /**
         * 登陆Cust
         */
        public static final String CUST_LOGIN = "farm_cust_login";
        
        /**
         * 农贸管理员
         */
        public static final String MARKET_ADMIN = "farm_market_admin";
    }
    
    @UtilityClass
    public static class DelF
    {
        
        /**
         * Boss系统模块
         */
        public static final String BOSS_SYS = "farm_boss_sys";
        
        /**
         * 登陆巡检APP
         */
        public static final String PATROL_APP_LOGIN = "farm_patrolApp_login";
        
        /**
         * 运营人员
         */
        public static final String OPERATOR = "farm_operator";
        
        /**
         * 实施总管
         */
        public static final String IMPLEMENTOR_HEAD = "farm_implementor_head";
        
        /**
         * 实施人员
         */
        public static final String IMPLEMENTOR = "farm_implementor";
        
        /**
         * 查看市场管理菜单
         */
        public static final String VIEW_MARKET_MANAGER = "farm_cust_view_marketManager";
        
        /**
         * 操作市场管理
         */
        public static final String OPER_MARKET_MANAGER = "farm_cust_oper_marketManager";
        
        /**
         * 查看商户管理菜单
         */
        public static final String VIEW_MERCHANT_MANAGER = "farm_cust_view_merchantManager";
        
        /**
         * 管理商户
         */
        public static final String OPER_MERCHANT_MANAGER = "farm_cust_oper_merchantManager";
        
        /**
         * 查看摊位管理菜单
         */
        public static final String VIEW_STALL_MANAGER = "farm_cust_view_stallManager";
        
        /**
         * 摊位管理
         */
        public static final String OPER_STALL_MANAGER = "farm_cust_oper_stallManager";
        
        /**
         * 显示商品管理菜单
         */
        public static final String VIEW_GOODS_MANAGER = "farm_cust_view_goodsManager";
        
        /**
         * 可以修改商品信息
         */
        public static final String OPER_GOODS_MANAGER = "farm_cust_oper_goodsManager";
        
        /**
         * 显示供应商管理菜单
         */
        public static final String VIEW_VENDOR_MANAGER = "farm_cust_view_vendorManager";
        
        /**
         * 可以修改供应商信息
         */
        public static final String OPER_VENDOR_MANAGER = "farm_cust_oper_vendorManager";
        
        /**
         * 显示订单管理菜单
         */
        public static final String VIEW_TRADE_MANAGER = "farm_cust_view_tradeManager";
        
        /**
         * 显示检测信息菜单
         */
        public static final String VIEW_DETECTION_MANAGER = "farm_cust_view_detectionManager";
        
        /**
         * 可以修改检测数据
         */
        public static final String OPER_DETECTION_MANAGER = "farm_cust_oper_detectionManager";
        
        /**
         * 显示溯源管理菜单
         */
        public static final String VIEW_BOOK_MANAGER = "farm_cust_view_bookManager";
        
        /**
         * 可以修改溯源数据
         */
        public static final String OPER_BOOK_MANAGER = "farm_cust_oper_bookManager";
        
        /**
         * 显示设备管理菜单
         */
        public static final String VIEW_DEVICE_MANAGER = "farm_cust_view_deviceManager";
        
        /**
         * 可以修改设备管理信息
         */
        public static final String OPER_DEVICE_MANAGER = "farm_cust_oper_deviceManager";
        
        /**
         * 可配置大数据屏的模拟数据
         */
        public static final String LARGE_SCREEN_DATA_MANAGER = "farm_cust_view_largeScreenDataManager";
        
        /**
         * 显示商户考评菜单
         */
        public static final String VIEW_MERCHANT_EVALUATION = "farm_cust_view_evaluation";
        
        /**
         * 可以修改商户考评数据
         */
        public static final String OPER_MERCHANT_EVALUATION = "farm_cust_oper_evaluation";
        
        /**
         * 显示商户评价菜单
         */
        public static final String VIEW_MERCHANT_COMMENT = "farm_cust_view_comment";
        
        /**
         * 可以修改商户评价数据
         */
        public static final String OPER_MERCHANT_COMMENT = "farm_cust_oper_comment";
        
        /**
         * 显示商户荣誉菜单
         */
        public static final String VIEW_MERCHANT_HONOR = "farm_cust_view_honor";
        
        /**
         * 可以修改商户荣誉数据
         */
        public static final String OPER_MERCHANT_HONOR = "farm_cust_oper_honor";
        
        /**
         * 显示投诉及建议菜单
         */
        public static final String VIEW_MARKET_SUGGESTION = "farm_cust_view_market_suggestion";
        
        /**
         * 可以修改投诉及建议数据
         */
        public static final String OPER_MARKET_SUGGESTION = "farm_cust_oper_market_suggestion";
        
        /**
         * 显示会员管理菜单
         */
        public static final String VIEW_VIP_MANAGER = "farm_cust_view_vipManager";
        
        /**
         * 可以操作会员卡业务
         */
        public static final String OPER_VIP_MANAGER = "farm_cust_oper_vipManager";
        
        /**
         * 显示巡检菜单
         */
        public static final String VIEW_PATROL = "farm_cust_view_patrol";
        
        /**
         * 可以进行巡检管理
         */
        public static final String OPER_PATROL = "farm_cust_oper_patrol";
        
        /**
         * 显示报修管理菜单
         */
        public static final String VIEW_REPAIR = "farm_cust_view_repair";
        
        /**
         * 可以管理报修
         */
        public static final String OPER_REPAIR = "farm_cust_oper_repair";
        
        /**
         * 显示救生管理菜单
         */
        public static final String VIEW_LIFESAVING = "farm_cust_view_lifesaving";
        
        /**
         * 可以管理救生图片
         */
        public static final String OPER_LIFESAVING = "farm_cust_oper_lifesaving";
        
        /**
         * 市场运营模块
         */
        public static final String MALL = "farm_cust_mall";
        
        /**
         * 显示财务管理菜单
         */
        public static final String VIEW_FINANCE = "farm_cust_view_finance";
        
        /**
         * 可以进行资金管理
         */
        public static final String OPER_FINANCE = "farm_cust_oper_finance";
        
        /**
         * 显示物业管理菜单
         */
        public static final String VIEW_PROPERTY = "farm_cust_view_property";
        
        /**
         * 可以增加导入商户账单
         */
        public static final String OPER_PROPERTY = "farm_cust_oper_property";
        
        /**
         * 查看公司信息
         */
        public static final String VIEW_COMPANY_INFO = "farm_cust_view_companyInfo";
        
        /**
         * 修改公司信息
         */
        public static final String OPER_COMPANY_INFO = "farm_cust_oper_companyInfo";
        
        /**
         * 查看员工
         */
        public static final String VIEW_SYS_USER = "farm_cust_view_sysUser";
        
        /**
         * 管理员工
         */
        public static final String OPER_SYS_USER = "farm_cust_oper_sysUser";
        
        /**
         * 查看角色
         */
        public static final String VIEW_SYS_ROLE = "farm_cust_view_sysRole";
        
        /**
         * 管理角色
         */
        public static final String OPER_SYS_ROLE = "farm_cust_oper_sysRole";
        
        /**
         * 操作日志
         */
        public static final String VIEW_LOG = "farm_cust_view_log";
        
        /**
         * 微信授权
         */
        public static final String VIEW_WX = "farm_cust_view_wx";
        
        /**
         * 意见反馈
         */
        public static final String OPER_ADVICE = "farm_cust_oper_advice";
        
        /**
         * 支付账户
         */
        public static final String OPER_PAY_NUMBER = "farm_cust_oper_payNumber";
        
        /**
         * 数据统计
         */
        public static final String REPORT_STATISTICS = "farm_cust_report_statistics";
        
        /**
         * 交易分析
         */
        public static final String REPORT_TRADE = "farm_cust_report_trade";
        
        /**
         * 过秤报表
         */
        public static final String REPORT_SCALE = "farm_cust_report_scale";
        
        /**
         * 客群分析
         */
        public static final String REPORT_USER = "farm_cust_report_user";
        
        /**
         * 客群分析
         */
        public static final String VIEW_EMPLOYEE_MANAGER = "farm_cust_view_employee_manager";
        
        /**
         * 客群分析
         */
        public static final String OPER_EMPLOYEE_MANAGER = "farm_cust_oper_employee_manager";
        
        /**
         * 客群分析
         */
        public static final String VIEW_TRAINING = "farm_cust_view_training";
        
        /**
         * 客群分析
         */
        public static final String OPER_TRAINING = "farm_cust_oper_training";
        
        /**
         * 称查询
         */
        public static final String SCALE_QUERY = "farm_cust_scaleQuery";
        
        /**
         * 客如云
         */
        public static final String KRY = "farm_cust_kry";
        
        /**
         * 监管监控
         *
         */
        public static final String SUPERVISON_MONITOR_WARNING = "farm_cust_supervison_monitor_warning";
        
        /**
         * 设备异常预警
         *
         */
        
        public static final String SUPERVISON_DEVICE_WRNING = "farm_cust_supervison_device_warning";
        
        /**
             * 证照过期预警
         *
         */
        public static final String SUPERVISON_CREDENTIALS_WRNING = "farm_supervison_credentials_warning";
        
        /**
         * 价格预警
        *
        */
        
        public static final String SUPERVISON_PRICE_WRNING = "farm_cust_supervison_price_warning";
        
        /**
         * 商户行为规范
        *
        */
        public static final String SUPERVISON_CONDUCT_WRNING = "farm_cust_supervison_conduct_warning";
        
    }
}
