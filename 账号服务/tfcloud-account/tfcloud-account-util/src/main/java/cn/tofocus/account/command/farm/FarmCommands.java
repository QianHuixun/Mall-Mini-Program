package cn.tofocus.account.command.farm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cn.tofocus.account.command.BaseCommands;
import cn.tofocus.account.command.bean.MenuEntityBuilder;
import cn.tofocus.account.command.farm.FarmConstant.C;
import cn.tofocus.account.command.farm.FarmConstant.DelF;
import cn.tofocus.account.command.farm.FarmConstant.F;
import cn.tofocus.account.command.farm.FarmConstant.M;
import cn.tofocus.account.command.farm.FarmConstant.R;
import cn.tofocus.account.command.farm.FarmConstant.SysF;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.cache.role.RoleAclCache;
import cn.tofocus.account.db.dao.application.AppLoginCheckDao;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.domain.ModelDao;
import cn.tofocus.account.db.dao.role.RoleAccessDao;
import cn.tofocus.account.db.dao.role.RoleMenuDao;
import cn.tofocus.account.db.entity.application.AppLoginCheckEntity;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.account.db.entity.role.RoleMenuEntity;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.enums.ModelStatus;
import cn.tofocus.core.security.AccessScopeType;

@ShellComponent
@ShellCommandGroup("农贸权限迁移")
public class FarmCommands extends BaseCommands
{
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private AppFunctionCache appFunctionCache;
    
    @Autowired
    private RoleAclCache roleAccessDao;
    
    @Autowired
    private RoleMenuDao roleMenuDao;
    
    @Autowired
    private AppLoginCheckDao appLoginCheckDao;
    
    @Autowired
    private ModelDao modelDao;
    
    @Autowired
    private MenuDao menuDao;
    
    @ShellMethod("农批权限迁移")
    public String updateNp()
    {
        Map<String, ModelStatus> modelStatus = new HashMap<>();
        modelStatus.put(M.BASE, ModelStatus.OnLine);
        modelStatus.put(M.WARNING, ModelStatus.OnLine);
        modelStatus.put(M.WATCH, ModelStatus.Disabled);
        modelStatus.put(M.MEMBER, ModelStatus.OnLine);
        modelStatus.put(M.HYDROPOWER, ModelStatus.Disabled);
        modelStatus.put(M.LIQUIDATION, ModelStatus.Disabled);
        modelStatus.put(M.MEASUREMENT, ModelStatus.Disabled);
        modelStatus.put(M.SHOPPINGCART, ModelStatus.Disabled);
        modelStatus.put(M.ZYYSC, ModelStatus.Disabled);
        modelStatus.put(M.OLDMALL, ModelStatus.Disabled);
        modelStatus.put(M.LEGACY, ModelStatus.Disabled);
        modelStatus.put(M.TRUCK, ModelStatus.OnLine);
        modelStatus.put(M.INVENTORY, ModelStatus.OnLine);
        modelStatus.put(M.CUSTOM, ModelStatus.Disabled);
        initFarm(modelStatus);
        return "ok";
    }
    
    @ShellMethod("农贸权限迁移")
    public String updateFarm()
    {
        Map<String, ModelStatus> modelStatus = new HashMap<>();
        modelStatus.put(M.BASE, ModelStatus.OnLine);
        modelStatus.put(M.WARNING, ModelStatus.OnLine);
        modelStatus.put(M.WATCH, ModelStatus.OnLine);
        modelStatus.put(M.MEMBER, ModelStatus.OnLine);
        modelStatus.put(M.HYDROPOWER, ModelStatus.OnLine);
        modelStatus.put(M.LIQUIDATION, ModelStatus.OnLine);
        modelStatus.put(M.MEASUREMENT, ModelStatus.OnLine);
        modelStatus.put(M.SHOPPINGCART, ModelStatus.OnLine);
        modelStatus.put(M.ZYYSC, ModelStatus.OnLine);
        modelStatus.put(M.OLDMALL, ModelStatus.OnLine);
        modelStatus.put(M.LEGACY, ModelStatus.OnLine);
        modelStatus.put(M.TRUCK, ModelStatus.Disabled);
        modelStatus.put(M.INVENTORY, ModelStatus.Disabled);
        modelStatus.put(M.CUSTOM, ModelStatus.OnLine);
        initFarm(modelStatus);
        return "ok";
    }
    
