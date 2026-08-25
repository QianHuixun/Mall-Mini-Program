package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MktOrderCountAmt
{
    private Integer pkey;
    
    private BigDecimal amt;
    
    private Long count;
}
