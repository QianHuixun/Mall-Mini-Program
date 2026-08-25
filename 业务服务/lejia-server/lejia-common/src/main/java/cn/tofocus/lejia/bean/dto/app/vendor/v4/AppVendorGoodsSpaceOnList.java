package cn.tofocus.lejia.bean.dto.app.vendor.v4;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorGoodsSpaceOnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "规格")
    private String space;
    
    @Schema(description = "照片1")
    private String photo1;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "库存数量")
    private Integer kcNum;
}
