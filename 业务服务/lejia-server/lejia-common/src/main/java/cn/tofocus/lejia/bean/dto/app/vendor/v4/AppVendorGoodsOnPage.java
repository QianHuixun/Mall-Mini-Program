package cn.tofocus.lejia.bean.dto.app.vendor.v4;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorGoodsOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "分类")
    private Integer gtype;
    
    @Schema(description = "商品库")
    private Integer goodsMain;
    
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    private MType mType;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "照片1")
    private List<String> photo1;
    
    @Schema(description = "照片2")
    private String photo2;
    
    @Schema(description = "照片3")
    private String photo3;
    
    @Schema(description = "起售日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "浏览数量")
    private Integer viewCount;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "价格范围-最大")
    private BigDecimal maxPrice;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "商品状态名称")
    private String statusName;
    
    public Integer getKcNum()
    {
        if(spaces == null || spaces.isEmpty())
            return 0;
        int kn = 0;
        for(AppVendorGoodsSpaceOnList s : spaces)
            kn = kn + s.getKcNum();
        return kn;
    }
    
    @Schema(description = "规格")
    private List<AppVendorGoodsSpaceOnList> spaces;
}
