package cn.tofocus.lejia.bean.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsWithGoodsMainDTO
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "商品库")
    private Integer goodsMain;
    
    @Schema(description = "标题")
    private String title;
}
