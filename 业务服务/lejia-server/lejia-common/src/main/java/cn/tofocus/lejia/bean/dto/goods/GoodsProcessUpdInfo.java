package cn.tofocus.lejia.bean.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsProcessUpdInfo
{
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "对应加工商品主键")
    private Integer process;
}
