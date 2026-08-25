package cn.tofocus.lejia.bean.dto.market;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import cn.tofocus.lejia.bean.enums.CouponType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class MktActivityCouponOnList
{
    @NotNull(message = "卡券类型不能为空")
    @Schema(description = "卡券类型")
    private CouponType couponType;
    
    @NotNull(message = "卡券主键不能为空")
    @Schema(description = "卡券主键")
    private Integer coupon;
    
    @NotNull(message = "卡券单次派发张数不能为空")
    @Min(value = 1, message = "卡券单次派发张数必须大于0")
    @Schema(description = "张数")
    private Integer num;

    @Schema(description = "卡券名称")
    private String couponTitle;

    @Schema(description = "有效期(天)")
    private Integer effective;

    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Schema(description = "卡券数量")
    private Integer couponCount;

    @Schema(description = "已发放数量")
    private Integer couponIssuedNum;
}
