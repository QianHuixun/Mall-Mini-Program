package cn.tofocus.lejia.api.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v2.screen.BigScreenTopRightInfo;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRank2OnList;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankMarketOnPage;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankOnList;
import cn.tofocus.lejia.bean.dto.v2.screen.TestOnPage;
import cn.tofocus.lejia.bean.enums.v2.TimeType;
import cn.tofocus.lejia.domain.v2.BigScreenManager;

@RequestMapping("/v2/sys/big/screen/")
@RestController
public class BigScreenApiImpl implements BigScreenApi
{
    @Autowired
    private BigScreenManager manager;
    
    @Override
    public Result<List<SalesRankOnList>> listTypeSales(TimeType timeType)
    {
        return new Result<>(manager.listTypeSales(timeType));
    }
    
    @Override
    public Result<List<SalesRankOnList>> listGoodsSales(TimeType timeType)
    {
        return new Result<>(manager.listGoodsSales(timeType));
    }
    
    @Override
    public Result<List<SalesRank2OnList>> listTypeNum(TimeType timeType)
    {
        return new Result<>(manager.listTypeNum(timeType));
    }
    
    @Override
    public Result<List<SalesRankOnList>> listGoodsNum(TimeType timeType)
    {
        return new Result<>(manager.listGoodsNum(timeType));
    }
    
    @Override
    public Result<List<SalesRankMarketOnPage>> queryMarket(TimeType timeType)
    {
        return new Result<>(manager.queryMarket(timeType));
    }
    
    @Override
    public Result<BigScreenTopRightInfo> getTopRight(TimeType timeType)
    {
        return new Result<>(manager.getTopRight(timeType));
    }
    
    @Override
    public Result<PageResult<TestOnPage>> queryTest(int page, int pagesize, TimeType timeType)
    {
        return new Result<>(manager.queryTest(page, pagesize, timeType));
    }
    
//    @PostMapping(value = "/run")
//    public Result<String> runOrderLine()
//    {
//        return new Result<>(manager.runOrderLine());
//    }
//    
//    @PostMapping(value = "/run/memberCard/spaceNum")
//    public Result<String> runMemberCard()
//    {
//        return new Result<>(runManager.runMemberCardAndSpaceNum());
//    }
}
