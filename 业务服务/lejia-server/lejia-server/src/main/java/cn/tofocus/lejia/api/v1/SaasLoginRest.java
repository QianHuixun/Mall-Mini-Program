package cn.tofocus.lejia.api.v1;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.notify.config.ISMSNotify;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.captcha.CaptchaErrType;
import cn.tofocus.core.enums.CaptchaPurpose;
import cn.tofocus.core.enums.CaptchaRouter;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.cache.CaptchaMap;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysCompanyDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1")
@RestController
public class SaasLoginRest
{
//    @Autowired
//    private SecurityContextUtil securityContextUtil;
    
    @Autowired
    private SysUserDao userDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private ISMSNotify smsNofity;
    
    @Autowired
    private CaptchaMap captchaMap;
    
    private long refreshTime = 0;
    
    @Autowired
    private SysCompanyDao companyDao;
    
    private CaptchaChecker checker;
    
    private static final int MAX_CHECK_ERR = 5;
    
    @Value("${small.zyysc.is.test:false}")
    private Boolean isTest;
    
    @PostConstruct
    private void init()
    {
        checker = new CaptchaChecker(refreshTime, captchaMap, smsNofity, MAX_CHECK_ERR);
    }
    
    @Operation(summary = "登录", tags = "登录")
    @PostMapping(value = "/login")
    @LogApi
    public Result<AuthenticationContext> login(@RequestParam("account") String account,
        @RequestParam("password") String password, 
        @RequestParam(value = "codeTarget", required = false) String codeTarget, @RequestParam(name = "code", required = false) String code,
        @RequestParam(name = "appid", required = false, defaultValue = "zyysc-web") String appid,
        @RequestParam(name = "appSecret", required = false, defaultValue = "") String appSecret)
    {
        SysAscription byAccount = ascriptionDao.byAccount(account);
        Integer judg = 1;
        if (byAccount == null)
        {
            SysUser exec = userDao.selectOne().eq("mobile", account).exec();
            if (exec == null) throw TofocusException.of(WsaleErrCode.UNKOWN_USER);
            account = "tf_" + exec.getPkey();
            judg = judg(exec);
        }
//        else
//        {
//            SysUser exec = userDao.selectOne()
//            .eq("nickname", byAccount.getAccount())
//            .eq("roleKey", Constant.Role.COMPANY_HEAD)
//            .exec();
//            if (exec == null) throw TofocusException.of(WsaleErrCode.UNKOWN_USER);
//            account = "tf_" + exec.getPkey();
//        }
        if((Constant.App.WEB.equals(appid) || Constant.App.WEB_COMPANY.equals(appid) || Constant.App.WEB_MARKET.equals(appid)) && Boolean.FALSE.equals(isTest))
        {
            validate(codeTarget, code);
        }
        if (StringUtils.isBlank(appSecret))
        {
            appSecret = System.getenv().getOrDefault("SAAS_WEB_APP_SECRET", "CHANGE_ME");
        }
        if(judg == 2)
        {
            appid = "zyysc-web-market";
            appSecret = System.getenv().getOrDefault("SAAS_MARKET_APP_SECRET", "CHANGE_ME");
        }
        if(judg == 3)
        {
            appid = "zyysc-web-company";
            appSecret = System.getenv().getOrDefault("SAAS_COMPANY_APP_SECRET", "CHANGE_ME");
        }
        
        AuthenticationContext login = SecurityContextUtil.loginAsTofocusUser(appid, appSecret, StringUtils.trim(account), password);
        Long userkey = login.getUserkey();
        SysUser user = userDao.get(userkey.intValue());
        log.info("userkey: {}", userkey);
        if (userkey == -1) return new Result<>(login);
        if (judg == 2)
        {
            SysCompany company = companyDao.selectOne().eq("pkey", user.getCompany()).exec();
            if (company.getIdDel() || !company.getEnabled())
                throw TofocusException.of(WsaleErrCode.UNKOWN_COMPANY_NOT_LOGIN);
            SysFarmer farmer = farmerDao.selectOne().eq("pkey", user.getFarmer()).exec();
            if (farmer.getIdDel() || !farmer.getEnabled())
                throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET_NOT_LOGIN);
        }
        if (judg == 3)
        {
            SysCompany company = companyDao.selectOne().eq("pkey", user.getCompany()).exec();
            if (company.getIdDel() || !company.getEnabled())
                throw TofocusException.of(WsaleErrCode.UNKOWN_COMPANY_NOT_LOGIN);
        }
        return new Result<>(login);
    }
    
    /**
     * 图片验证码校验
     *
     * @param request
     */
    private void validate(String target, String code)
    {
        if (StringUtil.isBlank(code))
        {
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "验证码的值不能为空");
        }
        CaptchaChecker.checkErr(check(target, code));
    }
    
    public CaptchaErrType check(String target, String code)
    {
        if (target == null)
            target = code;
        return checker.checkCaptcha(CaptchaRouter.system, CaptchaPurpose.captcha.name(), target, code);
    }
    
    @Operation(summary = "数据大屏登录", tags = "登录")
    @PostMapping(value = "/login/data")
    @LogApi
    public Result<AuthenticationContext> loginData(@RequestParam(value = "pkey", required = false, defaultValue = "1") String pkey)
    {
        if("1".equals(pkey))
        {
            AuthenticationContext login =
                SecurityContextUtil.loginAsTofocusUser("zyysc-mp", "cN1SeepaOp", StringUtils.trim("zySystem"), "123456");
            return new Result<>(login);
        }
        else
        {
            try
            {
                Integer of = Integer.valueOf(pkey);
                SysAscription sysAscription = ascriptionDao.get(of);
                if(sysAscription != null)
                {
                    AuthenticationContext login =
                        SecurityContextUtil.loginAsTofocusUser("zyysc-mp", "cN1SeepaOp", StringUtils.trim(sysAscription.getAccount()), "123456");
                    return new Result<>(login);
                }
            }
            catch (Exception e)
            {
            }
        }
        
        return new Result<>(new AuthenticationContext());
    }
    
    
    @Operation(summary = "获取身份", tags = "登录")
    @PostMapping(value = "/get/ideninfo")
    public Result<Map<String, Object>> getIdentity()
    {
        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
        Long userkey = context.getUserkey();
        SysUser user = userDao.get(userkey.intValue());
        Map<String, Object> map = new HashMap<>();
        Integer judg = judg(user);
        SysAscription sysAscription = ascriptionDao.get(user.getAscription());
        map.put("identity", judg);
        map.put("photo", sysAscription.getPhoto());
        map.put("name", sysAscription.getName());
        map.put("marketName", sysAscription.getName());
        map.put("ascription", user.getAscription());
        map.put("marketPkey", user.getFarmer());
        if(judg.intValue() == 2)
        {
            SysFarmer farmer = farmerDao.get(user.getFarmer());
            map.put("type", farmer.getType());
            map.put("marketName", farmer.getName());
        }
        if(judg.intValue() == 3)
        {
            map.put("marketPkey", user.getCompany());
            SysCompany sysCompany = companyDao.get(user.getCompany());
            map.put("marketName", sysCompany.getName());
        }
        return new Result<>(map);
    }
    
    private Integer judg(SysUser user)
    {
        if(user == null)
            return 1;
        if (user.getFarmer() != null)
            if ((Constant.Operation + user.getAscription()).equals(user.getFarmer()))
                // 1代表运营者
                return 1;
            else
                // 2代表市场
                return 2;
        else
            // 3代表公司
            return 3;
    }
}
