package cn.tofocus.lejia.bean.dto.data;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.tofocus.lejia.bean.enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SpecialAreaOnPage
{
    
    @Schema(description = "专区名称")
    public String getName()
    {
        if (orderType != null) return orderType.getName();
        return "";
    }
    
    @Schema(description = "销售额")
    @JsonProperty("Sales")
    private String Sales;
    
    @Schema(description = "销售笔数")
    @JsonProperty("SalesNum")
    private String SalesNum;
    
    @JsonIgnore
    private OrderType orderType;
    
    @JsonIgnore
    private BigDecimal amto;
}
