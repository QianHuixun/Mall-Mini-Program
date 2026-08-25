package cn.tofocus.lejia.app.v3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppGoodsAppOnList;
import cn.tofocus.lejia.domain.app.AppGoodsManager;

@RequestMapping("/v3/app/market/goods")
@RestController
public class AppGoodsV3ApiImpl implements AppGoodsV3Api
{
    @Autowired
    private AppGoodsManager goodsManager;    
    
    @Override
    public Result<PageResult<AppGoodsAppOnList>> queryAppGoods(Integer page, Integer pagesize, Integer gtype,
        Integer goodsMain)
    {
        return new Result<>(goodsManager.queryAppGoodsV3(page, pagesize, gtype, goodsMain, null, null, 0, 0, null, false, null));
    }
    
}
