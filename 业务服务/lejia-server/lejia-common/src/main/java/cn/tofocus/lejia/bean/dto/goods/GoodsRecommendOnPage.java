package cn.tofocus.lejia.bean.dto.goods;

import java.util.List;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsRecommendZone;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsRecommendOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    //@JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "mType")
    private MType mType;
    
    @Schema(description = "类型")
    @JoinEnum(from = "mType", order = 1001)
    private String mTypeName;
    
    @Schema(description = "商户")
    //@JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "vendor")
    private Integer vendor;
    
    @Schema(description = "商户名称")
    @JoinProperty(dataQuery = "mktVendorDao", from = "vendor", propertyName = "displayName")
    private String vendorName;
    
    @Schema(description = "供应商主键")
    private Integer supplier;
    
    @Schema(description = "供应商名称")
    @JoinProperty(dataQuery = "mktSupplierDao", from = "supplier", propertyName = "name")
    private String supplierName;
    
    @Schema(description = "商品归属名称（商户/供应商）")
    public String getOwnerName()
    {
        if (vendor != null)
            return vendorName;
        if (supplier != null)
            return supplierName;
        return null;
    }
    
    @Schema(description = "商品图片")
    //@JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "photo1", type = MktGoods.class)
    private List<String> photo1;
    
    @Schema(description = "商品标题")
    //@JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "title")
    private String title;
    
    @Schema(description = "启用标志")
    //@JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "enabled")
    private Boolean enabled;
    
    @Schema(description = "是否上下架名称")
    public String getEnabledName()
    {
        return Boolean.TRUE.equals(enabled) ? "已上架" : "已下架";
    }
    
    @Schema(description = "推荐区域列表")
    @JoinProperty(dataQuery = "mktGoodsRecommendZoneDao", referencedName = "goodsRecommend", propertyName = "zone", type = MktGoodsRecommendZone.class)
    private List<GoodsRecommendZone> zones;
    
    @Schema(description = "推荐区域名称列表")
    public String getZoneNames()
    {
        if (zones == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (GoodsRecommendZone zone : zones)
        {
            sb.append(zone.getName()).append("、");
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "商品所属市场")
    private String goodsFarmer;
    
    @Schema(description = "商品所属市场名称")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "goodsFarmer")
    private String goodsFarmerName;
    
    public String getGoodsFarmerName()
    {
        if (goodsFarmer == null || ascription == null)
            return null;
        if (goodsFarmer.startsWith(Constant.Operation))
            return ascriptionName;
        return goodsFarmerName;
    }
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
    @Schema(description = "归属主键名称")
    @JoinDTO(dataQuery = "sysAscriptionDao", from = "ascription")
    private String ascriptionName;
}