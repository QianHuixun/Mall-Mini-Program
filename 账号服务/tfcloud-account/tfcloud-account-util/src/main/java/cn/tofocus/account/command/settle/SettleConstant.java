package cn.tofocus.account.command.settle;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SettleConstant
{
    
    public static final String DOMAIN = "settle-saas";
    
    @UtilityClass
    public static class C
    {
        
        public static final String WEB = "web";
        
    }
    
    @UtilityClass
    public static class F
    {
        public static final String WEB_LOGIN = "settle_web_login";
        
    }
    
    @UtilityClass
    public static class R
    {
        
        /**
         * 角色组名称
         */
        public static final String GROUP_NAME = "settle_role";
        
        /**
         * 公司负责人
         */
        public static final String COMPANY_MANAGER = "settle_company_manager";
        
        /**
         * 市场管理员
         */
        public static final String MARKET_MANAGER = "settle_market_manager";
        
        /**
         * 系统管理员
         */
        public static final String ADMIN = "settle_admin";
        
    }
    
    @UtilityClass
    public static class SysF
    {
        public static final String MANAGER_ROLE = "managerRole";
        
        public static final String MANAGER_USER = "managerUser";
        
        public static final String MANAGER_ORG = "managerOrg";
        
        public static final String DOMAIN_ADMIN = "domainAdmin";
    }
}
