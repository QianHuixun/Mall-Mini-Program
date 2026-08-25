package cn.tofocus.account.api.v2.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.account.bean.application.AppKV;
import cn.tofocus.common.data.datadealer.MobileDealer;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.account.db.cache.application.AppReadCache;
import cn.tofocus.account.db.cache.org.DeptReadCache;
import cn.tofocus.domain.manager.MenuManager;
import cn.tofocus.domain.manager.UserManager;
import cn.tofocus.domain.user.User;

@RequestMapping("/v2/user")
@RestController
public class UserApiImpl implements UserApi
{
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private AppReadCache appCache;

    @Autowired
    private DeptReadCache deptCache;
    
    @Autowired
    private MenuManager menuManager;
    
    @Override
    public Result<?> loginCaptcha(@RequestParam("phone") String phone)
    {
        userManager.loginCaptcha(checkPhone(phone));
        return new Result<>();
    }
    
    @Override
    public Result<Object> prepareResetPassword(@RequestParam("phone") String phone)
    {
        userManager.prepareResetPassword(checkPhone(phone));
        return new Result<>();
    }
    
    @Override
    public Result<String> resetPassword(@RequestParam("phone") String phone, @RequestParam("captcha") String captcha,
        @RequestParam(value = "pwd", required = false) String pwd)
    {
        return new Result<>(userManager.resetPassword(checkPhone(phone), captcha, pwd));
    }
    
    @Override
    public Result<Object> prepareRebindPhone1()
    {
        userManager.prepareRebindPhone1();
        return new Result<>();
    }
    
    @Override
    public Result<String> prepareRebindPhone2(@RequestParam("captcha") String captcha)
    {
        String code = userManager.prepareRebindPhone2(captcha);
        return new Result<>(code);
    }
    
    @Override
    public Result<Object> prepareRebindPhone3(@RequestParam("phone") String phone)
    {
        userManager.prepareRebindPhone3(checkPhone(phone));
        return new Result<>();
    }
    
    @Override
    public Result<Object> rebindPhone(@RequestParam("phone") String phone, @RequestParam("captcha") String captcha,
        @RequestParam("code") String code)
    {
        userManager.rebindPhone(checkPhone(phone), captcha, code);
        return new Result<>();
    }
    
    @Override
    public Result<Object> modifyPassword(@RequestParam("oldpassword") String oldpassword,
        @RequestParam("newpassword") String newpassword)
    {
        userManager.modifyPassword(oldpassword, newpassword);
        return new Result<>();
    }
    
    /**
     * 验证手机号码
     * <p/>
     * <功能详细描述>
     * @param phone
     * @return
     */
    private String checkPhone(String phone)
    {
        MobileDealer dealer = new MobileDealer();
        String result = dealer.convert(phone);
        if (result == null || result.length() == 0)
        {
            throw TofocusException.of(SysErrCode.PHONE_ERROR, phone);
        }
        else
            return result;
    }
    
    @Override
    public Result<User> myInfo()
    {
        Long userkey = SecurityContextUtil.getAuthenticationContext().getUserkey();
        if (userkey != null)
        {
            return new Result<>(BeanUtil.beanFrom(User.class, userManager.getUser(userkey)));
        }
        else
        {
            throw TofocusException.of(SysErrCode.Auth.UNAUTHENTICATION);
        }
    }
    
    /**************************
     * 
     *    App自定义功能
     * 
     **************************/
    
    @Override
    public Result<List<AppMenu>> myAppMenu(String orgid, String deptid)
    {
        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
        AppKV app = appCache.currentApp();
        if(deptid != null)
        {
            orgid = deptCache.get(deptid).getOrgid();
        }
        List<AppMenu> top = menuManager.findMyMenuByApp(context.getUserkey(), app.getDomainid(), app.getPkey(), orgid, deptid);
        return new Result<>(top);
    }
}
