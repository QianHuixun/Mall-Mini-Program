package cn.tofocus.lejia.bean.dto.app.jd;

import java.math.BigDecimal;
import java.util.List;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdOrderInfo
{
    @Schema(description = "商品明细,提交订单只需传这个参数")
    private List<JdGoodsOnList> goodsList;
    
    @Schema(description = "备注,提交订单只需传这个参数")
    private String remark;
    
    @Schema(description = "配送费")
    private BigDecimal postage;
    
    @Schema(description = "订单金额")
    private BigDecimal sales;
    
    @Schema(description = "商品图片")
    private List<String> goodsPhotos;
    
    @Schema(description = "")
    private String supplierName;
}
