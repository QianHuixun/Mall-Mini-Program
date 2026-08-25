package cn.tofocus.account.command.watch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cn.tofocus.account.command.BaseCommands;
import cn.tofocus.account.command.bean.MenuEntityBuilder;
import cn.tofocus.account.command.watch.WatchConstant.C;
import cn.tofocus.account.command.watch.WatchConstant.DelF;
import cn.tofocus.account.command.watch.WatchConstant.F;
import cn.tofocus.account.command.watch.WatchConstant.G;
import cn.tofocus.account.command.watch.WatchConstant.R;
import cn.tofocus.account.command.watch.WatchConstant.SysF;
import cn.tofocus.account.db.cache.role.AppFunctionCache;
import cn.tofocus.account.db.cache.role.AppRoleCache;
import cn.tofocus.account.db.cache.role.RoleAclCache;
import cn.tofocus.account.db.dao.application.AppLoginCheckDao;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.account.db.dao.role.AppFunctionGroupDao;
import cn.tofocus.account.db.entity.application.AppLoginCheckEntity;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.account.db.entity.role.AppFunctionGroupEntity;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.account.db.entity.role.RoleAccessInstance;

@ShellComponent
@ShellCommandGroup("监管权限迁移")
public class WatchCommands extends BaseCommands
{
    @Autowired
    private AppRoleCache appRoleCache;
    
    @Autowired
    private AppFunctionCache appFunctionCache;
    
    @Autowired
    private AppFunctionGroupDao appFunctionGroupDao;
    
    @Autowired
    private RoleAclCache roleAclCache;
    
    @Autowired
    private AppLoginCheckDao appLoginCheckDao;
    
    @Autowired
    private MenuDao menuDao;
    
