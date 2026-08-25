package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预退款信息")
public class PreUpdRefundOrderInfo
{
    @Schema(description = "退款商品总价")
    private BigDecimal refundGoodsAmt;
    
    @Schema(description = "退款配送费")
    private BigDecimal refundPostage;
    
    @Schema(description = "退还优惠券")
    private Integer refundCard;
    
    @Schema(description = "退还优惠券名称")
    private String refundCardTitle;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "退还配送费优惠券")
    private Integer refundCardPostage;
    
    @Schema(description = "退还配送费优惠券名称")
    private String refundCardPostageTitle;
}
