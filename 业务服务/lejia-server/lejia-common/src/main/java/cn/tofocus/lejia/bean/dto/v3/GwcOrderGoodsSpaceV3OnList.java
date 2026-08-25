package cn.tofocus.lejia.bean.dto.v3;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GwcOrderGoodsSpaceV3OnList
{
    @Schema(description = "规格")
    private Integer space;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "要加工商品主键")
    private Integer association;
    
    @Schema(description = "要加工商品名称")
    private String associationName;
    
    @Schema(description = "购物车主键")
    private Integer gwcPkey;
    
    // **************
    
    @Schema(description = "毛重")
    @JsonIgnore
    private BigDecimal weight;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "会员价")
    private BigDecimal priceMember;
    
    @Schema(description = "积分")
    private Integer point;
    
    @Schema(description = "规格图片")
    private String photo;
}