    @ShellMethod("监管权限迁移")
    public String updateWatch()
    {
        //重置系统角色
        addRole(R.ORG_OWNER, "负责人", WatchConstant.root);
        addRole(R.ORG_MANAGER, "管理员", WatchConstant.root);
        addRole(R.WATCHER, "监管人员", WatchConstant.root);
        addRole(R.SUPERVISE_MANAGER, "督查管理员", WatchConstant.root);
        addRole(R.SUPERVISE, "督查员", WatchConstant.root);
        
        //权限组
        int sort = 0;
        addFuncGroup(G.ACCESS, "平台访问权限", WatchConstant.root, sort++);
        addFuncGroup(G.WATCH, "监管功能", WatchConstant.root, sort++);
        addFuncGroup(G.OPERATOR, "操作权限", WatchConstant.root, sort++);
        addFuncGroup(G.LOG, "日志功能", WatchConstant.root, sort++);
        
        //权限迁移
        sort = 0;
        addFunc(F.LOGIN_WEB, "登录监管平台", G.ACCESS, WatchConstant.root, sort++);
        addFunc(F.LOGIN_APP, "登录监管APP", G.ACCESS, WatchConstant.root, sort++);
        addFunc(F.M_MONITOR, "预警监管", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_FOODSAFE, "食安监管", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_DOCUMENT, "档案监管", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_ASSET, "资产管理", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_SUPERVISE, "督查管理(执行和交办单)", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_SUPERVISE_MANAGER, "督查管理(配置和报表)", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_ASSESSMENT_SCORING, "市场考核评分", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_ASSESSMENT_MANAGE, "市场考核管理", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_TASK, "任务协同", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_REPORT, "统计", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_DATACUBE, "大数据", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.M_AREA_MARKET, "区域市场总览", G.WATCH, WatchConstant.root, sort++);
        addFunc(F.OPER_EXPORT, "可导出监管数据", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.OPER_VIEW_DETAIL, "可查看监管数据详情", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.OPER_IMPORT, "可导入监管数据", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.OPER_DEL, "可删除监管数据", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.OPER_CONFIG, "可设置监管配置", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.OPER_ADD, "可新增监管数据", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.OPER_NOTIFY, "可通知市场", G.OPERATOR, WatchConstant.root, sort++);
        addFunc(F.VIEW_LOG, "查看操作日志", G.LOG, WatchConstant.root, sort++);
        
        //登录权限验证
        addLoginCheck(C.WEB, F.LOGIN_WEB);
        addLoginCheck(C.APP, F.LOGIN_APP);
        
        //初始化菜单
        menuDao.removeAll(menuDao.select().eq("domainid", WatchConstant.DOMAIN).exec());
        initWebMenus();
        initAppMenus();
        
        //初始化系统角色的权限
        addRoleAcl(R.WATCHER,
            Arrays.asList(F.LOGIN_WEB,
                F.LOGIN_APP,
                F.M_MONITOR,
                F.M_SUPERVISE_MANAGER,
                F.M_SUPERVISE,
                F.M_FOODSAFE,
                F.M_DOCUMENT,
                F.M_ASSESSMENT_SCORING,
                F.M_ASSESSMENT_MANAGE,
                F.M_TASK,
                F.M_REPORT,
                F.M_DATACUBE,
                F.M_AREA_MARKET,
                F.OPER_EXPORT,
                F.OPER_VIEW_DETAIL,
                F.OPER_IMPORT,
                F.OPER_DEL,
                F.OPER_ADD,
                F.OPER_CONFIG,
                F.OPER_NOTIFY));
        addRoleAcl(R.ORG_MANAGER,
            Arrays.asList(F.LOGIN_WEB,
                F.LOGIN_APP,
                F.M_MONITOR,
                F.M_SUPERVISE_MANAGER,
                F.M_SUPERVISE,
                F.M_FOODSAFE,
                F.M_DOCUMENT,
                F.M_ASSESSMENT_SCORING,
                F.M_ASSESSMENT_MANAGE,
                F.M_TASK,
                F.M_REPORT,
                F.M_DATACUBE,
                F.OPER_EXPORT,
                F.OPER_VIEW_DETAIL,
                F.OPER_IMPORT,
                F.OPER_DEL,
                F.OPER_ADD,
                F.OPER_CONFIG,
                F.OPER_NOTIFY,
                F.VIEW_LOG,
                F.M_AREA_MARKET,
                SysF.MANAGER_ORG,
                SysF.MANAGER_USER,
                SysF.MANAGER_ROLE));
        addRoleAcl(R.ORG_OWNER,
            Arrays.asList(F.LOGIN_WEB,
                F.LOGIN_APP,
                F.M_MONITOR,
                F.M_SUPERVISE_MANAGER,
                F.M_SUPERVISE,
                F.M_FOODSAFE,
                F.M_DOCUMENT,
                F.M_ASSESSMENT_SCORING,
                F.M_ASSESSMENT_MANAGE,
                F.M_TASK,
                F.M_REPORT,
                F.M_DATACUBE,
                F.OPER_EXPORT,
                F.OPER_VIEW_DETAIL,
                F.OPER_IMPORT,
                F.OPER_DEL,
                F.OPER_ADD,
                F.OPER_CONFIG,
                F.OPER_NOTIFY,
                F.VIEW_LOG,
                F.M_AREA_MARKET,
                SysF.MANAGER_ORG,
                SysF.MANAGER_USER,
                SysF.MANAGER_ROLE));
        addRoleAcl(R.SUPERVISE_MANAGER,
            Arrays.asList(F.LOGIN_WEB,
                F.LOGIN_APP,
                F.M_MONITOR,
                F.M_DOCUMENT,
                F.M_ASSESSMENT_SCORING,
                F.M_ASSESSMENT_MANAGE,
                F.M_SUPERVISE,
                F.M_SUPERVISE_MANAGER,
                F.OPER_VIEW_DETAIL));
        addRoleAcl(R.SUPERVISE,
            Arrays.asList(F.LOGIN_WEB,
                F.LOGIN_APP,
                F.M_MONITOR,
                F.M_DOCUMENT,
                F.M_ASSESSMENT_SCORING,
                F.M_SUPERVISE,
                F.OPER_VIEW_DETAIL));
        
        //删除旧权限
        delOldFunc();
        return "ok";
    }
    