    private void initFarm(Map<String, ModelStatus> modelStatus)
    {
        
        //重置系统角色
        addRole(R.ADMIN, "超级管理员", FarmConstant.root);
        addRole(R.IMPLEMENTOR_HEAD, "实施总管", FarmConstant.boss);
        addRole(R.IMPLEMENTOR, "实施人员", FarmConstant.boss);
        addRole(R.OPERATOR_HEAD, "运营总管", FarmConstant.boss);
        addRole(R.OPERATOR, "运营人员", FarmConstant.boss);
        addRole(R.COMPANY_HEAD, "公司负责人", FarmConstant.root);
        addRole(R.MARKET_HEAD, "市场负责人", FarmConstant.root);
        addRole(R.MARKET_MANAGER, "市场管理员", FarmConstant.cust);
        addRole(R.DETECTOR, "检测员", FarmConstant.cust);
        addRole(R.FINANCIAL_STAFF, "财务", FarmConstant.cust);
        addRole(R.PATROL, "巡检员", FarmConstant.cust);
        addRole(R.LARGE_SCREEN_DATA_MANAGER, "大屏数据管理员", FarmConstant.cust);
        addRole(R.MARKET_EMPLOYEE, "市场员工", FarmConstant.root);
        
        //权限迁移
        addFunc(F.BOSS_LOGIN, "登陆Boss管理后台", FarmConstant.boss);
        addFunc(F.CUST_LOGIN, "登录Cust管理后台", FarmConstant.cust);
        addFunc(F.MARKET_ADMIN, "农贸管理员", FarmConstant.root);
        
        //登录权限验证
        addLoginCheck(C.BOSS_WEB, F.BOSS_LOGIN);
        addLoginCheck(C.CUST_WEB, F.CUST_LOGIN);
        
        //初始化模块
        int sort = 0;
        addModel(M.BASE, "基础模块", modelStatus.get(M.BASE), sort++, true, true);
        addModel(M.WARNING, "市场监管", modelStatus.get(M.WARNING), sort++, true, true);
        addModel(M.WATCH, "监管任务", modelStatus.get(M.WATCH), sort++, true, true);
        addModel(M.MEMBER, "会员模块", modelStatus.get(M.MEMBER), sort++, false, true);
        addModel(M.HYDROPOWER, "智能水电", modelStatus.get(M.HYDROPOWER), sort++, false, true);
        addModel(M.LIQUIDATION, "二清", modelStatus.get(M.LIQUIDATION), sort++, false, true);
        addModel(M.MEASUREMENT, "计量管理", modelStatus.get(M.MEASUREMENT), sort++, false, true);
        addModel(M.SHOPPINGCART, "购物车", modelStatus.get(M.SHOPPINGCART), sort++, false, true);
        addModel(M.ZYYSC, "云商城", modelStatus.get(M.ZYYSC), sort++, false, true);
        addModel(M.OLDMALL, "商城模块", modelStatus.get(M.OLDMALL), sort++, false, true);
        addModel(M.LEGACY, "旧发布", modelStatus.get(M.LEGACY), sort++, false, true);
        addModel(M.TRUCK, "车辆管理", modelStatus.get(M.TRUCK), sort++, true, true);
        addModel(M.INVENTORY, "进销存", modelStatus.get(M.INVENTORY), sort++, true, true);
        addModel(M.CUSTOM, "定制模块", modelStatus.get(M.CUSTOM), sort++, false, false);
        
        //初始化菜单
        menuDao.removeAll(menuDao.select().eq("domainid", FarmConstant.DOMAIN).exec());
        StrList bossMenuKeys = initBossMenus();
        StrList custMenuKeys = initCustMenus();
        
        //初始化系统角色的权限
        addRoleAcl(R.ADMIN,
            Arrays.asList(F.BOSS_LOGIN, SysF.DOMAIN_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE, SysF.MANAGER_ORG));
        addRoleAcl(R.IMPLEMENTOR_HEAD,
            Arrays.asList(F.BOSS_LOGIN,
                F.CUST_LOGIN,
                F.MARKET_ADMIN,
                SysF.MANAGER_USER,
                SysF.MANAGER_ROLE,
                SysF.MANAGER_ORG));
        addRoleAcl(R.IMPLEMENTOR,
            Arrays.asList(F.BOSS_LOGIN,
                F.CUST_LOGIN,
                F.MARKET_ADMIN,
                SysF.MANAGER_USER,
                SysF.MANAGER_ROLE,
                SysF.MANAGER_ORG));
        addRoleAcl(R.OPERATOR_HEAD, Arrays.asList(F.BOSS_LOGIN));
        addRoleAcl(R.OPERATOR, Arrays.asList(F.BOSS_LOGIN));
        addRoleAcl(R.COMPANY_HEAD, Arrays.asList(F.CUST_LOGIN, F.MARKET_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE));
        addRoleAcl(R.MARKET_HEAD, Arrays.asList(F.CUST_LOGIN, F.MARKET_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE));
        addRoleAcl(R.MARKET_MANAGER, Arrays.asList(F.CUST_LOGIN, F.MARKET_ADMIN, SysF.MANAGER_USER, SysF.MANAGER_ROLE));
        addRoleAcl(R.DETECTOR, Arrays.asList(F.CUST_LOGIN));
        addRoleAcl(R.FINANCIAL_STAFF, Arrays.asList(F.CUST_LOGIN));
        addRoleAcl(R.LARGE_SCREEN_DATA_MANAGER, Arrays.asList(F.CUST_LOGIN));
        
        //初始化系统角色的菜单
        addRoleMenu(R.ADMIN, bossMenuKeys);
        addRoleMenu(R.IMPLEMENTOR_HEAD,
            custMenuKeys,
            StrList.fromArray("farm_boss_operate_implGrade"),
            bossPubMenus(),
            bossSysMenus(),
            bossImplementorMenus());
        addRoleMenu(R.IMPLEMENTOR, custMenuKeys, bossPubMenus(), bossImplementorMenus());
        addRoleMenu(R.OPERATOR_HEAD, bossPubMenus(), bossOperatorMenus());
        addRoleMenu(R.OPERATOR, bossPubMenus(), bossOperatorMenus());
        addRoleMenu(R.COMPANY_HEAD, custMenuKeys);
        addRoleMenu(R.MARKET_HEAD, custMenuKeys);
        addRoleMenu(R.MARKET_MANAGER, custMenuKeys);
        addRoleMenu(R.DETECTOR,
            StrList.fromArray("farm_cust_home", "farm_cust_foodsafe_detection", "farm_cust_foodsafe_detection_handle"));
        addRoleMenu(R.FINANCIAL_STAFF, custFinancialMenus());
        addRoleMenu(R.LARGE_SCREEN_DATA_MANAGER, StrList.fromArray("farm_cust_home"));
        
        //删除旧权限
        delOldFunc();
    }
    
