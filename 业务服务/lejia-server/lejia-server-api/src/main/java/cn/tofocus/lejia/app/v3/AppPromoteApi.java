package cn.tofocus.lejia.app.v3;

import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.v3.PromoteUpdDto;
import io.swagger.v3.oas.annotations.Operation;

public interface AppPromoteApi
{
    @Operation(summary = "获取推广信息", tags = AppTags.mobilePromoteV3)
    @PostMapping(value = "/get")
    public Result<PromoteUpdDto> get();
}
