package cn.tofocus.lejia.bean.dto.v2.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderV2Info
{
    @Schema(description = "订单明细主键,后续申请退款,pkey对应该字段")
    private Integer orderLinePkey;
    
    @Schema(description = "商品")
    private Long goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品图片")
    private String photo;
    
    @Schema(description = "商品类型")
    private MType mType;
    
    @Schema(description = "商品类型描述")
    public String getMTypeName()
    {
        if(mType != null)
            return mType.getName();
        return "";
    }
    
    @Schema(description = "规格")
    private Long space;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @Schema(description = "要加工商品主键")
    private Integer association;
    
    @Schema(description = "要加工商品名称")
    private String associationName;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "会员价")
    private BigDecimal priceMember;
    
    @Schema(description = "积分")
    private Integer point;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;

    // = 优惠券后金额 / 数量
    // 不精确，四舍五入的数据
    @Schema(description = "使用优惠券后的价格（四舍五入）")
    private BigDecimal couponPrice;

    // 精确数据，计算以这个为准
    @Schema(description = "使用优惠券后的金额（精确）")
    private BigDecimal couponAmt;
    
    @Schema(description = "数量")
    private Integer num;
    
    public BigDecimal getSales()
    {
        BigDecimal sales = BigDecimal.ZERO;
        for(OrderGwcV2OnList og : lines)
        {
            if (og.getPrice() != null && og.getNum() != null)
                sales = sales.add(og.getPrice().multiply(BigDecimal.valueOf(og.getNum())));
        }
        return sales;
    }
    
    @Schema(description = "规格详情")
    private List<OrderGwcV2OnList> lines = new ArrayList<>();
   
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款数量")
    private Integer refundNum;
    
    @Schema(description = "京东可退款数量")
    private Integer jdRefundNum;
    
    @Schema(description = "京东售后说明")
    private String jdAttributes;
    
    @Schema(description = "京东上门取件,true:可选")
    private Boolean jdDoor;
    
    @Schema(description = "自行寄出,true:可选")
    private Boolean selfMailing;
    
    @Schema(description = "是否全部退完")
    private Boolean wholeRefund;
    
    @Schema(description = "启用标志 true 还存在")
    private Boolean enabled;
    
}
