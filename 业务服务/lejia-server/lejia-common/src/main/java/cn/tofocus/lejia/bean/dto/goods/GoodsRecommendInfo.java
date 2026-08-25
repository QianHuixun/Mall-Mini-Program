package cn.tofocus.lejia.bean.dto.goods;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsRecommendZone;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsRecommendInfo
{
    @Schema(description = "pkey")
    private Integer pkey;

    @NotBlank(message = "商品所属市场不能为空")
    @Schema(description = "商品所属市场")
    private String goodsFarmer;
    
    @NotNull(message = "商品不能为空")
    @Schema(description = "商品")
    private Integer goods;

    @NotEmpty(message = "至少选择一个推荐区域")
    @Schema(description = "推荐区域列表")
    @JoinProperty(dataQuery = "mktGoodsRecommendZoneDao", referencedName = "goodsRecommend", propertyName = "zone", type = MktGoodsRecommendZone.class)
    private List<GoodsRecommendZone> zones;
    
    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "来源商品主键")
    private Integer sourceGoods;
}
