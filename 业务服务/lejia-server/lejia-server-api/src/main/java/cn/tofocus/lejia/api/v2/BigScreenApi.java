package cn.tofocus.lejia.api.v2;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.v2.screen.BigScreenTopRightInfo;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRank2OnList;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankMarketOnPage;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankOnList;
import cn.tofocus.lejia.bean.dto.v2.screen.TestOnPage;
import cn.tofocus.lejia.bean.enums.v2.TimeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface BigScreenApi
{
    @Operation(summary = "销量分类金额排行 TOP10", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/ranking/type/sales")
    public Result<List<SalesRankOnList>> listTypeSales(
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
    @Operation(summary = "销量商品金额排行榜 TOP20", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/ranking/goods/sales")
    public Result<List<SalesRankOnList>> listGoodsSales(
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
    @Operation(summary = "销量分类笔数排行榜TOP10", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/ranking/type/num")
    public Result<List<SalesRank2OnList>> listTypeNum(
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
    @Operation(summary = "销售商品笔数排行榜", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/ranking/goods/num")
    public Result<List<SalesRankOnList>> listGoodsNum(
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
    @Operation(summary = "市场销售详情及中间地图数据", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/ranking/market")
    public Result<List<SalesRankMarketOnPage>> queryMarket(
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
    @Operation(summary = "右上角数据及实时交易额", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/ranking/topRight")
    public Result<BigScreenTopRightInfo> getTopRight(
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
    @Operation(summary = "检测信息", tags = ApiTags.zyysc_big_screen)
    @PostMapping(value = "/query/test")
    public Result<PageResult<TestOnPage>> queryTest(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "6", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "timeType", required = false, defaultValue = "THE_DAY") TimeType timeType);
    
}
