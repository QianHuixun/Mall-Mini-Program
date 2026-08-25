package cn.tofocus.lejia.bean.dto.finance;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FundDetailsTotal
{
    @Schema(description = "可提现金额")
    private BigDecimal makePaymentAmt;
    
    @Schema(description = "待结算金额")
    private BigDecimal pendingSettlementAmt;
    
    @Schema(description = "银行账号")
    private String pan;
}
