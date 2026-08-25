package cn.tofocus.lejia.bean.dto.v2.order;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.goods.GoodsGiftInfo;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.v3.GwcSupplierPickupLocationInfo;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderTotalV2Info
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "pkey1")
    private Integer pkey1;
    
    @Schema(description = "pkey2")
    private Integer pkey2;
    
    @Schema(description = "推荐人")
    private Integer tjr;
    
    @Schema(description = "购买者")
    private Integer member;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    private OrderStatus status;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType unpayType;
    
    @Schema(description = "订单类型 砍价/团购/预售/佣金/普通")
    private OrderType orderType;
    
    @Schema(description = "是否自提")
    private Boolean pickupType;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
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
    private Integer pointn;
    
    @Schema(description = "我的积分")
    private Integer myPoints;
    
    @Schema(description = "支付佣金")
    private BigDecimal commn;
    
    @Schema(description = "我的佣金/钱包余额")
    private BigDecimal myCommn;
    
    @Schema(description = "用户民生豆")
    private BigDecimal myMsd;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "支付卡券")
    private Integer card;
    
    @Schema(description = "配送卡券主键")
    private Integer cardPostage;
    
    @Schema(description = "配送卡券名称")
    private String cardPostageName;

    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "卡券编号")
    private String cardCode;
    
    @Schema(description = "是否有卡券可用")
    private Boolean cardUsable = false;
    
    @Schema(description = "是否有配送费卡券可用")
    private Boolean cardPostageUsable = false;
    
    @Schema(description = "是否可以使用卡券可用- 只有市场商品才能使用卡券")
    private Boolean isCard = false;
    
    @Schema(description = "会员减少的金额")
    private BigDecimal reducePrice;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "收货地址")
    private MktAppAddrDTO addr;
    
    @Schema(description = "积分商品信息")
    private List<OrderV2Info> pointInfo;
    
    @Schema(description = "积分商品图片")
    private List<String> pointPhoto;
    
    @Schema(description = "积分商品数量")
    private Integer pointNum;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "市场商城商品信息")
    private List<OrderV2Info> farmerInfo;
    
    @Schema(description = "市场商城商品图片")
    private List<String> farmerPhoto;
    
    @Schema(description = "市场商城商品数量")
    private Integer farmerNum;
    
    @Schema(description = "配送类型")
    private DistributionType distributionType;
    
    @Schema(description = "配送配置")
    private Boolean distributionConfig;
    
    @Schema(description = "统一配置费")
    private BigDecimal fee;
    
    @Schema(description = "起步价")
    private BigDecimal startingPrice;
    
    @Schema(description = "配送时间")
    private String pstime;
    
    @Schema(description = "配送时间组")
    private List<String> pstimeOpt;
    
    @Schema(description = "配送选择")
    private List<DistributionTypeDTO> distype;
    
    @Schema(description = "自提金额")
    private BigDecimal pickupAmt;
    
    @Schema(description = "是否包邮")
    private Boolean postFree;
    
    @Schema(description = "是否开启配送")
    private Boolean delivery;
    
    @Schema(description = "是否开启自提")
    private Boolean pickup;
    
    @Schema(description = "自提地点主键")
    private Integer pickupPkey;
    
    @Schema(description = "微信支付数据")
    private WxPayData wxPayData;
    
    @Schema(description = "礼券相关信息")
    private GoodsGiftInfo giftInfo;
    
    @Schema(description = "优惠券类型")
    private CardCouponType type;
    
    @Schema(description = "是否只能堂食,true:是")
    private Boolean dineIn;
    
    private String weekTime;
    
    private String dayTime;
    
    @Schema(description = "自提地点")
    private List<GwcSupplierPickupLocationInfo> splList;
    
    @Schema(description = "是否可用热力豆支付")
    private Boolean msdPay = false;
    
    @Schema(description = "允许市场商品使用热力豆支付")
    private Boolean farmerGoods = false;
    
    @Schema(description = "允许自营、滨农、预售使用热力豆支付")
    private Boolean sysGoods = false;
}
