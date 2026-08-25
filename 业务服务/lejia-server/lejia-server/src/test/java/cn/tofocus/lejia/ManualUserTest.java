//package cn.tofocus.lejia;
//
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import cn.tofocus.common.util.date.DateUtil;
//import cn.tofocus.core.Result;
//import cn.tofocus.core.data.NamedBean;
//import cn.tofocus.core.security.AccessList;
//import cn.tofocus.core.security.AccessScopeType;
//import cn.tofocus.core.security.AuthenticationContext;
//import cn.tofocus.core.security.SecurityContextUtil;
//import cn.tofocus.core.user.SysFunctionEnum;
//import cn.tofocus.db.redis.id.RedisCounter;
//import cn.tofocus.domain.user.def.RoleInstanceDTO;
//import cn.tofocus.lejia.config.SysConfig.Role;
//import cn.tofocus.lejia.Constant;
//import cn.tofocus.lejia.dao.market.MktDrawConfDao;
//import cn.tofocus.lejia.dao.market.MktMemberDao;
//import cn.tofocus.lejia.domain.market.OrderManager;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@SpringBootTest
//public class ManualUserTest
//{
////    @Autowired
////    private FunctionApi functionApi;
////    
////    @Autowired
////    private AdminApi adminApi;
//    
//    @Autowired
//    private RedisCounter redisCounter;
//    
//    @Autowired
//    private OrderManager orderManager;
//    
//    @Test
//    public void test1()
//    {
//        
////        List<AppFunction> fetchResult = functionApi.listAppFuntion(10, "lj_conpany_manager", "lejia").fetchResult();
////        Iterator<AppFunction> iterator = fetchResult.iterator();
////        while (iterator.hasNext())
////        {
////            AppFunction af = iterator.next();
////            log.info("af: {}", af.toString());
////        }
//        
//    }
//    
//    @Autowired
//    private SecurityContextUtil securityContextUtil;
//    
//    @Test
//    public void test2()
//    {
//        
//        securityContextUtil.runAsUser("admin", "123456");
//        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
//        Long pkey = context.getUserkey();
//        System.out.println(pkey);
//        
//        Map<String, AccessList> map = context.getAuthorities();
//        NamedBean currentDomain = context.getCurrentDomain();
//        System.out.println("currentDomain: " + currentDomain);
//        AccessList acl = map.get(SysFunctionEnum.domainAdmin.name());
//        System.out.println("acl: " + acl);
//        boolean b = acl.canAccessDomain((String)currentDomain.getPkey());
//        System.out.println("b: " + b);
//    }
//    
//    @Autowired
//    private DomainAdminApi domainAdminApi;
//    
//    @Autowired
//    private RoleApiV3 roleApiV3;
//    
//    @Test
//    public void test3()
//    {
//        //        ExampleQueryPageParam<SimpleRole> parameter = new ExampleQueryPageParam<>(0, 1000);
//        //        QueryPageParam param = new QueryPageParam(parameter);
//        //        param.addMatch("domainid", "lejia");
//        //        PageResult<RoleDetail> p = roleApiV3.queryAppRole(param).fetchResult();
//        //        List<RoleDetail> content = p.getContent();
//        //        System.out.println(content.get(0).getName());
//        //        Result<UserRoleGroup> role = domainAdminApi.listUserDomainRole(10368L);
//        //        String name = role.getResult().getRoles().get(0).getName();
//        //        System.out.println(name);
//        //        String pkey = role.getResult().getRoles().get(0).getPkey();
//        //        System.out.println("pkey: " + pkey);
//    }
//    
//    @Test
//    public void test4()
//    {
//        RoleInstanceDTO result =
//            adminApi.addAppRole2User(10343L, "lj_market_manager", -1, AccessScopeType.dept, "lejia_mkt_0014", false)
//                .fetchResult();
//        log.info("result" + result);
//    }
//    
//    @Autowired
//    private MktDrawConfDao drawConfDao;
//    
//    @Test
//    public void test6()
//    {
//        securityContextUtil.runAsUser("lejia-web", "bu7rhGjvkb", "system", "123456");
//        int pkey = 10626;
//        Result<List<RoleInstanceDTO>> listAppRole2User = adminApi.listAppRole2User(Long.valueOf(pkey));
//        for (RoleInstanceDTO role : listAppRole2User.getResult())
//        {
//            if (Role.COMPANY_HEAD.equals(role.getPkey())) continue;
//            Result<Boolean> user = adminApi.delAppRole2User(Long.valueOf(pkey), role.getPkey());
//            log.info("user: {}", user);
//        }
//        Boolean result = adminApi.delUser(Long.valueOf(pkey)).fetchResult();
//        log.info("result: {}", result);
//    }
//    
//    @Autowired
//    private MktMemberDao memberDao;
//    
//    @Test
//    public void test7()
//    {
//        String sequence = "test" + DateUtil.formatDate(new Date(), "yyyyMMdd");
//        
//        Long id = redisCounter.increment(Constant.DomainId, Constant.App.SERVER, sequence);
//        System.out.println(id);
//        if (id == 1)
//        {
//            long a = (DateUtil.atEndOfToday().getTime() - System.currentTimeMillis()) / 1000;
//            System.out.println(a);
//            redisCounter.expire(Constant.DomainId, Constant.App.SERVER, sequence, a);
//        }
//        for (int i = 0; i < 2000; i++)
//        {
//            id = redisCounter.increment(Constant.DomainId, Constant.App.SERVER, sequence);
//            
//        }
//        System.out.println(id);
//    }
//    
//    @Test
//    public void test8()
//    {
////        orderManager.printOrder(pkey,false);
//    }
//    
//    public static void main(String[] args)
//    {
//        Long number = 10L;
//        int lastThreeDigits = (int)(number % 1000L);
//        System.out.println(lastThreeDigits);
//    }
//}
