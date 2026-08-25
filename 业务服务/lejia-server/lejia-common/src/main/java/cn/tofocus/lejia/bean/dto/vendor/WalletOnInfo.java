package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletOnInfo
{
    @Schema(description = "可提现余额")
    private BigDecimal walletAmt;
    
    @Schema(description = "待结算金额")
    private BigDecimal settlementAmt;
    
    @Schema(description = "列表明细")
    private PageResult<WalletOnPage> walletOnPage;
}
