package cn.tofocus.lejia.bean.dto.wanli;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WanliCourierOnInfo
{
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "距离")
    private BigDecimal distance;
    
    private String name;
    
    private String mobile;
    
    @Schema(description = "会员位置经度")
    private BigDecimal memberLongitude;
    
    @Schema(description = "会员位置纬度")
    private BigDecimal memberLatitude;
    
    @Schema(description = "市场位置经度")
    private BigDecimal marketLongitude;
    
    @Schema(description = "市场位置纬度")
    private BigDecimal marketLatitude;
}
