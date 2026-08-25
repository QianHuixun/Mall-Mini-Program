package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PriceStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderSettleOnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "订单明细主键")
    private Integer orderLinePkey;
    
    @Schema(description = "商户")
    private Integer vendor;
    
    @Schema(description = "goods")
    private Integer goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品原价")
    private BigDecimal goodsPrice;
    
    @Schema(description = "类型")
    private OrderType type;
    
    @Schema(description = "结算状态")
    private SettlementType status;
    
    @Schema(description = "采购状态")
    private PurchaseStatus purchaseStatus;
    
    @Schema(description = "价格异常状态")
    private PriceStatus priceStatus;
    
    @Schema(description = "推荐采购价格")
    private BigDecimal recommendPrice;
    
    @Schema(description = "规格主键")
    private Integer space;
    
    @Schema(description = "规格")
    private String spaceName;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "采购价格")
    private BigDecimal price;
    
    @Schema(description = "总价")
    private BigDecimal totalPrice;
    
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate;
    
    @Schema(description = "交易佣金")
    private BigDecimal commissions;
    
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    @Schema(description = "优惠金额")
    private BigDecimal discountAmt;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    @Schema(description = "差额")
    private BigDecimal difference;
    
    @Schema(description = "采购备注")
    private String remark;
    
    @Schema(description = "结算备注")
    private String settlementRemark;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "撤销时间")
    private Date revokeTime;
    
    @Schema(description = "商户确认时间")
    private Date vendorTime;
    
    @Schema(description = "市场确认时间")
    private Date farmerTime;
    
    @Schema(description = "结算主表")
    private Integer settlementPkey;
    
    @Schema(description = "中信银行主键")
    private String zxUserId;
    
    @Schema(description = "订单编号")
    private String code;
    
    @Schema(description = "第三方订单号", required = false)
    private String transactionId;
    
    @Schema(description = "支付金额")
    private BigDecimal amtn;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
}
