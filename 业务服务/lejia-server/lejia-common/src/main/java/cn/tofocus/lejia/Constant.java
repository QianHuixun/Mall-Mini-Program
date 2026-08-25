package cn.tofocus.lejia;

import java.math.BigDecimal;

public class Constant
{
    
    public static final String DomainId = "zyysc";
    
    public final static String rootRoleGroup = "zyysc_root";
    
    public static final String Operation = "system_";
    
    public static class App
    {
        /**
         * BOSS前端
         */
        public static final String WECHAT = "zyysc-wechat";
        
        /**
         * web前端
         */
        public static final String WEB = "zyysc-web";
        public static final String WEB_COMPANY = "zyysc-web-company";
        public static final String WEB_MARKET = "zyysc-web-market";

        
        /**
         * 后台服务
         */
        public static final String SERVER = "zyysc-server";
        
        /**
         * 管理小程序
         */
        public static final String MP_MANAGER = "zyysc-mp";
        
        /**
         * 商户Android
         */
        public static final String MERCHANT_ANDROID = "zyysc-android";
        
        /**
         * 商户IOS
         */
        public static final String MERCHANT_IOS = "zyysc-ios";
        
    }
    
    public static class Role
    {
        
        /**
         * 运营管理
         */
        public static final String COMPANY_HEAD = "zy_company_head";
        
        /**
         * 公司负责人
         */
        public static final String MARKET_HEAD = "zy_market_head";
        
        /**
         * 市场负责人
         */
        public static final String MARKET_MANAGER = "zy_market_manager";
        
        /**
         * H5用户
         */
        public static final String H5_USER = "h5_user";
        
    }
    
    public static class SysF
    {
        public static final String MANAGER_ROLE = "managerRole";
        
        public static final String MANAGER_USER = "managerUser";
        
        public static final String MANAGER_ORG = "managerOrg";
        
        public static final String DOMAIN_ADMIN = "domainAdmin";
    }
    
    public static class SysFunction
    {
        /**
         * 查询授权控制的数据
         */
        public static final String QUERY_INFO = "queryInfo";
        
        /**
         * 修改授权控制的数据
         */
        public static final String WRITE_INFO = "writeInfo";
        
        /**
         * 管理角色
         */
        public static final String MANAGER_ROLE = "managerRole";
        
        /**
         * 增加用户
         */
        public static final String ADD_USER = "addUser";
        
        /**
         * 修改用户
         */
        public static final String MOD_USER = "modUser";
        
        /**
         * 删除用户
         */
        public static final String DEL_USER = "delUser";
        
        /**
         * 修改用户的角色
         */
        public static final String MANAGER_USER_ROLE = "managerUserRole";
        
        /**
         * 增加机构
         */
        public static final String ADD_ORG = "addOrg";
        
        /**
         * 修改机构
         */
        public static final String MOD_ORG = "modOrg";
        
        /**
         * 删除机构
         */
        public static final String DEL_ORG = "delOrg";
        
        /**
         * 增加部门
         */
        public static final String ADD_DEPT = "addDept";
        
        /**
         * 修改部门
         */
        public static final String MOD_DEPT = "modDept";
        
        /**
         * 删除部门
         */
        public static final String DEL_DEPT = "delDept";
    }
    
    public static class Function
    {
        public static final String ZYYSC_LOGIN = "zy_login";
        /**
         * 首页
         */
        public static final String ZYYSC_BOSS_INDEX = "zy_boss_index";
        
        /**
         * 商城基础信息配置
         */
        public static final String BOSS_MALL_CONFIG = "zy_boss_mall_config";
        
        /**
         * 运费配置
         */
        public static final String BOSS_MALL_POSTAGE_CONFIG = "zy_mall_postage_config";
        
        /**
         * 商品分类
         */
        public static final String BOSS_MALL_GOODS_CLASSIFICATION = "zy_mall_goods_classification";
        
        /**
         * 商品库中心
         */
        public static final String BOSS_MALL_GOODS_HOUSE = "zy_mall_goods_house";
        
