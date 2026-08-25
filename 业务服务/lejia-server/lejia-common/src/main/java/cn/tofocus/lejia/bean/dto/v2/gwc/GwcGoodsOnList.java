package cn.tofocus.lejia.bean.dto.v2.gwc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GwcGoodsOnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "商品名称")
    private String goodsTitle;
    
    @Schema(description = "商品是否上架")
    private Boolean goodsEnabled;

    @Schema(description = "限购数量")
    private Integer goodsPurchaseNum;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    public String getMTypeName()
    {
        if (mType != null) return mType.getName();
        return "";
    }
    
    @JsonIgnore
    private MType mType;
    
    @Schema(description = "单个价格")
    private BigDecimal price;
    
    public String getPrice()
    {
        if(price != null)
        {
            return price.toString();
        }
        return "0.00";
    }
    
    private BigDecimal priceOld;
    
    public String getPriceOld()
    {
        if(priceOld != null)
        {
            return priceOld.toString();
        }
        return "0.00";
    }
    
    private BigDecimal priceMember;
    
    public String getPriceMember()
    {
        if(priceMember != null)
        {
            if(priceMember.compareTo(new BigDecimal(0)) == 0)
                return null;
            return priceMember.toString();
        }
        
        return null;
    }
    
    @Schema(description = "合计价格")
    private BigDecimal sumPrice;
    
    public String getSumPrice()
    {
        if(sumPrice != null)
        {
            return sumPrice.toString();
        }
        return "0.00";
    }
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "市场名称")
    private String farmerName;
    
    @Schema(description = "规格")
    private Integer space;
    
    @Schema(description = "规格名称")
    private String spaceName = "";
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "库存数量")
    private Integer kcNum;
    
    @Schema(description = "要加工的商品名称")
    private String associationName;
    
    @Schema(description = "规格")
    private List<GwcDetailsV2OnList> lines = new ArrayList<>();
}
