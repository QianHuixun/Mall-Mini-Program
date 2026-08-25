package cn.tofocus.lejia.app.v2.vendor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.enums.AppVendorLoginRole;
import cn.tofocus.lejia.domain.app.AppVendorLoginV2Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v2/app/vendorLogin")
@RestController
public class AppVendorLoginV2ApiImpl
{
    @Autowired
    private AppVendorLoginV2Manager appVendorLoginV2Manager;
    
    @Operation(summary = "获取手机登陆验证码", tags = AppTags.mobileVendorLoginV2)
    @PostMapping(value = "/captcha")
    public Result<Boolean> loginCaptcha(@RequestParam("phone") @Parameter(description = "手机号") String phone)
    {
        boolean result = appVendorLoginV2Manager.loginCaptcha(phone);
        return new Result<>(result);
    }
    
    @Operation(summary = "获取手机号允许登录角色", tags = AppTags.mobileVendorLoginV2)
    @PostMapping(value = "/roles")
    public Result<List<AppVendorLoginRole>> roles(@RequestParam("phone") @Parameter(description = "手机号") String phone,
        HttpServletRequest request)
    {
        String openid = request.getHeader("openid");
        List<AppVendorLoginRole> list = appVendorLoginV2Manager.roles(phone, openid);
        return new Result<>(list);
    }
    
    @Operation(summary = "登录", tags = AppTags.mobileVendorLoginV2)
    @PostMapping(value = "/login")
    public Result<Boolean> login(@RequestParam("phone") @Parameter(description = "手机号") String phone,
        @RequestParam("role") @Parameter(description = "商户小程序角色") AppVendorLoginRole role,
        @RequestParam("captcha") @Parameter(description = "验证码") String captcha, HttpServletRequest request)
    {
        String openid = request.getHeader("openid");
        boolean sign = appVendorLoginV2Manager.login(phone, role, captcha, openid);
        return new Result<>(sign);
    }
    
    @Operation(summary = "登录状态查询", tags = AppTags.mobileVendorLoginV2)
    @PostMapping(value = "/checkLogin")
    public Result<AppVendorLoginRole> checkLogin(HttpServletRequest request)
    {
        String openid = request.getHeader("openid");
        return new Result<>(appVendorLoginV2Manager.checkLogin(openid));
    }
}
