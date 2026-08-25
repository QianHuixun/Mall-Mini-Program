package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WithdrawalOnInfo
{
    @Schema(description = "打款笔数")
    private Integer num;
    
    @Schema(description = "待提现金额")
    private BigDecimal amount;
    
    @Schema(description = "提现明细")
    private PageResult<WithdrawalOnPage> withdrawalOnPage;
}
