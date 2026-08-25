package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import cn.tofocus.lejia.bean.dto.market.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.domain.MarketManager;

import javax.validation.Valid;

@RequestMapping("/v1/sys/market")
@RestController
public class MarketApiImpl implements MarketApi
{
    @Autowired
    private MarketManager marketManager;
    
    @Override
    @LogApi(operation = "新增市场", format = "新增市场,名称:{entity.name}", resultFormat = "")
    public Result<SysFarmerInfo> insMarket(SysFarmerInfo entity)
    {
        return new Result<>(marketManager.insMarket(entity));
    }
    
    @Override
    public Result<SysFarmerInfo> getMarket(String pkey)
    {
        return new Result<>(marketManager.getMarket(pkey));
    }
    
    @Override
    public Result<PageResult<SysFarmerOnList>> queryMarket(int page, int pagesize, String marketName)
    {
        return new Result<>(marketManager.queryMarket(page, pagesize, marketName));
    }
    
    @Override
    @LogApi(operation = "修改市场", format = "修改市场,名称:{entity.name}")
    public Result<SysFarmerInfo> updMarket(@RequestBody SysFarmerInfo entity)
    {
        return new Result<>(marketManager.updMarket(entity));
    }
    
    @Override
    @LogApi(operation = "删除市场", format = "删除市场")
    public Result<Boolean> delMarket(String pkey)
    {
        return new Result<>(marketManager.delMarket(pkey));
    }
    
    @Override
    @LogApi(operation = "启动市场", format = "启动市场")
    public Result<Boolean> startMarket(String pkey)
    {
        return new Result<>(marketManager.enableMarket(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止市场", format = "停止市场")
    public Result<Boolean> stopMarket(String pkey)
    {
        return new Result<>(marketManager.enableMarket(pkey, false));
    }

    @Override
    public Result<Boolean> updCourierDispatch(List<MktMarketCourierOnList> infos)
    {
        return new Result<>(marketManager.updCourierDispatch(infos));
    }

    @Override
    public Result<List<MktMarketCourierOnList>> listCourierDispatch()
    {
        return new Result<>(marketManager.listCourierDispatch());
    }

    @Override
    public Result<Boolean> updDispatch(Boolean automaticCourier, Boolean automaticPurchase)
    {
        return new Result<>(marketManager.updDispatch(automaticCourier, automaticPurchase));
    }

    @Override
    public Result<List<DropStringDown>> listDropName()
    {
        return new Result<>(marketManager.listDropName());
    }
    
    @Override
    public Result<List<DropStringDown>> listDropSupplyName()
    {
        return new Result<>(marketManager.listDropSupplyName());
    }
    
    @Override
    public Result<Boolean> updPrintCode(String code)
    {
        boolean sign = marketManager.updPrintCode(code);
        return new Result<>(true);
    }
    
    @Override
    public Result<String> getPrintCode()
    {
        String res = marketManager.getPrintCode();
        return new Result<>(res);
    }
    
    @Override
    public Result<MarketTechConfig> getTechConfig()
    {
        MarketTechConfig config = marketManager.getTechConfig();
        return new Result<>(config);
    }
    
    @Override
    public Result<Boolean> updTechConfig(@Valid MarketTechConfig config)
    {
        boolean sign = marketManager.updTechConfig(config);
        return new Result<>(sign);
    }
}
