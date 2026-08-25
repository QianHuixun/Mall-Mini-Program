package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderInfo
{
    @Schema(description = "总订单数")
    private Integer orderNum = 0;
    
    @Schema(description = "总采购数")
    private Integer num = 0;
    
    @Schema(description = "总采购金额")
    private BigDecimal amt = BigDecimal.ZERO;
    
    @Schema(description = "商品总价")
    private BigDecimal goodsTotalPrice = BigDecimal.ZERO;
    
    @Schema(description = "总结算金额")
    private BigDecimal totalAmt = BigDecimal.ZERO;
    
    
    @Schema(description = "订单")
    private PageResult<VendorOrderOnList> lines;
    
}
