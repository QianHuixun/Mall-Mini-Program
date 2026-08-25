package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettlementTotalInfo
{
    
    @Schema(description = "结算时间", required = false)
    private String time;
    
    @Schema(description = "优惠金额", required = false)
    private BigDecimal amtn;
    
    @Schema(description = "优惠金额", required = false)
    private BigDecimal discountAmt;
    
    @Schema(description = "邮费", required = false)
    private BigDecimal postage;
    
}