        /**
         * 商品供应库
         */
        public static final String BOSS_GOODS_SUPPLY = "zy_boss_goods_supply";
        
        /**
         * 推广管理
         */
        public static final String BOSS_PROMOTE_MANAGER = "zy_boss_promote_manager";

        /**
         * 推广管理
         */
        public static final String BOSS_THIRD_PAYMENT = "zy_boss_third_payment";
        
        /**
         * 积分商品管理
         */
        public static final String BOSS_GOODS_POINT_MANAGER = "zy_goods_point_manager";
        
        /**
         * 礼券商品管理
         */
        public static final String BOSS_MALL_GIFT = "zy_mall_gift";
        
        /**
         * 优惠券商品管理
         */
        public static final String BOSS_MALL_COUPON = "zy_mall_coupon";
        
        /**
         * 广告管理
         * 积分商城广告
         */
        public static final String BOSS_MALL_ADVER = "zy_mall_adver";
        
        /**
         * 意见反馈
         */
        public static final String BOSS_MALL_CUSTOMER_FEEDBACK = "zy_mall_customer_feedback";
        
        public static final String BOSS_MALL_CUSTOMER_FEEDBACK_2 = "zy_mall_customer_feedback_2";
        /**
         * 常见问题
         */
        public static final String BOSS_MALL_PROBLEM = "zy_mall_problem";
        
        /**
         * 弹窗广告
         */
        public static final String BOSS_INDEX_ADVERT = "zy_boss_index_advert";
        
        /**
         * 专区广告
         */
        public static final String BOSS_SPECIAL_AREA_ADVERT = "zy_boss_special_area_advert";
        
        /**
         * 会员中心
         */
        public static final String BOSS_MEMBER_CENTER = "zy_member_center";
        
        public static final String BOSS_MEMBER_CENTER_2 = "zy_member_center_2";
        
        /**
         * 充值记录
         */
        public static final String BOSS_MEMBER_RECHARGE_RECORD = "zy_member_recharge_record";
        
        /**
         * 积分查询
         */
        public static final String BOSS_MEMBER_POINT = "zy_member_point";

        /**
         * 钱包查询
         */
        public static final String BOSS_MEMBER_COMM = "zy_member_comm";

        /**
         * 标签管理
         */
        public static final String BOSS_TAG_MANAGE = "zy_tag_manage";
        
        /**
         * 奖品配置
         */
        public static final String BOSS_ACTIVITY_PRIZE = "zy_activity_prize";
        
        /**
         * 中奖查询
         */
        public static final String BOSS_ACTIVITY_INQUIRE = "zy_activity_inquire";
        
        public static final String BOSS_ACTIVITY_INQUIRE_2 = "zy_activity_inquire_2";
        
        /**
         * 卡券管理
         */
        public static final String BOSS_COUPON_MANAGER = "zy_coupon_manager";
        
        /**
         * 卡券发放
         */
        public static final String BOSS_COUPON_GRANT = "zy_coupon_grant";
        
        /**
         * 卡券使用查询
         */
        public static final String BOSS_COUPON_INQUIRE = "zy_coupon_inquire";
        
        /**
         * 商城订单
         */
        public static final String BOSS_ORDER_MALL = "zy_order_mall";
        
        /**
         * 退款管理 
         */
        public static final String BOSS_AFTER_SALE_REFUND_MANAGER = "zy_after_sale_refund_manager";
        
        public static final String BOSS_AFTER_SALE_REFUND_MANAGER_2 = "zy_after_sale_refund_manager_2";
        
        /**
         * 提现管理 
         */
        public static final String BOSS_ORDER_DRAW = "zy_order_draw";
        
        public static final String BOSS_ORDER_DRAW_2 = "zy_order_draw_2";
        
        /**
         * 对账中心
         */
        public static final String BOSS_BILL_MANAGER = "zy_bill_manager";
        
        /**
         * 账单明细 
         */
        public static final String BOSS_BILL_DETAIL = "zy_bill_detail";
        
