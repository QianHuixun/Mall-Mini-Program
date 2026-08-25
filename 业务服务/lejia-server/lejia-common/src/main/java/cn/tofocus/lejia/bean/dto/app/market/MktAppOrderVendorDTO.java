package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktRefund;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppOrderVendorDTO
{
    
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
     * pkey
     */
    @Schema(description = "pkey1")
    private Integer pkey1;
    
    /**
     * pkey
     */
    @Schema(description = "pkey2")
    private Integer pkey2;
    
    /**
     * 推荐人
     */
    @Schema(description = "推荐人")
    private Integer tjr;
    
    /**
     * 购买者
     */
    @Schema(description = "购买者")
    private Integer member;
    
    private String memberName;
    
    /**
     * 购买者
     */
    @Schema(description = "订单号")
    private String code;
    
    /**
    * 状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废
    */
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderStatus status;
    
    @Schema(description = "第三方配送状态")
    private ThirdPartyStatus thirdPartyStatus;
    
    public String getThirdPartyStatusName()
    {
        String res = "";
        if (thirdPartyStatus != null)
        {
            res =  thirdPartyStatus.getName();
        }
        if (expressStatus != null && 
            (thirdPartyStatus == null || ThirdPartyStatus.THIRD_PARTY_VOID.equals(thirdPartyStatus)
            || ThirdPartyStatus.THIRD_PARTY_ERROR.equals(thirdPartyStatus)))
        {
            res = expressStatus.getName();
        }
        return res;
    }
    
    /**
    * 订单来源 自营/积分商城/市场商城
    */
    @Schema(description = "订单来源 自营/积分商城/市场商城")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderOir orderOir;
    
    /**
     * 支付类型  支付宝 微信 电子账号
     */
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private PayType payType;
    
    public String getPayTypeName()
    {
        if (payType != null) return payType.getName();
        return "";
    }
    
    /**
    * 订单类型 砍价/团购/预售/佣金/普通
    */
    @Schema(description = "订单类型 砍价/团购/预售/佣金/普通")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private OrderType orderType;
    
    @Schema(description = "是否自提")
    private Boolean pickupType;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    /**
    * 邮费
    */
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    /**
    * 订单价格
    */
    @Schema(description = "订单价格")
    private BigDecimal amto;
    
    /**
    * 总价
    */
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
    
    /**
     * 我的积分
     */
    @Schema(description = "我的积分")
    private Integer myPoints;
    
    /**
    * 支付佣金
    */
    @Schema(description = "支付佣金")
    private BigDecimal commn;
    
    /**
    * 我的佣金
    */
    @Schema(description = "我的佣金")
    private BigDecimal myCommn;
    
    /**
    * 卡券优惠
    */
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    /**
    * 支付卡券
    */
    @Schema(description = "支付卡券")
    private Integer card;
    
    @Schema(description = "卡券名称")
    private String cardName;
    
    /**
     * 卡券编号
     */
    @Schema(description = "卡券编号")
    private String cardCode;
    
    /**
    * 砍价优惠
    */
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
    
    /**
    * 收货地址
    */
    @Schema(description = "收货地址")
    private MktAppAddrDTO addr;
    
    /**
     * 快递公司
     */
    @Schema(description = "快递公司")
    private String logistics;
    
    /**
     * 快递单
     */
    @Schema(description = "快递单")
    private String kdCode;
    
    /**
    * 积分商城购物清单
    */
    @Schema(description = "积分商城购物清单")
    private List<MktAppGwcDTO> list1;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    private Date createdTime;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;
    
    /**
    * 公司
    */
    @Schema(description = "公司")
    private String company;
    
    /**
     * 市场商城购物清单
     */
    @Schema(description = "市场商城购物清单")
    private List<MktAppGwcVendorDTO> list2;
    
    /**
     * 市场商城购物清单
     */
    @Schema(description = "退货信息")
    private MktRefund refund;
    
    /**
    * 配送员
    */
    @Schema(description = "配送员")
    private MktCourier courier;
    
    /**
     * 微信支付数据
     */
    @Schema(description = "微信支付数据")
    private WxPayData wxPayData;
    
    /**
    * 微信支付数据
    */
    @Schema(description = "礼品券状态")
    private CardStatus giftStatus;
    
    @Schema(description = "配送类型")
    private DistributionType distributionType;
    
    @Schema(description = "自有骑手配送状态")
    private ExpressStatus expressStatus;
    
    @Schema(description = "配送配置")
    @Column(nullable = false, columnDefinition = "bit")
    private Boolean distributionConfig;
    
    /**
    * 统一配置费
    */
    @Schema(description = "统一配置费")
    private BigDecimal fee;
    
    /**
    * 起步价
    */
    @Schema(description = "起步价")
    private BigDecimal startingPrice;
    
    /**
     * 配送时间
     */
    @Schema(description = "配送时间")
    private String pstime;
    
    /**
     * 配送时间
     */
    @Schema(description = "配送时间组")
    private List<String> pstimeOpt;
    
    /**
     * 配送选择
     */
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

    @Schema(description = "送达照片")
    private List<String> arrivedPhoto;
}
