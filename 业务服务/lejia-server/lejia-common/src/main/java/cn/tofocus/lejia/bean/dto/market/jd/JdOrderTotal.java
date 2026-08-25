package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdOrderTotal
{
    @Schema(description = "订单笔数")
    private Long count;
    
    @Schema(description = "总金额")
    private BigDecimal sum;
}