        /**
         * 公司市场管理
         */
        public static final String BOSS_MARKET_COMPANY_MANAGER = "zy_market_company_manager";
        
        public static final String BOSS_MARKET_COMPANY_MANAGER_2 = "zy_market_company_manager_2";
        
        /**
         * 合作商户 - 商户管理
         * 积分商城商户
         */
        public static final String BOSS_VENDOR_MANAGER = "zy_vendor_manager";
        
        public static final String BOSS_VENDOR_MANAGER_2 = "zy_vendor_manager_2";
        
        /**
         * 合作商户 - 商户管理
         * 市场商城商户
         */
        public static final String BOSS_MARKET_VENDOR_MANAGER = "zy_boss_market_vendor_manager";
        
        public static final String BOSS_MARKET_VENDOR_MANAGER_2 = "zy_market_vendor_manager_2";
        
        /**
         * 商户结算
         */
        public static final String BOSS_VENDOR_SETTLEMENT = "zy_boss_vendor_settlement";
        /**
         * 商户结算报表
         */
        public static final String BOSS_VENDOR_SETTLEMMARKET_VENDOR_SETTLEMENTENT_REPORT = "zy_boss_vendor_settlement_report";
        
        /**
         * 商户对账
         */
        public static final String BOSS_VENDOR_BILL = "zy_boss_vendor_bill";
        
        /**
         * 积分订单
         */
        public static final String BOSS_VENDOR_POINT_ORDER = "zy_vendor_point_order";
        
        /**
         * 客如云商户
         */
        public static final String BOSS_KRU_MERCHANT = "zy_kru_merchant";
        
        public static final String BOSS_KRU_MERCHANT_2 = "zy_kru_merchant_2";
        
        /**
         * 客如云订单
         */
        public static final String BOSS_ORDER_KRY = "zy_order_kry";
        
        /**
         * boss数据中心
         */
        public static final String BOSS_DATA_CENTER = "zy_data_center";
        
        public static final String BOSS_DATA_CENTER_1 = "zy_data_center_01";
        
        public static final String BOSS_DATA_CENTER_2 = "zy_data_center_02";
        
        public static final String BOSS_DATA_CENTER_3 = "zy_data_center_03";
        
        public static final String BOSS_DATA_CENTER_4 = "zy_data_center_04";
        
        public static final String BOSS_DATA_CENTER_5 = "zy_data_center_05";
        
        public static final String BOSS_DATA_CENTER_6 = "zy_data_center_06";
        
        public static final String BOSS_DATA_CENTER_7 = "zy_data_center_07";
        
        public static final String BOSS_DATA_CENTER_8 = "zy_data_center_08";
        
        public static final String BOSS_DATA_CENTER_9 = "zy_data_center_09";
        
        public static final String BOSS_DATA_CENTER_10 = "zy_data_center_10";
        
        public static final String BOSS_DATA_CENTER_11 = "zy_data_center_11";
        
        public static final String BOSS_DATA_CENTER_12 = "zy_data_center_12";
        
        public static final String BOSS_DATA_CENTER_13 = "zy_data_center_13";
        
        public static final String BOSS_DATA_CENTER_14 = "zy_data_center_14";
        
        public static final String BOSS_DATA_CENTER_15 = "zy_data_center_15";
        
        public static final String BOSS_DATA_CENTER_16 = "zy_data_center_16";
        
        public static final String BOSS_DATA_CENTER_17 = "zy_data_center_17";
        
        public static final String BOSS_DATA_CENTER_18 = "zy_data_center_18";

        public static final String BOSS_DATA_CENTER_19 = "zy_data_center_19";
        
        // 市场端
        /**
         * 首页
         */
        public static final String ZYYSC_MARKET_INDEX = "zy_market_index";
        
        /**
         * 市场维护
         */
        public static final String MARKET_BASIS_MARKET_MAINTENANCE = "zy_basis_market_maintenance";
        
        /**
         * 市场端运费配置
         */
        public static final String MARKET_MALL_POSTAGE_CONFIG = "zy_basis_market_postage_config";
        