    private void delOldFunc()
    {
        delFunc("queryInfo");
        delFunc("writeInfo");
        delFunc("addUser");
        delFunc("modUser");
        delFunc("delUser");
        delFunc("managerUserRole");
        delFunc("managerUserFunction");
        delFunc("modDept");
        delFunc("modOrg");
        delFunc("farm_marketApp_login");
        delFunc("addDept");
        delFunc("addOrg");
        delFunc("delDept");
        delFunc("delOrg");
        delFunc(DelF.BOSS_SYS);
        delFunc(DelF.PATROL_APP_LOGIN);
        delFunc(DelF.OPERATOR);
        delFunc(DelF.IMPLEMENTOR_HEAD);
        delFunc(DelF.IMPLEMENTOR);
        delFunc(DelF.VIEW_MARKET_MANAGER);
        delFunc(DelF.OPER_MARKET_MANAGER);
        delFunc(DelF.VIEW_MERCHANT_MANAGER);
        delFunc(DelF.OPER_MERCHANT_MANAGER);
        delFunc(DelF.VIEW_STALL_MANAGER);
        delFunc(DelF.OPER_STALL_MANAGER);
        delFunc(DelF.VIEW_GOODS_MANAGER);
        delFunc(DelF.OPER_GOODS_MANAGER);
        delFunc(DelF.VIEW_VENDOR_MANAGER);
        delFunc(DelF.OPER_VENDOR_MANAGER);
        delFunc(DelF.VIEW_TRADE_MANAGER);
        delFunc(DelF.VIEW_DETECTION_MANAGER);
        delFunc(DelF.OPER_DETECTION_MANAGER);
        delFunc(DelF.VIEW_BOOK_MANAGER);
        delFunc(DelF.OPER_BOOK_MANAGER);
        delFunc(DelF.VIEW_DEVICE_MANAGER);
        delFunc(DelF.OPER_DEVICE_MANAGER);
        delFunc(DelF.LARGE_SCREEN_DATA_MANAGER);
        delFunc(DelF.VIEW_MERCHANT_EVALUATION);
        delFunc(DelF.OPER_MERCHANT_EVALUATION);
        delFunc(DelF.VIEW_MERCHANT_COMMENT);
        delFunc(DelF.OPER_MERCHANT_COMMENT);
        delFunc(DelF.VIEW_MERCHANT_HONOR);
        delFunc(DelF.OPER_MERCHANT_HONOR);
        delFunc(DelF.VIEW_MARKET_SUGGESTION);
        delFunc(DelF.OPER_MARKET_SUGGESTION);
        delFunc(DelF.VIEW_VIP_MANAGER);
        delFunc(DelF.OPER_VIP_MANAGER);
        delFunc(DelF.VIEW_PATROL);
        delFunc(DelF.OPER_PATROL);
        delFunc(DelF.VIEW_REPAIR);
        delFunc(DelF.OPER_REPAIR);
        delFunc(DelF.VIEW_LIFESAVING);
        delFunc(DelF.OPER_LIFESAVING);
        delFunc(DelF.MALL);
        delFunc(DelF.VIEW_FINANCE);
        delFunc(DelF.OPER_FINANCE);
        delFunc(DelF.VIEW_PROPERTY);
        delFunc(DelF.OPER_PROPERTY);
        delFunc(DelF.VIEW_COMPANY_INFO);
        delFunc(DelF.OPER_COMPANY_INFO);
        delFunc(DelF.VIEW_SYS_USER);
        delFunc(DelF.OPER_SYS_USER);
        delFunc(DelF.VIEW_SYS_ROLE);
        delFunc(DelF.OPER_SYS_ROLE);
        delFunc(DelF.VIEW_LOG);
        delFunc(DelF.VIEW_WX);
        delFunc(DelF.OPER_ADVICE);
        delFunc(DelF.OPER_PAY_NUMBER);
        delFunc(DelF.REPORT_STATISTICS);
        delFunc(DelF.REPORT_TRADE);
        delFunc(DelF.REPORT_SCALE);
        delFunc(DelF.REPORT_USER);
        delFunc(DelF.VIEW_EMPLOYEE_MANAGER);
        delFunc(DelF.OPER_EMPLOYEE_MANAGER);
        delFunc(DelF.VIEW_TRAINING);
        delFunc(DelF.OPER_TRAINING);
        delFunc(DelF.SCALE_QUERY);
        delFunc(DelF.KRY);
        delFunc(DelF.SUPERVISON_MONITOR_WARNING);
        delFunc(DelF.SUPERVISON_DEVICE_WRNING);
        delFunc(DelF.SUPERVISON_CREDENTIALS_WRNING);
        delFunc(DelF.SUPERVISON_PRICE_WRNING);
        delFunc(DelF.SUPERVISON_CONDUCT_WRNING);
    }
    
