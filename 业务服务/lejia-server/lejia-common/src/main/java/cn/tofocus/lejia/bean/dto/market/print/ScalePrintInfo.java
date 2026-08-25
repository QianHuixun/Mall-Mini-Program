package cn.tofocus.lejia.bean.dto.market.print;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ScalePrintInfo
{
    private String appid;
    
    private String scaleCode;
    
    @NotNull
    @Schema(description = "市场商户主键")
    private Integer merchant;
    
    @NotNull
    @Schema(description = "订单追溯号")
    private String orderTrace;
    
    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    @Size(max = 20)
    private String orderNo;
    
    @Schema(description = "市场名")
    @NotBlank(message = "市场名不能为空")
    @Size(max = 50)
    private String marketName;
    
    @Schema(description = "商户名")
    @NotBlank(message = "商户名不能为空")
    @Size(max = 50)
    private String merchantName;
    
    @Schema(description = "摊位号不能为空")
    @NotBlank(message = "摊位号不能为空")
    @Size(max = 50)
    private String booth;
    
    @Size(max = 14, min = 14, message = "交易时间必须是14位格式YYMMDDhhmmss")
    private String tradeTime;
    
    @Schema(description = "交易金额")
    @NotNull(message = "金额不能为空")
    private BigDecimal orderAmt;//金额
    
    @Schema(description = "溯源明细")
    @NotNull(message = "溯源明细")
    @Valid
    private List<ScalePrintOriIfo> ori;
}
