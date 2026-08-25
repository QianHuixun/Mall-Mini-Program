package cn.tofocus.lejia.bean.dto.v3;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GwcSupplierPickupLocationInfo
{
    private Integer pkey;
    
    @Schema(description = "自提点地址")
    private String address;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "距离")
    private BigDecimal distance = BigDecimal.ZERO;
}
