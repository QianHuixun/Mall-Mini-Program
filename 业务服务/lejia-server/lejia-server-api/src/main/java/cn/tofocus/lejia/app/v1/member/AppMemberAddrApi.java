package cn.tofocus.lejia.app.v1.member;

import javax.validation.Valid;

import cn.tofocus.lejia.bean.dto.app.market.AppMemberAddrFourArea;
import cn.tofocus.lejia.bean.dto.app.market.JdAddressOption;
import cn.tofocus.lejia.bean.entity.jd.JdAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppMktAddrOnList;
import cn.tofocus.lejia.bean.enums.AddrType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-member-addr", path = "/v1/app/market/lm/member/addr", fallbackFactory = AppMemberFallback.class, configuration = FeignConfig.class)
public interface AppMemberAddrApi
{
    @Operation(summary = "新增地址", tags = AppTags.mobileAddr)
    @PostMapping("/ins")
    public Result<AppMktAddrOnList> insAddr(@RequestBody @Valid AppMktAddrOnList entity);
    
    @Operation(summary = "获取地址", tags = AppTags.mobileAddr)
    @PostMapping("/get")
    public Result<AppMktAddrOnList> getAddr(
        @RequestParam(value = "pkey") @Parameter(description = "地址主键") Integer pkey);
    
    @Operation(summary = "获取地址列表", tags = AppTags.mobileAddr)
    @PostMapping(value = "/query")
    public Result<PageResult<AppMktAddrOnList>> queryAddr(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "1000") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "type", defaultValue = "DELIVERY") @Parameter(description = "类型（默认为配送）") AddrType type);
    
    @Operation(summary = "修改地址", tags = AppTags.mobileAddr)
    @PostMapping(value = "/upd")
    public Result<AppMktAddrOnList> updAddr(@RequestBody @Valid AppMktAddrOnList entity);
    
    @Operation(summary = "删除地址", tags = AppTags.mobileAddr)
    @PostMapping(value = "/del")
    public Result<Boolean> delAddr(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "设置默认地址", tags = AppTags.mobileAddr)
    @PostMapping(value = "/default")
    public Result<Boolean> defaultAddr(@RequestParam(name = "pkey") Integer pkey);

    @Operation(summary = "根据省市区获取街道列表", tags = AppTags.mobileAddr)
    @PostMapping("/listTown")
    public Result<List<JdAddressOption>> listTown(@RequestParam(value = "pro") @Parameter(description = "省") String pro,
        @RequestParam(value = "city") @Parameter(description = "市") String city,
        @RequestParam(value = "area") @Parameter(description = "区") String area);
    
    @Operation(summary = "根据经纬度获取省市区街道", tags = AppTags.mobileAddr)
    @PostMapping("/convertFourAreaByLatLng")
    public Result<AppMemberAddrFourArea> convertFourAreaByLatLng(
        @RequestParam(value = "longitude") @Parameter(description = "经度") BigDecimal longitude,
        @RequestParam(value = "latitude") @Parameter(description = "纬度") BigDecimal latitude);
    
}
