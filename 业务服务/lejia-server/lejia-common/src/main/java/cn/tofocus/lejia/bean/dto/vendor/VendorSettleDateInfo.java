package cn.tofocus.lejia.bean.dto.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorSettleDateInfo
{
    private Long start;
    
    private Long end;
    
    @Schema(description = "颜色， true: 红色")
    private Boolean colour;
}
