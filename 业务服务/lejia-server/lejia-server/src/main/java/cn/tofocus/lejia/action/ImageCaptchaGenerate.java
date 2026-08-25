package cn.tofocus.lejia.action;

import java.awt.image.BufferedImage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.code.kaptcha.Producer;

import cn.tofocus.common.notify.config.ISMSNotify;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.captcha.CaptchaErrType;
import cn.tofocus.core.enums.CaptchaPurpose;
import cn.tofocus.core.enums.CaptchaRouter;
import cn.tofocus.lejia.cache.CaptchaMap;

@Service("imageCaptchaGenerate")
public class ImageCaptchaGenerate
{
    @Autowired
    private CaptchaMap captchaMap;
    
    @Autowired
    private Producer producer; 
    
    @Autowired
    private ISMSNotify smsNofity;
    
    private long refreshTime = 0;
    
    private CaptchaChecker checker;

    private static final int MAX_CHECK_ERR = 5;
    
    @PostConstruct
    private void init()
    {
        checker = new CaptchaChecker(refreshTime, captchaMap, smsNofity, MAX_CHECK_ERR);
    }
    
    public BufferedImage generate(String target)
    {
        String code = producer.createText();
        if (target == null)
            target = code;
        BufferedImage bufferedImage = producer.createImage(code);
        checker.createCaptcha(CaptchaRouter.system, CaptchaPurpose.captcha.name(), target, code, null, true);
        return bufferedImage;
    }
    
    public CaptchaErrType check(String target, String code)
    {
        if(target == null)
            target = code;
        return checker.checkCaptcha(CaptchaRouter.system, CaptchaPurpose.captcha.name(), target, code);
    }
    
    public CaptchaErrType check(CaptchaRouter route, CaptchaPurpose purpose, String target, String captcha)
    {
        return checker.checkCaptcha(route, purpose.toString(), target, captcha);
    }
}
