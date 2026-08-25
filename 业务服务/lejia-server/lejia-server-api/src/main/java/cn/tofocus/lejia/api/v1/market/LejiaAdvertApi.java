package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktAdvertOnList;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigInfo;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigOnList;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-advert", path = "/v1/market/img", fallbackFactory = LejiaAdvertFallback.class, configuration = FeignConfig.class)
public interface LejiaAdvertApi
{
    
    @Operation(summary = "新增广告", tags = ApiTags.custAdvert)
    @PostMapping("/ins")
    public Result<MktAdvertOnList> insAdvert(@RequestBody @Valid MktAdvertOnList entity);
    
    @Operation(summary = "新增专区广告", tags = ApiTags.custAdvert)
    @PostMapping("/special/ins")
    public Result<MktAdvertOnList> insSpecialAdvert(@RequestBody MktAdvertOnList entity);
    
    @Operation(summary = "获取广告", tags = ApiTags.custAdvert)
    @PostMapping("/get")
    public Result<MktAdvertOnList> getAdvert(@RequestParam(value = "pkey") @Parameter(description = "广告主键") Integer pkey);
    
    @Operation(summary = "获取广告列表", tags = ApiTags.custAdvert)
    @PostMapping(value = "/query")
    public Result<PageResult<MktAdvertOnList>> queryAdvert(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "1000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "position", required = false) @Parameter(description = "位置") AdvertPosition position);
    
    @Operation(summary = "获取专区广告列表", tags = ApiTags.custAdvert)
    @PostMapping(value = "/special/query")
    public Result<PageResult<MktAdvertOnList>> querySpecialAdvert(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "position", required = false) @Parameter(description = "位置") AdvertPosition position,
        @RequestParam(value = "farmers", required = false) List<String> farmers);
    
    @Operation(summary = "修改广告", tags = ApiTags.custAdvert)
    @PostMapping(value = "/upd")
    public Result<MktAdvertOnList> updAdvert(@RequestParam(name = "pkey") Integer pkey,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "position", required = false) AdvertPosition position,
        @RequestParam(name = "positionObj", required = false) String positionObj,
        @RequestParam(name = "photo", required = false) String photo,
        @RequestParam(name = "urlType", required = false) LinkType urlType,
        @RequestParam(name = "objKey", required = false) String objKey,
        @RequestParam(name = "sort", required = false) Integer sort,
        @RequestParam(value = "farmers", required = false) @Parameter(description = "投放市场") List<String> farmers,
        @RequestParam(value = "locationType", required = false) @Parameter(description = "位置") LocationType locationType,
        @RequestParam(value = "targerKeys", required = false) @Parameter(description = "标签")  List<Integer> targerKeys,
        @RequestParam(value = "visibleRange", required = false) @Parameter(description = "用户可见范围")MemberVisibleRange visibleRange
        );
    
    @Operation(summary = "删除广告", tags = ApiTags.custAdvert)
    @PostMapping(value = "/del")
    public Result<Boolean> delAdvert(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "广告启用", tags = ApiTags.custAdvert)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startAdvert(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "广告停用", tags = ApiTags.custAdvert)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopAdvert(@RequestParam(name = "pkey") Integer pkey);
    
//    /*
//         * 组合广告是否启用
//     * 
//     * */
//    
//    @Operation(summary = "(通用)获取组合广告启动/停用", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/combination/enable")
//    public Result<Boolean> enableCombinationAdvert(@RequestParam(name = "pkey") Integer pkey,Boolean  enabled);
//    
//    
//    @Operation(summary = "(市场端)获取组合广告列表", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/combination/query")
//    public Result<PageResult<MktCombinationAdviseOnList>> queryCombinationAdvert(
//        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
//        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小", hidden = true) int pagesize,
//        @RequestParam(value = "farmers", required = false) @Parameter(description = "投放市场") List<String> farmers);
//    
//    
//    
//    
//    
//    @Operation(summary = "(通用)获取组合广告详情", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/combination/get")
//    public Result<MktCombinationAdviseInfo> getCombinationAdvert(@RequestParam(name = "pkey") Integer pkey);
//    
//    
//    @Operation(summary = "(市场端)新增或者修改组合广告详情", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/combination/upd")
//    public Result<Boolean> updCombinationAdvert(@RequestBody @Valid MktCombinationAdviseInfo info );
//    
//    
//    
//    
//    @Operation(summary = "(通用)删除组合广告", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/combination/del")
//    public Result<Boolean> delCombinationAdvert(@RequestParam(name = "pkey") Integer pkey);
//    
//    
//    
//    @Operation(summary = "(中心端)获取组合广告列表", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/center/combination/query")
//    public Result<PageResult<CenterMktCombinationAdviseOnList>> queryCenterMktCCombinationAdvert(
//        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
//        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小", hidden = true) int pagesize);
//    
//    @Operation(summary = "(中心端)新增组合广告详情", tags = ApiTags.custAdvert)
//    @PostMapping(value = "/combination/add")
//    public Result<Boolean> addCombinationAdvert(@RequestBody @Valid CenterMktCombinationAdviseOnList info );
    
    
    
    /**
     * 功能菜单配置
     */
    @Operation(summary = "(通用)功能菜单配置启动/停用", tags = ApiTags.custAdvert)
    @PostMapping(value = "/funmenu/config/enable")
    public Result<Boolean> enableFunMenu(@RequestParam(name = "pkey") Integer pkey,Boolean  enabled);
    
    
    @Operation(summary = "(市场端)获取功能菜单配置列表", tags = ApiTags.custAdvert)
    @PostMapping(value = "/funmenu/config/query")
    public Result<PageResult<MktFunMenuConfigOnList>> queryFunMenuConfig(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小", hidden = true) int pagesize);
    
    
    
    
    @Operation(summary = "获取功能菜单配详情", tags = ApiTags.custAdvert)
    @PostMapping(value = "/funmenu/config/get")
    public Result<MktFunMenuConfigInfo> getFunMenuConfig(@RequestParam(name = "pkey") Integer pkey);
    
    
    @Operation(summary = "修改或者新增功能菜单配详情", tags = ApiTags.custAdvert)
    @PostMapping(value = "/funmenu/config/upd")
    public Result<Boolean> updFunMenuConfig(@RequestBody @Valid MktFunMenuConfigInfo info ); 
    
    @Operation(summary = "删除功能菜单配置", tags = ApiTags.custAdvert)
    @PostMapping(value = "/funmenu/del")
    public Result<Boolean> delFunMenuConfig(@RequestParam(name = "pkey") Integer pkey);
    
}
