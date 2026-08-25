package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MktGoodsOrderLineOnPage extends MktOrderLineOnPage
{
    @Schema(description = "使用优惠券后的金额")
    private BigDecimal couponAmt;
    
    @Schema(description = "实付金额")
    public BigDecimal getTradeAmt()
    {
        if (couponAmt != null) return couponAmt;
        return getAmt();
    }
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;

    public BigDecimal getRefundAmt()
    {
        if (refundAmt == null) return BigDecimal.ZERO;
        return refundAmt;
    }
    
    @Schema(description = "优惠")
    public BigDecimal getDiscount()
    {
        if (couponAmt == null || getAmt() == null) return BigDecimal.ZERO;
        return getAmt().subtract(couponAmt);
    }
    
    @Schema(description = "配送类型")
    private DistributionType distributionType;
    
    @Schema(description = "配送方式")
    public String getDeliveryTypeName()
    {
        if (distributionType == null) return null;
        switch (distributionType)
        {
            case IMMEDIATELY:
            case ORDERED:
                return "配送";
            case PICKUP:
                return "自提";
            default:
                return distributionType.getName();
        }
    }
    
    @Schema(description = "订单状态")
    private OrderStatus status;
    
    @Schema(description = "订单状态名称")
    @JoinEnum(from = "status")
    private String statusName;
}
