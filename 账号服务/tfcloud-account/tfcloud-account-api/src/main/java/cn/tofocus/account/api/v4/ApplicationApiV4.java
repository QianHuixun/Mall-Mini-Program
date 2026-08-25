package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.account.api.ApiTags;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "account", contextId = "applicationV4", path = "/v4/application", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface ApplicationApiV4
{
    @PostMapping(value = "/listFrontEndAppName")
    @Operation(summary = "前端应用名称下拉", tags = ApiTags.app)
    Result<List<StrKeyName>> listFrontEndAppName(@RequestParam(value = "domain") String domain);
}
