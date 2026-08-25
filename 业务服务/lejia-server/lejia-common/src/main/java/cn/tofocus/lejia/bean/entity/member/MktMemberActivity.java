package cn.tofocus.lejia.bean.entity.member;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import cn.tofocus.db.AutoRedisID;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/4/26]
 */
@Data
@Entity
@Schema(description = "会员卡券活动参与表")
@Table(name = "mkt_member_activity")
@FieldNameConstants(innerTypeName = "F")
public class MktMemberActivity implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_member_activity")
    private Integer pkey;
    
    @Column
    @Schema(description = "会员")
    private Integer member;
    
    @Column
    @Schema(description = "卡券活动")
    private Integer activity;
    
    @Schema(description = "订单号")
    @Column(name = "kc_code", length = 30)
    private String code;
    
    @Digits(integer = 9, fraction = 2)
    @Column(precision = 11, scale = 2)
    @DecimalMax(value = "999999999.99")
    @DecimalMin(value = "-999999999.99")
    @Schema(description = "金额")
    private BigDecimal amt;

    // 目前仅使用
    // [未付款] UNPAID_ORDER
    // [确认] CONFIRM_ORDER
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    @Column(columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private OrderStatus status;
    
    @Schema(description = "结算状态")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private SettlementType settlementType;
    
    @Schema(description = "手续费承担")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CommissionType commissionType;
    
    @Schema(description = "支付时间")
    @Column
    private Date payTime;
    
    @Schema(description = "清分文件对应的pkey")
    private Integer filePkey;

    @Schema(description = "市场")
    @Column(length = 40, nullable = false)
    private String farmer;

    @Schema(description = "公司")
    @Column(length = 40, nullable = false)
    private String company;

    @Schema(description = "建档时间")
    @Column(nullable = false)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    @Column(nullable = false)
    private Integer ascription;
}
