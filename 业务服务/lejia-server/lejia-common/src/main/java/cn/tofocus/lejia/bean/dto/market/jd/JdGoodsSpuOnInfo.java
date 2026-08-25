package cn.tofocus.lejia.bean.dto.market.jd;

import java.util.List;

import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsSpuOnInfo
{
    @Schema(description = "主商品ID")
    private Long spuId;
    
    @Schema(description = "主商品名称")
    private String spuName;
    
    @Schema(description = "商品分类(已经组合好 xxx分类/xxx分类/xxx分类)")
    private String categoryName;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "标签主键")
    private List<Integer> tagKeys;
    
    @Schema(description = "sku商品明细")
    private List<JdGoodsSkuOnInfo> skuList;
}
