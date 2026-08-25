package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderOnList
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
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    private OrderStatus status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
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
    
    @Schema(description = "采购状态")
    private PurchaseStatus purchaseStatus;
    
    @Schema(description = "采购状态名称")
    public String getPurchaseStatusName()
    {
        if (purchaseStatus != null) return purchaseStatus.getName();
        return "";
    }
    
    @Schema(description = "采购金额")
    private BigDecimal purchaseAmt;
    
    @Schema(description = "订单来源 自营/积分商城/市场商城")
    private OrderOir orderOir;
    
    /**
    * 订单类型 砍价/团购/预售/佣金/普通
    */
    @Schema(description = "订单类型 砍价/团购/预售/佣金/市场/积分")
    private OrderType orderType;
    
    @Schema(description = "订单类型")
    private String orderTypeName;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private PayType payType;
    
    @Schema(description = "支付类型")
    private String payTypeName;
    
    private Integer cgCheck;
    
    private String cgCheckName;
    
    @Schema(description = "配送时间")
    private String pstime;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    @Schema(description = "邮费")
    private BigDecimal oldPostage;
    
    @Schema(description = "邮费")
    private BigDecimal postage;
    
    public BigDecimal getPostage()
    {
        if(oldPostage != null)
            return oldPostage;
        return postage;
    }
    
    @Schema(description = "订单价格")
    private BigDecimal amto;
    
    @Schema(description = "总价")
    private BigDecimal amtall;
    
    @Schema(description = "支付金额")
    private BigDecimal amtn;
    
    @Schema(description = "支付积分")
    private Integer pointn;
    
    @Schema(description = "支付佣金")
    private BigDecimal commn;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "支付卡券")
    private Integer card;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "退款积分")
    private Integer refundPoint;
    
    @Schema(description = "推荐人")
    private Integer tjr;
    
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
    * 建档时间
    */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    private String qrCode;
    
    @Schema(description = "快递公司", required = false)
    private String logistics;
    
    List<Map<String, Object>> goodsList = new ArrayList<>();
    
    @Schema(description = "地址", required = false)
    private String addr;
    
    /**
     * 收货人
     */
    @Schema(description = "收货人", required = false)
    private String name;
    
    private String remark;
    
    /**
     * 收货人手机
     */
    @Schema(description = "收货人手机", required = false)
    private String mobile;
    
    @Schema(description = "自提和配送")
    private DistributionType distributionType;
    
    @Schema(description = "骑手类型,可为空")
    private ExpressType expressType;
    
    @Schema(description = "自有骑手配送状态")
    private ExpressStatus expressStatus;
    
    @JoinEnum(from = "expressType")
    private String expressTypeName;
    
    @Schema(description = "自提码")
    private String pickupCode;
    
    @Schema(description = "小票码")
    private String orderTrace;
    
    private Integer smallTicket;

    @Schema(description = "核销时间")
    private String pickupTime;
    
    @Schema(description = "是否核销")
    private Boolean pickupFlag;
    
    @Schema(description = "配送方式")
    public String getDistributionTypeName()
    {
        if (distributionType != null) return distributionType.getName();
        return null;
    }
    
    @Schema(description = "退款情况")
    private String refundInfo;
    
    @Schema(description = "用户标签")
    private String tagName;
}
