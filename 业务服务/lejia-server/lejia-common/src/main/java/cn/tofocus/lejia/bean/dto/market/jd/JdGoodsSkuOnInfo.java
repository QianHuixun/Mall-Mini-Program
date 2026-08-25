package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsSkuOnInfo
{
    @Schema(description = "skuid")
    private Long pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "照片1")
    private List<String> photo1;
    
    @Schema(description = "价格(商城销售价格)")
    private BigDecimal price;
    
    @Schema(description = "京东价格")
    private BigDecimal salePrice;
    
    @Schema(description = "主站上下架状态 (1上架 0下架)")
    private Integer skuState;
    
    @Schema(description = "状态")
    private Boolean enabled;
    
    @Schema(description = "规格")
    private String space1;
    
    @Schema(description = "规格")
    private String space2;
    
    @Schema(description = "规格")
    private String space3;
    
    @Schema(description = "规格")
    private String space4;
    
    @Schema(description = "规格")
    private String space5;
    
    @Schema(description = "规格")
    private String space6;
    
    @Schema(description = "规格")
    private String space7;
    
    @Schema(description = "规格")
    private String space8;
    
    @Schema(description = "规格")
    private String space9;
    
    @Schema(description = "规格")
    private String space10;
    
    @Schema(description = "规格")
    private String spaceValue1;
    
    @Schema(description = "规格")
    private String spaceValue2;
    
    @Schema(description = "规格")
    private String spaceValue3;
    
    @Schema(description = "规格")
    private String spaceValue4;
    
    @Schema(description = "规格")
    private String spaceValue5;
    
    @Schema(description = "规格")
    private String spaceValue6;
    
    @Schema(description = "规格")
    private String spaceValue7;
    
    @Schema(description = "规格")
    private String spaceValue8;
    
    @Schema(description = "规格")
    private String spaceValue9;
    
    @Schema(description = "规格")
    private String spaceValue10;
}
