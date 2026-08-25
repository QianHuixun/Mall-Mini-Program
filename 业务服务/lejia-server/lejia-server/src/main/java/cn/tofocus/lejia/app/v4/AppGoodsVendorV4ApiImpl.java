package cn.tofocus.lejia.app.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.vendor.v4.AppVendorGoodsOnPage;
import cn.tofocus.lejia.bean.dto.app.vendor.v4.AppVendorGoodsSpaceOnList;
import cn.tofocus.lejia.domain.app.v4.AppGoodsVendorV4Manager;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v4/app/vendor/goods")
@RestController
public class AppGoodsVendorV4ApiImpl
{
    
    @Autowired
    private AppGoodsVendorV4Manager manager;
    
    @Operation(summary = "获取商品列表", tags = AppTags.mobileVendorGoodsV4)
    @PostMapping(value = "/query")
    public Result<PageResult<AppVendorGoodsOnPage>> queryAppVendorGoods(
      @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description = "页号") int page,
      @RequestParam(value = "pagesize", required = false, defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
      @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
      @RequestParam(value = "status", required = false) @Parameter(description = "商品状态, 不填返回所有, 1:在售商品, 2:已下架, 3:已售罄") Integer status)
    {
        return new Result<>(manager.queryAppVendorGoods(page, pagesize, title, status));
    }
    
    @Operation(summary = "修改库存和价格", tags = AppTags.mobileVendorGoodsV4)
    @PostMapping(value = "/updKcAndPrice")
    public Result<Boolean> updAppVendorGoods(@RequestBody List<AppVendorGoodsSpaceOnList> spaces)
    {
        return new Result<>(manager.updAppVendorGoods(spaces));
    }
    
    @Operation(summary = "商品上架", tags = AppTags.mobileVendorGoodsV4)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startGoods(@RequestParam(name = "pkey") Integer pkey)
    {
        Boolean res = manager.enabledGoods(pkey, true);
        return new Result<>(res);
    }
    
    @Operation(summary = "商品下架", tags = AppTags.mobileVendorGoodsV4)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopGoods(@RequestParam(name = "pkey") Integer pkey)
    {
        Boolean res = manager.enabledGoods(pkey, false);
        return new Result<>(res);
    }
    
    
}
