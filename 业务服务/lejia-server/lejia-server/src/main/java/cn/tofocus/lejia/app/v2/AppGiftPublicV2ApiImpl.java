package cn.tofocus.lejia.app.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.AppGiftV2ForPublicWriteOff;
import cn.tofocus.lejia.domain.v2.GiftV2Manager;

@RequestMapping("/v2/app/public/gift")
@RestController
public class AppGiftPublicV2ApiImpl implements AppGiftPublicV2Api
{
    @Autowired
    private GiftV2Manager giftManager;
    
    @Override
    public Result<AppGiftV2ForPublicWriteOff> load4WriteOff(Integer cardNumber)
    {
        AppGiftV2ForPublicWriteOff gift = giftManager.load4PublicWriteOff(cardNumber);
        return new Result<>(gift);
    }
    
    @Override
    public Result<Boolean> writeOff(Integer cardNumber, String password)
    {
        boolean sign = giftManager.publicWriteOff(cardNumber, password);
        return new Result<>(sign);
    }
}
