package cn.tofocus.lejia.bean.dto.v2.screen;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SalesRank2OnList
{
    @Schema(description = "商品名称")
    private String name;
    
    @Schema(description = "商品分类名称")
    @JoinDTO(dataQuery = "mktGtypeDao", from = "gtype")
    private String typeName;
    
    @Schema(description = "市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "market")
    private String marketName;
    
    @Schema(description = "销售金额")
    public String getSales()
    {
        if (orderSales != null)
            return orderSales.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        return "0";
    }
    
    @Schema(description = "笔数")
    private Integer num;
    
    @Schema(description = "占比")
    public String getProportion()
    {
        if (percentage != null)
            return percentage.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        return "0";
    }
    
    @JsonIgnore
    private BigDecimal percentage;
    
    @JsonIgnore
    private BigDecimal orderSales;
    
    @JsonIgnore
    private String market;
    
    @JsonIgnore
    private Integer gtype;
    
    @JsonIgnore
    private Integer goods;
}
