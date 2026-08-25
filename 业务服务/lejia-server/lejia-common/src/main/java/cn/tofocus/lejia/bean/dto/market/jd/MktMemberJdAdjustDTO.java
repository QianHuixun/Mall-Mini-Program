package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;

import javax.validation.constraints.*;

import cn.tofocus.common.data.Amt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberJdAdjustDTO
{
    @Schema(description = "主键")
    @NotNull(message = "主键不能为空")
    private Integer pkey;

    @Schema(description = "调整方式（true:加 false:减）")
    @NotNull(message = "调整方式不能为空")
    private Boolean direct;
    
    @Amt
    @Schema(description = "调整金额")
    @NotNull(message = "调整金额不能为空")
    @Digits(integer = 8, fraction = 2)
    @DecimalMax(value = "99999999.99")
    @DecimalMin(value = "0.00")
    private BigDecimal amt;
    
    @Schema(description = "备注")
    @NotBlank(message = "备注不能为空")
    @Size(max = 100)
    private String remark;
}
