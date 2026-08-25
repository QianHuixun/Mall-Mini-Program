package cn.tofocus.lejia.bean.dto.jd;

import java.math.BigDecimal;

import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsDto
{
    private Long pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "用户可见范围")
    private String visibleRangeName;
    
    @Schema(description = "标签,多个标签逗号隔开,")
    private String tag;
    
    @Schema(description = "京东价格")
    private BigDecimal salePrice;
    
    @Schema(description = "价格(商城销售价格)")
    private BigDecimal price;
    
    @Schema(description = "状态, TRUE: 上架; FALSE: 下架")
    private Boolean enabled;
    
    @Schema(description = "商品分类")
    private String categoryName;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "二级分类名称")
    private String twoCategoryName;
    
    @Schema(description = "三级分类名称")
    private String threeCategoryName;
}
