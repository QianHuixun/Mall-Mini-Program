package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 优惠券
 */
@Data
public class MktCardOnList
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
    
    @Schema(description = "有效期")
    private String effectiveDate;
    
    /**
     * 介绍
     */
    @Schema(description = "介绍", required = true)
    private String content;
    
    @Schema(description = "使用范围")
    private String rangUse;
    
    @Schema(description = "使用市场pkey")
    private String userFarmer;
    
    @Schema(description = "使用市场名称")
    private String userFarmerName = "";
    
    @Schema(description = "使用分类")
    private Integer userType;
    
    @Schema(description = "使用分类名称")
    private String userTypeName = "";
    
    @Schema(description = "使用商品")
    private Integer userGoods;
    
    @Schema(description = "使用商品名称")
    private String userGoodsName = "";
    
    @Schema(description = "使用商品")
    private List<Integer> userGoodsList;
    
    @Schema(description = "使用专区")
    private List<Integer> userMtype;
    
    @Schema(description = "使用专区名称")
    private String userMtypeName = "";
    
    @Schema(description = "使用订单类型")
    private CardUserOrderType userOrderType;
    
    @Schema(description = "使用订单类型名称")
    public String getUserOrderTypeName()
    {
        return userOrderType == null ? null : userOrderType.getName();
    }
    
    @Schema(description = "优惠券类型")
    private CardCouponType type;
    
    @Schema(description = "优惠券类型名称")
    @JoinEnum(from = "type")
    private String typeName;
    
    @Schema(description = "免邮费, true免邮费")
    private Boolean avoidPostage;
    
    @Schema(description = "已发放数量", hidden = true)
    private Integer issuedNum = 0;
    
    @Schema(description = "已使用数量", hidden = true)
    private Integer usedNum = 0;
    
    @Schema(description = "领取方式")
    private CardType cardType;
    
    /**
     * 启用标志
     */
    @Schema(description = "启用标志", hidden = true)
    private Boolean enabled;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    /**
     * 建档时间
     */
    @Schema(description = "建档时间", hidden = true)
    private Date createdTime;
    
    @Schema(description = "优惠券数量")
    private Integer count;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "标签主键")
    private List<Integer> tagKeys;
    
    @JsonIgnore
    private String farmer;
}
