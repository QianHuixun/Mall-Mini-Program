package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppWalletOrderOnInfo
{
    @Schema(description = "预计结算时间/已结算时间")
    private String settlementTime;
    
    @Schema(description = "账单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "订单金额")
    private BigDecimal amt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;
    
    @Schema(description = "是否显示打包费用 true:显示")
    private Boolean isPackingCharge;
    
    @Schema(description = "明细")
    private List<AppWalletOrderOnList> appWalletOrderOnList;
}
