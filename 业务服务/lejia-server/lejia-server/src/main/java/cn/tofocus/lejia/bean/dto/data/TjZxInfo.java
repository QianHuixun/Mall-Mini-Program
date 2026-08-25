package cn.tofocus.lejia.bean.dto.data;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TjZxInfo
{
    @Schema(description = "平台商户编号")
    private String mchntId;
  
    @Schema(description = "用户编号")
    private String userId;
    
    @Schema(description = "交易日期")
    private String date;
    
    @Schema(description = "交易时间")
    private String dateTime;
    
    @Schema(description = "支付渠道名称")
    private String qdName;
    
    @Schema(description = "平台商户业务订单号")
    private String sysCode;
    
    @Schema(description = "平台商户支付订单号")
    private String kcCode;
    
    @Schema(description = "支付渠道交易流水号")
    private String payCode;
    
    @Schema(description = "平台商户业务子订单号")
    private String micCode;
    
    @Schema(description = "支付订单交易类型(1-支付 2-退款 空格-其他)")
    private String payType;
    
    @Schema(description = "业务订单交易类型")
    private String type;
    
    @Schema(description = "清算资金来源")
    private String qdLy;
    
    @Schema(description = "渠道手续费承担方式(1-平台商户承担  2-用户承担)")
    private String qdCd;
    
    @Schema(description = "原始订单金额")
    private String amto;
    
    @Schema(description = "原始支付金额")
    private String amtn;
    
    @Schema(description = "平台优惠金额")
    private String sysCoupon;
    
    @Schema(description = "平台分成金额")
    private String sysAmt;
    
    @Schema(description = "平台垫款金额")
    private String sysd;
    
    @Schema(description = "渠道手续费")
    private String qdComm;
    
    @Schema(description = "资金类型")
    private String userRole;
    
}
