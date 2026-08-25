package cn.tofocus.lejia.bean.dto.pub;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberUsingCouponInfo
{   
    private Integer pkey;
    
    private String cardTypeName;
    
    private String status;
    
    @Schema(description = "结算状态 初始/未结算/已结算")
    private String settleStatus;
    
    @Schema(description = "会员")
    private Integer memberKey;
    
    @Schema(description = "会员昵称")
    private String memberName;
    
    @JsonIgnore
    private String memberMobile;
    
    private String mobile;
    
    @Schema(description = "会员手机号码")
    public String getMobile()
    {
        if (memberMobile != null)
            return memberMobile.replaceAll("(\\d{2})\\d{5}(\\d{4})", "$1****$2");//脱敏处理
        else if (mobile != null)
            return mobile;
        else
            return "";
        
    }
    
    @Schema(description = "优惠券")
    private Integer coupon;
    
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @Schema(description = "卡券编号")
    private String couponNumber;
    
    @Schema(description = "卡券价值")
    private BigDecimal cost;
    
    @Schema(description = "最低消费")
    private BigDecimal limitCost;
    
    @Schema(description = "开始日期")
    private Date startDate;
    
    @Schema(description = "到期日期")
    private Date endDate;
    
    @Schema(description = "使用商户")
    private String userMerchant;
    
    @Schema(description = "使用日期")
    private Date userTime;
    
    @Schema(description = "结算日期")
    private Date settleDate;
    
    @Schema(description = "交易时间")
    private Date tradeTime;
    
    @Schema(description = "订单编号")
    private String orderNumber;
    
    @Schema(description = "订单金额")
    private BigDecimal totalAmt;
    
    @Schema(description = "支付金额")
    private BigDecimal orderAmt;
    
    /**
     * 新加 优惠券礼包名字和pkey
     */
    @Schema(description = "礼包券主键")
    private Integer giftPacks;
    
    @Schema(description = "优惠券礼包名字")
    private String giftpacksName;
    
    @Schema(description = "领取时间")
    private Date createdTime;
}
