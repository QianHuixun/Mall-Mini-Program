package cn.tofocus.lejia.bean.dto.v3;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderTotalV3Info
{
    @Schema(description = "收货地址主键")
    private Integer addrPkey;
    
//    @Schema(description = "优惠券主键")
//    private Integer card;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    @Schema(description = "订单明细")
    private List<OrderV3Info> infos;
    
//    @Schema(description = "配送卡券主键")
//    private Integer cardPostage;
    
    /** 以下数据 不需要传参进来   */
    
    @Schema(description = "合计商品金额(最后支付的金额)")
    private BigDecimal goodsSumAmtn;
    
    @Schema(description = "合计商品积分")
    private Integer sumPointn;
    
    @Schema(description = "合计配送费")
    private BigDecimal sumPostage;
    
    @Schema(description = "微信支付数据")
    private WxPayData wxPayData;
    
    @Schema(description = "支付成功后,用于跳转订单详情")
    private Integer orderPkey;
    
    @JsonIgnore
    private BigDecimal goodsSumWeixinAmtn;
    @JsonIgnore
    private BigDecimal goodsSumOtherAmtn;
}
