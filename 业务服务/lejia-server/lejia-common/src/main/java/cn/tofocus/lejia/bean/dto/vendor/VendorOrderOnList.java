package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEntity;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.utils.DateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderOnList
{
    @JsonIgnore
    private Integer orderPkey;
    
    @Schema(description = "订单编号")
    @JoinDTO(dataQuery = "mktOrderDao", from = "orderPkey")
    private String code;
//    public String getCode()
//    {
//        return mktOrder.getCode();
//    }
//    
//    @JsonIgnore
//    @JoinEntity(dataQuery = "mktOrderDao", from = "orderPkey")
//    private MktOrder mktOrder;
    
    @JsonIgnore
    private OrderType type;
    
    @Schema(description = "订单类型")
    @JoinEnum(from = "type")
    private String typeName;
    
    private Integer vendor;
    
    @JsonIgnore
    @JoinEntity(dataQuery = "mktVendorDao", from = "vendor")
    private MktVendor mktVendor;
    
    @Schema(description = "商户")
//    @JoinDTO(dataQuery = "mktVendorDao", from = "vendor")
    public String getVendorName()
    {
        if(mktVendor != null)
            return mktVendor.getDisplayName();
        return "";
    }
    
    
    @Schema(description = "摊位号")
    public String getBooth()
    {
        if(mktVendor != null)
            return mktVendor.getBooth();
        return "";
    }
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "商品原价")
    private BigDecimal goodsPrice;
    
    @Schema(description = "商品总价")
    public BigDecimal getGoodsTotalPrice()
    {
        if(goodsPrice != null && num != null)
        {
            return new BigDecimal(num).multiply(goodsPrice);
        }
        return BigDecimal.ZERO;
    }
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "采购单价")
    private BigDecimal price;
    
    @Schema(description = "采购总价")
    private BigDecimal totalPrice;
//    public BigDecimal getTotalPrice()
//    {
//        return amt;
//    }
//    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @JsonIgnore
    private Date farmerTime;
    
//    @JsonIgnore
    private BigDecimal amt;
    
    @Schema(description = "采购确认日期")
    public String getBuyDate()
    {
        if (farmerTime != null) return DateUtil.formatDate(farmerTime, "yyyy-MM-dd HH:mm:ss");
        return "";
    }
    
    @Schema(description = "结算时间")
    private Date startDate;
    
    @Schema(description = "付款时间")
    private Date endDate;
    
    @Schema(description = "采购退款金额")
    private BigDecimal procureRefundAmt;
    
    @Schema(description = "支付渠道手续费")
    private BigDecimal payComm;
    
//    @JsonIgnore
//    private Integer settlementPkey;
//    
//    @JsonIgnore
//    @JoinEntity(dataQuery = "mktSettlementDao", from = "settlementPkey")
//    private MktSettlement settlement;
//    
//    @Schema(description = "报表周期")
//    public String getReportPeriod()
//    {
//        if (settlement != null)
//        {
//            String startDate = settlement.getStartDate().replace("-", "/");
//            String endDate = settlement.getEndDate().replace("-", "/");
//            return startDate + " - " + endDate;
//        }
//        return "-";
//    }
    
    @JsonIgnore
    private BigDecimal commissionRate;
    
    @Schema(description = "佣金费率")
    public String getCommissionRateStr()
    {
        if (commissionRate != null) return commissionRate.stripTrailingZeros().toPlainString() + "%";
        return "-";
    }
    
    @Schema(description = "交易佣金")
    private BigDecimal commissions;
    
    @Schema(description = "规格")
    private String spaceName;
    
    @Schema(description = "应结金额")
    private BigDecimal needAmt;
    
    
//    @JsonIgnore
    private SettlementType status;
    
    @JoinEnum(from = "status")
    @Schema(description = "结算状态")
    private String statusName;
    
    @Schema(description = "打包费用")
    @JsonIgnore
    private BigDecimal packingCharge;
    
    @Schema(description = "优惠退款金额")
    @JsonIgnore
    private BigDecimal discountRefundAmt;
    
    @JsonIgnore
    private CommissionType commissionType;
    
}