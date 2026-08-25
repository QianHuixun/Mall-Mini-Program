package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 优惠券
 */

@Entity
@Data
@Table(name = "mkt_card")
@FieldNameConstants(innerTypeName = "F")
public class MktCard implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_card")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "价值")
    private BigDecimal cost;
    
    @Schema(description = "最低消费")
    private BigDecimal limitCost;
    
    @Schema(description = "有效期(天)")
    private Integer effective;
    
    @Schema(description = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "介绍")
    private String content;
    
    @Schema(description = "使用市场")
    private String userFarmer;
    
    @Schema(description = "使用商户")
    private Integer userVendor;
    
    @Schema(description = "使用分类")
    private Integer userType;
    
    @Deprecated
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
    
    @Schema(description = "领取方式")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CardType cardType;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "优惠券类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CardCouponType type;
    
    @Schema(description = "免邮费")
    private Boolean avoidPostage;
    
    @Schema(description = "领券码")
    private String cardCode;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "优惠券数量")
    private Integer count;
    
    @Schema(description = "已发放数量")
    private Integer issuedNum;
    
    @Schema(description = "已使用数量")
    private Integer usedNum;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
