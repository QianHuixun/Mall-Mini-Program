package cn.tofocus.lejia.bean.dto.v2.order;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GoodsCardInfo
{
    String farmer;
    
    Integer userType;
    
    Integer userGoods;
    
    BigDecimal cost;
    
}