    private void addFuncGroup(String pkey, String name, String group, int sort)
    {
        AppFunctionGroupEntity entity = new AppFunctionGroupEntity();
        entity.setPkey(pkey);
        entity.setName(name);
        entity.setGroup(group);
        entity.setSort(sort);
        entity.setDomainid(WatchConstant.DOMAIN);
        appFunctionGroupDao.put(entity);
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
        delFunc(DelF.LOGIN_APP_SUPERVISE);
        delFunc(DelF.ORG_MANAGER);
        delFunc(DelF.ORG_DEL);
        delFunc(DelF.USER_QUERY);
        delFunc(DelF.USER_MANAGER);
        delFunc(DelF.USER_DEL);
        delFunc(DelF.ROLE_MANAGER);
        delFunc(DelF.M_CREDIT);
        delFunc(DelF.M_COMPLAINT);
        delFunc(DelF.M_PUNISHMENT);
        delFunc(DelF.M_REFERENCE);
        delFunc(DelF.M_PRICE);
    }
    
    private void initWebMenus()
    {
        //@formatter:off
        List<MenuEntity> menulist = new MenuEntityBuilder(WatchConstant.DOMAIN, C.WEB)
            .menu("watch_home", "首页")
            .menu("watch_map", "智慧地图")
            .menu("watch_data_bulletin_board", "数据看板")
            .model("watch_manager", "预警监管")
               .sub()
               .menu("watch_merchantBehavior_1", "监控监管")
               .menu("watch_deviceAbnormal", "设备异常预警")
               .menu("watch_expiredLicense", "证照过期预警")
               .menu("watch_priceAbnormal", "价格预警")
               .menu("watch_passengerFlow", "客流预警")
               .menu("watch_evaluation", "商户行为规范")
               .menu("watch_patrol", "巡检监管")
               .done()
          .model("watch_scale", "计量监管")
               .sub()
               .menu("watch_scale_monitor_all", "异常预警")
               .menu("watch_scale_online_count", "在线统计")
               .menu("watch_scale_calibration_count", "标定统计")
               .menu("watch_scale_missstand_count", "失准统计")
               .menu("watch_scale_teardown_count", "拆机统计")
               .menu("watch_scale_lock_count", "锁机统计")
               .menu("watch_scale_abnormal_count", "使用异常统计")
               .menu("watch_scale_datacube_count", "计量大数据")
               .done()      
            .model("watch_foodsafe", "食安监管")
               .sub()
               .menu("watch_detection", "检测溯源总览")
               .menu("watch_detection_detail", "检测详情")
               .menu("watch_detection_report", "检测分析")
               .menu("watch_book_detail", "溯源详情")
               .menu("watch_book_report", "溯源分析")
               .done()
            .model("watch_document", "档案监管")
               .sub()
               .menu("watch_document_market", "市场档案")
               .menu("watch_document_merchant", "商户档案")
               .menu("watch_document_vendor", "供应商档案")
               .menu("watch_document_report", "档案分析报表")
               .done()
            .model("watch_asset_manage", "资产管理")
               .sub()
               .menu("watch_material_list", "物资目录")
               .menu("watch_document_merchant", "物资管理")
               .menu("watch_material_purchase", "物资采购管理")
               .menu("watch_material_report", "物资采购报表")
               .menu("watch_asset_list", "资产目录")
               .menu("watch_asset", "资产管理")
               .menu("watch_asset_inventory", "资产盘点")
               .menu("watch_asset_transfer", "资产调拨")
               .menu("watch_uom_manage", "单位配置")
               .done()
            .model("watch_supervise", "督查管理")
                  .sub()
                  .menu("watch_execute", "督查执行")
                  .menu("watch_handover", "交办单管理")
                  .menu("watch_supervise_cfg", "督查项目配置")
                  .menu("watch_supervise_report", "督查报表")
                  .done()
            .model("watch_assessment", "市场考核")
               .sub()
               .menu("watch_assessment_scoring", "考核评分")
               .menu("watch_assessment_handle", "考核处理")
               .menu("watch_assessment_task", "考核任务")
               .menu("watch_assessment_template", "考核模板")
               .menu("watch_assessment_report", "统计报表")
               .done()
           .model("watch_task", "任务协同")
               .sub()
               .menu("watch_task_manager", "任务管理")
               .menu("watch_coordination_manager", "协同管理")
               .menu("watch_task_type", "任务类型")
               .menu("watch_coordination_type", "协同类型")
               .menu("watch_task_todo", "由我执行")
               .done()
            .model("watch_report", "统计")
               .sub()
               .menu("watch_report_cpdlph", "菜品大类排行")
               .menu("watch_report_cpzlph", "菜品中类排行")
               .menu("watch_report_cpxlph", "菜品小类排行")
               .menu("watch_report_shslph", "商户销量排行")
               .menu("watch_report_cpdltj", "菜品大类统计")
               .menu("watch_report_cpzltj", "菜品中类统计")
               .menu("watch_report_cpxltj", "菜品小类统计")
               .menu("watch_report_shxltj", "商户销量统计")
               .menu("watch_report_scxltj", "市场销量统计")
               .done()
            .model("watch_datacube", "大数据")
               .sub()
               .menu("watch_datacube_overall", "大数据-总览")
               .menu("watch_datacube_market", "大数据-市场")
               .menu("watch_datacube_trade", "交易大数据")
               .menu("watch_datacube_price", "价格大数据")
               .done()
            .model("watch_sys", "系统")
               .sub()
               .menu("watch_sys_org", "机构管理")
               .menu("watch_sys_user", "账号管理")
               .menu("watch_sys_role", "角色管理")
               .menu("watch_sys_logs", "操作日志")
               .done()
           .menu("watch_area_market", "区域市场总览")
           .build();
        //@formatter:on
        menuDao.putAll(menulist);
    }
    
