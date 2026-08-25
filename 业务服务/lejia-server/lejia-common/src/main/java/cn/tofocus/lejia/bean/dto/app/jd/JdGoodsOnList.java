package cn.tofocus.lejia.bean.dto.app.jd;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsOnList
{
    @Schema(description = "skuid主键,提交订单只需传这个参数")
    private Long space;
    
    @Schema(description = "数量,提交订单只需传这个参数")
    private Integer num;
    
    @Schema(description = "购物车主键,提交订单只需传这个参数")
    private Integer gwcPkey;
    
    @Schema(description = "规格详情")
    private List<JdGoodsSpaceOnInfo> spaceList;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "价格")
    private BigDecimal price;
}
