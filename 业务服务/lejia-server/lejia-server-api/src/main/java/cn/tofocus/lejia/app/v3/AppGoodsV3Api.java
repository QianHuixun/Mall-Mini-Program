package cn.tofocus.lejia.app.v3;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppGoodsAppOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppGoodsV3Api
{
    @Operation(summary = "获取商品列表", tags = AppTags.mobileGoods)
    @PostMapping(value = "/query")
    public Result<PageResult<AppGoodsAppOnList>> queryAppGoods(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "二级分类pkey") Integer goodsMain);
    
}
