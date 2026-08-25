package cn.tofocus.lejia.bean.dto.v3;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GwcOrderTotalV3Info2
{
    @Schema(description = "收货地址主键")
    private Integer addrPkey;
    
    @Schema(description = "地址")
    private String addr;
    
    @Schema(description = "详细地址")
    private String addrDetail;
    
    @Schema(description = "收货人")
    private String name;
    
    @Schema(description = "收货人手机")
    private String mobile;
    
    // ------------------------------------------
    
//    @Schema(description = "优惠券主键")
//    private Integer card;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
//    @Schema(description = "配送卡券主键")
//    private Integer cardPostage;
//    
//    @Schema(description = "配送卡券名称")
//    private String cardPostageName;

    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
//    @Schema(description = "卡券编号")
//    private String cardCode;
//    
//    @Schema(description = "是否有卡券可用")
//    private Boolean cardUsable = false;
//    
//    @Schema(description = "是否有配送费卡券可用")
//    private Boolean cardPostageUsable = false;
//    
//    @Schema(description = "是否可以使用卡券可用- 只有市场商品才能使用卡券")
//    private Boolean isCard = false;
//    
//    @Schema(description = "会员减少的金额")
//    private BigDecimal reducePrice;
    
    // ------------------------------------------
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    @Schema(description = "订单明细")
    private List<GwcOrderV3Info> infos;
    
    /** 以下数据 不需要传参进来   */
    
    @Schema(description = "合计商品金额(没扣除其他费用)")
    private BigDecimal goodsSumAmto;
    
    @Schema(description = "合计商品金额(最后支付的金额)")
    private BigDecimal goodsSumAmtn;
    
    @Schema(description = "合计商品积分")
    private Integer sumPointn;
    
    @Schema(description = "合计配送费(没扣除其他费用)")
    private BigDecimal sumPostage;

    @Schema(description = "微信支付数据")
    private WxPayData wxPayData;
    
    @Schema(description = "支付成功后,用于跳转订单详情")
    private Integer orderPkey;
    
    @Schema(description = "用户钱包")
    private BigDecimal myCommn;

    @Schema(description = "用户民生豆")
    private BigDecimal myMsd;
    
    @Schema(description = "是否可用热力豆支付")
    private Boolean msdPay = false;
    
    @Schema(description = "允许市场商品使用热力豆支付")
    private Boolean farmerGoods = false;
    
    @Schema(description = "允许自营、滨农、预售使用热力豆支付")
    private Boolean sysGoods = false;
}
