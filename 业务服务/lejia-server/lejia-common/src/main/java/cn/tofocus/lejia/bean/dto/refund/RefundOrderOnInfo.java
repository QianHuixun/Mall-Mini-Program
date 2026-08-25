package cn.tofocus.lejia.bean.dto.refund;

import java.util.List;

import cn.tofocus.lejia.bean.enums.jd.CourierType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefundOrderOnInfo
{
    @Schema(description = "订单主键")
    private Integer pkey;
    
    @Schema(description = "退单明细")
    private List<RefundOnLine> lines;
    
    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "描述")
    private String describe;
    
    @Schema(description = "退款理由-图片")
    private List<String> photo;
    
    /******************************以下是京东订单需要传的参数****************************/
    
    @Schema(description = "京东已收货订单,需要传,其他不需要传 退货/换货")
    private RefundJdType jdType;
    
    @Schema(description = "快递方式 上门取件或自己寄出")
    private CourierType courierType;
    
    @Schema(description = "预约取件时间")
    private String appointmentPickupTime;
    
    @Schema(description = "预约取件开始时间")
    private String pickupTimeStart;
    
    @Schema(description = "预约取件结束时间")
    private String pickupTimeEnd;
    
    @Schema(description = "取件地址主键")
    private Integer addrPkey;
    
    @Schema(description = "收件地址主键")
    private Integer receiptAddrPkey;
    
    
//    @Schema(description = "省")
//    private String pro;
//
//    @Schema(description = "市")
//    private String city;
//
//    @Schema(description = "区")
//    private String area;
//
//    @Schema(description = "街道")
//    private String town;
//    
//    @Schema(description = "取件地址")
//    private String addr;
//    
//    @Schema(description = "取件地址-寄件人")
//    private String name;
//    
//    @Schema(description = "取件地址-寄件人电话")
//    private String mobile;
    
//    @Schema(description = "选自己寄-快递公司")
//    private String courierCompany;
//    
//    @Schema(description = "选自己寄-快递单号")
//    private String courierNumber;
//    
//    @Schema(description = "选自己寄-运费")
//    private BigDecimal postage;
    
//    @Schema(description = "省")
//    private String receiptPro;
//
//    @Schema(description = "市")
//    private String receiptCity;
//
//    @Schema(description = "区")
//    private String receiptArea;
//
//    @Schema(description = "街道")
//    private String receiptTown;
//    
//    @Schema(description = "换货-收货地址")
//    private String receiptAddr;
//    
//    @Schema(description = "换货-收货名称")
//    private String receiptName;
//    
//    @Schema(description = "换货-收货人手机")
//    private String receiptMobile;
}
