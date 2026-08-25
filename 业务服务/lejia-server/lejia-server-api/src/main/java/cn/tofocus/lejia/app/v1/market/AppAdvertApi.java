package cn.tofocus.lejia.app.v1.market;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppAdvertOnList;
import cn.tofocus.lejia.bean.dto.market.MktCombinationAdviseInfo;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigOnList;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-advert", path = "/v1/app/market/img",
        fallbackFactory = AppAdvertFallback.class, configuration = FeignConfig.class)
public interface AppAdvertApi {

    @Operation(summary = "获取广告", tags = AppTags.mobileAdvert)
    @PostMapping("/get")
    public Result<AppAdvertOnList> getAdvert(@RequestParam(value = "pkey") @Parameter(description = "广告主键") Integer pkey);
    
    @Operation(summary = "获取广告列表", tags = AppTags.mobileAdvert)
    @PostMapping(value = "/query")
    public Result<List<AppAdvertOnList>> queryAdvert(
        @RequestParam(value = "position", required = false) @Parameter(description = "位置") AdvertPosition position,
        @RequestParam(value = "positionObj", required = false) @Parameter(description = "位置关联对象主键") String positionObj);

    
    @Operation(summary = "获取弹窗广告", tags = AppTags.mobileAdvert)
    @PostMapping(value = "/query/index")
    public Result<List<MktIndexAdvertOnList>> listIndexAdvert();
    
    @Operation(summary = "不再显示此弹框", tags = AppTags.mobileAdvert)
    @PostMapping(value = "/notDisplay/index")
    public Result<Boolean> notDisplayIndexAdvert(@RequestParam(value = "pkey") @Parameter(description = "弹窗广告主键")Integer pkey);
    
  

    @Operation(summary = "获取功能区", tags = AppTags.mobileAdvert)
    @PostMapping(value = "/funMenuConfig/list")
    public Result<List<MktFunMenuConfigOnList>> listFunMenuConfig();
    
}
