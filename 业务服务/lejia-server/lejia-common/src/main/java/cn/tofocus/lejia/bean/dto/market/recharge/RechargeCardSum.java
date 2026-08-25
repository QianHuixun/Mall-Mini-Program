package cn.tofocus.lejia.bean.dto.market.recharge;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.RechargeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RechargeCardSum
{
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "面值合计")
    private BigDecimal sumCost;
    
    @Schema(description = "已使用张数量")
    private Integer useNum;
    
    @Schema(description = "已使用面值合计")
    private BigDecimal sumUseCost;
    
    @JsonIgnore
    private RechargeStatus status;
    
    public RechargeCardSum()
    {
        this.num = 0;
        this.sumCost = BigDecimal.ZERO;
        this.useNum = 0;
        this.sumUseCost = BigDecimal.ZERO;
    }
}
