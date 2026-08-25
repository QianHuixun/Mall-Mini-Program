package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PackingChargeStatistics
{
    @Schema(description = "订单笔数")
    private Integer orderCount;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "列表明细")
    private PageResult<PackingChargeOnPage> lines;
}