        /**
         * 市场端派单配置
         */
        public static final String MARKET_MALL_DISPATCH = "zy_basis_market_dispatch";

        /**
         * 市场端派单配置
         */
        public static final String MARKET_MALL_GTYPE = "zy_basis_market_gtype";
        
        /**
         * 检测信息
         */
        public static final String MARKET_MAINTENANCE_DETECTION_INFO = "zy_market_maintenance_detection_info";
        
        /**
         * 追溯信息
         */
        public static final String MARKET_MAINTENANCE_RETROACTIVE_INFO = "zy_market_maintenance_retroactive_info";
        
        /**
         * 市场端广告管理
         */
        public static final String MARKET_MALL_ADVER = "zy_market_mall_adver";
        
        /**
         * 骑手管理
         */
        public static final String MARKET_ORDER_RIDER_MANAGER = "zy_order_rider_manager";
        
        /**
         * 意见反馈
         */
        public static final String MARKET_CUSTOMER_FEEDBACK = "zy_market_customer_feedback";
        /**
         * 意见反馈
         */
        public static final String MARKET_DESKTOP_MANAGER = "zy_market_desktop_manager";
        
        /**
         * 市场商品管理
         */
        public static final String GOODS_ORDINARY_MANAGER = "zy_goods_ordinary_manager";
        
        /**
         * 分享商品管理
         */
        public static final String MARKET_GOODS_SHARE_MANAGER = "zy_market_goods_share_manager";
        
        /**
         * 特价商品管理
         */
        public static final String MARKET_GOODS_SPECIAL_MANAGER = "zy_market_goods_special_manager";
        
        /**
         * 预售商品管理
         */
        public static final String MARKET_GOODS_PRESALE_MANAGER = "zy_market_goods_presale_manager";
        
        /**
         * 扶贫商品管理
         */
        public static final String MARKET_GOODS_POVERTY_MANAGER = "zy_market_goods_poverty_manager";
        
        /**
         * 团购商品管理
         */
        public static final String MARKET_GOODS_COLLAGE_MANAGER = "zy_market_goods_collage_manager";
        
        /**
         * 砍价商品管理
         */
        public static final String MARKET_GOODS_CUT_MANAGER = "zy_market_goods_cut_manager";
        
        /**
         * 商品供应库
         */
        public static final String MARKET_GOODS_SUPPLY = "zy_market_goods_supply";
        
        /**
         * 菜谱管理
         */
        public static final String GOODS_COOKFD_MANAGER = "zy_goods_cookfd_manager";
        
        /**
         * 菜谱分类
         */
        public static final String GOODS_COOKFD_TYPE = "zy_goods_cookfd_type";
        
        /**
         * 市场端优惠券管理
         */
        public static final String MARKET_COUPON_MANAGER = "zy_market_coupon_manager";

        /**
         * 市场端礼品券管理
         */
        public static final String MARKET_GIFT_MANAGER = "zy_market_gift_manager";

        /**
         * 市场端卡券活动
         */
        public static final String MARKET_ACTIVITY_MANAGER = "zy_market_activity_manager";
        
        /**
         * 市场端卡券发放
         */
        public static final String MARKET_COUPON_GRANT = "zy_market_coupon_grant";
        
        /**
         * 市场端优惠券使用查询
         */
        public static final String MARKET_COUPON_INQUIRE = "zy_market_coupon_inquire";

        /**
         * 市场端礼品券使用查询
         */
        public static final String MARKET_GIFT_USAGE_QUERY = "zy_market_gift_usage_query";
        
        /**
         * 市场订单
         */
        public static final String ORDER_MARKET_OFFLINE = "zy_order_market_offline";
        /**
         * 市场订单
         */
        public static final String ORDER_MARKET_REFUND = "zy_order_market_refund";
        
        /**
         * 骑手订单
         */
        public static final String ORDER_RIDER_OFFLINE = "zy_order_rider_offline";
        
