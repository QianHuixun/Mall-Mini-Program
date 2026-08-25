package cn.tofocus.lejia.bean.dto.h5;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class H5GoodsSpaceOnInfo
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "规格")
    private String space;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "库存数量")
    private Integer kcNum;
    
    @Schema(description = "销售数量")
    private Integer xsNum;
    
}
