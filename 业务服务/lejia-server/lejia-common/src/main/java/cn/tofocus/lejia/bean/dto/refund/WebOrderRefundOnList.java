package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WebOrderRefundOnList
{
    @Schema(description = "明细主键")
    private Integer pkey;
    
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Schema(description = "规格主键")
    private Integer space;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "重量")
    private BigDecimal weight;
    
    @Schema(description = "实付金额")
    private BigDecimal sumPrice;
    
    @Schema(description = "剩余可退款金额")
    private BigDecimal surplusRefundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "积分")
    private Integer point;
}
