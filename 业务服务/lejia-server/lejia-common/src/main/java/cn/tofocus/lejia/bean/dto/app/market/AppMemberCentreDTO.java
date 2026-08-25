package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员中心
 */
@Data
public class AppMemberCentreDTO
{
    @Schema(description = "名称")
    private String nickName;
    
    @Schema(description = "头像")
    private String photo;
    
    @Schema(description = "手机")
    private String mobile;
    
    private String hideMobile;
    
    @Schema(description = "等级")
    private LevelType level;
    
    @Schema(description = "连续签到天数")
    private Integer signNum;
    
    @Schema(description = "会员可以领取的卡券数量")
    private Integer acceptCardNum;
    
    @Schema(description = "钱包金额")
    private BigDecimal comms;
    
    @Schema(description = "农贸会员卡金额")
    private BigDecimal xaszComms;
    
    @Schema(description = "积分")
    private Integer points;
    
    @Schema(description = "会员已有卡券数量")
    private Integer cardNum;
    
    @Schema(description = "待付款数量")
    private Integer unpaidOrderNum = 0;
    
    @Schema(description = "待发货数量")
    private Integer deliveredOrderNum = 0;
    
    @Schema(description = "已发货数量")
    private Integer shippedOrderNum = 0;
    
    @Schema(description = "状态 正常/注销中/已注销")
    private MemberStatus status;
    
    private Boolean nowDays;
    
    @Schema(description = "true:民生豆用户")
    private Boolean isMsd;
    
    @Schema(description = "民生豆余额")
    private BigDecimal msdBalance;

    @Schema(description = "是否允许分发活动")
    private Boolean allowedDistributeActivity;
    
    @Schema(description = "状态")
    public String getStatusName()
    {
        if (status != null) return status.getName();
        return "";
    }
}
