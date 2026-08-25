package cn.tofocus.lejia.bean.dto.refund;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppGwcDTO;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderExpressInfo;
import cn.tofocus.lejia.bean.dto.market.MktOrderExpressRouteInfo;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktRefund;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefundOrderDetails
{
    @Schema(description = "订单主键")
    private Integer pkey;
    
    @Schema(description = "退款订单主键")
    private Integer refundPkey;
    
    @Schema(description = "推荐人")
    private Integer tjr;
    
    @Schema(description = "购买者")
    private Integer member;
    
    private String memberName;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderStatus status;
    
    @Schema(description = "第三方配送状态")
    private ThirdPartyStatus thirdPartyStatus;
    
    public String getThirdPartyStatusName()
    {
        if (thirdPartyStatus != null) return thirdPartyStatus.getName();
        return "";
    }
    
    @Schema(description = "订单来源 自营/积分商城/市场商城")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderOir orderOir;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private PayType payType;
    
    public String getPayTypeName()
    {
        if (payType != null) return payType.getName();
        return "";
    }
    
    @Schema(description = "订单类型 砍价/团购/预售/佣金/普通")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderType orderType;
    
    @Schema(description = "订单类型 砍价/团购/预售/佣金/普通")
    private String orderTypeName;
    
    @Schema(description = "是否自提")
    private Boolean pickupType;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    @Schema(description = "订单价格")
    private BigDecimal amto;
    
    @Schema(description = "总价")
    private BigDecimal amtall;
    
    @Schema(description = "支付金额")
    private BigDecimal amtn;
    
    @Schema(description = "微信支付金额")
    private BigDecimal weixinAmt;
    
    @Schema(description = "其他支付金额")
    private BigDecimal otherAmt;
    
    @Schema(description = "热力豆支付金额")
    public BigDecimal getMsdAmt()
    {
        if(PayType.MSD_COMBINATION.equals(this.payType))
            return this.otherAmt;
        return BigDecimal.ZERO;
    }
    
    @Schema(description = "电子账户支付金额")
    public BigDecimal getElectronicAccountAmt()
    {
        if(PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(this.payType))
            return this.otherAmt;
        return BigDecimal.ZERO;
    }
    
    @Schema(description = "支付积分")
    private Integer pointn;
    
    @Schema(description = "我的积分")
    private Integer myPoints;
    
    @Schema(description = "支付佣金")
    private BigDecimal commn;
    
    @Schema(description = "我的佣金")
    private BigDecimal myCommn;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "支付卡券")
    private Integer card;
    
    @Schema(description = "卡券名称")
    private String cardName;
    
    @Schema(description = "卡券编号")
    private String cardCode;
    
    @Schema(description = "砍价优惠")
    private BigDecimal cutAmt;
    
    @Schema(description = "还剩多少可以砍")
    private BigDecimal rCutAmt;
    
    @Schema(description = "剩余砍价时间")
    private Long endTime;
    
    @Schema(description = "已经砍价成功人数")
    private Integer cutSuccessNum;
    
    @Schema(description = "会员减少的金额")
    private BigDecimal reducePrice;
    
    private String remark;
    
    @Schema(description = "收货地址")
    private MktAppAddrDTO addr;
    
    @Schema(description = "快递公司")
    private String logistics;
    
    @Schema(description = "快递单")
    private String kdCode;
    
    @Schema(description = "积分商城购物清单")
    private List<MktAppGwcDTO> list1;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "市场商城购物清单")
    private List<MktAppGwcDTO> list2;
    
    @Schema(description = "退货信息")
    private MktRefund refund;
    
    @Schema(description = "配送员")
    private MktCourier courier;
    
    @Schema(description = "微信支付数据")
    private WxPayData wxPayData;
    
    @Schema(description = "礼品券状态")
    private CardStatus giftStatus;
    
    @Schema(description = "配送类型")
    private DistributionType distributionType;
    
    @Schema(description = "配送配置")
    @Column(nullable = false, columnDefinition = "bit")
    private Boolean distributionConfig;
    
    @Schema(description = "统一配置费")
    private BigDecimal fee;
    
    @Schema(description = "起步价")
    private BigDecimal startingPrice;
    
    @Schema(description = "发货方式")
    private ExpressType expressType;
    
    @Schema(description = "配送时间")
    private String pstime;
    
    @Schema(description = "配送时间组")
    private List<String> pstimeOpt;
    
    @Schema(description = "配送选择")
    private List<DistributionTypeDTO> distype;
    
    @Schema(description = "核销码")
    private String pickupCode;
    
    @Schema(description = "核销时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy年MM月dd日 HH:mm", timezone = "GMT+8")
    private String pickupTime;
    
    @Schema(description = "是否核销")
    private Boolean pickupFlag;
    
    @Schema(description = "自提金额")
    private BigDecimal pickupAmt;
    
    @Schema(description = "是否包邮")
    private Boolean postFree;
    
    @Schema(description = "合计商品数")
    private Integer orderNum;
    
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "其他支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @Schema(description = "热力豆退款金额")
    public BigDecimal getRefundMsdAmt()
    {
        if(PayType.MSD_COMBINATION.equals(this.payType))
            return this.refundOtherAmt;
        return BigDecimal.ZERO;
    }
    
    @Schema(description = "电子账户退款金额")
    public BigDecimal getRefundElectronicAccountAmt()
    {
        if(PayType.ELECTRONIC_ACCOUNT_COMBINATION.equals(this.payType))
            return this.refundOtherAmt;
        return BigDecimal.ZERO;
    }
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "描述")
    private String describe;
    
    private RefundStatus refundStatus;

    @Schema(description = "退款状态")
    private String refundStatusName;
    
    @Schema(description = "处理意见")
    private String delDesc;

    @Schema(description = "本次退款金额合计")
    private BigDecimal currentRefundAmt;
    
    @Schema(description = "退款照片")
    private List<String> refundPhoto;
    
    @Schema(description = "退款明细")
    private List<VendorRefundOrderOnInfo> refundOrder;

    @Schema(description = "退款商品金额")
    private BigDecimal refundGoodsAmt;

    @Schema(description = "退款配送费")
    private BigDecimal refundPostage;

    @Schema(description = "退还优惠券")
    private Integer refundCard;

    @Schema(description = "退还优惠券名称")
    private String refundCardTitle;

    @Schema(description = "退还配送费优惠券")
    private Integer refundCardPostage;
    
    @Schema(description = "退还配送费优惠券名称")
    private String refundCardPostageTitle;
    
    @Schema(description = "物流单")
    private MktOrderExpressInfo orderExpressInfo;
    
    @Schema(description = "物流节点")
    private List<MktOrderExpressRouteInfo> expressRoutes;
}
