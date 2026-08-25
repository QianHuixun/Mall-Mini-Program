package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdOrderOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    private String code;

    @Schema(description = "用户")
    private Integer member;
    
    @Schema(description = "用户手机号")
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "mobile")
    private String memberMobile;
    
    @Schema(description = "用户标签")
    private String tagName;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    private OrderStatus status;
    
    @Schema(description = "状态名称")
    @JoinEnum(from = "status")
    private String statusName;
    
    private DistributionType distributionType;
    
    @Schema(description = "配送方式")
    @JoinEnum(from = "distributionType")
    private String distributionTypeName;
    
    @Schema(description = "付款时间")
    private String createdTime;
    
    @Schema(description = "商品价格")
    private BigDecimal goodsPrice;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    @Schema(description = "总价")
    private BigDecimal amtall;
    
    @Schema(description = "支付金额")
    private BigDecimal amtn;

    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    @Schema(description = "支付类型")
    @JoinEnum(from = "payType")
    private String payTypeName;
    
    @JsonIgnore
    private BigDecimal amto;
}
