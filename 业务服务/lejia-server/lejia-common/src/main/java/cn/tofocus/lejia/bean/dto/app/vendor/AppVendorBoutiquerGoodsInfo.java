package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppVendorBoutiquerGoodsInfo
{
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    @Schema(description = "列表小图")
    private String wrapperPhoto;
    
}