    private StrList custFinancialMenus()
    {
        return StrList.fromArray("farm_cust_home",
            "farm_cust_market_trade",
            "farm_cust_market_trade_mall",
            "farm_cust_market_trade_scale",
            "farm_cust_market_trade_ori",
            "farm_cust_member_config",
            "farm_cust_member_query",
            "farm_cust_member_consume",
            "farm_cust_card_mamager",
            "farm_cust_recharge_activity",
            "farm_cust_account_manger",
            "farm_cust_accout_operate",
            "farm_cust_point_shop",
            "farm_cust_point_stock",
            "farm_cust_point_exchange",
            "farm_cust_point_data",
            "farm_cust_member_anaylyze",
            "farm_cust_merchant_count",
            "farm_cust_merchant_settle",
            "farm_cust_member_settle_config",
            "farm_cust_member_goods_unit",
            "farm_cust_member_coupon_manager",
            "farm_cust_member_coupon_giftpacks",
            "farm_cust_member_gift_manager",
            "farm_cust_member_card_using",
            "farm_cust_member_card_settle_report",
            "farm_cust_member_coupon_count_report",
            "farm_cust_member_shoppingcart_config",
            "farm_cust_member_shoppingcart_order",
            "farm_cust_finance_statements",
            "farm_cust_finance_statements_cfg",
            "farm_cust_finance_bill",
            "farm_cust_finance_refund",
            "farm_cust_sk_pay_settle",
            "farm_cust_finance_booth_rent",
            "farm_cust_finance_iae",
            "farm_cust_finance_report",
            "farm_cust_property_manager",
            "farm_cust_property_bill",
            "farm_cust_property_booth_bill",
            "farm_cust_property_cost");
    }
    
    private StrList bossPubMenus()
    {
        return StrList.fromArray("farm_boss_home");
    }
    
    private StrList bossSysMenus()
    {
        
        return StrList.fromArray("farm_boss_sys_user", "farm_boss_sys_user_log");
    }
    
    private StrList bossImplementorMenus()
    {
        return StrList.fromArray("farm_boss_company",
            "farm_boss_maintain_logistics",
            "farm_boss_maintain_scale",
            "farm_boss_maintain_infoScreenToken",
            "farm_boss_maintain_pubApiCode",
            "farm_boss_maintain_goods",
            "farm_boss_maintain_detection",
            "farm_boss_maintain_category",
            "farm_boss_maintain_hardwareCode",
            "farm_boss_maintain_areatype",
            "farm_boss_maintain_payNumber",
            "farm_boss_maintain_unit",
            "farm_boss_maintain_appManage",
            "farm_boss_maintain_device",
            "farm_boss_maintain_file",
            "farm_boss_maintain_resetPwd",
            "farm_boss_maintain_custVersionInfo",
            "farm_boss_maintain_demoData",
            "farm_boss_maintain_directAcquisition",
            "farm_boss_watch_abnormal",
            "farm_boss_sys_warning_data",
            "farm_boss_sys_warning_task",
            "farm_boss_sys_warning_remove");
    }
    
    private StrList bossOperatorMenus()
    {
        return StrList.fromArray("farm_boss_operate_adv",
            "farm_boss_operate_complaints",
            "farm_boss_operate_datacube",
            "farm_boss_operate_findtool",
            "farm_boss_operate_marketdetail");
    }
    
