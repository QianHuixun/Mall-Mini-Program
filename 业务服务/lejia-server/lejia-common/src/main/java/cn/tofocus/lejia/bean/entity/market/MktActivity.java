package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import cn.tofocus.lejia.bean.enums.ActivityDistributeType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/4/25]
 */
@Data
@Entity
@Schema(description = "卡券活动表")
@Table(name = "mkt_activity")
@FieldNameConstants(innerTypeName = "F")
public class MktActivity implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_activity")
    private Integer pkey;
    
    @Size(max = 50)
    @Column(length = 50)
    @Schema(description = "名称")
    private String name;
    
    // 闭区间
    @Column
    @Schema(description = "开始时间")
    private Date startTime;
    
    // 开区间
    @Column
    @Schema(description = "结束时间")
    private Date endTime;
    
    @FileUrl
    @Size(max = 200)
    @Column(length = 200)
    @Schema(description = "图片")
    private String photo;
    
    @Digits(integer = 9, fraction = 2)
    @Column(precision = 11, scale = 2)
    @DecimalMax(value = "999999999.99")
    @DecimalMin(value = "-999999999.99")
    @Schema(description = "价格")
    private BigDecimal price;

    // 冗余，方便展示
    @Column
    @Schema(description = "卡券数量")
    private Integer couponNum;
    
    @Column
    @Schema(description = "套餐总数")
    private Integer num;

    // 冗余，方便展示
    @Column
    @Schema(description = "已发放数量")
    private Integer issuedNum = 0;
    
    @Column
    @Schema(description = "已领取卡券数")
    private Integer receiveNum = 0;
    
    @Column
    @Schema(description = "已使用卡券数")
    private Integer useNum = 0;
    
    @Column
    @Schema(description = "限制用户参与次数")
    private Integer limitMemberTimes;
    
    // -1表示无限制
    @Column
    @Schema(description = "每日限量")
    private Integer limitDailyNum;

    // -1表示无限制
    @Column
    @Schema(description = "限制优惠券每日张数")
    private Integer limitDailyCardNum;

    // -1表示无限制
    @Column
    @Schema(description = "限制礼品券每日张数")
    private Integer limitDailyGiftNum;

    @Column(columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "活动分发方式")
    private ActivityDistributeType distributeType;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "会员福利展示图")
    private String welfarePhoto;
    
    @Column(columnDefinition = "tinyint(4)")
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "市场")
    @Column(length = 40, nullable = false)
    private String farmer;
    
    @Schema(description = "公司")
    @Column(length = 40, nullable = false)
    private String company;
    
    @Schema(description = "修改时间")
    @Column(nullable = false)
    @LastModifiedDate
    private Date updatedTime;
    
    @Schema(description = "修改人")
    @Column(nullable = false)
    @LastModifiedBy
    private Integer updatedBy;
    
    @Schema(description = "建档时间")
    @Column(nullable = false)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档人")
    @Column(nullable = false)
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    @Column(nullable = false)
    private Integer ascription;
}
