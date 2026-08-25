package cn.tofocus.account.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.account.db.cache.CaptchaMap;
import cn.tofocus.common.notify.config.ISMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.captcha.CaptchaErrType;
import cn.tofocus.core.enums.CaptchaPurpose;
import cn.tofocus.core.enums.CaptchaRouter;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年4月4日]
 */
@Component
public class CaptchaManager
{
    @Autowired
    private ISMSNotify smsNofity;
    
    @Autowired
    private CaptchaMap captchaMap;
    
    private long refreshTime = 1L * 60 * 1000;
    
    private CaptchaChecker checker;

    private static final int MAX_CHECK_ERR = 5;
    
    @PostConstruct
    private void init()
    {
        checker = new CaptchaChecker(refreshTime, captchaMap, smsNofity, MAX_CHECK_ERR);
    }
    
    public String createCaptcha(CaptchaPurpose purpose, String target)
    {
        return checker.createCaptcha(CaptchaRouter.phone, purpose.toString(), target, 6, null, false);
    }
    
    public String createCaptcha(CaptchaRouter route, CaptchaPurpose purpose, String target)
    {
        return checker.createCaptcha(route, purpose.toString(), target, 6, null, false);
    }
    
    public CaptchaErrType checkCaptcha(CaptchaPurpose purpose, String target, String captcha)
    {
        return checker.checkCaptcha(CaptchaRouter.phone, purpose.toString(), target, captcha);
    }
    
    public CaptchaErrType checkCaptcha(CaptchaRouter route, CaptchaPurpose purpose, String target, String captcha)
    {
        return checker.checkCaptcha(route, purpose.toString(), target, captcha);
    }
    
}
