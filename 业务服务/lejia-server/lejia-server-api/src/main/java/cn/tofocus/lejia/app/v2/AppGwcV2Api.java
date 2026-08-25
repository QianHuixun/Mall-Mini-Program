package cn.tofocus.lejia.app.v2;

import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.v2.gwc.GwcV2Info;
import io.swagger.v3.oas.annotations.Operation;

public interface AppGwcV2Api
{
    @Operation(summary = "获取购物车列表", tags = AppTags.mobileMemberGwcV2)
    @PostMapping(value = "/query")
    public Result<GwcV2Info> getGwc();
}
