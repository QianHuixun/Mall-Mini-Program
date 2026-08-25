package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsSpuOnPage
{
    @Schema(description = "skuid")
    private Long pkey;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "照片1")
    private List<String> photo1;
    
    @Schema(description = "sku数量")
    private Long skuNum;
    
    @Schema(description = "微信商品详情")
    private String introduceWechat;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "京东销售价，实际下单价格以此为准")
    private BigDecimal salePrice;
    
    @Schema(description = "主站上下架状态 (1上架 0下架)")
    private Integer skuState;
    
    @Schema(description = "主商品ID")
    private Long spuId;
    
    @Schema(description = "主商品名称")
    private String spuName;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
}