    private StrList initBossMenus()
    {
        //@formatter:off
        List<MenuEntity> menulist = new MenuEntityBuilder(FarmConstant.DOMAIN, C.BOSS_WEB)
            .menu("farm_boss_home", "首页")
            .menu("farm_boss_company", "公司管理")
            .model("farm_boss_maintain", "维护")
               .sub()
               .menu("farm_boss_maintain_logistics", "物流公司")
               .menu("farm_boss_maintain_scale", "秤升级")
               .menu("farm_boss_maintain_infoScreenToken", "信息屏秘钥管理")
               .menu("farm_boss_maintain_pubApiCode", "公开接口秘钥管理")
               .menu("farm_boss_maintain_goods", "商品信息")
               .menu("farm_boss_maintain_detection", "检测项目")
               .menu("farm_boss_maintain_category", "类目管理")
               .menu("farm_boss_maintain_hardwareCode", "硬件编码")
               .menu("farm_boss_maintain_areatype", "区域管理")
               .menu("farm_boss_maintain_payNumber", "支付账号")
               .menu("farm_boss_maintain_unit", "单位管理")
               .menu("farm_boss_maintain_appManage", "app管理")
               .menu("farm_boss_maintain_device", "终端管理")
               .menu("farm_boss_maintain_file", "文件管理")
               .menu("farm_boss_maintain_resetPwd", "重置密码")
               .menu("farm_boss_maintain_custVersionInfo", "cust版本维护")
               .menu("farm_boss_maintain_demoData", "数据模拟")
               .menu("farm_boss_maintain_directAcquisition", "省直采管理")
               .done()
            .model("farm_boss_watch", "市场监管")
               .sub()
               .menu("farm_boss_watch_abnormal", "异常预警")
               .done()
            .model("farm_boss_operate", "运营")
               .sub()
               .menu("farm_boss_operate_adv", "广告管理")
               .menu("farm_boss_operate_complaints", "客诉管理")
               .menu("farm_boss_operate_datacube", "数据总览")
               .menu("farm_boss_operate_findtool", "查找工具")
               .menu("farm_boss_operate_marketdetail", "市场明细")
               .menu("farm_boss_operate_implGrade", "实施评分")
               .done()
            .model("farm_boss_sys", "系统")
               .sub()
               .menu("farm_boss_sys_user", "员工管理")
               .menu("farm_boss_sys_user_log", "操作日志")
               .done()
            .model("farm_boss_sys_command_center", "指挥中心")
               .sub()
               .menu("farm_boss_sys_warning_data", "数据总览")
               .menu("farm_boss_sys_warning_task", "预警类任务")
               .menu("farm_boss_sys_warning_remove", "排查类任务")
               .done()
           .build();
        //@formatter:on
        menuDao.putAll(menulist);
        StrList r = new StrList();
        r.addAll(CollectionUtil.keyList(menulist));
        return r;
    }
    
