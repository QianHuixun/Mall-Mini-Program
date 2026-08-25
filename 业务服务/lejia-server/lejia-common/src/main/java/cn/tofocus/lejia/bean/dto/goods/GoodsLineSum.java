package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsLineSum
{
    
    @Schema(description = "订单笔数")
    private Long orderCount;
    
    @Schema(description = "销售数量")
    private Long goodsCount;
    
    @Schema(description = "商品总价")
    private BigDecimal amtSum;
    
    @Schema(description = "合计毛重")
    private BigDecimal weight;
    
    @Schema(description = "使用优惠券后的金额")
    private BigDecimal couponAmtSum;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmtSum;
    
    @Schema(description = "销售额")
    public BigDecimal getActualAmtSum()
    {
        if (couponAmtSum == null) return BigDecimal.ZERO;
        if (refundAmtSum == null) return couponAmtSum;
        return couponAmtSum.subtract(refundAmtSum);
    }
    
    public void add(GoodsLineSum summary)
    {
        this.fillDefault();
        if (summary.getOrderCount() != null) orderCount += summary.getOrderCount();
        if (summary.getGoodsCount() != null) goodsCount += summary.getGoodsCount();
        if (summary.getAmtSum() != null) amtSum = amtSum.add(summary.getAmtSum());
        // 如果有优惠后金额，用优惠后金额；否则用商品总价
        if (summary.getCouponAmtSum() != null)
            couponAmtSum = couponAmtSum.add(summary.getCouponAmtSum());
        else if (summary.getAmtSum() != null) couponAmtSum = couponAmtSum.add(summary.getAmtSum());
        if (summary.getRefundAmtSum() != null) refundAmtSum = refundAmtSum.add(summary.getRefundAmtSum());
        if (summary.getWeight() != null) weight = weight.add(summary.getWeight());
    }
    
    public GoodsLineSum fillDefault()
    {
        if (orderCount == null) orderCount = 0L;
        if (goodsCount == null) goodsCount = 0L;
        if (amtSum == null) amtSum = BigDecimal.ZERO;
        if (couponAmtSum == null) couponAmtSum = BigDecimal.ZERO;
        if (refundAmtSum == null) refundAmtSum = BigDecimal.ZERO;
        if (weight == null) weight = BigDecimal.ZERO;
        return this;
    }
    
    public static GoodsLineSum empty()
    {
        return new GoodsLineSum().fillDefault();
    }
    
}
