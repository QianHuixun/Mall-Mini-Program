package cn.tofocus.lejia.bean.dto.finance;

import java.math.BigDecimal;
import java.util.Date;


import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SettlementBillOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;

    @JsonIgnore
    private String farmer;
    
    @Schema(description = "市场")
    private String farmerName;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "商品金额")
    private BigDecimal amto;
    
    @Schema(description = "配送费金额")
    private BigDecimal oldPostage;
    
    @Schema(description = "商品优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "配送费优惠")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "商品退款")
    private BigDecimal refundAmt;
    
    @Schema(description = "商品优惠退款")
    private BigDecimal refundCardAmt;
    
    @Schema(description = "配送费退款")
    private BigDecimal refundPostageAmt;
    
    @Schema(description = "应结金额")
    private BigDecimal needAmt;
    
    @Schema(description = "实付金额")
    private BigDecimal actualPayment;
    
    @Schema(description = "手续费")
    private BigDecimal payComm;
    
    @Schema(description = "手续费承担")
    private String commissionTypeName;
    
    @JsonIgnore
    private CommissionType commissionType;
    
    @Schema(description = "商品应结")
    private BigDecimal goodsNeedAmt;
    
    @Schema(description = "集团抽佣")
    private BigDecimal sysCommissions;
    
    @Schema(description = "市场抽佣")
    private BigDecimal marketCommissions;
    
    @Schema(description = "商户结算")
    private BigDecimal amt; 
    
    @Schema(description = "配送费应结")
    private BigDecimal needPostageAmt;
    
    @Schema(description = "配送费优惠")
    private BigDecimal cardPostageAmt2;
    
    @Schema(description = "配送费-集团结算")
    private BigDecimal postageAmtSys;
    
    @Schema(description = "配送费-市场结算")
    private BigDecimal postageAmtMarket;
    
    @Schema(description = "实际结算-集团结算")
    private BigDecimal actualAmtSys; 
    
    @Schema(description = "实际结算-市场结算")
    private BigDecimal actualAmtMarket; 
    
    @Schema(description = "实际结算-商户结算")
    private BigDecimal actualAmtVendor;
    
    @Schema(description = "结算状态")
    private String settlementTypeName;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    private SettlementType settlementType;
    
    
    
    //----------------------------------------------------
    @JsonIgnore
    private BigDecimal postage;
    @JsonIgnore
    private BigDecimal amtall;
    @JsonIgnore
    private BigDecimal amtn;
    @JsonIgnore
    private OrderOir orderOir;
    @JsonIgnore
    private PayType payType;
    
    @Schema(description = "微信支付金额")
    @JsonIgnore
    private BigDecimal weixinAmt;
    @Schema(description = "其他支付金额")
    @JsonIgnore
    private BigDecimal otherAmt;
    @Schema(description = "微信支付退款金额")
    @JsonIgnore
    private BigDecimal refundWeixinAmt;
    @Schema(description = "其他支付退款金额")
    @JsonIgnore
    private BigDecimal refundOtherAmt;
}
