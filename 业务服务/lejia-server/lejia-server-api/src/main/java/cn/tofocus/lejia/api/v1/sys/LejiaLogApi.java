package cn.tofocus.lejia.api.v1.sys;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.sys.SysLogOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-log", path = "/v1/sys/log", 
fallbackFactory = LejiaLogFallback.class, configuration = FeignConfig.class)
public interface LejiaLogApi 
{
	@Operation(summary = "获取日志列表", tags = ApiTags.custLog)
    @PostMapping(value = "/query")
    public Result<PageResult<SysLogOnList>> queryLog(
        @RequestParam(value = "page", defaultValue = "0" ) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "截止时间") String endTime);
}
