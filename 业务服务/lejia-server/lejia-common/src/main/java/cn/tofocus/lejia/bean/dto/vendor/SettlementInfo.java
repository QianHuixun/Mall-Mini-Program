package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettlementInfo
{
//    @Schema(description = "结算日期")
//    private String time;
    
    @Schema(description = "总商户数")
    private Integer numMerchant = 0;
    
    @Schema(description = "总采购笔数")
    private Integer num = 0;
    
    @JsonIgnore
    private BigDecimal amt = BigDecimal.ZERO;
    
    @JsonIgnore
    private BigDecimal awaitAmt = BigDecimal.ZERO;
    
    @Schema(description = "明细")
    private PageResult<SettlementLineOnList> lines;
    
    @Schema(description = "总采购价格")
    public String getAmtStr()
    {
        if (amt != null) return amt.stripTrailingZeros().toPlainString();
        return "0";
    }
    
    @Schema(description = "总结算金额")
    public String getAwaitAmtStr()
    {
        if (awaitAmt != null) return awaitAmt.stripTrailingZeros().toPlainString();
        return "0";
    }
    
}