        /**
         * 团购订单
         */
        public static final String ORDER_COLLAGE_OFFLINE = "zy_order_collage_offline";
        
        /**
         * 商户管理
         */
        public static final String MARKET_VENDOR_MANAGER = "zy_market_vendor_manager";
        /**
         * 精选商户管理
         */
        public static final String MARKET_BOUTIQUE_VENDOR = "zy_market_boutique_vendor";
        
        /**
         * 商户结算
         */
        public static final String MARKET_VENDOR_SETTLEMENT = "zy_market_vendor_settlement";
        /**
         * 商户结算报表
         */
        public static final String MARKET_VENDOR_SETTLEMENT_REPORT = "zy_market_vendor_settlement_report";
        
        /**
         * 商户提现打款
         */
        public static final String MARKET_VENDOR_WITHDRAWAL = "zy_market_vendor_withdrawal";
        
        /**
         * 商户对账
         */
        public static final String MARKET_VENDOR_BILL = "zy_market_vendor_bill";
        
        /**
         * 商户钱包
         */
        public static final String MARKET_VENDOR_WALLET = "zy_market_vendor_wallet";
        
        /**
         * 撤销记录
         */
        public static final String MARKET_VENDOR_REVOKE = "zy_market_vendor_revoke";
        
        
        /**
         * 市场端数据中心
         */
        public static final String MARKET_DATA_CENTER = "zy_market_data_center";
        
        public static final String MARKET_DATA_CENTER_1 = "zy_market_data_center_1";
        
        public static final String MARKET_DATA_CENTER_2 = "zy_market_data_center_2";
        
        public static final String MARKET_DATA_CENTER_3 = "zy_market_data_center_3";
        
        public static final String MARKET_DATA_CENTER_4 = "zy_market_data_center_4";
        
        public static final String MARKET_DATA_CENTER_5 = "zy_market_data_center_5";
        
        public static final String MARKET_DATA_CENTER_6 = "zy_market_data_center_6";
        
        public static final String MARKET_DATA_CENTER_7 = "zy_market_data_center_7";
        
        public static final String MARKET_DATA_CENTER_8 = "zy_market_data_center_8";
        
        public static final String MARKET_DATA_CENTER_9 = "zy_market_data_center_9";
        
        public static final String MARKET_DATA_CENTER_10 = "zy_market_data_center_10";
        
        public static final String MARKET_DATA_CENTER_11 = "zy_market_data_center_11";
        
        public static final String MARKET_DATA_CENTER_12 = "zy_market_data_center_12";

        public static final String MARKET_DATA_CENTER_13 = "zy_market_data_center_13";
        
        /**
         * 用户管理
         */
        public static final String MARKET_USER_MANAGER = "zy_market_user_manager";
        
        /**
         * 角色管理
         */
        public static final String MARKET_ROLE_MANAGER = "zy_market_role_manager";
        
        /**
         * 消息推送
         */
        public static final String MARKET_INFORMATION = "zy_market_information";
        
        // 系统管理
        /**
         * 用户管理
         */
        public static final String USER_MANAGER = "zy_user_manager";
        
        /**
         * 角色管理
         */
        public static final String ROLE_MANAGER = "zy_role_manager";
        
        /**
         * 日志
         */
        public static final String LOG_INFO = "zy_log_info";

        /**
         * 版本屏蔽设置
         */
        public static final String SHIELD_VERSION_MANAGER = "zy_shield_version_manager";
        
        // 以下暂时不用
        
        /**
         * 线下订单
         */
        public static final String ORDER_OFFLINE = "zy_order_offline";
        
        /**
         * 会员商品管理
         */
        public static final String GOODS_MEMBER_MANAGER = "zy_goods_member_manager";
        
        /**
         * 分享商品管理
         */
        public static final String GOODS_SHARE_MANAGER = "zy_goods_share_manager";
        
        /**
         * 团购商品管理
         */
        public static final String GOODS_COLLAGE_MANAGER = "zy_goods_collage_manager";
        
