package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;
import java.util.Date;


import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderRefundOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "单据号")
    private String code;

    @Schema(description = "京东订单号")
    private Long jdOrderId;

    @Schema(description = "商品价格")
    private BigDecimal goodsAmt;
    
    @Schema(description = "原配送费")
    private BigDecimal oldPostage;
    
    @Schema(description = "配送费")
    private BigDecimal postage;
    
//    public BigDecimal getPostage()
//    {
//        if(oldPostage != null)
//            return oldPostage;
//        return postage;
//    }
    
    @Schema(description = "优惠金额")
    private BigDecimal preferentialAmt;
    
    @Schema(description = "配送费优惠金额")
    private BigDecimal preferentialPostageAmt;
    
    @Schema(description = "退款商品总价")
    private BigDecimal refundGoodsAmt;

    @Schema(description = "配送费退款")
    private BigDecimal refundPostage;
    
    @Schema(description = "订单合计")
    private BigDecimal amtall;
    
    @Schema(description = "退款金额")
    private BigDecimal amtre;
    
    @Schema(description = "微信退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    private RefundStatus status;
    
    @Schema(description = "状态 申请中/同意/已退款/拒绝")
    @JoinEnum(from = "status")
    private String statusName;
    
    @Schema(description = "类型,市场/用户 退款")
    @JoinEnum(from = "jdType")
    private String jdTypeName;
    
    private RefundJdType jdType;
    
    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "时间")
    private Date createdTime;
    
    @Schema(description = "重新退款 true:可以重新退款")
    private Boolean againRefund;
}
