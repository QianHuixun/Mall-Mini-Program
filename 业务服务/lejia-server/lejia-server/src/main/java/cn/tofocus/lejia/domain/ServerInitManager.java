package cn.tofocus.lejia.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.api.v4.AdminApiV4;
import cn.tofocus.account.api.v4.UserInDeptApiV4;
import cn.tofocus.account.api.v4.UserInDomainApiV4;
import cn.tofocus.account.api.v4.UserInOrgApiV4;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.config.LejiaConfig;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerInitManager
{
    
    @Autowired
    private LejiaConfig config;

    @Autowired
    private SysUserDao sysUserDao;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private AdminApiV4 adminApi;
    
    @Autowired
    private UserInDeptApiV4 userInDeptApiV4;
    
    @Autowired
    private UserInOrgApiV4 userInOrgApiV4;
    
    @Autowired
    private UserInDomainApiV4 userInDomainApiV4;
    
    /**
     * 创建system账号 及公司和市场(机构和市场) 
     * system账号 在账号中心  数据都创建
     * 公司和市场 pkey均为1 只在账号中心创建
     * @return
     */
    public Boolean createSystem(String userId, String mobile, Integer ascription, String name, String pwd)
    {
        if(StringUtils.isBlank(userId))
            userId = "zySystem";
        if(StringUtils.isBlank(mobile))
            mobile = "00000000000";
        if(StringUtils.isBlank(pwd))
            pwd = System.getenv().getOrDefault("DEFAULT_SYSTEM_PASSWORD", "CHANGE_ME");
        List<SysUser> exec = sysUserDao.select().eq("mobile", mobile).exec();
        if (!exec.isEmpty()) return false;
//        SysUserInfo sysUser = new SysUserInfo();
//        sysUser.setNickname(userId);
//        sysUser.setUserid(userId);
//        sysUser.setRegistFromApp(App.WEB);
//        sysUser = adminApi.addUser(sysUser).fetchResult();
//        log.info("sysUser-pkey: {}", sysUser.getPkey());
        SysUserInfo userInfo = adminApi.addUserByMobile(userId, true, mobile).fetchResult();
        adminApi.resetPassword(userInfo.getPkey(), pwd);
        SysUser user = new SysUser();
        user.setPkey(userInfo.getPkey().intValue());
        user.setMobile(mobile);
        user.setRowVension(1);
        user.setNickname(userInfo.getNickname());
        user.setCompany(Constant.Operation + ascription);
        user.setFarmer(Constant.Operation + ascription);
        user.setAscription(ascription);
        user.setRoleKey(Constant.Role.COMPANY_HEAD);
        sysUserDao.add(user);
        
        userInDomainApiV4.addUserRoleInDomain(userInfo.getPkey(), Constant.Role.COMPANY_HEAD);
//        RoleInstanceDTO fetchUserResult =
//            adminApi.addAppRole2User(userInfo.getPkey(), Constant.Role.COMPANY_HEAD, -1, AccessScopeType.domain, "*", false)
//                .fetchResult();
//        log.info("addAppRole2User: " + fetchUserResult);
        
//        OrginazationInfo org = new OrginazationInfo();
//        org.setPkey(user.getCompany());
//        org.setEnable(true);
//        org.setDomainid(Constant.DomainId);
//        Orginazation fetchOrgResult = orginazationApi.saveOrginazation(OrgType.company, org).fetchResult();
//        log.info("saveOrginazation: " + fetchOrgResult.getOrgid());
        adminApi.saveOrginazation(user.getCompany(), "默认公司");
//        userInOrgApiV4.addUserRole(userInfo.getPkey(), Constant.Role.COMPANY_HEAD, user.getCompany());
        
//        DepartmentInfo dept = new DepartmentInfo();
//        dept.setPkey(user.getFarmer());
//        dept.setOrgid(user.getCompany());
//        dept.setEnable(true);
//        dept.setDomainid(Constant.DomainId);
//        Department fetchDeptResult = orginazationApi.saveDepartment(OrgType.market, dept).fetchResult();
//        log.info("saveDepartment: " + fetchDeptResult.getOrgid());
        adminApi.saveDepartment(user.getFarmer(), user.getCompany(), "积分商城");
        // 基础配置生成
        appConfigManager.initConfig(userInfo.getPkey(), userInfo.getNickname(), ascription, mobile);
        SysAscription sa = new SysAscription();
        sa.setPkey(ascription);
        sa.setAccount(userId);
        sa.setName(name);
        sa.setPhoto("https://small.xinanshizu.com/file/v3/image?file=1443.png&code=391530ADCCD02831A220F6D85D498691");
        ascriptionDao.add(sa);
        return true;
    }
    
    public Boolean updateSystem(Long key, String moblie, String userId, Integer ascription)
    {
//        SysUser user = new SysUser();
//        user.setPkey(key.intValue());
//        user.setMobile(moblie);
//        user.setRowVension(0);
//        user.setNickname(userId);
//        user.setCompany(Constant.Operation + ascription);
//        user.setFarmer(Constant.Operation + ascription);
//        user.setRoleKey(Constant.Role.COMPANY_HEAD);
//        sysUserDao.put(user);
//        
//        Result<SysUserInfo> bindUser2Domain = adminApi.bindUser2Domain(moblie);
//        System.out.println("bindUser2Domain: " + JsonUtil.toString(bindUser2Domain, true));
//        RoleInstanceDTO fetchUserResult =
//            adminApi.addAppRole2User(key, "zy_company_head", -1, AccessScopeType.domain, "*", false)
//                .fetchResult();
//        log.info("addAppRole2User: " + fetchUserResult);
//        
//        OrginazationInfo org = new OrginazationInfo();
//        org.setPkey(Constant.DomainId + Constant.Operation + ascription);
//        org.setEnable(true);
//        org.setDomainid(Constant.DomainId);
//        Orginazation fetchOrgResult = orginazationApi.saveOrginazation(OrgType.company, org).fetchResult();
//        log.info("saveOrginazation: " + fetchOrgResult.getOrgid());
//        
//        DepartmentInfo dept = new DepartmentInfo();
//        dept.setPkey(Constant.DomainId + Constant.Operation + ascription);
//        dept.setOrgid(Constant.DomainId + Constant.Operation + ascription);
//        dept.setEnable(true);
//        dept.setDomainid(Constant.DomainId);
//        Department fetchDeptResult = orginazationApi.saveDepartment(OrgType.market, dept).fetchResult();
//        log.info("saveDepartment: " + fetchDeptResult.getOrgid());
//        // 基础配置生成
//        appConfigManager.initConfig(key, user.getNickname(), ascription, moblie);
        return true;
    }
    
    public Boolean createLocalSystem(Integer pkey, String name, Integer ascription, String mobile)
    {
        // 基础配置生成
        appConfigManager.initConfig(pkey.longValue(), name, ascription, mobile);
        return true;
    }
    
//   
//    
//    // 初始化 运营菜单
//    public AppMenuList initBossMenus()
//    {
//        ArrayList<AppMenu> bossMenulist = new ArrayList<>();
//        
//        AppMenu bosstop1 = AppMenu.builder()
//            .pkey("zy_boss_01")
//            .code("zy_boss_index")
//            .name("首页")
//            .type(MenuType.menu)
//            .functions(Function.ZYYSC_BOSS_INDEX)
//            .build();
//        
//        AppMenu bosstop2 = AppMenu.builder()
//            .pkey("zy_boss_02")
//            .code("zy_boss_basisinfo_manager")
//            .name("基础设置")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_MALL_CONFIG,
//                Function.BOSS_MALL_POSTAGE_CONFIG,
//                Function.BOSS_MALL_GOODS_CLASSIFICATION,
//                Function.BOSS_MALL_GOODS_HOUSE,
//                Function.BOSS_GOODS_SUPPLY,
//                Function.BOSS_PROMOTE_MANAGER,
//                Function.BOSS_THIRD_PAYMENT))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_1")
//                .code("zy_basis_mall")
//                .name("商城配置")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_CONFIG)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_2")
//                .code("zy_basis_postage_config")
//                .name("运费配置")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_POSTAGE_CONFIG)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_3")
//                .code("zy_basis_goods_classification")
//                .name("商品分类")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_GOODS_CLASSIFICATION)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_4")
//                .code("zy_basis_goods_house")
//                .name("商品库")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_GOODS_HOUSE)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_5")
//                .code("zy_boss_goods_supply")
//                .name("商品供应库")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_GOODS_SUPPLY)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_6")
//                .code("zy_boss_promote_manager")
//                .name("推广管理")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_PROMOTE_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_02_7")
//                .code("zy_boss_third_payment")
//                .name("第三方支付渠道")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_THIRD_PAYMENT)
//                .build())
//            
//            .build();
//        
//        AppMenu bosstop3 = AppMenu.builder()
//            .pkey("zy_boss_03")
//            .code("zy_boss_mall_manager")
//            .name("商城管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_GOODS_POINT_MANAGER,
//                Function.BOSS_MALL_ADVER,
//                Function.BOSS_MALL_GIFT,
//                Function.BOSS_MALL_COUPON,
//                Function.BOSS_MALL_CUSTOMER_FEEDBACK,
//                Function.BOSS_MALL_CUSTOMER_FEEDBACK_2,
//                Function.BOSS_MALL_PROBLEM))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_03_1")
//                .code("zy_mall_goods_point")
//                .name("商品管理")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_GOODS_POINT_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_03_2")
//                .code("zy_mall_gift")
//                .name("礼券管理")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_GIFT)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_03_3")
//                .code("zy_mall_coupon")
//                .name("优惠券管理")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_COUPON)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_03_5")
//                .code("zy_mall_feedback")
//                .name("意见反馈")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_CUSTOMER_FEEDBACK)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_03_6")
//                .code("zy_mall_feedback_2")
//                .name("意见反馈(仅浏览)")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_CUSTOMER_FEEDBACK_2)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_03_7")
//                .code("zy_mall_problem")
//                .name("常见问题")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_PROBLEM)
//                .build())
//            .build();
//        
//        AppMenu bosstop4 = AppMenu.builder()
//            .pkey("zy_boss_04")
//            .code("zy_adver_manager")
//            .name("广告管理")
//            .type(MenuType.menu)
//            .functions(
//                Arrays.asList(Function.BOSS_MALL_ADVER, Function.BOSS_SPECIAL_AREA_ADVERT, Function.BOSS_INDEX_ADVERT))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_04_1")
//                .code("zy_boss_index_advert")
//                .name("弹窗广告")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_INDEX_ADVERT)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_04_2")
//                .code("zy_boss_special_area_advert")
//                .name("专区广告")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_SPECIAL_AREA_ADVERT)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_04_3")
//                .code("zy_mall_adver")
//                .name("积分商城广告")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MALL_ADVER)
//                .build())
//            .build();
//        
//        AppMenu bosstop5 = AppMenu.builder()
//            .pkey("zy_boss_05")
//            .code("zy_boss_member_manager")
//            .name("会员管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_MEMBER_CENTER,
//                Function.BOSS_MEMBER_CENTER_2,
//                Function.BOSS_MEMBER_POINT,
//                Function.BOSS_MEMBER_COMM,
//                Function.BOSS_TAG_MANAGE))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_05_1")
//                .code("zy_member_center")
//                .name("会员中心")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MEMBER_CENTER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_05_2")
//                .code("zy_member_center_2")
//                .name("会员中心(仅浏览)")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MEMBER_CENTER_2)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_05_3")
////                .code("zy_member_recharge_record")
////                .name("充值记录")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_MEMBER_RECHARGE_RECORD)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_05_4")
//                .code("zy_member_point")
//                .name("积分查询")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MEMBER_POINT)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_05_6")
//                .code("zy_member_comm")
//                .name("钱包查询")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MEMBER_COMM)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_05_5")
//                .code("zy_tag_manage")
//                .name("标签管理")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_TAG_MANAGE)
//                .build())
//            .build();
//        
////        AppMenu bosstop6 = AppMenu.builder()
////            .pkey("zy_boss_06")
////            .code("zy_boss_activity_manager")
////            .name("活动管理")
////            .type(MenuType.menu)
////            .functions(Arrays
////                .asList(Function.BOSS_ACTIVITY_PRIZE, Function.BOSS_ACTIVITY_INQUIRE, Function.BOSS_ACTIVITY_INQUIRE_2))
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_06_1")
////                .code("zy_activity_prize")
////                .name("奖品配置")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_ACTIVITY_PRIZE)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_06_2")
////                .code("zy_activity_inquire")
////                .name("中奖查询")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_ACTIVITY_INQUIRE)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_06_3")
////                .code("zy_activity_inquire_2")
////                .name("中奖查询(仅浏览)")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_ACTIVITY_INQUIRE_2)
////                .build())
////            .build();
//        
//        AppMenu bosstop7 = AppMenu.builder()
//            .pkey("zy_boss_07")
//            .code("zy_boss_coupon_manager")
//            .name("卡券管理")
//            .type(MenuType.menu)
//            .functions(
//                Arrays.asList(Function.BOSS_COUPON_MANAGER, Function.BOSS_COUPON_GRANT, Function.BOSS_COUPON_INQUIRE))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_07_1")
//                .code("zy_coupon_manager")
//                .name("卡券管理")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_COUPON_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_07_2")
//                .code("zy_coupon_grant")
//                .name("卡券发放")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_COUPON_GRANT)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_07_3")
//                .code("zy_coupon_inquire")
//                .name("优惠券发放记录")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_COUPON_INQUIRE)
//                .build())
//            .build();
//        
//        AppMenu bosstop8 = AppMenu.builder()
//            .pkey("zy_boss_08")
//            .code("zy_boss_order_manager")
//            .name("交易管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_ORDER_MALL, Function.BOSS_AFTER_SALE_REFUND_MANAGER))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_08_1")
//                .code("zy_order_mall")
//                .name("商城订单")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_ORDER_MALL)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_08_2")
////                .code("zy_order_refund")
////                .name("退款管理")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_AFTER_SALE_REFUND_MANAGER)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_08_3")
////                .code("zy_order_refund_2")
////                .name("退款管理(仅浏览)")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_AFTER_SALE_REFUND_MANAGER_2)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_08_4")
////                .code("zy_order_draw")
////                .name("提现管理")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_ORDER_DRAW)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_08_5")
////                .code("zy_order_draw_2")
////                .name("提现管理(仅浏览)")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_ORDER_DRAW_2)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_08_6")
//                .code("zy_bill_manager")
//                .name("对账中心")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_BILL_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_08_7")
//                .code("zy_bill_detail")
//                .name("账单明细")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_BILL_DETAIL)
//                .build())
//            .build();
//        
//        AppMenu bosstop9 = AppMenu.builder()
//            .pkey("zy_boss_09")
//            .code("zy_boss_boss_operation")
//            .name("市场运营")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_MARKET_COMPANY_MANAGER, Function.BOSS_MARKET_COMPANY_MANAGER_2))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_09_1")
//                .code("zy_boss_company")
//                .name("公司市场")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MARKET_COMPANY_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_09_2")
//                .code("zy_boss_company_2")
//                .name("公司市场(仅浏览)")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MARKET_COMPANY_MANAGER_2)
//                .build())
//            .build();
//        
//        AppMenu bosstop10 = AppMenu.builder()
//            .pkey("zy_boss_10")
//            .code("zy_boss_vendor")
//            .name("合作商户")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_VENDOR_MANAGER,
//                Function.BOSS_VENDOR_POINT_ORDER,
//                Function.BOSS_VENDOR_MANAGER_2,
//                Function.BOSS_MARKET_VENDOR_MANAGER,
//                Function.BOSS_MARKET_VENDOR_MANAGER_2,
////                Function.BOSS_VENDOR_SETTLEMENT,
////                Function.BOSS_VENDOR_SETTLEMENT_REPORT,
//                Function.BOSS_VENDOR_BILL
//                ))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_10_1")
//                .code("zy_vendor_manager")
//                .name("积分商城商户")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_VENDOR_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_10_2")
//                .code("zy_vendor_manager_2")
//                .name("积分商城商户(仅浏览)")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_VENDOR_MANAGER_2)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_10_3")
//                .code("zy_vendor_point_order")
//                .name("积分订单")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_VENDOR_POINT_ORDER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_10_4")
//                .code("zy_boss_market_vendor_manager")
//                .name("市场商城商户")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MARKET_VENDOR_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_10_5")
//                .code("zy_market_vendor_manager_2")
//                .name("市场商城商户(仅浏览)")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_MARKET_VENDOR_MANAGER_2)
//                .build())
//            
//            // 2021-12-28 新增菜单 zdw
////          .addSub(AppMenu.builder()
////              .pkey("zy_boss_10_6")
////              .code("zy_boss_vendor_settlement")
////              .name("商户结算")
////              .type(MenuType.menu)
////              .functions(Function.BOSS_VENDOR_SETTLEMENT)
////              .build())
////          .addSub(AppMenu.builder()
////              .pkey("zy_boss_10_7")
////              .code("zy_boss_vendor_settlement_report")
////              .name("结算报表")
////              .type(MenuType.menu)
////              .functions(Function.BOSS_VENDOR_SETTLEMENT_REPORT)
////              .build())
//          .addSub(AppMenu.builder()
//              .pkey("zy_boss_10_8")
//              .code("zy_boss_vendor_bill")
//              .name("商户对账")
//              .type(MenuType.menu)
//              .functions(Function.BOSS_VENDOR_BILL)
//              .build())
//            
//            .build();
//        
////        AppMenu bosstop11 = AppMenu.builder()
////            .pkey("zy_boss_11")
////            .code("zy_boss_kry")
////            .name("客如云")
////            .type(MenuType.menu)
////            .functions(Arrays.asList(Function.BOSS_KRU_MERCHANT, Function.BOSS_KRU_MERCHANT_2, Function.BOSS_ORDER_KRY))
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_11_1")
////                .code("zy_kry_vendor")
////                .name("客如云商户")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_KRU_MERCHANT)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_11_2")
////                .code("zy_kry_vendor_2")
////                .name("客如云商户(仅浏览)")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_KRU_MERCHANT_2)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_11_3")
////                .code("zy_kry_order")
////                .name("客如云订单")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_ORDER_KRY)
////                .build())
////            .build();
//        
//        AppMenu bosstop12 = AppMenu.builder()
//            .pkey("zy_boss_12")
//            .code("zy_boss_data_center")
//            .name("数据中心")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.BOSS_DATA_CENTER_1,
//                Function.BOSS_DATA_CENTER_2,
//                Function.BOSS_DATA_CENTER_3,
//                Function.BOSS_DATA_CENTER_4,
//                Function.BOSS_DATA_CENTER_5,
//                Function.BOSS_DATA_CENTER_6,
//                Function.BOSS_DATA_CENTER_7,
//                Function.BOSS_DATA_CENTER_8,
//                Function.BOSS_DATA_CENTER_9,
//                Function.BOSS_DATA_CENTER_10,
//                Function.BOSS_DATA_CENTER_12,
//                Function.BOSS_DATA_CENTER_12,
//                Function.BOSS_DATA_CENTER_13,
//                Function.BOSS_DATA_CENTER_14,
//                Function.BOSS_DATA_CENTER_15,
//                Function.BOSS_DATA_CENTER_16,
//                Function.BOSS_DATA_CENTER_17,
//                Function.BOSS_DATA_CENTER_19))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_1")
//                .code("zy_data_01")
//                .name("专区营业报表")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_1)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_2")
//                .code("zy_data_02")
//                .name("商品销售统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_2)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_3")
//                .code("zy_data_03")
//                .name("商品销售分析")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_3)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_12_4")
////                .code("zy_data_04")
////                .name("奖品统计")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_DATA_CENTER_4)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_5")
//                .code("zy_data_05")
//                .name("时间段销售额")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_5)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_12_6")
////                .code("zy_data_06")
////                .name("付费会员办理")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_DATA_CENTER_6)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_12_7")
////                .code("zy_data_07")
////                .name("付费会员明细")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_DATA_CENTER_7)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_8")
//                .code("zy_data_08")
//                .name("积分兑换统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_8)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_12_9")
////                .code("zy_data_09")
////                .name("付费会员消费")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_DATA_CENTER_9)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_10")
//                .code("zy_data_10")
//                .name("用户访问报表")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_10)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_11")
//                .code("zy_data_11")
//                .name("新增用户报表")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_11)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_12")
//                .code("zy_data_12")
//                .name("卡券使用统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_12)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_13")
//                .code("zy_data_13")
//                .name("品类销售统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_13)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_14")
//                .code("zy_data_14")
//                .name("商户积分统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_14)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_15")
//                .code("zy_data_15")
//                .name("公司销售统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_15)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_16")
//                .code("zy_data_16")
//                .name("市场销售统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_16)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_17")
//                .code("zy_data_17")
//                .name("运费报表")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_17)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_12_18")
////                .code("zy_data_18")
////                .name("数据大屏")
////                .type(MenuType.menu)
////                .functions(Function.BOSS_DATA_CENTER_18)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_12_19")
//                .code("zy_data_19")
//                .name("经营数据统计")
//                .type(MenuType.menu)
//                .functions(Function.BOSS_DATA_CENTER_19)
//                .build())
//            .type(MenuType.menu)
//            .functions(Function.BOSS_DATA_CENTER)
//            .build();
//        
//        AppMenu bosstop13 = AppMenu.builder()
//            .pkey("zy_boss_13")
//            .code("zy_boss_sys_manager")
//            .name("系统管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.USER_MANAGER, Function.ROLE_MANAGER, Function.LOG_INFO, Function.SHIELD_VERSION_MANAGER))
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_13_1")
//                .code("zy_sys_user")
//                .name("用户")
//                .type(MenuType.menu)
//                .functions(Function.USER_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_13_2")
//                .code("zy_sys_role")
//                .name("角色")
//                .type(MenuType.menu)
//                .functions(Function.ROLE_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_13_3")
//                .code("zy_sys_log")
//                .name("日志")
//                .type(MenuType.menu)
//                .functions(Function.LOG_INFO)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_boss_13_4")
////                .code("zy_sys_information")
////                .name("消息推送")
////                .type(MenuType.menu)
////                .functions(Function.LOG_INFORMATION)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_boss_13_5")
//                .code("zy_sys_shield_version")
//                .name("版本屏蔽设置")
//                .type(MenuType.menu)
//                .functions(Function.SHIELD_VERSION_MANAGER)
//                .build())
//            .build();
//        
//        bossMenulist.add(bosstop1);
//        bossMenulist.add(bosstop2);
//        bossMenulist.add(bosstop3);
//        bossMenulist.add(bosstop4);
//        bossMenulist.add(bosstop5);
//        bossMenulist.add(bosstop7);
//        bossMenulist.add(bosstop8);
//        bossMenulist.add(bosstop9);
//        bossMenulist.add(bosstop10);
//        bossMenulist.add(bosstop12);
//        bossMenulist.add(bosstop13);
//        AppMenuList result = new AppMenuList();
//        result.setAppid(App.WEB);
//        result.setMenus(bossMenulist);
//        return result;
//    }
//    
//    // 初始化 公司和市场菜单
//    public AppMenuList initMarketMenus()
//    {
//        ArrayList<AppMenu> marketMenulist = new ArrayList<>();
//        
//        AppMenu companytop1 = AppMenu.builder()
//            .pkey("zy_company_01")
//            .code("zy_company_index")
//            .name("首页")
//            .type(MenuType.menu)
//            .functions(Function.ZYYSC_COMPANY_INDEX)
//            .build();
//        
//        AppMenu companytop2 = AppMenu.builder()
//            .pkey("zy_company_02")
//            .code("zy_company_manager")
//            .name("公司管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.ZYYSC_COMPANY_INFO))
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_02_1")
//                .code("zy_company_info")
//                .name("公司信息")
//                .type(MenuType.menu)
//                .functions(Function.ZYYSC_COMPANY_INFO)
//                .build())
//            .build();
//        
//        AppMenu companytop3 = AppMenu.builder()
//            .pkey("zy_company_03")
//            .code("zy_report_inquire")
//            .name("报表查询")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.COMPANY_DATA_CENTER_1,
//                Function.COMPANY_DATA_CENTER_2,
//                Function.COMPANY_DATA_CENTER_3,
//                Function.COMPANY_DATA_CENTER_4,
//                Function.COMPANY_DATA_CENTER_5,
//                Function.COMPANY_DATA_CENTER_6,
//                Function.COMPANY_DATA_CENTER_7))
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_1")
//                .code("zy_company_data_01")
//                .name("专区营业报表")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_1)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_2")
//                .code("zy_company_data_02")
//                .name("商品销售统计")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_2)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_3")
//                .code("zy_company_data_03")
//                .name("商品销售分析")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_3)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_4")
//                .code("zy_company_data_04")
//                .name("时间段销售额")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_4)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_5")
//                .code("zy_company_data_05")
//                .name("卡券使用统计")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_5)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_6")
//                .code("zy_company_data_06")
//                .name("品类销售统计")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_6)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_company_03_7")
//                .code("zy_company_data_07")
//                .name("市场销售统计")
//                .type(MenuType.menu)
//                .functions(Function.COMPANY_DATA_CENTER_7)
//                .build())
//            .build();
//        
//        AppMenu markettop1 = AppMenu.builder()
//            .pkey("zy_market_01")
//            .code("zy_market_index")
//            .name("首页")
//            .type(MenuType.menu)
//            .functions(Function.ZYYSC_MARKET_INDEX)
//            .build();
//        
//        AppMenu markettop2 = AppMenu.builder()
//            .pkey("zy_market_02")
//            .code("zy_market_basisinfo_manager")
//            .name("基础设置")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.MARKET_BASIS_MARKET_MAINTENANCE,
//                Function.MARKET_MALL_POSTAGE_CONFIG,
//                Function.MARKET_MALL_DISPATCH,
//                Function.MARKET_MALL_GTYPE))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_02_1")
//                .code("zy_basis_market_maintenance")
//                .name("市场维护")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_BASIS_MARKET_MAINTENANCE)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_02_2")
//                .code("zy_basis_market_postage_config")
//                .name("运费配置")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_MALL_POSTAGE_CONFIG)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_02_3")
//                .code("zy_basis_market_dispatch")
//                .name("派单配置")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_MALL_DISPATCH)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_02_4")
//                .code("zy_basis_market_gtype")
//                .name("商品分类")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_MALL_GTYPE)
//                .build())
//            
//            .build();
//        
//        AppMenu markettop3 = AppMenu.builder()
//            .pkey("zy_market_03")
//            .code("zy_market_publicity")
//            .name("食安公示")
//            .type(MenuType.menu)
//            .functions(
//                Arrays.asList(Function.MARKET_MAINTENANCE_DETECTION_INFO, Function.MARKET_MAINTENANCE_RETROACTIVE_INFO))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_03_1")
//                .code("zy_market_maintenance_detection_info")
//                .name("检测信息")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_MAINTENANCE_DETECTION_INFO)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_03_2")
//                .code("zy_market_maintenance_retroactive_info")
//                .name("追溯信息")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_MAINTENANCE_RETROACTIVE_INFO)
//                .build())
//            .build();
//        
//        AppMenu markettop4 = AppMenu.builder()
//            .pkey("zy_market_04")
//            .code("zy_market_manager")
//            .name("市场管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.MARKET_MALL_ADVER,
//                Function.MARKET_ORDER_RIDER_MANAGER,
//                Function.MARKET_CUSTOMER_FEEDBACK,
//                Function.MARKET_DESKTOP_MANAGER))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_04_1")
//                .code("zy_market_adver")
//                .name("广告管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_MALL_ADVER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_04_2")
//                .code("zy_order_rider_manager")
//                .name("骑手管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_ORDER_RIDER_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_04_3")
//                .code("zy_market_customer_feedback")
//                .name("意见反馈")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_CUSTOMER_FEEDBACK)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_04_4")
//                .code("zy_market_desktop_manager")
//                .name("桌位管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DESKTOP_MANAGER)
//                .build())
//            .build();
//        
//        AppMenu markettop5 = AppMenu.builder()
//            .pkey("zy_market_05")
//            .code("zy_market_goods_manager")
//            .name("商品管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.GOODS_ORDINARY_MANAGER,
//                Function.MARKET_GOODS_SHARE_MANAGER,
//                Function.MARKET_GOODS_SPECIAL_MANAGER,
//                Function.MARKET_GOODS_PRESALE_MANAGER,
//                Function.MARKET_GOODS_POVERTY_MANAGER,
//                Function.MARKET_GOODS_COLLAGE_MANAGER,
//                Function.MARKET_GOODS_CUT_MANAGER,
//                Function.MARKET_GOODS_SUPPLY))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_1")
//                .code("zy_market_goods_ordinary_manager")
//                .name("市场商品管理")
//                .type(MenuType.menu)
//                .functions(Function.GOODS_ORDINARY_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_2")
//                .code("zy_market_goods_share_manager")
//                .name("分享商品管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_SHARE_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_3")
//                .code("zy_market_goods_special_manager")
//                .name("特价商品管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_SPECIAL_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_4")
//                .code("zy_market_goods_presale_manager")
//                .name("预售商品管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_PRESALE_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_5")
//                .code("zy_market_goods_poverty_manager")
//                .name("扶贫商品管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_POVERTY_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_6")
//                .code("zy_market_goods_collage_manager")
//                .name("团购商品管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_COLLAGE_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_7")
//                .code("zy_market_goods_cut_manager")
//                .name("砍价商品管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_CUT_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_05_8")
//                .code("zy_market_goods_supply")
//                .name("商品供应库")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GOODS_SUPPLY)
//                .build())
//            .build();
//        
//        AppMenu markettop6 = AppMenu.builder()
//            .pkey("zy_market_06")
//            .code("zy_market_cookfd_manager")
//            .name("菜谱管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.GOODS_COOKFD_MANAGER, Function.GOODS_COOKFD_TYPE))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_06_1")
//                .code("zy_cookfd_manager")
//                .name("菜谱管理")
//                .type(MenuType.menu)
//                .functions(Function.GOODS_COOKFD_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_06_2")
//                .code("zy_cookfd_type")
//                .name("菜谱分类")
//                .type(MenuType.menu)
//                .functions(Function.GOODS_COOKFD_TYPE)
//                .build())
//            .build();
//        
//        AppMenu markettop7 = AppMenu.builder()
//            .pkey("zy_market_07")
//            .code("zy_market_coupon_manager")
//            .name("卡券管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.MARKET_COUPON_MANAGER,
//                Function.MARKET_GIFT_MANAGER,
//                Function.MARKET_ACTIVITY_MANAGER,
//                Function.MARKET_COUPON_GRANT,
//                Function.MARKET_COUPON_INQUIRE,
//                Function.MARKET_GIFT_USAGE_QUERY))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_07_1")
//                .code("zy_market_coupon_manager")
//                .name("优惠券管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_COUPON_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_07_4")
//                .code("zy_market_gift_manager")
//                .name("礼品券管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GIFT_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_07_5")
//                .code("zy_market_activity_manager")
//                .name("卡券活动")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_ACTIVITY_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_07_2")
//                .code("zy_market_coupon_grant")
//                .name("卡券发放")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_COUPON_GRANT)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_07_3")
//                .code("zy_market_coupon_inquire")
//                .name("优惠券发放记录")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_COUPON_INQUIRE)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_07_6")
//                .code("zy_market_gift_usage_query")
//                .name("礼品券发放记录")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_GIFT_USAGE_QUERY)
//                .build())
//            .build();
//        
//        AppMenu markettop8 = AppMenu.builder()
//            .pkey("zy_market_08")
//            .code("zy_market_order_manager")
//            .name("交易管理")
//            .type(MenuType.menu)
//            .functions(Arrays
//                .asList(Function.ORDER_MARKET_OFFLINE, Function.ORDER_MARKET_REFUND, Function.ORDER_RIDER_OFFLINE, Function.ORDER_COLLAGE_OFFLINE))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_08_1")
//                .code("zy_order_market_offline")
//                .name("市场订单")
//                .type(MenuType.menu)
//                .functions(Function.ORDER_MARKET_OFFLINE)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_08_4")
//                .code("zy_order_market_refund")
//                .name("市场退款订单")
//                .type(MenuType.menu)
//                .functions(Function.ORDER_MARKET_REFUND)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_08_2")
//                .code("zy_order_rider_offline")
//                .name("骑手订单")
//                .type(MenuType.menu)
//                .functions(Function.ORDER_RIDER_OFFLINE)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_08_3")
////                .code("zy_order_collage_offline")
////                .name("团购订单")
////                .type(MenuType.menu)
////                .functions(Function.ORDER_COLLAGE_OFFLINE)
////                .build())
//            .build();
//        
//        AppMenu markettop9 = AppMenu.builder()
//            .pkey("zy_market_09")
//            .code("zy_market_vendor")
//            .name("合作商户")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.MARKET_VENDOR_MANAGER,
//                Function.MARKET_BOUTIQUE_VENDOR,
//                Function.MARKET_VENDOR_WITHDRAWAL,
//                Function.MARKET_VENDOR_BILL,
//                Function.MARKET_VENDOR_WALLET
////                Function.MARKET_VENDOR_SETTLEMENT,
////                Function.MARKET_VENDOR_SETTLEMENT_REPORT,
////                Function.MARKET_VENDOR_REVOKE
//                ))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_09_1")
//                .code("zy_market_vendor_manager")
//                .name("商户管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_VENDOR_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_09_2")
//                .code("zy_market_boutique_vendor")
//                .name("精选商户管理")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_BOUTIQUE_VENDOR)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_09_3")
////                .code("zy_market_vendor_settlement")
////                .name("商户结算")
////                .type(MenuType.menu)
////                .functions(Function.MARKET_VENDOR_SETTLEMENT)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_09_4")
////                .code("zy_market_vendor_settlement_report")
////                .name("结算报表")
////                .type(MenuType.menu)
////                .functions(Function.MARKET_VENDOR_SETTLEMENT_REPORT)
////                .build())
//             .addSub(AppMenu.builder()
//                  .pkey("zy_market_09_4")
//                  .code("zy_market_vendor_withdrawal")
//                  .name("提现打款")
//                  .type(MenuType.menu)
//                  .functions(Function.MARKET_VENDOR_WITHDRAWAL)
//                  .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_09_5")
//                .code("zy_market_vendor_bill")
//                .name("商户对账")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_VENDOR_BILL)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_09_6")
//                .code("zy_market_vendor_wallet")
//                .name("商户钱包")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_VENDOR_WALLET)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_09_6")
////                .code("zy_market_vendor_revoke")
////                .name("撤销记录")
////                .type(MenuType.menu)
////                .functions(Function.MARKET_VENDOR_REVOKE)
////                .build())
//            
//            .build();
//        
//        AppMenu markettop10 = AppMenu.builder()
//            .pkey("zy_market_10")
//            .code("zy_market_data_center")
//            .name("数据中心")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.MARKET_DATA_CENTER,
//                Function.MARKET_DATA_CENTER_1,
//                Function.MARKET_DATA_CENTER_2,
//                Function.MARKET_DATA_CENTER_3,
//                Function.MARKET_DATA_CENTER_4,
//                Function.MARKET_DATA_CENTER_5,
//                Function.MARKET_DATA_CENTER_6,
//                Function.MARKET_DATA_CENTER_7,
//                Function.MARKET_DATA_CENTER_8,
//                Function.MARKET_DATA_CENTER_9,
//                Function.MARKET_DATA_CENTER_10,
//                Function.MARKET_DATA_CENTER_11,
//                Function.MARKET_DATA_CENTER_12,
//                Function.MARKET_DATA_CENTER_13))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_13")
//                .code("zy_market_data_13")
//                .name("经营数据统计")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_13)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_1")
//                .code("zy_market_data_01")
//                .name("专区营业报表")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_1)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_2")
//                .code("zy_market_data_02")
//                .name("商品销售统计")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_2)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_3")
//                .code("zy_market_data_03")
//                .name("商品销售分析")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_3)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_4")
//                .code("zy_market_data_04")
//                .name("异常货品分析")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_4)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_5")
//                .code("zy_market_data_05")
//                .name("时间段销售额")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_5)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_6")
//                .code("zy_market_data_06")
//                .name("卡券使用统计")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_6)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_7")
//                .code("zy_market_data_07")
//                .name("品类销售统计")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_7)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_8")
//                .code("zy_market_data_08")
//                .name("市场销售统计")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_8)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_10_9")
////                .code("zy_market_data_09")
////                .name("佣金达人")
////                .type(MenuType.menu)
////                .functions(Function.MARKET_DATA_CENTER_9)
////                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_10_10")
////                .code("zy_market_data_10")
////                .name("佣金收入明细")
////                .type(MenuType.menu)
////                .functions(Function.MARKET_DATA_CENTER_10)
////                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_11")
//                .code("zy_market_data_11")
//                .name("配送员绩效表")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_11)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_10_12")
//                .code("zy_market_data_12")
//                .name("商户采购报表")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_DATA_CENTER_12)
//                .build())
//            .build();
//        
//        AppMenu markettop11 = AppMenu.builder()
//            .pkey("zy_market_11")
//            .code("zy_market_sys_manager")
//            .name("系统管理")
//            .type(MenuType.menu)
//            .functions(Arrays.asList(Function.MARKET_USER_MANAGER, Function.MARKET_ROLE_MANAGER, Function.MARKET_INFORMATION))
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_11_1")
//                .code("zy_sys_user")
//                .name("用户")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_USER_MANAGER)
//                .build())
//            .addSub(AppMenu.builder()
//                .pkey("zy_market_11_2")
//                .code("zy_sys_role")
//                .name("角色")
//                .type(MenuType.menu)
//                .functions(Function.MARKET_ROLE_MANAGER)
//                .build())
////            .addSub(AppMenu.builder()
////                .pkey("zy_market_11_3")
////                .code("zy_sys_information")
////                .name("消息推送")
////                .type(MenuType.menu)
////                .functions(Function.MARKET_INFORMATION)
////                .build())
//            
//            .build();
//        
//        marketMenulist.add(companytop1);
//        marketMenulist.add(companytop2);
//        marketMenulist.add(companytop3);
//        marketMenulist.add(markettop1);
//        marketMenulist.add(markettop2);
//        marketMenulist.add(markettop3);
//        marketMenulist.add(markettop4);
//        marketMenulist.add(markettop5);
//        marketMenulist.add(markettop6);
//        marketMenulist.add(markettop7);
//        marketMenulist.add(markettop8);
//        marketMenulist.add(markettop9);
//        marketMenulist.add(markettop10);
//        marketMenulist.add(markettop11);
//        AppMenuList result = new AppMenuList();
//        result.setAppid(App.WEB);
//        result.setMenus(marketMenulist);
//        return result;
//    }
//    
//    // 初始化角色 和 权限
//    public void initDomainAuthority()
//    {
//        
//        Map<String, List<AppMenu>> menus = new HashMap<>();
////        AppMenuList bossMenu = initBossMenus();
////        menus.put(bossMenu.getAppid(), bossMenu.getMenus());
////        menus.get(bossMenu.getAppid()).addAll(initMarketMenus().getMenus());
//        
//        // @formatter:on
////        DomainAuthority authority = new DomainAuthority();
////        authority.setDomainid(Constant.DomainId);
////        authority.setFunctionRoot(initFunctionRoot());
////        authority.setRoles(getRoles());
////        authority.setMenus(menus);
//        
//        //	        authority.setFunctions(functions);
//        
//        Result<?> r = domainAdminApi.iniAllRoleFunMenu(authority);
//        r.fetchResult();
//        config.put("inited", "true");
//    }
//    
//    public List<SimpleRole> getRoles()
//    {
//        List<SimpleRole> roles = new ArrayList<>();
//        roles.add(SimpleRole.builder()
//            .pkey(Role.COMPANY_HEAD)
//            .name("超级管理员")
//            // 首页
//            .addFunc(Function.ZYYSC_BOSS_INDEX)
//            // 基础设置
//            .addFunc(Function.BOSS_MALL_CONFIG)
//            .addFunc(Function.BOSS_MALL_POSTAGE_CONFIG)
//            .addFunc(Function.BOSS_MALL_GOODS_CLASSIFICATION)
//            .addFunc(Function.BOSS_MALL_GOODS_HOUSE)
//            .addFunc(Function.BOSS_GOODS_SUPPLY)
//            .addFunc(Function.BOSS_PROMOTE_MANAGER)
//            .addFunc(Function.BOSS_THIRD_PAYMENT)
//            // 商城管理
//            .addFunc(Function.BOSS_GOODS_POINT_MANAGER)
//            .addFunc(Function.BOSS_MALL_GIFT)
//            .addFunc(Function.BOSS_MALL_COUPON)
//            .addFunc(Function.BOSS_MALL_CUSTOMER_FEEDBACK)
//            .addFunc(Function.BOSS_MALL_CUSTOMER_FEEDBACK_2)
//            .addFunc(Function.BOSS_MALL_PROBLEM)
//            // 广告管理
//            .addFunc(Function.BOSS_MALL_ADVER)
//            .addFunc(Function.BOSS_INDEX_ADVERT)
//            .addFunc(Function.BOSS_SPECIAL_AREA_ADVERT)
//            // 会员管理
//            .addFunc(Function.BOSS_MEMBER_CENTER)
//            .addFunc(Function.BOSS_MEMBER_CENTER_2)
//            .addFunc(Function.BOSS_MEMBER_POINT)
//            .addFunc(Function.BOSS_MEMBER_COMM)
//            .addFunc(Function.BOSS_TAG_MANAGE)
//            // 活动管理
////            .addFunc(Function.BOSS_ACTIVITY_PRIZE)
////            .addFunc(Function.BOSS_ACTIVITY_INQUIRE)
////            .addFunc(Function.BOSS_ACTIVITY_INQUIRE_2)
//            // 卡券管理
//            .addFunc(Function.BOSS_COUPON_MANAGER)
//            .addFunc(Function.BOSS_COUPON_GRANT)
//            .addFunc(Function.BOSS_COUPON_INQUIRE)
//            // 交易管理
//            .addFunc(Function.BOSS_ORDER_MALL)
//            .addFunc(Function.BOSS_AFTER_SALE_REFUND_MANAGER)
//            .addFunc(Function.BOSS_AFTER_SALE_REFUND_MANAGER_2)
//            .addFunc(Function.BOSS_ORDER_DRAW)
//            .addFunc(Function.BOSS_ORDER_DRAW_2)
//            .addFunc(Function.BOSS_BILL_MANAGER)
//            .addFunc(Function.BOSS_BILL_DETAIL)
//            // 市场运营
//            .addFunc(Function.BOSS_MARKET_COMPANY_MANAGER)
//            .addFunc(Function.BOSS_MARKET_COMPANY_MANAGER_2)
//            // 合作商户
//            .addFunc(Function.BOSS_VENDOR_MANAGER)
//            .addFunc(Function.BOSS_VENDOR_MANAGER_2)
//            .addFunc(Function.BOSS_VENDOR_POINT_ORDER)
//            .addFunc(Function.BOSS_MARKET_VENDOR_MANAGER)
////            .addFunc(Function.MARKET_BOUTIQUE_VENDOR)
//            .addFunc(Function.BOSS_MARKET_VENDOR_MANAGER_2)
////            .addFunc(Function.BOSS_VENDOR_SETTLEMENT)
////            .addFunc(Function.BOSS_VENDOR_SETTLEMENT_REPORT)
//            .addFunc(Function.BOSS_VENDOR_BILL)
//            // 客如云
////            .addFunc(Function.BOSS_KRU_MERCHANT)
////            .addFunc(Function.BOSS_ORDER_KRY)
////            .addFunc(Function.BOSS_KRU_MERCHANT_2)
//            // 数据中心
//            .addFunc(Function.BOSS_DATA_CENTER)
//            .addFunc(Function.BOSS_DATA_CENTER_1)
//            .addFunc(Function.BOSS_DATA_CENTER_2)
//            .addFunc(Function.BOSS_DATA_CENTER_3)
//            .addFunc(Function.BOSS_DATA_CENTER_4)
//            .addFunc(Function.BOSS_DATA_CENTER_5)
//            .addFunc(Function.BOSS_DATA_CENTER_6)
//            .addFunc(Function.BOSS_DATA_CENTER_7)
//            .addFunc(Function.BOSS_DATA_CENTER_8)
//            .addFunc(Function.BOSS_DATA_CENTER_9)
//            .addFunc(Function.BOSS_DATA_CENTER_10)
//            .addFunc(Function.BOSS_DATA_CENTER_11)
//            .addFunc(Function.BOSS_DATA_CENTER_12)
//            .addFunc(Function.BOSS_DATA_CENTER_13)
//            .addFunc(Function.BOSS_DATA_CENTER_14)
//            .addFunc(Function.BOSS_DATA_CENTER_15)
//            .addFunc(Function.BOSS_DATA_CENTER_16)
//            .addFunc(Function.BOSS_DATA_CENTER_17)
//            .addFunc(Function.BOSS_DATA_CENTER_19)
//            // 系统管理
//            .addFunc(Function.USER_MANAGER)
//            .addFunc(Function.ROLE_MANAGER)
//            .addFunc(Function.LOG_INFO)
//            .addFunc(Function.SHIELD_VERSION_MANAGER)
//            
//            .addFunc(SysFunction.MANAGER_ROLE)
//            .addFunc(SysFunction.ADD_USER)
//            .addFunc(SysFunction.MOD_USER)
//            .addFunc(SysFunction.DEL_USER)
//            .addFunc(SysFunction.MANAGER_USER_ROLE)
//            .addFunc(SysFunction.ADD_ORG)
//            .addFunc(SysFunction.MOD_ORG)
//            .addFunc(SysFunction.DEL_ORG)
//            .addFunc(SysFunction.ADD_DEPT)
//            .addFunc(SysFunction.MOD_DEPT)
//            .addFunc(SysFunction.DEL_DEPT)
//            
//            .addFunc(SysFunction.QUERY_INFO)
//            .addFunc(SysFunction.WRITE_INFO)
//            .build());
//        
//        roles.add(SimpleRole.builder()
//            .pkey(Role.MARKET_HEAD)
//            .name("公司负责人")
//            .addFunc(Function.ZYYSC_COMPANY_INDEX)
//            // 公司管理
//            .addFunc(Function.ZYYSC_COMPANY_INFO)
//            // 报表查询
//            .addFunc(Function.COMPANY_DATA_CENTER_1)
//            .addFunc(Function.COMPANY_DATA_CENTER_2)
//            .addFunc(Function.COMPANY_DATA_CENTER_3)
//            .addFunc(Function.COMPANY_DATA_CENTER_4)
//            .addFunc(Function.COMPANY_DATA_CENTER_5)
//            .addFunc(Function.COMPANY_DATA_CENTER_6)
//            .addFunc(Function.COMPANY_DATA_CENTER_7)
//            .addFunc(SysFunction.QUERY_INFO)
//            .addFunc(SysFunction.WRITE_INFO)
//            .build());
//        
//        roles.add(SimpleRole.builder()
//            .pkey(Role.MARKET_MANAGER)
//            .name("市场负责人")
//            .addFunc(Function.ZYYSC_MARKET_INDEX)
//            // 基础设置
//            .addFunc(Function.MARKET_BASIS_MARKET_MAINTENANCE)
//            .addFunc(Function.MARKET_MALL_POSTAGE_CONFIG)
//            .addFunc(Function.MARKET_MALL_DISPATCH)
//            .addFunc(Function.MARKET_MALL_GTYPE)
//            // 食安公示
//            .addFunc(Function.MARKET_MAINTENANCE_DETECTION_INFO)
//            .addFunc(Function.MARKET_MAINTENANCE_RETROACTIVE_INFO)
//            // 市场管理
//            .addFunc(Function.MARKET_MALL_ADVER)
//            .addFunc(Function.MARKET_ORDER_RIDER_MANAGER)
//            .addFunc(Function.MARKET_CUSTOMER_FEEDBACK)
//            .addFunc(Function.MARKET_DESKTOP_MANAGER)
//            // 商品管理
//            .addFunc(Function.GOODS_ORDINARY_MANAGER)
//            .addFunc(Function.MARKET_GOODS_SHARE_MANAGER)
//            .addFunc(Function.MARKET_GOODS_SPECIAL_MANAGER)
//            .addFunc(Function.MARKET_GOODS_PRESALE_MANAGER)
//            .addFunc(Function.MARKET_GOODS_POVERTY_MANAGER)
//            .addFunc(Function.MARKET_GOODS_COLLAGE_MANAGER)
//            .addFunc(Function.MARKET_GOODS_CUT_MANAGER)
//            .addFunc(Function.MARKET_GOODS_SUPPLY)
//            // 菜谱管理
//            .addFunc(Function.GOODS_COOKFD_MANAGER)
//            .addFunc(Function.GOODS_COOKFD_TYPE)
//            // 卡券管理
//            .addFunc(Function.MARKET_COUPON_MANAGER)
//            .addFunc(Function.MARKET_GIFT_MANAGER)
//            .addFunc(Function.MARKET_ACTIVITY_MANAGER)
//            .addFunc(Function.MARKET_COUPON_GRANT)
//            .addFunc(Function.MARKET_COUPON_INQUIRE)
//            .addFunc(Function.MARKET_GIFT_USAGE_QUERY)
//            // 交易管理
//            .addFunc(Function.ORDER_MARKET_OFFLINE)
//            .addFunc(Function.ORDER_MARKET_REFUND)
//            .addFunc(Function.ORDER_RIDER_OFFLINE)
//            .addFunc(Function.ORDER_COLLAGE_OFFLINE)
//            // 商户管理
//            .addFunc(Function.MARKET_VENDOR_MANAGER)
//            .addFunc(Function.MARKET_BOUTIQUE_VENDOR)
////            .addFunc(Function.MARKET_VENDOR_SETTLEMENT)
////            .addFunc(Function.MARKET_VENDOR_SETTLEMENT_REPORT)
//            .addFunc(Function.MARKET_VENDOR_WITHDRAWAL)
//            .addFunc(Function.MARKET_VENDOR_BILL)
//            .addFunc(Function.MARKET_VENDOR_WALLET)
////            .addFunc(Function.MARKET_VENDOR_REVOKE)
//            // 数据中心
//            .addFunc(Function.MARKET_DATA_CENTER)
//            .addFunc(Function.MARKET_DATA_CENTER_1)
//            .addFunc(Function.MARKET_DATA_CENTER_2)
//            .addFunc(Function.MARKET_DATA_CENTER_3)
//            .addFunc(Function.MARKET_DATA_CENTER_4)
//            .addFunc(Function.MARKET_DATA_CENTER_5)
//            .addFunc(Function.MARKET_DATA_CENTER_6)
//            .addFunc(Function.MARKET_DATA_CENTER_7)
//            .addFunc(Function.MARKET_DATA_CENTER_8)
//            .addFunc(Function.MARKET_DATA_CENTER_9)
//            .addFunc(Function.MARKET_DATA_CENTER_10)
//            .addFunc(Function.MARKET_DATA_CENTER_11)
//            .addFunc(Function.MARKET_DATA_CENTER_12)
//            .addFunc(Function.MARKET_DATA_CENTER_13)
//            // 系统管理
//            .addFunc(Function.MARKET_USER_MANAGER)
//            .addFunc(Function.MARKET_ROLE_MANAGER)
//            .addFunc(Function.MARKET_INFORMATION)
//            
//            .addFunc(SysFunction.MANAGER_ROLE)
//            .addFunc(SysFunction.ADD_USER)
//            .addFunc(SysFunction.MOD_USER)
//            .addFunc(SysFunction.DEL_USER)
//            .addFunc(SysFunction.MANAGER_USER_ROLE)
//            .addFunc(SysFunction.QUERY_INFO)
//            .addFunc(SysFunction.WRITE_INFO)
//            .addFunc(SysFunction.MOD_DEPT)
//            .build());
//        
//        roles.add(SimpleRole.builder()
//            .pkey(Role.H5_USER)
//            .name("H5用户")
//            .build());
//        
//        return roles;
//    }
//    
//    // 初始化权限
//    public FuncRoot initFunctionRoot()
//    {
//        FuncRoot functionRoot = new FuncRoot();
//        functionRoot
//            .add(func(Function.ZYYSC_BOSS_INDEX, "首页", "登陆和显示首页", SysFunction.QUERY_INFO, SysFunction.WRITE_INFO));
//        functionRoot
//            .add(func(Function.ZYYSC_COMPANY_INDEX, "首页", "登陆和显示首页", SysFunction.QUERY_INFO, SysFunction.WRITE_INFO));
//        functionRoot
//            .add(func(Function.ZYYSC_MARKET_INDEX, "首页", "登陆和显示首页", SysFunction.QUERY_INFO, SysFunction.WRITE_INFO));
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_02")
//            .name("基础设置")
//            .add(func(Function.BOSS_MALL_CONFIG, "商城配置"))
//            .add(func(Function.BOSS_MALL_POSTAGE_CONFIG, "运费配置"))
//            .add(func(Function.BOSS_MALL_GOODS_CLASSIFICATION, "商品分类"))
//            .add(func(Function.BOSS_MALL_GOODS_HOUSE, "商品库"))
//            .add(func(Function.BOSS_GOODS_SUPPLY, "商品供应库"))
//            .add(func(Function.BOSS_PROMOTE_MANAGER, "推广管理"))
//            .add(func(Function.BOSS_THIRD_PAYMENT, "第三方支付渠道"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_03")
//            .name("商城管理")
//            .add(func(Function.BOSS_GOODS_POINT_MANAGER, "商品管理"))
//            .add(func(Function.BOSS_MALL_GIFT, "礼券管理"))
//            .add(func(Function.BOSS_MALL_COUPON, "优惠券管理"))
//            .add(func(Function.BOSS_MALL_CUSTOMER_FEEDBACK, "意见反馈"))
//            .add(func(Function.BOSS_MALL_CUSTOMER_FEEDBACK_2, "意见反馈(仅浏览)"))
//            .add(func(Function.BOSS_MALL_PROBLEM, "常见问题"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_04")
//            .name("广告管理")
//            .add(func(Function.BOSS_INDEX_ADVERT, "弹窗广告"))
//            .add(func(Function.BOSS_SPECIAL_AREA_ADVERT, "专区广告"))
//            .add(func(Function.BOSS_MALL_ADVER, "积分商城广告"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_05")
//            .name("会员管理")
//            .add(func(Function.BOSS_MEMBER_CENTER, "会员中心"))
//            .add(func(Function.BOSS_MEMBER_CENTER_2, "会员中心(仅浏览)"))
//            .add(func(Function.BOSS_MEMBER_RECHARGE_RECORD, "充值记录"))
//            .add(func(Function.BOSS_MEMBER_POINT, "积分查询"))
//            .add(func(Function.BOSS_MEMBER_COMM, "钱包查询"))
//            .add(func(Function.BOSS_TAG_MANAGE, "标签管理"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_06")
//            .name("活动管理")
//            .add(func(Function.BOSS_ACTIVITY_PRIZE, "奖品配置"))
//            .add(func(Function.BOSS_ACTIVITY_INQUIRE, "中奖查询"))
//            .add(func(Function.BOSS_ACTIVITY_INQUIRE_2, "中奖查询(仅浏览)"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_07")
//            .name("卡券管理")
//            .add(func(Function.BOSS_COUPON_MANAGER, "卡券管理"))
//            .add(func(Function.BOSS_COUPON_GRANT, "卡券发放"))
//            .add(func(Function.BOSS_COUPON_INQUIRE, "优惠券发放记录"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_08")
//            .name("交易管理")
//            .add(func(Function.BOSS_ORDER_MALL, "商城订单"))
//            .add(func(Function.BOSS_AFTER_SALE_REFUND_MANAGER, "退款管理"))
//            .add(func(Function.BOSS_AFTER_SALE_REFUND_MANAGER_2, "退款管理(仅浏览)"))
//            .add(func(Function.BOSS_ORDER_DRAW, "提现管理"))
//            .add(func(Function.BOSS_ORDER_DRAW_2, "提现管理(仅浏览)"))
//            .add(func(Function.BOSS_BILL_MANAGER, "对账中心"))
//            .add(func(Function.BOSS_BILL_DETAIL, "账单明细"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_09")
//            .name("市场运营")
//            .add(func(Function.BOSS_MARKET_COMPANY_MANAGER, "公司市场"))
//            .add(func(Function.BOSS_MARKET_COMPANY_MANAGER_2, "公司市场(仅浏览)"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_10")
//            .name("合作商户")
//            .add(func(Function.BOSS_VENDOR_MANAGER, "积分商城商户"))
//            .add(func(Function.BOSS_VENDOR_MANAGER_2, "积分商城商户(仅浏览)"))
//            .add(func(Function.BOSS_VENDOR_POINT_ORDER, "积分订单"))
//            .add(func(Function.BOSS_MARKET_VENDOR_MANAGER, "市场商城商户"))
//            .add(func(Function.BOSS_MARKET_VENDOR_MANAGER_2, "市场商城商户(仅浏览)"))
//            
////            .add(func(Function.BOSS_VENDOR_SETTLEMENT, "商户结算"))
////            .add(func(Function.BOSS_VENDOR_SETTLEMENT_REPORT, "结算报表"))
//            .add(func(Function.BOSS_VENDOR_BILL, "商户对账"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_11")
//            .name("客如云")
//            .add(func(Function.BOSS_KRU_MERCHANT, "客如云商户"))
//            .add(func(Function.BOSS_ORDER_KRY, "客如云订单"))
//            .add(func(Function.BOSS_KRU_MERCHANT_2, "客如云商户(仅浏览)"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_boss_12")
//            .name("数据中心")
//            .add(func(Function.BOSS_DATA_CENTER, "数据中心"))
//            .add(func(Function.BOSS_DATA_CENTER_1, "专区营业报表"))
//            .add(func(Function.BOSS_DATA_CENTER_2, "商品销售统计"))
//            .add(func(Function.BOSS_DATA_CENTER_3, "商品销售分析"))
//            .add(func(Function.BOSS_DATA_CENTER_4, "奖品统计"))
//            .add(func(Function.BOSS_DATA_CENTER_5, "时间段销售额"))
//            .add(func(Function.BOSS_DATA_CENTER_6, "付费会员办理"))
//            .add(func(Function.BOSS_DATA_CENTER_7, "付费会员明细"))
//            .add(func(Function.BOSS_DATA_CENTER_8, "积分兑换统计"))
//            .add(func(Function.BOSS_DATA_CENTER_9, "付费会员消费"))
//            .add(func(Function.BOSS_DATA_CENTER_10, "用户访问报表"))
//            .add(func(Function.BOSS_DATA_CENTER_11, "新增用户报表"))
//            .add(func(Function.BOSS_DATA_CENTER_12, "卡券使用统计"))
//            .add(func(Function.BOSS_DATA_CENTER_13, "品类销售统计"))
//            .add(func(Function.BOSS_DATA_CENTER_14, "商户积分统计"))
//            .add(func(Function.BOSS_DATA_CENTER_15, "公司销售统计"))
//            .add(func(Function.BOSS_DATA_CENTER_16, "市场销售统计"))
//            .add(func(Function.BOSS_DATA_CENTER_17, "运费报表"))
//            .add(func(Function.BOSS_DATA_CENTER_19, "经营数据统计"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_system")
//            .name("系统管理")
//            .add(func(Function.USER_MANAGER,
//                "用户",
//                "用户",
//                SysFunction.MANAGER_ROLE,
//                SysFunction.ADD_USER,
//                SysFunction.MOD_USER,
//                SysFunction.DEL_USER,
//                SysFunction.MANAGER_USER_ROLE))
//            .add(func(Function.ROLE_MANAGER,
//                "角色",
//                "角色",
//                SysFunction.MANAGER_ROLE,
//                SysFunction.QUERY_INFO,
//                SysFunction.WRITE_INFO))
//            .add(func(Function.LOG_INFO, "日志"))
//            .add(func(Function.SHIELD_VERSION_MANAGER, "版本屏蔽设置"))
//            .build());
//        
//        // 公司管理
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_company_01")
//            .name("公司管理")
//            .add(func(Function.ZYYSC_COMPANY_INFO, "公司信息"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_company_02")
//            .name("报表查询")
//            .add(func(Function.COMPANY_DATA_CENTER_1, "专区营业报表"))
//            .add(func(Function.COMPANY_DATA_CENTER_2, "商品销售统计"))
//            .add(func(Function.COMPANY_DATA_CENTER_3, "商品销售分析"))
//            .add(func(Function.COMPANY_DATA_CENTER_4, "时间段销售额"))
//            .add(func(Function.COMPANY_DATA_CENTER_5, "卡券使用统计"))
//            .add(func(Function.COMPANY_DATA_CENTER_6, "品类销售统计"))
//            .add(func(Function.COMPANY_DATA_CENTER_7, "市场销售统计"))
//            .build());
//        
//        // 市场管理
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_02")
//            .name("基础设置")
//            .add(func(Function.MARKET_BASIS_MARKET_MAINTENANCE, "市场维护"))
//            .add(func(Function.MARKET_MALL_POSTAGE_CONFIG, "运费配置"))
//            .add(func(Function.MARKET_MALL_DISPATCH, "派单配置"))
//            .add(func(Function.MARKET_MALL_GTYPE, "商品分类"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_03")
//            .name("食安公示")
//            .add(func(Function.MARKET_MAINTENANCE_DETECTION_INFO, "检测信息"))
//            .add(func(Function.MARKET_MAINTENANCE_RETROACTIVE_INFO, "溯源信息"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_04")
//            .name("市场管理")
//            .add(func(Function.MARKET_MALL_ADVER, "广告管理"))
//            .add(func(Function.MARKET_ORDER_RIDER_MANAGER, "骑手管理"))
//            .add(func(Function.MARKET_CUSTOMER_FEEDBACK, "意见反馈"))
//            .add(func(Function.MARKET_DESKTOP_MANAGER, "桌位管理"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_05")
//            .name("商品管理")
//            .add(func(Function.GOODS_ORDINARY_MANAGER, "市场商品管理"))
//            .add(func(Function.MARKET_GOODS_SHARE_MANAGER, "分享商品管理"))
//            .add(func(Function.MARKET_GOODS_SPECIAL_MANAGER, "特价商品管理"))
//            .add(func(Function.MARKET_GOODS_PRESALE_MANAGER, "预售商品管理"))
//            .add(func(Function.MARKET_GOODS_POVERTY_MANAGER, "扶贫商品管理"))
//            .add(func(Function.MARKET_GOODS_COLLAGE_MANAGER, "团购商品管理"))
//            .add(func(Function.MARKET_GOODS_CUT_MANAGER, "砍价商品管理"))
//            .add(func(Function.MARKET_GOODS_SUPPLY, "商品供应库"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_06")
//            .name("菜谱管理")
//            .add(func(Function.GOODS_COOKFD_MANAGER, "菜谱管理"))
//            .add(func(Function.GOODS_COOKFD_TYPE, "菜谱分类"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_07")
//            .name("卡券管理")
//            .add(func(Function.MARKET_COUPON_MANAGER, "优惠券管理"))
//            .add(func(Function.MARKET_GIFT_MANAGER, "礼品券管理"))
//            .add(func(Function.MARKET_ACTIVITY_MANAGER, "卡券活动"))
//            .add(func(Function.MARKET_COUPON_GRANT, "卡券发放"))
//            .add(func(Function.MARKET_COUPON_INQUIRE, "优惠券发放记录"))
//            .add(func(Function.MARKET_GIFT_USAGE_QUERY, "礼品券发放记录"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_08")
//            .name("交易管理")
//            .add(func(Function.ORDER_MARKET_OFFLINE, "市场订单"))
//            .add(func(Function.ORDER_MARKET_REFUND, "市场退款订单"))
//            .add(func(Function.ORDER_RIDER_OFFLINE, "骑手订单"))
//            .add(func(Function.ORDER_COLLAGE_OFFLINE, "团购订单"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_09")
//            .name("合作商户")
//            .add(func(Function.MARKET_VENDOR_MANAGER, "商户管理"))
//            .add(func(Function.MARKET_BOUTIQUE_VENDOR, "精选商户管理"))
////            .add(func(Function.MARKET_VENDOR_SETTLEMENT, "商户结算"))
////            .add(func(Function.MARKET_VENDOR_SETTLEMENT_REPORT, "结算报表"))
//            .add(func(Function.MARKET_VENDOR_WITHDRAWAL, "提现打款"))
//            .add(func(Function.MARKET_VENDOR_BILL, "商户对账"))
//            .add(func(Function.MARKET_VENDOR_WALLET, "商户钱包"))
////            .add(func(Function.MARKET_VENDOR_REVOKE, "撤销记录"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_10")
//            .name("数据中心")
//            .add(func(Function.MARKET_DATA_CENTER, "数据中心"))
//            .add(func(Function.MARKET_DATA_CENTER_1, "专区营业报表"))
//            .add(func(Function.MARKET_DATA_CENTER_2, "商品销售统计"))
//            .add(func(Function.MARKET_DATA_CENTER_3, "商品销售分析"))
//            .add(func(Function.MARKET_DATA_CENTER_4, "异常货品分析"))
//            .add(func(Function.MARKET_DATA_CENTER_5, "时间段销售额"))
//            .add(func(Function.MARKET_DATA_CENTER_6, "卡券使用统计"))
//            .add(func(Function.MARKET_DATA_CENTER_7, "品类销售统计"))
//            .add(func(Function.MARKET_DATA_CENTER_8, "市场销售统计"))
//            .add(func(Function.MARKET_DATA_CENTER_9, "佣金达人"))
//            .add(func(Function.MARKET_DATA_CENTER_10, "佣金收入明细"))
//            .add(func(Function.MARKET_DATA_CENTER_11, "配送员绩效表"))
//            .add(func(Function.MARKET_DATA_CENTER_12, "商户采购报表"))
//            .add(func(Function.MARKET_DATA_CENTER_13, "经营数据统计"))
//            .build());
//        
//        functionRoot.add(FuncGroup.builder()
//            .pkey("zy_market_system")
//            .name("系统管理")
//            .add(func(Function.MARKET_USER_MANAGER,
//                "用户",
//                "用户",
//                SysFunction.MANAGER_ROLE,
//                SysFunction.ADD_USER,
//                SysFunction.MOD_USER,
//                SysFunction.DEL_USER,
//                SysFunction.MANAGER_USER_ROLE))
//            .add(func(Function.MARKET_ROLE_MANAGER,
//                "角色",
//                "角色",
//                SysFunction.MANAGER_ROLE,
//                SysFunction.QUERY_INFO,
//                SysFunction.WRITE_INFO))
//            .add(func(Function.MARKET_INFORMATION,"消息推送"))
//            .build());
//        
//        return functionRoot;
//    }
//    
//    private FunctionItem func(String pkey, String name)
//    {
//        FunctionItem item = new FunctionItem();
//        item.setPkey(pkey);
//        item.setName(name);
//        return item;
//    }
//    
//    private FunctionItem func(String pkey, String name, String description, String... functionlist)
//    {
//        FunctionItem item = new FunctionItem();
//        item.setPkey(pkey);
//        item.setName(name);
//        item.setDescription(description);
//        if (functionlist != null)
//        {
//            Set<String> relatedFunctions = new HashSet<>();
//            for (String f : functionlist)
//            {
//                relatedFunctions.add(f);
//            }
//            item.setRelatedFunctions(relatedFunctions);
//        }
//        return item;
//    }
//    
//    /**
//     * 给角色加个权限
//     * @param roleid		角色pkey
//     * @param subNodePkey	新增权限pkey
//     * @param subNodeName	新增权限name
//     * @param subIndex		新增权限的位置
//     * @param sort			新增权限的顺序
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public Boolean insRoleAcl(String roleid, String subNodePkey, String subNodeName, int subIndex, int sort)
//    {
//        RoleAccessGroup group = roleApiV3.listAppRoleAcl(roleid).fetchResult();
//        RoleFunctionTree functions = buildRoleFunctionTree(roleid, group.getGroup().get(Constant.rootRoleGroup));
//        
//        TreeModel<String, ?> data = functions.getData();
//        List<?> sub = data.getSub();
//        TreeModel<String, Object> object = (TreeModel<String, Object>)sub.get(subIndex);
//        log.info("object: {}", JsonUtil.toString(object, true));
//        TreeModel<String, Object> subNode = new TreeModel<>();
//        subNode.setName(subNodeName);
//        subNode.setPkey(subNodePkey);
//        subNode.setDisabled(false);
//        subNode.setLeaf(true);
//        subNode.setSelected(true);
//        subNode.setSort(sort);
//        subNode.setSub(null);
//        subNode.setValue(null);
//        List<TreeModel<String, Object>> sub2 = object.getSub();
//        sub2.add(subNode);
//        log.info("组装后data: {}", JsonUtil.toString(data, true));
//        
//        buildAccessConfig(group.getGroup().get(Constant.rootRoleGroup), data);
//        roleApiV3.setAppRoleAcl(group);
//        
//        return true;
//    }
//    
//    private RoleFunctionTree buildRoleFunctionTree(String pkey, AccessConfig accessConfig)
//    {
//        RoleFunctionTree tree = new RoleFunctionTree();
//        tree.setRolePkey(pkey);
//        tree.setData(buildTree(accessConfig));
//        return tree;
//    }
//    
//    private TreeModel<String, String> buildTree(AccessConfig accessConfig)
//    {
//        if (accessConfig == null
//            || (accessConfig.isGroup() && (accessConfig.getSub() == null || accessConfig.getSub().size() == 0)))
//            return null;
//        TreeModel<String, String> tree = new TreeModel<String, String>();
//        tree.setPkey(accessConfig.getPkey());
//        tree.setName(accessConfig.getName());
//        tree.setSort(accessConfig.getSort());
//        tree.setDisabled(accessConfig.isReadonly());
//        tree.setSelected(accessConfig.getAccept() != null && accessConfig.getAccept());
//        if (accessConfig.getSub() != null && accessConfig.getSub().size() > 0)
//        {
//            for (AccessConfig subConfig : accessConfig.getSub())
//            {
//                TreeModel<String, String> subitem = buildTree(subConfig);
//                if (subitem != null && subitem.isSelected()) tree.setSelected(true);
//                tree.addSub(subitem);
//            }
//        }
//        if (!tree.isSelected()) return null;
//        return tree;
//    }
//    
//    private void buildAccessConfig(AccessConfig accessConfig, TreeModel<String, ?> data)
//    {
//        if (accessConfig == null || data == null) return;
//        if (accessConfig.isGroup())
//        {
//            if (accessConfig.getSub() != null && data.getSub() != null)
//            {
//                Map<Integer, TreeModel<String, ?>> map = new HashMap<>();
//                for (TreeModel<String, ?> subNode : data.getSub())
//                {
//                    map.put(subNode.getSort(), subNode);
//                }
//                for (AccessConfig subConfig : accessConfig.getSub())
//                {
//                    TreeModel<String, ?> subNode = map.get(subConfig.getSort());
//                    buildAccessConfig(subConfig, subNode);
//                }
//            }
//        }
//        else
//        {
//            if (!accessConfig.isReadonly())
//            {
//                if (data.isShow())
//                    accessConfig.setAccept(true);
//                else
//                    accessConfig.setAccept(null);
//            }
//        }
//    }
    
    
}
