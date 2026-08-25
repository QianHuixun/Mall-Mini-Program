package cn.tofocus.lejia.bean.entity.member;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.MType;
import lombok.experimental.FieldNameConstants;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户优惠券
 */

@Entity
@Data
@Table(name = "mkt_member_card")
@FieldNameConstants(innerTypeName = "F")
public class MktMemberCard implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_member_card")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "状态 初始/已使用/已过期")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CardStatus status;
    
    @Schema(description = "会员")
    @Column(name = "member_key")
    private Integer member;
    
    @Schema(description = "优惠券")
    private Integer card;
    
    @Schema(description = "优惠券类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CardCouponType type;

    @Schema(description = "免邮费")
    private Boolean avoidPostage;
    
    // 为空表示非活动发放
    @Schema(description = "卡券活动")
    private Integer activity;
    
    @Schema(description = "优惠券编码")
    private String cardNumber;
    
    @Schema(description = "卡券价值")
    private BigDecimal cost;
    
    @Schema(description = "最低消费")
    private BigDecimal limitCost;
    
    @Schema(description = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    private Date endDate;
    
    @Schema(description = "订单编号")
    private Integer orderId;
    
    @Schema(description = "市场")
    private String userFarmer;

    @Schema(description = "商户")
    private Integer userVendor;
    
    @Schema(description = "心安食足商户主键")
    private Integer userMerchant;
    
    @Schema(description = "心安食足商户名称")
    private String userMerchantName;
    
    @Schema(description = "使用分类")
    private Integer userType;
    
    @Schema(description = "使用商品")
    private Integer userGoods;
    
    @Schema(description = "使用商品")
    @Convert(converter = ListConverter.class)
    private List<Integer> userGoodsList;
    
    @Schema(description = "使用专区")
    @Convert(converter = ListConverter.class)
    private List<Integer> userMtype;

    @Schema(description = "使用订单类型")
    @Column(columnDefinition = "tinyint(4)")
    private CardUserOrderType userOrderType;
    
    @Schema(description = "使用时间")
    private Date userTime;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "是否已读")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean isRead;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
