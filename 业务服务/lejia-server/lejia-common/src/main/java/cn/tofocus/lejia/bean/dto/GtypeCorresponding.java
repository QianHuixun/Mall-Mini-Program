package cn.tofocus.lejia.bean.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeCorresponding
{
    @Schema(description = "分类")
    private Integer gtype;
    
    @Schema(description = "二级分类")
    private Integer goodsMain;
    
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "原先分类")
    private Integer yGtype;
    
    @Schema(description = "原二级分类")
    private Integer yGoodsMain;
    
    @Schema(description = "原先三级分类")
    private Integer yThreeGtype;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
