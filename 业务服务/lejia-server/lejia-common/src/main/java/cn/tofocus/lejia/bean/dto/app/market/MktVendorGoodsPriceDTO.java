package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorGoodsPriceDTO
{
    @Schema(description = "商户")
    private Integer vendor;
    
    @Schema(description = "最后采购价格")
    private BigDecimal price;
    
    @Schema(description = "商户名称")
    private String vendorName;
    
    public String getVendorName()
    {
        if (vendor != null && vendor == 0) return "自采";
        return vendorName;
    }
}
