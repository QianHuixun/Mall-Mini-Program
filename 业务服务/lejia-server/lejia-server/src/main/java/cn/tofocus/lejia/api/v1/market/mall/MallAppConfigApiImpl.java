package cn.tofocus.lejia.api.v1.market.mall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.sys.AppConfig;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;

@RequestMapping("/v1/market/mall/app/config")
@RestController
public class MallAppConfigApiImpl implements MallAppConfigApi
{
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Override
    public Result<AppConfig> getAppConfig()
    {
        AppConfig config = appConfigManager.getConfig();
        return new Result<>(config);
    }
    
    @Override
    public Result<Boolean> updAppConfig(AppConfig config)
    {
        appConfigManager.updAppConfig(config);
        return new Result<>(true);
    }
    
}
