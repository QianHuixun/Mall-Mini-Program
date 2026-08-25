package cn.tofocus.lejia.api.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.market.DropStringDown;
import cn.tofocus.lejia.domain.v2.MarketV2Manager;

@RequestMapping("/v2/sys/market")
@RestController
public class MarketV2ApiImpl implements MarketV2Api
{
    @Autowired
    private MarketV2Manager marketV2Manager;
    
    @Override
    public Result<List<DropStringDown>> listDropName(boolean includeAscription)
    {
        return new Result<>(marketV2Manager.listDropName(includeAscription));
    }
}
