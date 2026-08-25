package cn.tofocus.lejia.bean.dto.pub;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberUsingCouponDto
{
    private Integer pkey;
    
    @Schema(description = "分享受类型")
    private String shareLinkType;
    
    @Schema(description = "优惠券")
    private Integer coupon;
    
    @Schema(description = "优惠券名称")
    private String couponName;
    
    @Schema(description = "卡券编号")
    private String couponNumber;
    
    @Schema(description = "礼品券图片")
    private String photo;
    
    @Schema(description = "卡券价值")
    private BigDecimal cost;
    
    @Schema(description = "最低消费")
    private BigDecimal limitCost;
    
    @Schema(description = "开始日期")
    private Date startDate;
    
    @Schema(description = "到期日期")
    private Date endDate;
    
    @Schema(description = "使用商户，在未使用时填入 范围，在使用时，填入 商户名，在过期时填入 范围")
    private String userMerchant;
    
    @Schema(description = "使用日期")
    private Date userTime;
    
    @Schema(description = "目前该消费券可用 ture,停用false")
    private Boolean enbaled;
    
    @Schema(description = "是否可以用 false 不可能用，其他可用")
    private Boolean available;
    
    @Schema(description = "领取时间")
    private Date createdTime;
    
    private String marketName;
    
    @Schema(description = "经度")
    private String lon;
    
    @Schema(description = "纬度")
    private String lat;
}
