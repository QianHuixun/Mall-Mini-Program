package cn.tofocus.lejia.bean.dto.app.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppSupplierInfo
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "供应商名称")
    private String name;
    
    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "开始营业时间")
    private String startBusinessTime;
    
    @Schema(description = "结束营业时间")
    private String endBusinessTime;
    
    @Schema(description = "是否支持自提")
    private Boolean allowedPickup;
}