    private void initAppMenus()
    {
        //@formatter:off
        List<MenuEntity> menulist = new MenuEntityBuilder(WatchConstant.DOMAIN, C.APP)
            .menu("watch_app_home", "首页")
            .model("watch_app_manager", "预警监管")
               .sub()
               .menu("watch_app_detection", "溯源监管")
               .menu("watch_app_book", "检测监管")
               .menu("watch_app_passengerFlow", "客流预警")
               .menu("watch_app_expiredLicense", "证照预警")
               .menu("watch_app_merchantBehavior_1", "监控监管")
               .menu("watch_app_evaluation", "商户行为规范")
               .menu("watch_app_priceAbnormal", "价格预警")
               .menu("watch_app_deviceAbnormal", "设备预警")
               .done()
            .model("watch_app_data", "数据中心")
               .sub()
               .menu("watch_app_task", "任务协同")
               .menu("watch_app_monitor", "预警通知")
               .menu("watch_app_document", "档案信息")
               .done()
            .menu("watch_app_assign", "交办单管理")
            .menu("watch_app_assessment", "市场考核")
           .build();
        //@formatter:on
        menuDao.putAll(menulist);
    }
    
    private void addLoginCheck(String app, String func)
    {
        AppLoginCheckEntity entity = new AppLoginCheckEntity();
        entity.setPkey(app);
        entity.setDomainid(WatchConstant.DOMAIN);
        entity.setFuncKey(func);
        appLoginCheckDao.put(entity);
    }
    
    private void addRoleAcl(String role, List<String> funcs)
    {
        roleAclCache.removeAll(roleAclCache.select().eq("ownerid", role).exec());
        List<RoleAccessInstance> list = new ArrayList<>();
        int o = 0;
        for (String func : funcs)
        {
            RoleAccessInstance entity = new RoleAccessInstance();
            entity.setPkey(role + "_func_" + o);
            entity.setOwnerid(role);
            entity.setFuncKey(func);
            entity.setDomainid(WatchConstant.DOMAIN);
            entity.setAccept(true);
            list.add(entity);
            o++;
        }
        roleAclCache.putAll(list);
    }
    
    private void addFunc(String func, String name, String funcGroup, String group, Integer sort)
    {
        AppFunctionEntity entity = new AppFunctionEntity();
        entity.setPkey(func);
        entity.setName(name);
        entity.setGroup(group);
        entity.setFuncGroup(funcGroup);
        entity.setSort(sort);
        entity.setDomainid(WatchConstant.DOMAIN);
        appFunctionCache.put(entity);
    }
    
    /**
     * 删除权限和关联关系
     * @param func
     */
    private void delFunc(String func)
    {
        List<RoleAccessInstance> list = roleAclCache.select().eq("funcKey", func).exec();
        roleAclCache.removeAll(list);
        appFunctionCache.removeById(func);
    }
    
    private void addRole(String role, String name, String group)
    {
        AppRoleEntity entity = new AppRoleEntity();
        entity.setPkey(role);
        entity.setName(name);
        entity.setGroup(group);
        entity.setDomainid(WatchConstant.DOMAIN);
        entity.setEnable(true);
        appRoleCache.put(entity);
    }
    
}
