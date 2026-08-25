package cn.tofocus.authentication.action.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.db.cache.CaptchaMap;
import cn.tofocus.core.Result;
import cn.tofocus.core.captcha.CaptchaChecker;
import cn.tofocus.core.enums.CaptchaPurpose;
import cn.tofocus.core.enums.CaptchaRouter;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 验证码控制器
 * @author CaiRui
 * @date 2018-12-10 12:13
 */
@RestController
public class CaptchaController
{
    
    public static final String IMAGE_CAPTCHA_SESSION_KEY = "image_captcha_session_key";
    
    private static final String FORMAT_NAME = "JPEG";
    
    @Autowired
    private ImageCaptchaGenerate captchaGenerate;
    
    /**
     * 获取图片验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/captcha/image")
    public void createKaptcha(@RequestParam(name = "codeTarget", required = false) String target,
        HttpServletResponse response)
        throws IOException
    {
        //1.接口生成验证码
        BufferedImage image = captchaGenerate.generate(target);
        //2.写到响应流中
        response.setHeader("Cache-Control", "no-store, no-cache");// 没有缓存
        response.setContentType("image/jpeg");
        ImageIO.write(image, FORMAT_NAME, response.getOutputStream());
    }
    
    @GetMapping("/captcha/test")
    public Result<Boolean> testCaptcha(@RequestParam(name = "codeTarget", required = false) String target, String code)
    {
        CaptchaChecker.checkErr(captchaGenerate.check(target, code));
        return new Result<>(true);
    }
    
}
