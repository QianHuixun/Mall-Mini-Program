package cn.tofocus.lejia.bean.dto.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysAccountShieldVersion
{
    @Schema(description = "商城小程序版本号")
    private String userVersion;
    
    @Schema(description = "商户小程序版本号")
    private String vendorVersion;
    
    @Schema(description = "骑手小程序版本号")
    private String courierVersion;
}
