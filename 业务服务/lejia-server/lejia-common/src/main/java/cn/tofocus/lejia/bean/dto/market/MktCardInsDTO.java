package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Convert;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktCardInsDTO
{
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;
    
    /**
     * 标题
     */
    @Schema(description = "标题", required = true)
    private String title;
    
    /**
     * 价值
     */
    @Schema(description = "价值", required = true)
    private BigDecimal cost;
    
    /**
     * 最低消费
     */
    @Schema(description = "最低消费", required = true)
    private BigDecimal limitCost;
    
    @Schema(description = "到期选择, true 为 多少天后到期   false 为 指定到期日期", required = true)
    private Boolean expireChoose;
    
    /**
     * 有效期(天)
     */
    @Schema(description = "有效期(天)")
    private Integer effective;
    
    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /**
     * 到期日期
     */
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    /**
     * 介绍
     */
    @Schema(description = "介绍", required = true)
    private String content;
    
    /**
     * 使用市场pkey
     */
    @Schema(description = "使用市场pkey")
    private String userFarmer;
    
    /**
     * 使用分类
     */
    @Schema(description = "使用分类")
    private Integer userType;
    
    /**
     * 使用商品
     */
    @Schema(description = "使用商品")
    private Integer userGoods;
    
    @Schema(description = "使用商品")
    private List<Integer> userGoodsList;
    
    @Schema(description = "使用专区")
    private List<Integer> userMtype;
    
    @Schema(description = "使用订单类型")
    private CardUserOrderType userOrderType;
    
    @Schema(description = "领取方式")
    private CardType cardType;
    
    @Schema(description = "优惠券数量")
    @Min(value = 0L, message = "优惠券数量最小为0")
    @Max(value = 2147483647L, message = "优惠券数量最大为2147483647")
    private Integer count;
    
    @Schema(description = "优惠券类型")
    private CardCouponType type;
    
    @Schema(description = "免邮费, true免邮费")
    private Boolean avoidPostage;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "标签主键")
    private List<Integer> tagKeys;

    @Schema(description = "发送微信订阅消息")
    private boolean sendWechatMsg;
}