        /**
         * 砍价商品管理
         */
        public static final String GOODS_CUT_MANAGER = "zy_goods_cut_manager";
        
        /**
         * 特价商品管理
         */
        public static final String GOODS_SPECIAL_MANAGER = "zy_goods_special_manager";
        
        /**
         * 预售商品管理
         */
        public static final String GOODS_PRESALE_MANAGER = "zy_goods_presale_manager";
        
        /**
         * 积分配置
         */
        public static final String MARKETING_POINT_DEPLOY = "zy_marketing_point_deploy";
        
        /**
         * 会员配置
         */
        public static final String MARKETING_MEMBER_DEPLOY = "zy_marketing_member_deploy";
        
        /**
         * 会员积分
         */
        public static final String MARKETING_MEMBER_POINT = "zy_marketing_member_point";
        
        /**
         * 抽奖活动配置
         */
        public static final String MARKETING_LOTTERY_DEPLOY = "zy_marketing_lottery_deploy";
        
        /**
         * 中奖清单
         */
        public static final String MARKETING_WINNING_LIST = "zy_marketing_winning_list";
        
        /**
         * 商城经营总览
         */
        public static final String DATA_MALL_REPORT = "zy_data_mall_report";
        
        /**
         * 市场经营报表
         */
        public static final String DATA_DEPT_REPORT = "zy_data_dept_report";
        
        /**
         * 收单流水查询
         */
        public static final String DATA_ACQUIRING = "zy_data_acquiring";
        
        /**
         * 市场经营总览
         */
        public static final String DATA_MARKET_MANAGEMENT_OVERVIEW = "zy_data_market_management_overview";
        
        /**
         * 市场经营报表
         */
        public static final String DATA_MARKET_OPERATION_REPORT = "zy_data_market_operation_report";
        
        /**
         * 积分商户管理
         */
        public static final String MARKET_POINT_MERCHANT_MANAGER = "zy_market_point_merchant_manager";
        
        /**
         * 商户流水订单
         */
        public static final String MARKET_MERCHANTING_ORDER = "zy_market_merchanting_order";
        
        /**
         * 首页
         */
        public static final String ZYYSC_COMPANY_INDEX = "zy_company_index";
        
        /**
         * 公司信息
         */
        public static final String ZYYSC_COMPANY_INFO = "zy_company_info";
        
        public static final String COMPANY_DATA_CENTER_1 = "zy_company_data_center_1";
        
        public static final String COMPANY_DATA_CENTER_2 = "zy_company_data_center_2";
        
        public static final String COMPANY_DATA_CENTER_3 = "zy_company_data_center_3";
        
        public static final String COMPANY_DATA_CENTER_4 = "zy_company_data_center_4";
        
        public static final String COMPANY_DATA_CENTER_5 = "zy_company_data_center_5";
        
        public static final String COMPANY_DATA_CENTER_6 = "zy_company_data_center_6";
        
        public static final String COMPANY_DATA_CENTER_7 = "zy_company_data_center_7";
        //	        /**
        //	         * 订单查询
        //	         */
        //	        public static final String REPORT_ORDER_INQUIRE = "zy_report_order_inquire";
        //	        /**
        //	         * 市场销售统计
        //	         */
        //	        public static final String REPORT_MARKETING_STATISTICS = "zy_report_marketing_statistics";
        //	        
        //	        /**
        //	         * 菜品大类销售统计
        //	         */
        //	        public static final String REPORT_DISHES_STATISTICS = "zy_report_dishes_statistics";
        //	        
        //	        /**
        //	         * 菜品单品销售统计
        //	         */
        //	        public static final String REPORT_DISH_SALES_STATISTICS = "zy_report_dish_sales_statistics";
        //	        
        //	        /**
        //	         * 会员商品销售统计
        //	         */
        //	        public static final String REPORT_MEMBER_GOODS_SALES_STATISTICS = "zy_report_member_goods_sales_statistics";
        
        /**
         * 市场信息
         */
        public static final String MARKET_MAINTENANCE_INFO = "zy_market_maintenance_info";
        
