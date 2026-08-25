package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorPackingChargeInfo
{
  private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "区间")
    private Integer grade;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;
}
