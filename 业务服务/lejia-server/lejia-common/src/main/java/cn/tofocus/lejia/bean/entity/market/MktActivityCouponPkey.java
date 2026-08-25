package cn.tofocus.lejia.bean.entity.market;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import cn.tofocus.lejia.bean.enums.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MktActivityCouponPkey implements Serializable
{
    private static final long serialVersionUID = -8159455905157684752L;

    @Id
    @NotNull(message = "卡券活动不能为空")
    @Column(nullable = false)
    @Schema(description = "卡券活动")
    private Integer activity;

    @Id
    @NotNull(message = "卡券类型不能为空")
    @Column(columnDefinition = "tinyint(4)", nullable = false)
    @Schema(description = "卡券类型")
    private CouponType couponType;

    @Id
    @NotNull(message = "卡券主键不能为空")
    @Column(nullable = false)
    @Schema(description = "卡券主键")
    private Integer coupon;
}
