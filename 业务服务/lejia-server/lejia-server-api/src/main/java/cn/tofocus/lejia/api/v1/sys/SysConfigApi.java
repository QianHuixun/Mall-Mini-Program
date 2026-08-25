package cn.tofocus.lejia.api.v1.sys;

import cn.tofocus.lejia.bean.enums.PointType;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.account.bean.AppMenu;
import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-config", path = "/v1/sys/config", 
fallbackFactory = SysConfigFallback.class, configuration = FeignConfig.class)
public interface SysConfigApi
{
    @Operation(summary = "修改配置", tags = ApiTags.sysConfig)
    @PostMapping(value = "/upd")
    public Result<Boolean> upd(String pkey, Boolean flag);
    
    @Operation(summary = "获取配置", tags = ApiTags.sysConfig)
    @PostMapping(value = "/get")
    public Result<Boolean> get(String pkey);

    @Operation(summary = "运营端/市场端/公司端判断",  tags = ApiTags.sysConfig)
    @PostMapping(value = "/judgePoint")
    Result<PointType> judgePoint();
    
    @Operation(summary = "获取菜单",  tags = ApiTags.sysConfig)
    @PostMapping(value = "/getMenu")
    Result<List<AppMenu>> getMenu();
}
