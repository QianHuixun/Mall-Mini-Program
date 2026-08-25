package cn.tofocus.lejia.bean.dto.v2.order;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderGwcV2OnList
{
    
    private Integer pkey;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "规格")
    private Integer space;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "会员价")
    private BigDecimal priceMember;

    // = 优惠券后金额 / 数量
    // 不精确，四舍五入的数据
    @Schema(description = "使用优惠券后的价格（四舍五入）")
    private BigDecimal couponPrice;

    // 精确数据，计算以这个为准
    @Schema(description = "使用优惠券后的金额（精确）")
    private BigDecimal couponAmt;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "积分")
    private Integer point;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "毛重")
    @JsonIgnore
    private BigDecimal weight;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款数量")
    private Integer refundNum;
    
}
