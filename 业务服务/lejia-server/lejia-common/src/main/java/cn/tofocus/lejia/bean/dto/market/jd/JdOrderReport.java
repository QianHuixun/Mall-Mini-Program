package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(innerTypeName = "F")
public class JdOrderReport
{
    @Schema(description = "订单pkey")
    private Integer orderPkey;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "商品金额")
    private BigDecimal amto;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    @Schema(description = "商品退款")
    private BigDecimal refundGoodsAmt = BigDecimal.ZERO;
    
    @Schema(description = "邮费退款")
    private BigDecimal refundPostage = BigDecimal.ZERO;
    
    @JsonIgnore
    private BigDecimal refundAmt;
    
    @Schema(description = "热力豆合计")
    private BigDecimal amt = BigDecimal.ZERO;
    
    @Schema(description = "京东商品金额")
    private BigDecimal jdGoodsAmt;

    @Schema(description = "京东邮费")
    private BigDecimal oldPostage;
    
    @Schema(description = "京东商品退款")
    private BigDecimal refundJdGoodsAmt = BigDecimal.ZERO;

    @Schema(description = "京东邮费退款")
    private BigDecimal refundJdPostage = BigDecimal.ZERO;

    @JsonIgnore
    private BigDecimal refundJd;
    
    @Schema(description = "京东合计")
    private BigDecimal jdAmt = BigDecimal.ZERO;
    
    @Schema(description = "其他支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @Schema(description = "微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "微信支付金额")
    private BigDecimal weixinAmt;
    
    @Schema(description = "其他支付金额")
    private BigDecimal otherAmt;
}
