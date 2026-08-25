package cn.tofocus.lejia.bean.dto.v2.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.goods.GoodsGiftInfo;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderExpressRouteInfo;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.utils.OrderVerifyCodeGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderDetailsV2Info
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "微信支付订单号")
    private String transactionId;
    
    @Schema(description = "礼品券编号")
    private String cardCode;
    
    @Schema(description = "订单来源 自营/积分商城/市场商城")
    private OrderOir orderOir;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    private OrderStatus status;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    public String getPayTypeName()
    {
        if(payType != null)
            return payType.getName();
        return "";
    }
    
    @Schema(description = "订单类型 砍价/团购/预售/佣金/市场/积分")
    private OrderType orderType;
    
    private ThirdPartyStatus thirdPartyStatus;
    
    public String getThirdPartyStatusName()
    {
//        if(thirdPartyStatus != null)
//        {
//            if(ThirdPartyStatus.THIRD_PARTY_VOID.equals(thirdPartyStatus) 
//                || ThirdPartyStatus.THIRD_PARTY_ERROR.equals(thirdPartyStatus))
//                return "骑手取货中";
//            return thirdPartyStatus.getName();
//        }
//        return "";
        String res = "";
        if (thirdPartyStatus != null)
        {
            res =  thirdPartyStatus.getName();
        }
        if (expressStatus != null && 
            (thirdPartyStatus == null || ThirdPartyStatus.THIRD_PARTY_VOID.equals(thirdPartyStatus)
            || ThirdPartyStatus.THIRD_PARTY_ERROR.equals(thirdPartyStatus)))
        {
            if (expressStatus == ExpressStatus.EXPRESS_ORDER) res = ExpressStatus.EXPRESS_GOODS.getName();
            else res = expressStatus.getName();
        }
        return res;
    }
    
    @Schema(description = "原配送费")
    private BigDecimal oldPostage;
    
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
    
    @Schema(description = "支付积分")
    private Integer pointn = 0;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "配送卡券名称")
    private String cardPostageName;

    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "会员减少的金额")
    private BigDecimal reducePrice;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "收货地址")
    private MktAppAddrDTO addr;
    
    @Schema(description = "快递公司")
    private String logistics;
    
    @Schema(description = "快递单")
    private String kdCode;
    
    @Schema(description = "骑手名称")
    private String courierName = "";
    
    @Schema(description = "骑手电话")
    private String courierMobile = "";
    
    @Schema(description = "商品信息")
    private List<OrderV2Info> infos;

    @Schema(description = "供应商")
    private String supplierName;
    
    @Schema(description = "市场")
    private String farmerName;
    
    @Schema(description = "售后电话")
    private String tel;
    
    @Schema(description = "配送类型")
    private DistributionType distributionType;

    @Schema(description = "发货方式")
    private ExpressType expressType;
    
    @Schema(description = "自有骑手配送状态")
    private ExpressStatus expressStatus;
    
    @Schema(description = "配送时间")
    private String pstime = "";
    
    @Schema(description = "配送选择")
    private List<DistributionTypeDTO> distype;
    
    @Schema(description = "自提金额")
    private BigDecimal pickupAmt;
    
    @Schema(description = "是否包邮")
    private Boolean postFree;
    
    @Schema(description = "核销码")
    private String pickupCode;
    
    @Schema(description = "建档时间")
    private Date createdTime;
    
    @Schema(description = "礼券相关信息")
    private GoodsGiftInfo giftInfo;
    
    @Schema(description = "退款主键")
    private Integer refundPkey;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @Schema(description = "其他支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "退款理由")
    private String reason;
    
    @Schema(description = "描述")
    private String describe;
    
    @Schema(description = "照片")
    private List<String> photo;
    
    private RefundStatus refundStatus;
    
    @Schema(description = "退款状态")
    private String refundStatusName;
    
    @Schema(description = "类型,退货 换货")
    private RefundJdType jdType;
    
    @Schema(description = "京东订单退换货快递情况")
    private String jdExpress;
    
    @Schema(description = "退款时间")
    private Date refundTime;
    
    @Schema(description = "是否有多条退款记录, true:有多条, false:就一条,null:无退款记录")
    private Boolean isComplex;

    @Schema(description = "是否有申请中的退款")
    private Boolean hasApplyingRefund;

    @Schema(description = "送达照片")
    private List<String> arrivedPhoto;
    
    @Schema(description = "包厢名称")
    private String boxName;

    @Schema(description = "物流节点")
    private List<MktOrderExpressRouteInfo> expressRoutes;

    @Schema(description = "跳转url")
    private String url;
    
    // md5(订单编号+pkey+创建时间戳)
    @Schema(description = "自提核销码")
    public String getVerifyCode()
    {
        if (distributionType == DistributionType.PICKUP)
        {
            return OrderVerifyCodeGenerator.build(this.code, this.pkey, this.createdTime);
        }
        return null;
    }
    
    @Schema(description = "微信确认收货状态,true:未确认,需要调起确认")
    private Boolean openBusinessView = true;
    
    private Integer ascription;
    
    
    @Schema(description = "渠道订单号")
    private String targetOrderId;
    
    @Schema(description = "确认时间")
    private Date drTime;
    
    @Schema(description = "是否允许评价")
    private Boolean allowedComment = false;
    
    @Schema(description = "是否已评价")
    private Boolean hasComment = false;
    
    private String merOrderId;
}
