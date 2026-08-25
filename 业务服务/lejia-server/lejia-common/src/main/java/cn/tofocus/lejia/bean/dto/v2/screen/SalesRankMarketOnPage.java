package cn.tofocus.lejia.bean.dto.v2.screen;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SalesRankMarketOnPage
{
    
    
    @Schema(description = "市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "market")
    private String marketName;
    
    @Schema(description = "销售金额")
    public String getSales()
    {
        if (orderSales != null)
        {
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(orderSales.setScale(0, BigDecimal.ROUND_HALF_UP));
        }
        return "";
    }
    
    public String getNum()
    {
        if(num != null)
        {
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(num);
        }
        return "0";
    }
    
    @Schema(description = "笔数")
    private Integer num;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @JsonIgnore
    private BigDecimal orderSales;
    
    @JsonIgnore
    private String market;
    
}
