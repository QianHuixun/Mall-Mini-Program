package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppSaasInfo
{
    @Schema(description = "logo")
    private String photo;
    
    @Schema(description = "商城小程序名称")
    private String userName;
    
    @Schema(description = "商户小程序名称")
    private String vendorName;
    
    @Schema(description = "骑手小程序名称")
    private String courierName;
    
    @Schema(description = "微信公众号名称")
    private String wxName;
    
}