    private StrList initCustMenus()
    {
        //@formatter:off
        List<MenuEntity> menulist = new MenuEntityBuilder(FarmConstant.DOMAIN, C.CUST_WEB)
            .menu("farm_cust_home", "首页", M.BASE)
            .model("farm_cust_market", "市场管理")
               .sub()
               .menu("farm_cust_market_manager", "市场管理", M.BASE)
               .menu("farm_cust_market_merchant", "商户管理", M.BASE)
               .menu("farm_cust_market_goods", "商品管理", M.BASE)
               .menu("farm_cust_market_stall", "摊位管理", M.BASE)
               .menu("farm_cust_market_contract", "合同管理", M.BASE)
               .menu("farm_cust_emploee_manager", "人事管理", M.BASE)
               .menu("farm_cust_emploee_training", "培训记录", M.BASE)
               .menu("farm_cust_truck_enter", "车辆入场", M.TRUCK)
               .menu("farm_cust_truck_out", "车辆出场", M.TRUCK)
               .menu("farm_cust_truck_weighbridge", "车辆称重统计", M.TRUCK)
               .menu("farm_cust_market_area", "区域管理", M.BASE)
               .done()
            .model("farm_cust_inv", "进销存")
               .sub()
               .menu("farm_cust_inv_reservation", "入场预约", M.INVENTORY)
               .menu("farm_cust_inv_purchase", "商品采购", M.INVENTORY)
               .menu("farm_cust_inv_sale", "商品销售", M.INVENTORY)
               .menu("farm_cust_inv_customer", "客户账单", M.INVENTORY)
               .menu("farm_cust_inv_goods_inventory", "商品库存", M.INVENTORY)
               .menu("farm_cust_inv_goods_check", "商品盘点", M.INVENTORY)
               .menu("farm_cust_inv_trade", "交易订单", M.INVENTORY)
               .menu("farm_cust_inv_report", "统计报表", M.INVENTORY)
               .done()
            .model("farm_cust_foodsafe", "食安管理")
               .sub()
               .menu("farm_cust_foodsafe_detection", "检测信息", M.BASE)
               .menu("farm_cust_foodsafe_detection_handle", "检测处理", M.BASE)
               .menu("farm_cust_foodsafe_book", "溯源管理", M.BASE)
               .menu("farm_cust_market_vendor", "供应商管理", M.BASE)
               .done()
            .model("farm_cust_device", "设备管理")
               .sub()
               .menu("farm_cust_device_all", "设备汇总", M.BASE)
               .menu("farm_cust_device_scale", "电子秤管理", M.BASE)
               .menu("farm_cust_device_merchantScreen", "商户屏管理", M.BASE)
               .menu("farm_cust_device_twosidedScreen", "双面屏称管理", M.BASE)
               .menu("farm_cust_device_queryScreen", "查询屏管理", M.BASE)
               .menu("farm_cust_device_infoScreen", "信息大屏管理", M.BASE)
               .menu("farm_cust_device_advScreen", "广告屏管理", M.BASE)
               .menu("farm_cust_device_multiMediaScreen", "商户多媒体屏管理", M.BASE)
               .menu("farm_cust_device_fairScales", "公平秤管理", M.BASE)
               .menu("farm_cust_device_monitor", "监控管理", M.BASE)
               .menu("farm_cust_device_custflow", "客流仪管理", M.BASE)
               .menu("farm_cust_device_merchantVoice", "音响设备管理", M.CUSTOM)
               .menu("farm_cust_device_bluetoothLock", "蓝牙锁设备管理", M.SHOPPINGCART)
               .menu("farm_cust_device_gateWay", "购物车网关设备管理", M.SHOPPINGCART)
               .done()
            .model("farm_cust_tradeOrder", "订单管理")
               .sub()
               .menu("farm_cust_market_trade", "交易订单", M.BASE)
               .menu("farm_cust_market_trade_mall", "线上订单", M.OLDMALL)
               .menu("farm_cust_market_trade_scale", "过秤订单", M.BASE)
               .menu("farm_cust_market_trade_ori", "交易明细", M.BASE)
               .menu("farm_cust_market_trade_identitfy", "识别明细", M.BASE)
               .menu("farm_cust_finance_statements", "电子支付交易结算", M.LIQUIDATION)
               .menu("farm_cust_finance_statements_cfg", "电子支付结算配置", M.LIQUIDATION)
               .menu("farm_cust_finance_bill", "电子支付结算对账", M.LIQUIDATION)
               .menu("farm_cust_finance_refund", "退款", M.OLDMALL)
               .menu("farm_cust_sk_pay_settle", "餐卡支付交易结算", M.CUSTOM)
               .done()
            .model("farm_cust_info", "信息公示")
               .sub()
               .menu("farm_cust_device_program", "素材管理", M.BASE)
               .menu("farm_cust_safe_lifesaving", "消防地图", M.BASE)
               .menu("farm_cust_market_map", "大屏图显", M.BASE)
               .menu("farm_cust_market_honor", "市场荣誉", M.BASE)
               .menu("farm_cust_market_play_config", "播放规则", M.BASE)
               .done()
            .model("farm_cust_credit", "信用管理")
               .sub()
               .menu("farm_cust_credit_evaluation_handle", "考评处理", M.BASE)
               .menu("farm_cust_credit_evaluation_list", "考评列表", M.BASE)
               .menu("farm_cust_credit_evaluation_template", "考评模板", M.BASE)
               .menu("farm_cust_credit_evaluation_task", "考评任务", M.BASE)
               .menu("farm_cust_market_merchant_deposit", "保证金管理", M.BASE)
               .menu("farm_cust_credit_comment", "商户评价", M.BASE)
               .menu("farm_cust_credit_honor", "商户荣誉", M.BASE)
               .menu("farm_cust_market_suggestion", "投诉及建议", M.BASE)
               .done()
            .model("farm_cust_member", "会员管理")
               .sub()
               .menu("farm_cust_member_config", "会员配置", M.MEMBER)
               .menu("farm_cust_member_query", "普通会员查询", M.MEMBER)
               .menu("farm_cust_member_consume", "普通会员消费明细", M.MEMBER)
               .menu("farm_cust_card_mamager", "储值卡管理", M.MEMBER)
               .menu("farm_cust_recharge_activity", "充值活动", M.MEMBER)
               .menu("farm_cust_account_manger", "储值会员管理", M.MEMBER)
               .menu("farm_cust_accout_operate", "储值卡消费明细", M.MEMBER)
               .menu("farm_cust_point_shop", "积分商城", M.MEMBER)
               .menu("farm_cust_point_stock", "商品库存", M.MEMBER)
               .menu("farm_cust_point_exchange","商品兑换", M.MEMBER)
               .menu("farm_cust_point_data", "积分明细", M.MEMBER)
               .menu("farm_cust_member_anaylyze", "会员分析", M.MEMBER)
               .menu("farm_cust_merchant_count", "对账统计", M.MEMBER)
               .menu("farm_cust_merchant_settle", "会员卡交易结算", M.MEMBER)
               .menu("farm_cust_member_settle_config", "会员卡结算配置", M.MEMBER)
               .menu("farm_cust_member_goods_unit", "单位配置", M.MEMBER)
               .menu("farm_cust_member_coupon_manager", "优惠券管理", M.MEMBER)
               .menu("farm_cust_member_coupon_giftpacks", "优惠券礼包", M.MEMBER)
               .menu("farm_cust_member_gift_manager", "礼品券管理", M.MEMBER)
               .menu("farm_cust_member_card_using", "卡券使用查询", M.MEMBER)
               .menu("farm_cust_member_card_settle_report", "卡券结算报表", M.MEMBER)
               .menu("farm_cust_member_coupon_count_report", "优惠券统计报表", M.MEMBER)
               .menu("farm_cust_member_shoppingcart_config", "购物车配置", M.SHOPPINGCART)
               .menu("farm_cust_member_shoppingcart_order", "购物车订单", M.SHOPPINGCART)
               .done()
            .model("farm_cust_mall", "商城运营")
               .sub()
               .menu("farm_cust_mall_1", "满减活动", M.OLDMALL)
               .menu("farm_cust_mall_2", "折扣活动", M.OLDMALL)
               .menu("farm_cust_mall_3", "红包", M.OLDMALL)
               .menu("farm_cust_mall_4", "花样菜篮", M.OLDMALL)
               .menu("farm_cust_mall_5", "市场商城", M.OLDMALL)
               .menu("farm_cust_mall_6", "邮费管理", M.OLDMALL)
               .menu("farm_cust_mall_7", "物流管理", M.OLDMALL)
               .menu("farm_cust_mall_8", "成长折扣", M.OLDMALL)
               .menu("farm_cust_mall_9", "积分管理", M.OLDMALL)
               .done()
            .model("farm_cust_property", "物业管理")
               .sub()
               .menu("farm_cust_property_manager", "物业总览", M.HYDROPOWER)
               .menu("farm_cust_finance_booth_rent", "摊位租金表", M.BASE)
               .menu("farm_cust_property_bill", "商户账单", M.BASE)
               .menu("farm_cust_property_booth_bill", "摊位账单", M.BASE)
               .menu("farm_cust_property_cost", "水电管理", M.HYDROPOWER)
               .menu("farm_cust_safe_repair", "报修管理", M.BASE)
               .menu("farm_cust_safe_patrol", "巡检管理", M.BASE)
               .menu("farm_cust_safe_patrol_count", "巡检数据统计", M.BASE)
               .menu("farm_cust_finance_iae", "资金管理", M.BASE)
               .menu("farm_cust_safe_fireFightingFacility", "消防设施", M.BASE)
               .menu("farm_cust_safe_fireFightingTrain", "消防培训", M.BASE)
               .menu("farm_cust_market_plasticBag", "塑料袋集中购销", M.BASE)
               .menu("farm_cust_market_pay_config", "商户APP支付配置", M.BASE)
               .menu("farm_cust_market_rubbishDecrement", "垃圾减量", M.BASE)
               .done()
            .model("farm_cust_report", "统计分析")
               .sub()
               .menu("farm_cust_finance_report", "报表", M.BASE)
               .menu("farm_cust_report_statistics", "数据分析", M.BASE)
               .menu("farm_cust_report_trade", "交易分析", M.BASE)
               .menu("farm_cust_report_scale", "过秤报表", M.BASE)
               .menu("farm_cust_report_user", "客群分析", M.BASE)
               .done()
            .model("farm_cust_supervision", "监控预警")
               .sub()
               .menu("farm_cust_supervison_monitor_warning", "监控监管", M.WARNING)
               .menu("farm_cust_supervison_device_warning", "设备异常预警", M.WARNING)
               .menu("farm_cust_supervison_credentials_warning", "证照过期预警", M.WARNING)
               .menu("farm_cust_supervison_price_warning", "价格预警", M.WARNING)
               .menu("farm_cust_supervison_conduct_warning", "商户行为规范", M.WARNING)
               .menu("farm_cust_area_price_warning", "区域总价预警", M.WARNING)
               .done()
            .model("farm_cust_old", "旧发布")
               .sub()
               .menu("farm_cust_old1", "公示屏", M.LEGACY)
               .menu("farm_cust_old2", "查询屏", M.LEGACY)
               .menu("farm_cust_old4", "电子秤", M.LEGACY)
               .menu("farm_cust_old5", "广告屏", M.LEGACY)
               .menu("farm_cust_old6", "多媒体屏", M.LEGACY)
               .done()
            .model("farm_cust_sys", "系统管理")
               .sub()
               .menu("farm_cust_sys_company", "公司信息", M.BASE)
               .menu("farm_cust_sys_user", "员工管理", M.BASE)
               .menu("farm_cust_sys_role", "角色管理", M.BASE)
               .menu("farm_cust_sys_wx", "微信授权", M.BASE)
               .menu("farm_cust_sys_advice", "意见反馈", M.BASE)
               .menu("farm_cust_sys_payNumber", "支付账户", M.BASE)
               .done()
            .model("farm_cust_scale", "计量管理")
               .sub()
               .menu("farm_cust_scale_warning", "预警", M.MEASUREMENT)
               .menu("farm_cust_scale_query", "在线查询", M.MEASUREMENT)
               .menu("farm_cust_scale_calibration", "标定查询", M.MEASUREMENT)
               .menu("farm_cust_scale_missstand", "失准查询", M.MEASUREMENT)
               .menu("farm_cust_scale_teardown", "拆机查询", M.MEASUREMENT)
               .menu("farm_cust_scale_lock", "锁机查询", M.MEASUREMENT)
               .menu("farm_cust_scale_report", "过秤报表", M.MEASUREMENT)
               .menu("farm_cust_scale_maintenanceRecord", "维修记录查询", M.MEASUREMENT)
               .menu("farm_cust_scale_base_config", "基础配置", M.MEASUREMENT)
               .done()
            .model("farm_cust_watch_task", "监管任务")
               .sub()
               .menu("farm_cust_watch_task_1", "任务协同", M.WATCH)
               .menu("farm_cust_watch_task_2", "预警通知", M.WATCH)
               .menu("farm_cust_watch_task_3", "督查交办", M.WATCH)
               .done()
           .build();
        //@formatter:on
        menuDao.putAll(menulist);
        StrList r = new StrList();
        r.addAll(CollectionUtil.keyList(menulist));
        return r;
    }
    
