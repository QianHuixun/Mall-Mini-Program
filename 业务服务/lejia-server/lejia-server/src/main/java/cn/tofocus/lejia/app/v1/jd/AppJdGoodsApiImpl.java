package cn.tofocus.lejia.app.v1.jd;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.jd.AppJdGoodsDetails;
import cn.tofocus.lejia.bean.dto.app.jd.AppJdGoodsOnPage;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryDrop;
import cn.tofocus.lejia.domain.app.AppJdManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/app/jd")
@RestController
public class AppJdGoodsApiImpl
{
    @Autowired
    private AppJdManager manager;
    
    @Operation(summary = "APP获取京东商品一级分类列表", tags = AppTags.mobileJd)
    @PostMapping(value = "/category/drop")
    public Result<List<JdCategoryDrop>> categoryDrop()
    {
        return new Result<>(manager.categoryDrop());
    }

    @Operation(summary = "APP根据一级分类获取京东商品列表", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/query")
    public Result<PageResult<AppJdGoodsOnPage>> queryGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page, 
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "category")Long category)
    {
        return new Result<>(manager.queryGoods(page, pagesize, category));
    }
    
    @Operation(summary = "APP获取商品详情", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/get")
    public Result<List<AppJdGoodsDetails>> getGoodsDetails(@RequestParam(value = "pkey")long pkey)
    {
        return new Result<>(manager.getGoodsDetails(pkey));
    }

    @Operation(summary = "APP获取商品服务内容", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/content")
    public Result<List<String>> getGoodsContent()
    {
        return new Result<>(manager.getGoodsContent());
    }

    @Operation(summary = "APP商品加入购物车", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/gwc/ins")
    public Result<Boolean> gwcIns(@RequestParam(value = "pkey")long pkey, 
        @RequestParam(value = "goodsNum")int goodsNum,
        @RequestParam(value = "longitude", required = false)BigDecimal longitude, 
        @RequestParam(value = "latitude", required = false)BigDecimal latitude)
    {
        return new Result<>(manager.gwcIns(pkey, goodsNum, longitude, latitude));
    }
    
    @Operation(summary = "增加购物车里单个商品的数量", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/gwc/add/num")
    public Result<Boolean> addGwcNum(@RequestParam(name = "pkey")long pkey,
        @RequestParam(name = "goodsNum", required = false, defaultValue = "1") int goodsNum)
    {
        return new Result<>(manager.addGwcNum(pkey, goodsNum));
    }
    
    @Operation(summary = "减少购物车里单个商品的数量", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/gwc/less/num")
    public Result<Boolean> lessGwcNum(@RequestParam(name = "pkey")long pkey,
        @RequestParam(name = "goodsNum", required = false, defaultValue = "1")  int goodsNum)
    {
        return new Result<>(manager.lessGwcNum(pkey, goodsNum));
    }

    @Operation(summary = "APP根据名称获取京东商品", tags = AppTags.mobileJd)
    @PostMapping(value = "/goods/by/title")
    public Result<PageResult<AppJdGoodsOnPage>> byTitleGoods(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page, 
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "title", required = false)String title)
    {
        return new Result<>(manager.byTitleGoods(page, pagesize, title));
    }
}
