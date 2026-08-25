package cn.tofocus.lejia.api.v3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.v3.GzhUserOnInfo;
import cn.tofocus.lejia.domain.v3.GzhV3Manager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v3/sys/market/gzh")
@RestController
public class MarketGzhApiV3Impl
{
    
    @Autowired
    private GzhV3Manager manager;
    
    @Operation(summary = "获取公众号关注列表", tags = ApiTags.wx_gzh)
    @PostMapping(value = "/query")
    public Result<PageResult<GzhUserOnInfo>> queryGzh(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "会员名称") String name,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "状态") Boolean enabled)
    {
        return new Result<>(manager.queryGzh(page, pagesize, name, enabled));
    }
    @Operation(summary = "开启消息推送", tags = ApiTags.wx_gzh)
    @PostMapping(value = "/enabled/start")
    public Result<Boolean> enabledStart(@RequestParam(value = "pkey")Integer pkey)
    {
        return new Result<>(manager.enabled(pkey, true));
    }
    
    @Operation(summary = "关闭消息推送", tags = ApiTags.wx_gzh)
    @PostMapping(value = "/enabled/stop")
    public Result<Boolean> enabledStop(@RequestParam(value = "pkey")Integer pkey)
    {
        return new Result<>(manager.enabled(pkey, false));
    }
    
}
