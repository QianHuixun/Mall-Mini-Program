package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsProcessOnInfo
{
    @Schema(description = "加工主键")
    private Integer process;
    
    @Schema(description = "加工名称")
    private String processName;
    
    @Schema(description = "图片")
    private String photo;
    
    @Schema(description = "价格")
    private BigDecimal price;
}
