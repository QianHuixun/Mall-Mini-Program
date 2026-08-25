package cn.tofocus.lejia.bean.dto.v2.gwc;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AmtoWeightTotal
{
    BigDecimal pointAmto = BigDecimal.ZERO;
    BigDecimal farmerAmto = BigDecimal.ZERO;
    BigDecimal pointWeight = BigDecimal.ZERO;
    BigDecimal farmerWeight = BigDecimal.ZERO;
    // 用来计算的重量
    BigDecimal pointCalculateWeight = BigDecimal.ZERO;
    BigDecimal farmerCalculateWeight = BigDecimal.ZERO;
    
    int points = 0;
}
