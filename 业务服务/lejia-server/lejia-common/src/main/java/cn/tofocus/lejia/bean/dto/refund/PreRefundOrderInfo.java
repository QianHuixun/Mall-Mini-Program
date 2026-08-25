package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预退款信息")
public class PreRefundOrderInfo
{
    @Schema(description = "退款商品总价")
    private BigDecimal refundGoodsAmt;
    
    @Schema(description = "退款配送费")
    private BigDecimal refundPostage;
    
    @Schema(description = "是否退还优惠券")
    private boolean hasRefundCard;
    
    @Schema(description = "是否退还配送优惠券")
    private boolean hasRefundCardPostage;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
}
