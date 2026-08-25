package cn.tofocus.lejia.app.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.market.MemberManager;
import io.swagger.v3.oas.annotations.Operation;


@RequestMapping("/v1/app/market")
@RestController
public class AppLoginApiImpl {

    @Autowired
    private MemberManager memberManger;

    @Autowired
    private MobileSession mobileSession;

    @Operation(summary = "切换市场", tags = AppTags.mobileLogin)
    @PostMapping("/changeFarmer")
    public Result<Boolean> changeFarmer(String farmer) {
        mobileSession.setFarmer(farmer);
        memberManger.updateFarmer(farmer);
        return new Result<>(true);
    }
}
