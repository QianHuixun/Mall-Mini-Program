package cn.tofocus.lejia.bean.dto.jd;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdSplitOrderLine
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    private OrderStatus status;
    
    @Schema(description = "分类")
    private Integer gtype;
    
    @Schema(description = "商品pkey")
    private Long goods;
    
    @Schema(description = "规格pkey")
    private Long space;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    private Integer card;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "合计毛重")
    private BigDecimal weight;
    
    @Schema(description = "关联主键")
    private Integer association;
    
    @Schema(description = "关联名称")
    private String associationName;
    
    @Schema(description = "原价")
    private BigDecimal price;
    
    @Schema(description = "（普通会员）价格/（年费会员）会员价")
    private BigDecimal pricen;
    
    // = 优惠券后金额 / 数量
    // 不精确，四舍五入的数据
    @Schema(description = "使用优惠券后的价格")
    private BigDecimal couponPrice;
    
    // 精确数据，计算以这个为准
    @Schema(description = "使用优惠券后的金额")
    private BigDecimal couponAmt;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "积分")
    private Integer point;
    
    @Schema(description = "退款数量")
    private Integer refundNum = 0;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt = BigDecimal.ZERO;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
    @Schema(description = "京东数量,只有数量不一样才会有值")
    private Integer jdNum; 
}
