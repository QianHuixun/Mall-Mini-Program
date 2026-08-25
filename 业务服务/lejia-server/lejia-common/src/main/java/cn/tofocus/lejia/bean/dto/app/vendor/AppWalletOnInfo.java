package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppWalletOnInfo
{
    @Schema(description = "可提现余额")
    private BigDecimal walletAmt;
    
    @Schema(description = "待结算金额")
    private BigDecimal settlementAmt;
}
