package cn.tofocus.lejia.app.v2.market.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.v2.goods.AppGoodsApiV2;
import cn.tofocus.lejia.bean.dto.v2.goods.PresaleTimeOnInfo;
import cn.tofocus.lejia.domain.v2.GoodsV2Manager;

@RequestMapping("/v2/app/market/goods")
@RestController
public class AppGoodsApiV2Impl implements AppGoodsApiV2
{
    
    @Autowired
    private GoodsV2Manager manager;
    
    @Override
    public Result<PresaleTimeOnInfo> getPresaleTime()
    {
        return new Result<>(manager.getPresaleTime());
    }
    
}
