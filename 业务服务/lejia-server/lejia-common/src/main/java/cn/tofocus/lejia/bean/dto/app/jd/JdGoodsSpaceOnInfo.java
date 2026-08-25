package cn.tofocus.lejia.bean.dto.app.jd;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsSpaceOnInfo
{
    
    @Schema(description = "skuid主键")
    private Long space;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "购物车主键")
    private Integer gwcPkey;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "规格图片")
    private String photo;
}
