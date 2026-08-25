package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppWalletOrderOnList
{
    @Schema(description = "订单主键")
    public Integer getPkey()
    {
        return orderPkey;
    }
    
    @Schema(description = "订单号")
    @JoinProperty(dataQuery = "mktOrderDao", from = "orderPkey", propertyName = "code")
    private String code;
    
    @Schema(description = "小票码")
    @JoinProperty(dataQuery = "mktOrderDao", from = "orderPkey", propertyName = "smallTicket")
    private Integer smallTicket;
    
    @Schema(description = "核销码")
    @JoinProperty(dataQuery = "mktOrderDao", from = "orderPkey", propertyName = "pickupCode")
    private String pickupCode;
    
    @Schema(description = "配送类型")
    @JoinProperty(dataQuery = "mktOrderDao", from = "orderPkey", propertyName = "distributionType")
    private DistributionType distributionType;
    
    @Schema(description = "下单时间")
    @JoinProperty(dataQuery = "mktOrderDao", from = "orderPkey", propertyName = "createdTime")
    private Date orderTime;
    
    @Schema(description = "结算金额")
    public BigDecimal amount;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmt;
    
    @Schema(description = "打包费用")
    private BigDecimal packingCharge;
    
    @JsonIgnore
    @Schema(description = "结算金额（没扣手续费，扣了佣金，扣了打包费）")
    private BigDecimal amt;
    
    @JsonIgnore
    private Integer orderPkey;
    
    @JsonIgnore
    private Date startDate;
    
    @JsonIgnore
    private SettlementType status;
    
    @JsonIgnore
    private RefundStatus refundStatus;
    
    @Schema(description = "退款金额")
    @JsonIgnore
    private BigDecimal refundAmt;
    
    @Schema(description = "交易佣金")
    @JsonIgnore
    private BigDecimal commissions;
    
    @Schema(description = "支付渠道手续费")
    @JsonIgnore
    private BigDecimal payComm;
    
    @Schema(description = "手续费承担")
    @JsonIgnore
    private CommissionType commissionType;
    
    public void plus(AppWalletOrderOnList line)
    {
        this.orderAmt = this.orderAmt.add(line.getOrderAmt());
        this.amount = this.amount.add(line.getAmount());
        this.amt = this.amt.add(line.getAmt());
        this.packingCharge = this.packingCharge.add(line.getPackingCharge());
    }
}