    private void addLoginCheck(String app, String func)
    {
        AppLoginCheckEntity entity = new AppLoginCheckEntity();
        entity.setPkey(app);
        entity.setDomainid(FarmConstant.DOMAIN);
        entity.setFuncKey(func);
        appLoginCheckDao.put(entity);
    }
    
    private static class StrList extends ArrayList<String>
    {
        /**
         * 注释内容
         */
        private static final long serialVersionUID = 1L;
        
        public static StrList fromArray(String... a)
        {
            StrList l = new StrList();
            l.addAll(Arrays.asList(a));
            return l;
        }
    }
    
    private void addRoleMenu(String role, StrList... menuArray)
    {
        roleMenuDao.removeAll(roleMenuDao.select().eq("ownerid", role).exec());
        List<RoleMenuEntity> list = new ArrayList<>();
        int o = 0;
        for (List<String> menus : menuArray)
        {
            for (String menu : menus)
            {
                RoleMenuEntity entity = new RoleMenuEntity();
                entity.setPkey(role + "_menu_" + o);
                entity.setOwnerid(role);
                entity.setMenu(menu);
                entity.setDomainid(FarmConstant.DOMAIN);
                entity.setAccept(true);
                list.add(entity);
                o++;
            }
        }
        roleMenuDao.putAll(list);
    }
    
