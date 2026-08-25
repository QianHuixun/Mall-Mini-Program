package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.ActivityDistributeType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.exception.LejiaErrCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktActivityInfo
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Size(max = 50, message = "活动名称长度不能超过50")
    @Schema(description = "活动名称")
    private String name;
    
    // 闭区间
    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间")
    private Date startTime;
    
    // 开区间
    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private Date endTime;
    
    @NotBlank(message = "结束时间不能为空")
    @Size(max = 200, message = "活动宣传图片地址长度异常")
    @Schema(description = "活动宣传图")
    private String photo;
    
    @NotNull(message = "售卖价格不能为空")
    @Digits(integer = 9, fraction = 2)
    @DecimalMax(value = "999999999.99", message = "售卖价格超出限制")
    @DecimalMin(value = "-999999999.99", message = "售卖价格超出限制")
    @Schema(description = "售卖价格")
    private BigDecimal price;
    
    @NotNull(message = "套餐总数不能为空")
    @Min(value = 1, message = "套餐总数必须大于0")
    @Schema(description = "套餐总数")
    private Integer num;
    
    @NotNull(message = "用户可参与次数不能为空")
    @Min(value = 1, message = "用户可参与次数必须大于0")
    @Schema(description = "用户可参与次数")
    private Integer limitMemberTimes;
    
    // -1表示无限制，获取时候判断-1，则无限制设为true，该字段设为空
    @Min(value = 1, message = "每日限量必须大于0")
    @Schema(description = "每日限量")
    private Integer limitDailyNum;
    
    @Schema(description = "是否不限制每日限量")
    private Boolean isNoLimitDailyNum;
    
    // -1表示无限制，获取时候判断-1，则无限制设为true，该字段设为空
    @Min(value = 1, message = "优惠券限用张数必须大于0")
    @Schema(description = "优惠券限用张数")
    private Integer limitDailyCardNum;
    
    @Schema(description = "是否不限制优惠券限用张数")
    private Boolean isNoLimitDailyCardNum;
    
    // -1表示无限制，获取时候判断-1，则无限制设为true，该字段设为空
    @Min(value = 1, message = "礼品券限用张数必须大于0")
    @Schema(description = "礼品券限用张数")
    private Integer limitDailyGiftNum;
    
    @Schema(description = "是否不限制礼品券限用张数")
    private Boolean isNoLimitDailyGiftNum;

    @NotNull(message = "活动分发方式不能为空")
    @Schema(description = "活动分发方式")
    private ActivityDistributeType distributeType;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Valid
    @NotEmpty(message = "卡券列表不能为空")
    @Schema(description = "卡券列表")
    @JoinDTO(dataQuery = "mktActivityCouponDao", referencedName = "activity", type = MktActivityCouponOnList.class)
    private List<MktActivityCouponOnList> coupons;
    
    @Schema(description = "标签主键")
    private List<Integer> tagKeys;
    
    @Schema(description = "会员福利展示图")
    private String welfarePhoto;
    
    public void checkLimit4Save()
    {
        if (!Boolean.TRUE.equals(isNoLimitDailyNum) && limitDailyNum == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请设置每日限量");
        if (!Boolean.TRUE.equals(isNoLimitDailyCardNum) && limitDailyCardNum == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请设置优惠券限用张数");
        if (!Boolean.TRUE.equals(isNoLimitDailyGiftNum) && limitDailyGiftNum == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请设置礼品券限用张数");
    }
    
    public void handleLimit4Get()
    {
        if (limitDailyNum == -1)
        {
            isNoLimitDailyNum = true;
            limitDailyNum = null;
        }
        if (limitDailyCardNum == -1)
        {
            isNoLimitDailyCardNum = true;
            limitDailyCardNum = null;
        }
        if (limitDailyGiftNum == -1)
        {
            isNoLimitDailyGiftNum = true;
            limitDailyGiftNum = null;
        }
    }

    @Schema(description = "发送微信订阅消息")
    private boolean sendWechatMsg;
}
