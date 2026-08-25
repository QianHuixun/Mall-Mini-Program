package cn.tofocus.lejia.app.v2.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.v2.AppGwcV2Api;
import cn.tofocus.lejia.bean.dto.v2.gwc.GwcV2Info;
import cn.tofocus.lejia.domain.v2.GwcV2Manager;

@RequestMapping("/v2/app/market/lm/gwc")
@RestController
public class AppGwcV2ApiImpl implements AppGwcV2Api
{
    @Autowired
    private GwcV2Manager manager;
    
    @Override
    public Result<GwcV2Info> getGwc()
    {
        return new Result<>(manager.queryGwc());
    }
    
}
