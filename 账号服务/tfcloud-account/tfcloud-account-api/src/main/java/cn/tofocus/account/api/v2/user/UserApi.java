package cn.tofocus.account.api.v2.user;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "account", contextId = "user", path = "/v2/user", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface UserApi
{
    //-----------------------------------------
    
    /**
     * 获取手机登陆验证码
     * @param phone
     */
    @Operation(summary = "手机登陆：获取登陆验证码", tags = ApiTags.user)
    @PostMapping(value = "/loginCaptcha")
    Result<?> loginCaptcha(@RequestParam("phone") @Parameter(description = "手机号码") String phone);
    
    //-----------------------------------------
    
    /**
     * 重置密码step1：获取验证码
     * @param phone 手机号码
     */
    @Operation(summary = "重置密码step1：获取验证码", tags = ApiTags.user)
    @PostMapping(value = "/resetpwd/setp1")
    Result<Object> prepareResetPassword(@RequestParam("phone") @Parameter(description = "手机号码") String phone);
    
    /**
     * 重置密码step2：重置自己的密码
     * @param phone 手机号码
     * @param captcha 验证码
     * @param pwd 新密码(如果是空，自动生成密码)
     * @return 新密码
     */
    @Operation(summary = "重置密码step2：重置自己的密码（不设置新密码会生成随机密码，重置后会返回新密码）", tags = ApiTags.user)
    @PostMapping(value = "/resetpwd/confirm")
    Result<String> resetPassword(@RequestParam("phone") @Parameter(description = "手机号码") String phone,
        @RequestParam("captcha") @Parameter(description = "验证码") String captcha,
        @RequestParam(value = "pwd", required = false) @Parameter(description = "新密码") String pwd);
    
    //-----------------------------------------
    
    /**
     * 重新绑定手机step1：获取验证码
     */
    @Operation(summary = "重新绑定手机step1：获取验证码", tags = ApiTags.user)
    @PostMapping(value = "/rebindphone/setp1")
    Result<Object> prepareRebindPhone1();
    
    /**
     * 重新绑定手机step2:验证旧手机验证码，确认是本人操作
     * @param captcha 验证码
     * @return 重新绑定手机的操作码，用于进入step4
     */
    @Operation(summary = "重新绑定手机step2:验证旧手机验证码，确认是本人操作。返回操作码，用于step4", tags = ApiTags.user)
    @PostMapping(value = "/rebindphone/setp2")
    Result<String> prepareRebindPhone2(@RequestParam("captcha") @Parameter(description = "验证码") String captcha);
    
    /**
     * 重新绑定手机step3：向新手机发生验证码
     * @param phonenumber 新手机号码
     */
    @Operation(summary = "重新绑定手机step3：向新手机发生验证码", tags = ApiTags.user)
    @PostMapping(value = "/rebindphone/setp3")
    Result<Object> prepareRebindPhone3(@RequestParam("phone") @Parameter(description = "新手机号码") String phone);
    
    /**
     * 重新绑定手机step4：绑定新号码
     * @param phonenumber
     * @param captcha
     * @param code
     */
    @Operation(summary = "重新绑定手机step4：绑定新号码", tags = ApiTags.user)
    @PostMapping(value = "/rebindphone/confirm")
    Result<Object> rebindPhone(@RequestParam("phone") @Parameter(description = "新手机号码") String phone,
        @RequestParam("captcha") @Parameter(description = "验证码") String captcha, @RequestParam("code") @Parameter(description = "操作码") String code);
    
    
    //-----------------------------------------
    /**
     * 修改密码
     * <功能详细描述>
     * @param oldpassword
     * @param newpassword
     */
    @Operation(summary = "修改用户密码", tags = ApiTags.user)
    @PostMapping(value = "/modifypwd")
    Result<Object> modifyPassword(@RequestParam("oldpassword") @Parameter(description = "旧密码") String oldpassword,
        @RequestParam("newpassword") @Parameter(description = "新密码") String newpassword);
    
    //-----------------------------------------
    
    
    @GetMapping(value = "/info")
    Result<User> myInfo();
    
    /**************************
     * 
     *    App自定义功能
     * 
     **************************/
    
    /**
     * 获取用户的应用模块菜单按钮
     * @param appid
     * @return
     */
    @GetMapping(value = "/myAppMenu")
    Result<List<AppMenu>> myAppMenu(@RequestParam(name = "orgid", required = false) String orgid,
        @RequestParam(name = "deptid", required = false) String deptid);
    
    /**************************
     * 
     *    权限
     * 
     **************************/
    
}
