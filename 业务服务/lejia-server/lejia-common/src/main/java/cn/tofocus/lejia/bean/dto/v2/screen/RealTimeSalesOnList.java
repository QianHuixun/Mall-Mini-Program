package cn.tofocus.lejia.bean.dto.v2.screen;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RealTimeSalesOnList
{
    private String time;
    
    @Schema(description = "销售金额")
    public String getSales()
    {
        if(orderSales != null)
            return orderSales.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        return "";
    }
    
    @JsonIgnore
    private BigDecimal orderSales;
    
    @JsonIgnore
    private Date date;
    
    
}
