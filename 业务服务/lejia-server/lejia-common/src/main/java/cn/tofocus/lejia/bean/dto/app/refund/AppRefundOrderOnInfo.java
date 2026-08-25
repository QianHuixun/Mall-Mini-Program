package cn.tofocus.lejia.bean.dto.app.refund;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.dto.refund.VendorRefundOrderOnList;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.jd.CourierType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AppRefundOrderOnInfo
{
    private Integer pkey;
    
    @Schema(description = "单据号")
    private String code;
    
    private Integer orderPkey;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    @Schema(description = "退款商品金额")
    private BigDecimal refundGoodsAmt;
    
    @Schema(description = "退款配送费")
    private BigDecimal refundPostage;
    
    @Schema(description = "是否退还优惠券")
    private boolean hasRefundCard;

    @Schema(description = "是否退还配送优惠券")
    private boolean hasRefundCardPostage;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "其他支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @Schema(description = "热力豆退款金额")
    private BigDecimal refundMsdAmt;
    
    @Schema(description = "电子账户退款金额")
    private BigDecimal refundElectronicAccountAmt;
    
//    @Schema(description = "其他支付退款名称(目前有 'I DO支付'和'热力豆')")
//    private String refundOtherTypeName;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "提交时间")
    private Date createdTime;
    
    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "描述")
    private String describe;
    
    @Schema(description = "退款照片")
    private List<String> refundPhoto;
    
    @Schema(description = "售后电话")
    private String tel;
    
    @Schema(description = "处理意见")
    private String delDesc;
    
    private RefundStatus status;
    
    @JoinEnum(from = "status")
    private String statusName;
    
    @Schema(description = "合计商品数量")
    private Integer num;

    @Deprecated
    @Schema(description = "退款商品总价")
    private BigDecimal sumAmt;
    
    private DistributionType distributionType;
    
    @Schema(description = "退款明细")
    private List<VendorRefundOrderOnList> list;
    
    @Schema(description = "售后类型,退货 换货")
    private RefundJdType jdType;

    @Schema(description = "售后类型,退货 换货")
    private String jdTypeName;
    
    @Schema(description = "是否是京东退款订单")
    private Boolean isJd;
    
    @Schema(description = "返件方式 上门取件或自己寄出")
    private CourierType courierType;
    
    @Schema(description = "返件方式 上门取件或自己寄出")
    private String courierTypeName;
    
    @Schema(description = "取件地址")
    private String addr;
    
    @Schema(description = "收货地址")
    private String receiptAddr;
    
    @Schema(description = "自行寄出-快递公司")
    private String courierCompany;
    
    @Schema(description = "自行寄出-快递单号")
    private String courierNumber;
    
    @Schema(description = "京东拒绝后退货快递公司")
    private String refuseCourierCompany;
    
    @Schema(description = "京东拒绝后退货快递单号")
    private String refuseCourierNumber;
    
    @Schema(description = "售后收货人")
    private String afterService;
    
    @Schema(description = "售后收货人电话")
    private String afterServiceTel;
    
    @Schema(description = "售后收货人手机号")
    private String afterServicePhone;
    
    @Schema(description = "售后地址")
    private String afterServiceAddr;
    
    @Schema(description = "京东订单退换货快递情况")
    private String jdExpress;

    // 目前主要用于京东售后取消、确认操作后等待京东处理
    @Schema(description = "外部系统处理中")
    private Boolean outProcessing;
}
