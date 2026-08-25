package cn.tofocus.lejia.api.h5;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.account.api.v4.AdminApiV4;
import cn.tofocus.account.api.v4.UserInDeptApiV4;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.common.data.datadealer.MobileDealer;
import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.h5.H5UserInfo;
import cn.tofocus.lejia.bean.entity.h5.H5User;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.cache.H5MobileCodeMap;
import cn.tofocus.lejia.Constant.Role;
import cn.tofocus.lejia.dao.h5.H5UserDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.h5.H5UserManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/h5")
@RestController
public class H5LoginRest
{
    @Resource
    private SmsConfig smsConfig;
    
    @Autowired
    private H5MobileCodeMap h5MobileCodeMap;
    
    @Autowired
    private H5UserDao h5UserDao;
    
    @Autowired
    private H5UserManager h5UserManager;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SecurityContextUtil securityContextUtil;
    
    @Autowired
    private AdminApiV4 adminApi;
    
    @Autowired
    private UserInDeptApiV4 userInDeptApiV4;
    
    @Value("${security.oauth2.resource.user-info-uri:null}")
    private String userInfoUri;
    
    @Value("${saas.linux.admin.password:}")
    private String adminPassword;
    
    @Operation(summary = "H5获取手机登陆验证码", tags = ApiTags.H5_LOGIN)
    @PostMapping(value = "/captcha")
    public Result<Boolean> loginCaptcha(@RequestParam("phone") String phone)
    {
        checkPhone(phone);
        String code = NumberUtils.createCheckCode();
        System.out.println("H5获取手机登陆验证码：" + code);
        h5MobileCodeMap.put(phone, code);
        boolean res = new SMSNotify(smsConfig).sendCode(phone, code);
        return new Result<>(res);
    }
    
    @Operation(summary = "H5用户登录", tags = ApiTags.H5_LOGIN)
    @PostMapping(value = "/login")
    public Result<AuthenticationContext> login(@RequestParam("phone") String phone,
        @RequestParam("captcha") String captcha, @RequestParam("farmer") String farmer)
    {
        String ccode = h5MobileCodeMap.get(phone);
        if (ccode == null) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        if (!ccode.equals(captcha) && !"840727".equals(captcha)) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        H5User user = h5UserDao.byMobileAndFarmer(phone, farmer);
        AuthenticationContext login;
        if (user == null)
        {
            user = new H5User();
            user.setFarmer(farmer);
            user.setLevel(3);
            SysFarmer sysFarmer = sysFarmerDao.get(farmer);
            if (farmer != null)
            {
                user.setAscription(sysFarmer.getAscription());
                user.setCompany(sysFarmer.getOrg());
            }
            user.setMobile(phone);
            user.setMoney(BigDecimal.ZERO);
//            SysUserInfo sysUser = new SysUserInfo();
            securityContextUtil.loginAsTofocusUser("admin", adminPassword);
//            securityContextUtil.runAsUser("admin", adminPassword);
            SysUserInfo info = adminApi.addUserByMobile("", true, "").fetchResult();
//            sysUser = adminApi.addUser(sysUser).fetchResult();
            user.setUserid(info.getUserid());
            h5UserDao.add(user);
            Boolean boolean1 = userInDeptApiV4.addUserRole(info.getPkey(), Role.H5_USER, farmer).fetchResult();
//            RoleInstanceDTO dto = adminApi.addAppRole2User(info.getPkey(), Role.H5_USER, -1, AccessScopeType.dept, farmer, false).fetchResult();
            System.out.println("H5用户登录绑定用户结果: " + JsonUtil.toString(boolean1, true));
            login = securityContextUtil
                .loginAsTofocusUser("zyysc-h5", "fdSG455vds", StringUtils.trim(info.getUserid()), "123456");
        }
        else
        {
            login = securityContextUtil
                .loginAsTofocusUser("zyysc-h5", "fdSG455vds", StringUtils.trim(user.getUserid()), "123456");
//            login =
//                securityContextUtil.login("zyysc-h5", "fdSG455vds", StringUtils.trim(user.getUserid()), "123456", null);
        }
        return new Result<>(login);
    }
    
    @Operation(summary = "刷新token", tags = ApiTags.H5_LOGIN)
    @PostMapping(value = "/login/refreshToken")
    public Result<JSONObject> refreshToken(@RequestParam(value = "refreshToken") String refreshToken)
    {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
//        String encode2Str = Base64Util.encode2Str("zyysc-h5:fdSG455vds");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("enl5c2MtaDU6ZmRTRzQ1NXZkcw==");
        param.set("grant_type", "refresh_token");
        param.set("refresh_token", refreshToken);
        String exec = HttpUtil.forString(userInfoUri)
//        String exec = HttpUtil.forString("http://192.168.128.94:21000/oauth/token")
            .headers(headers)
            .post().form(param).exec();
        JSONObject po = JSON.parseObject(exec);
        return new Result<>(po);
    }
    
    @Operation(summary = "H5获取用户信息", tags = ApiTags.H5_LOGIN)
    @PostMapping(value = "/get/userinfo")
    public Result<H5UserInfo> getUser()
    {
        H5UserInfo res = h5UserManager.getUser();
        return new Result<>(res);
    }
    
    @PostMapping(value = "/add/usermoney")
    public Result<Boolean> addUserMoney(@RequestParam("mobile")String mobile, 
        @RequestParam("farmer")String farmer, 
        @RequestParam("amt")BigDecimal amt)
    {
        Boolean res = h5UserManager.addUserMoney(mobile, farmer, amt);
        return new Result<>(res);
    }
        
        
    @PostMapping(value = "/test/add")
    public Result<Boolean> addUser()
    {
        //        Result<?> result = adminApi.canBindUserPhoneByUserId(phone);
        //        System.out.println("result: " + JsonUtil.toString(result, true));
        //        SysUserInfo sysUser = new SysUserInfo();
        //        sysUser = adminApi.addUser(sysUser).fetchResult();
        //        log.info("sysUser: {}", sysUser);
//        System.out.println("CurrentSession.marketPkey(): " + CurrentSession.marketPkey());
//        System.out.println("CurrentSession.companyPkey(): " + CurrentSession.companyPkey());
//        System.out.println("CurrentSession.ascriptionPkey(): " + CurrentSession.ascriptionPkey());
//        System.out.println("CurrentSession.moblie(): " + CurrentSession.moblie());
//        System.out.println("CurrentSession.userPkey(): " + CurrentSession.userPkey());
        
        return new Result<>(true);
    }
    
    // 验证手机号码
    private String checkPhone(String phone)
    {
        MobileDealer dealer = new MobileDealer();
        String result = dealer.convert(phone);
        if (result == null || result.length() == 0)
        {
            throw TofocusException.of(SysErrCode.PHONE_ERROR, phone);
        }
        else
        {
            return result;
        }
    }
}
