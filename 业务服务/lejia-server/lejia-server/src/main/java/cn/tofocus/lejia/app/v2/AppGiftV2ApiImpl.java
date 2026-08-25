package cn.tofocus.lejia.app.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.AppGiftV2ForWriteOff;
import cn.tofocus.lejia.domain.v2.GiftV2Manager;

@RequestMapping("/v2/app/market/lm/gift")
@RestController
public class AppGiftV2ApiImpl implements AppGiftV2Api
{
    @Autowired
    private GiftV2Manager giftManager;
    
    @Override
    public Result<AppGiftV2ForWriteOff> load4WriteOff(String cardNumber)
    {
        AppGiftV2ForWriteOff gift = giftManager.load4WriteOff(cardNumber);
        return new Result<>(gift);
    }
    
    @Override
    public Result<Boolean> writeOff(String cardNumber)
    {
        boolean sign = giftManager.writeOff(cardNumber);
        return new Result<>(sign);
    }
}
