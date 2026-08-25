package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOperatingStatisticsDTO
{
    @Schema(description = "访问人数")
    private int accCount = 0;
    
    @Schema(description = "成交订单")
    private int orderCount = 0;
    
    @Schema(description = "营收金额")
    private BigDecimal revenueAmt = BigDecimal.ZERO;
    
}
