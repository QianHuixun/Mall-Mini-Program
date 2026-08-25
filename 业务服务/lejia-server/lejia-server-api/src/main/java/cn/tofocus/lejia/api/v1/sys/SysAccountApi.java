package cn.tofocus.lejia.api.v1.sys;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.sys.SysAccountShieldVersion;
import io.swagger.v3.oas.annotations.Operation;

public interface SysAccountApi
{
    @Operation(summary = "获取版本屏蔽设置", tags = ApiTags.custAccount)
    @PostMapping(value = "/shieldVersion/get")
    Result<SysAccountShieldVersion> getShieldVersion();
    
    @Operation(summary = "保存版本屏蔽设置", tags = ApiTags.custAccount)
    @PostMapping(value = "/shieldVersion/save")
    Result<Boolean> saveShieldVersion(@RequestBody SysAccountShieldVersion shieldVersion);
}