        /**
         * 广告位管理
         */
        public static final String MARKET_MAINTENANCE_ADVERT_MANAGER = "zy_market_maintenance_advert_manager";
        
        /**
         * 骑手管理
         */
        public static final String MARKET_MAINTENANCE_RIDER_MANAGER = "zy_market_maintenance_rider_manager";
        
    }

    public static class SysConfig
    {
        /**
         * 商品供应库配置（0-市场自定义，1-统一配置）
         */
        public static final String GOODS_SUPPLY_DEPLOY = "goods_supply_deploy";
        
        /**
         * 广告配置
         */
        public static final String ADVERTISE_MANAGER_DEPLOY = "advertise_manager_deploy";
        
        /**
         * 合作商户配置（0-市场自定义、1-统一配置）
         */
        public static final String VENDOR_MANAGER_DEPLOY = "vendor_manager_deploy";
        
        /**
         * 是否自动采购配置 （0-人工指派，1-自动指派）
         */
        public static final String GOODS_PURCHASE_DEPLOY = "goods_purchase_deploy";
        
        /**
         * 商户消息推送
         */
        public static final String TEMPLATE_VENODR = "template_venodr"; 
        
        /**
         * 骑手消息推送
         */
        public static final String TEMPLATE_COURIER = "template_courier"; 
        
        /**
         * 市场消息推送-配送
         */
        public static final String TEMPLATE_MARKET = "template_market"; 
        
        /**
         * 市场消息推送-自提
         */
        public static final String TEMPLATE_MARKET_PICK = "template_market_pick"; 

        /**
         * 秒杀开始提醒
         */
        public static final String TEMPLATE_SPECIAL_GOODS = "template_special_goods"; 

        /**
         * 优惠券开抢提醒
         */
        public static final String TEMPLATE_NEW_CARD = "template_new_card"; 

        /**
         * 优惠券到账通知
         */
        public static final String TEMPLATE_NEW_MEMBERCARD = "template_new_membercard"; 

        /**
         * 活动通知
         */
        public static final String TEMPLATE_ACTIVITY = "template_activity"; 
        
    }
    
    public static class ZxConfig
    {
        public static final BigDecimal COMMISSION_RATE = new BigDecimal("0.00215");
//        private static final String COMMISSION_RATE = "0.00215";
        public static final BigDecimal TJ_COMMISSION_RATE = new BigDecimal("0.003");
        
        public static final String TJ_MID = "898120100008284";
        public static final String TJ_TID = "YZBCDBU4";
        public static final String TJ_SUBAPPID = "wxe8e4269393cd1295";
//        public static final String TJ_SUBAPPID = "8a81c1be96cf23aa019768554d860698";
        
    }
    
    public static class FarmSaas
    {
        /** 心安食足 登陆url */
        public static final String appletLoginUrl = "/v2/saas/applet/login";
        /** 心安食足 刷新token url */
        public static final String appletRefreshTokenUrl = "/v2/saas/applet/refreshToken";
        /** 获取农贸会员卡余额 url */
        public static final String appletAccountBalanceUrl = "/v2/mobile/saas/applet/get/account/balance";
        /** 农贸聚合支付-储值卡消费 url */
        public static final String ecardAccountConsumUrl = "/v1/mobile/ecard/account/consum";
        /** 农贸对云商城专门退款接口 */
        public static final String saasRefund = "/v1/mobile/ecard/account/saas/refund";
        /** 心安食足 获取所有市场名称 url */
        public static final String listMarketNameUrl = "/v2/saas/applet/list/market";
        /** 心安食足 使用电子秤打印 url */
        public static final String printVendorOrderUrl = "/farm-pay/v1/hard/scale/print";
        /** 获取农贸会员卡余额 url */
        public static final String appletDayConsumptionAmtUrl = "/v2/mobile/saas/applet/get/account/dayConsumption";
    }

    public static class GoodsMainRecommend
    {
        public static final Integer pkey = -99;

        public static final String name = "热销推荐";
    }
}
