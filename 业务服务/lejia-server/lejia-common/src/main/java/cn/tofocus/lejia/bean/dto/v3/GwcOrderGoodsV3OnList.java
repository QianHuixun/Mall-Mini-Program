package cn.tofocus.lejia.bean.dto.v3;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GwcOrderGoodsV3OnList
{
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "规格详情")
    private List<GwcOrderGoodsSpaceV3OnList> spaceList;
    
    @Schema(description = "是否包邮")
    @JsonIgnore
    private Boolean isPostage;
    
    @JsonIgnore
    private Integer gtype;
    
    @JsonIgnore
    private Integer vendor;
}
