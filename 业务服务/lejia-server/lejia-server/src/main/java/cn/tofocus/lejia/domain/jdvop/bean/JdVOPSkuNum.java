package cn.tofocus.lejia.domain.jdvop.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JdVOPSkuNum
{
    private long skuId;
    
    private int skuNum;
    
    public int getSkuNumber()
    {
        return skuNum;
    }
}
