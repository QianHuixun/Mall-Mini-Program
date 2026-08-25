package cn.tofocus.lejia.api.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktAdviseOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-advise", path = "/v1/market/advise", 
fallbackFactory = LejiaAdviseFallback.class, configuration = FeignConfig.class)
public interface LejiaAdviseApi 
{
	
	
	@Operation(summary = "获取建议反馈列表", tags = ApiTags.custAdviset)
    @PostMapping(value = "/query")
    public Result<PageResult<MktAdviseOnList>> queryAdviset(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "1000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "提交人手机") String mobile);
	
	@Operation(summary = "删除建议反馈", tags = ApiTags.custAdviset)
    @PostMapping(value = "/del")
    public Result<Boolean> delAdviset(@RequestParam(name = "pkey") Integer pkey);
	
	
	
}
