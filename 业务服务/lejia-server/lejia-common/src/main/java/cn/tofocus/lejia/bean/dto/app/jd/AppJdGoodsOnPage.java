package cn.tofocus.lejia.bean.dto.app.jd;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppJdGoodsOnPage
{
    @Schema(description = "skuid")
    private Long pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "主商品ID")
    private Long spuId;
    
    @Schema(description = "照片1")
    private List<String> photo1;

    @Schema(description = "购物车数量")
    private Integer gwcNum;
    
    @Schema(description = "最低起购量")
    private Integer lowestBuy;
    
    private String wrapperPhoto;
    
//    @Schema(description = "规格")
//    private String space1;
//    
//    @Schema(description = "规格")
//    private String space2;
//    
//    @Schema(description = "规格")
//    private String space3;
//    
//    @Schema(description = "规格")
//    private String space4;
//    
//    @Schema(description = "规格")
//    private String space5;
//    
//    @Schema(description = "规格")
//    private String space6;
//    
//    @Schema(description = "规格")
//    private String space7;
//    
//    @Schema(description = "规格")
//    private String space8;
//    
//    @Schema(description = "规格")
//    private String space9;
//    
//    @Schema(description = "规格")
//    private String space10;
//    
//    @Schema(description = "规格")
//    private String spaceValue1;
//    
//    @Schema(description = "规格")
//    private String spaceValue2;
//    
//    @Schema(description = "规格")
//    private String spaceValue3;
//    
//    @Schema(description = "规格")
//    private String spaceValue4;
//    
//    @Schema(description = "规格")
//    private String spaceValue5;
//    
//    @Schema(description = "规格")
//    private String spaceValue6;
//    
//    @Schema(description = "规格")
//    private String spaceValue7;
//    
//    @Schema(description = "规格")
//    private String spaceValue8;
//    
//    @Schema(description = "规格")
//    private String spaceValue9;
//    
//    @Schema(description = "规格")
//    private String spaceValue10;
}
