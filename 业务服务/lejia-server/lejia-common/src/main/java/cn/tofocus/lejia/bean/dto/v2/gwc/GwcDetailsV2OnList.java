package cn.tofocus.lejia.bean.dto.v2.gwc;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 购物车
 *
 * @author zdw 2020-07-16
 */

@Data
public class GwcDetailsV2OnList
{
    
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "规格")
    private Integer space;
    
    @Schema(description = "规格名称")
    private String spaceName = "";
    
    @Schema(description = "商品图片")
    private String photo1;
    
    @Schema(description = "单个价格")
    private BigDecimal price;
    
    public String getPrice()
    {
        if (price != null)
        {
            return price.toString();
        }
        return null;
    }
    
    private BigDecimal priceOld;
    
    public String getPriceOld()
    {
        if (priceOld != null)
        {
            return priceOld.toString();
        }
        return null;
    }
    
    private BigDecimal priceMember;
    
    public String getPriceMember()
    {
        if (priceMember != null)
        {
            if(priceMember.compareTo(new BigDecimal(0)) == 0)
                return null;
            return priceMember.toString();
        }
        return null;
    }
    
    @Schema(description = "数量")
    private Integer num;
    
    private Integer kcNum;
    
}
