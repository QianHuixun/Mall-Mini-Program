package cn.tofocus.lejia.bean.entity.member;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.Name;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Deprecated
@Entity
@Data
@Table(name = "mkt_member_coupon_linshi")
@FieldNameConstants(innerTypeName = "F")
public class MktMemberCouponLinshi implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_member_coupon_linshi")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    @Column(name = "kc_code")
    @Name
    private String code;
    
    @Schema(description = "优惠券主键")
    private Integer card;

    @Schema(description = "活动主键")
    private String activity;
    
    @Schema(description = "openid1")
    private String openid1;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private OrderStatus status;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