    private void addRoleAcl(String role, List<String> funcs)
    {
        roleAccessDao.removeAll(roleAccessDao.select().eq("ownerid", role).exec());
        List<RoleAccessInstance> list = new ArrayList<>();
        int o = 0;
        for (String func : funcs)
        {
            RoleAccessInstance entity = new RoleAccessInstance();
            entity.setPkey(role + "_func_" + o);
            entity.setOwnerid(role);
            entity.setFuncKey(func);
            entity.setDomainid(FarmConstant.DOMAIN);
            entity.setAccept(true);
            list.add(entity);
            o++;
        }
        roleAccessDao.putAll(list);
    }
    
    private void addModel(String model, String name, ModelStatus status, Integer sort, boolean defEnable,
        boolean defShowMenu)
    {
        ModelEntity entity = new ModelEntity();
        entity.setPkey(model);
        entity.setName(name);
        entity.setDomainid(FarmConstant.DOMAIN);
        entity.setStatus(status);
        entity.setDefEnable(defEnable);
        entity.setSort(sort);
        entity.setDefShowMenu(defShowMenu);
        modelDao.put(entity);
    }
    
    private void addFunc(String func, String name, String group)
    {
        AppFunctionEntity entity = new AppFunctionEntity();
        entity.setPkey(func);
        entity.setName(name);
        entity.setGroup(group);
        entity.setDomainid(FarmConstant.DOMAIN);
        appFunctionCache.put(entity);
    }
    
    /**
     * 删除权限和关联关系
     * @param func
     */
    private void delFunc(String func)
    {
        List<RoleAccessInstance> list = roleAccessDao.select().eq("funcKey", func).exec();
        roleAccessDao.removeAll(list);
        appFunctionCache.removeById(func);
    }
    
    private void addRole(String role, String name, String group)
    {
        AppRoleEntity entity = new AppRoleEntity();
        entity.setPkey(role);
        entity.setName(name);
        entity.setGroup(group);
        entity.setDomainid(FarmConstant.DOMAIN);
        entity.setEnable(true);
        appRoleCache.put(entity);
    }
    
}
