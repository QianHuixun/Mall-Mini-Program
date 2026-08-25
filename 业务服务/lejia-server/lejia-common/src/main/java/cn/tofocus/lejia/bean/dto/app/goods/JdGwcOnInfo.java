package cn.tofocus.lejia.bean.dto.app.goods;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGwcOnInfo
{
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "用户")
    private Integer member;

    @Schema(description = "商品")
    private Long goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品图片")
    private String photo;
    

    @Schema(description = "规格")
    private Long space;
    
    private String spaceName;

    
    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "num")
    private Integer num;
    

}
