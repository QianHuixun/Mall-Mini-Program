package cn.tofocus.account.command.watch;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WatchConstant
{
    public static final String DOMAIN = "watch";
    
    public static final String root = "watch_root";
    
    @UtilityClass
    public static class C
    {
        public static final String WEB = "watch-web";
        
        public static final String APP = "watch-app";
    }
    
    @UtilityClass
    public static class G
    {
        /**
         * 平台访问权限
         */
        public static final String ACCESS = "watch_access";
        
        /**
         * 日志功能
         */
        public static final String LOG = "watch_log";
        
        /**
         * 监管功能
         */
        public static final String WATCH = "watch_watch";
        
        /**
         * 操作权限
         */
        public static final String OPERATOR = "watch_operator";
    }
    
    @UtilityClass
    public static class R
    {
        /**
         * 负责人
         */
        public static final String ORG_OWNER = "watch_org_owner";
        
        /**
         * 管理员
         */
        public static final String ORG_MANAGER = "watch_org_manager";
        
        /**
         * 监管人员
         */
        public static final String WATCHER = "watch_watcher";
        
        /**
         * 督查管理员
         */
        public static final String SUPERVISE_MANAGER = "watch_supervise_manager";
        
        /**
         * 督查员
         */
        public static final String SUPERVISE = "watch_supervise";
        
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
         * 登录监管平台
         */
        public static final String LOGIN_WEB = "watch_login_web";
        
        /**
         * 登录监管APP
         */
        public static final String LOGIN_APP = "watch_login_app";
        
        /**
         * 预警监管
         */
        public static final String M_MONITOR = "watch_m_monitor";
        
        /**
         * 食安监管
         */
        public static final String M_FOODSAFE = "watch_m_foodsafe";
        
        /**
         * 档案监管
         */
        public static final String M_DOCUMENT = "watch_m_document";
        
        /**
         * 督查管理(执行和交办单)
         */
        public static final String M_SUPERVISE = "watch_m_supervise";
        
        /**
         * 督查管理(配置和报表)
         */
        public static final String M_SUPERVISE_MANAGER = "watch_m_supervise_manager";
        
        /**
         * 市场考核评分
         */
        public static final String M_ASSESSMENT_SCORING = "watch_m_assessment_scoring";
        
        /**
         * 市场考核管理
         */
        public static final String M_ASSESSMENT_MANAGE = "watch_m_assessment_manage";
        
        /**
         * 任务协同
         */
        public static final String M_TASK = "watch_m_task";
        
        /**
         * 统计
         */
        public static final String M_REPORT = "watch_m_report";
        
        /**
         * 大数据
         */
        public static final String M_DATACUBE = "watch_m_datacube";
        
        /**
         * 区域市场总览
         */
        public static final String M_AREA_MARKET = "watch_m_areamarket";
        
        /**
         * 资产管理
         */
        public static final String M_ASSET = "watch_m_asset";
        
        /**
         * 可导出监管数据
         */
        public static final String OPER_EXPORT = "watch_oper_export";
        
        /**
         * 可查看监管数据详情
         */
        public static final String OPER_VIEW_DETAIL = "watch_oper_view_detail";
        
        /**
         * 可导入监管数据
         */
        public static final String OPER_IMPORT = "watch_oper_import";
        
        /**
         * 可删除监管数据
         */
        public static final String OPER_DEL = "watch_oper_del";
        
        /**
         * 可设置监管配置
         */
        public static final String OPER_CONFIG = "watch_oper_config";
        
        /**
         * 可新增监管数据
         */
        public static final String OPER_ADD = "watch_oper_add";
        
        /**
         * 可通知市场
         */
        public static final String OPER_NOTIFY = "watch_oper_notify";
        
        /**
         * 查看操作日志
         */
        public static final String VIEW_LOG = "watch_view_log";
        
    }
    
    @UtilityClass
    public static class DelF
    {
        
        /**
         * 登录督查APP
         */
        public static final String LOGIN_APP_SUPERVISE = "watch_login_app_supervise";
        
        /**
         * 机构管理
         */
        public static final String ORG_MANAGER = "watch_org_manager";
        
        /**
         * 机构删除
         */
        public static final String ORG_DEL = "watch_org_del";
        
        /**
         * 账号查询
         */
        public static final String USER_QUERY = "watch_user_query";
        
        /**
         * 账号管理
         */
        public static final String USER_MANAGER = "watch_user_manager";
        
        /**
         * 账号删除
         */
        public static final String USER_DEL = "watch_user_del";
        
        /**
         * 角色管理
         */
        public static final String ROLE_MANAGER = "watch_role_manager";
        
        /**
         * 信用监管
         */
        public static final String M_CREDIT = "watch_m_credit";
        
        /**
         * 投诉监管
         */
        public static final String M_COMPLAINT = "watch_m_complaint";
        
        /**
         * 管理处罚
         */
        public static final String M_PUNISHMENT = "watch_m_punishment";
        
        /**
         * 农贸宝典
         */
        public static final String M_REFERENCE = "watch_m_reference";
        
        /**
         * 价格监管
         */
        public static final String M_PRICE = "watch_m_price";
    }
}
