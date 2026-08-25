package cn.tofocus.lejia.bean.dto.market.print;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ScalePrintOriIfo
{
    @Schema(description = "货品名不能为空")
    @NotBlank(message = "货品名不能为空")
    private String goodsName;
    
    @Schema(description = "规格不能为空")
    @Size(max = 30, message = "规格大于30个字")
    private String specifications;
    
    @NotNull(message = "货品重量不能为空")
    private BigDecimal goodsWeight;
    
    @NotNull(message = "货品单价不能为空")
    private BigDecimal goodsPrice;
    
    @NotNull(message = "货品总价不能为空")
    private BigDecimal goodsAmt;
}
