package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppRecommendGoodsOnPage
{
    @Schema(description = "商品主键")
    private Integer pkey;
    
    @Schema(description = "商品推荐主键")
    private Integer recommendPkey;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    private MType mType;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "商户主键")
    @JoinProperty(dataQuery = "mktVendorDao", from = "vendor", propertyName = "displayName")
    private String vendorName;
    
    @Schema(description = "商品图片")
    @JoinProperty(dataQuery = "mktGoodsDao", from = "pkey", propertyName = "photo1", type = MktGoods.class)
    private List<String> photo1;
    
    @Schema(description = "列表小图")
    public String getWrapperPhoto()
    {
        if (getPhoto1() == null)
            return "";
        return !getPhoto1().isEmpty() ? getPhoto1().get(0) : "";
    }
    
    @Schema(description = "商品标题")
    //@JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "title")
    private String title;

    @Schema(description = "商品标签")
    private String tag;
    
    @Schema(description = "卖点列表")
    @JoinProperty(dataQuery = "mktGoodsSellingPointDao", referencedName = "goods", propertyName = "content", type = String.class)
    private List<String> sellingPoints;
    
    @Schema(description = "购物车商品数量")
    private Integer gwcNum = 0;
    
    @Schema(description = "规格")
    private List<MktGoodsSpaceOnList> spaces;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    public BigDecimal getPrice()
    {
        if (getSpaces() != null && !getSpaces().isEmpty())
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.POVERTY_ALLEVIATION_GOODS)
                || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getPrice();
            else
                return getSpaces().get(0).getPrice();
        }
        return price;
    }
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    public BigDecimal getPriceOld()
    {
        if (getSpaces() != null && !getSpaces().isEmpty())
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
            {
                if (getSpaces().get(getSpaces().size() - 1).getPriceOld().compareTo(getPrice()) < 0)
                    return getPrice();
                return getSpaces().get(getSpaces().size() - 1).getPriceOld();
            }
            else
            {
                if (getSpaces().get(0).getPriceOld().compareTo(getPrice()) < 0)
                    return getPrice();
                return getSpaces().get(0).getPriceOld();
            }
        }
        return priceOld;
    }
    
    @Schema(description = "会员价")
    private BigDecimal priceMember;
    
    public BigDecimal getPriceMember()
    {
        if (getSpaces() != null && !getSpaces().isEmpty())
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getPriceMember();
            else
                return getSpaces().get(0).getPriceMember();
        }
        return priceMember;
    }
    
    @Schema(description = "积分")
    private Integer point;
    
    public Integer getPoint()
    {
        if (getSpaces() != null && !getSpaces().isEmpty())
        {
            if (mType != null && (mType.equals(MType.SHARE_GOODS) || mType.equals(MType.SPECIAL_GOODS)
                || mType.equals(MType.POVERTY_ALLEVIATION_GOODS) || mType.equals(MType.PRESALE_GOODS)))
                return getSpaces().get(getSpaces().size() - 1).getPoint();
            else
                return getSpaces().get(0).getPoint();
        }
        return point;
    }
}
