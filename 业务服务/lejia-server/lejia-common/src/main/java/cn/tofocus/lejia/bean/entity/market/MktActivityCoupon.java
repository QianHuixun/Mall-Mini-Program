package cn.tofocus.lejia.bean.entity.market;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/4/25]
 */
@Data
@Entity
@Schema(description = "卡券活动与卡券关联表")
@Table(name = "mkt_activity_coupon")
@IdClass(MktActivityCouponPkey.class)
@FieldNameConstants(innerTypeName = "F")
public class MktActivityCoupon implements HasPkey<MktActivityCouponPkey>
{
    @Id
    @Column(nullable = false)
    @Schema(description = "卡券活动")
    private Integer activity;
    
    @Id
    @Column(columnDefinition = "tinyint(4)", nullable = false)
    @Schema(description = "卡券类型")
    private CouponType couponType;
    
    @Id
    @Column(nullable = false)
    @Schema(description = "卡券主键")
    private Integer coupon;
    
    @Column
    @Schema(description = "张数")
    private Integer num;
    
    @Override
    public MktActivityCouponPkey getPkey()
    {
        return new MktActivityCouponPkey(activity, couponType, coupon);
    }
    
    @Override
    public void setPkey(MktActivityCouponPkey pkey)
    {
        setActivity(pkey.getActivity());
        setCouponType(pkey.getCouponType());
        setCoupon(pkey.getCoupon());
    }
}
