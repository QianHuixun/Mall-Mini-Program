package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 运营端-派单配置
 * @author geshaojian
 * @since Created in 2021/9/30 14:38
 */
@Data
public class SupplySendConfDTO
{
    @Schema(description = "派单配置（false-市场自定义，true-统一配置）", required = true)
    @NotNull(message = "派单配置不能为空")
    private Boolean isOperation;

    @Schema(description = "统一配置是人工还是自动（false-人工，true-自动）")
    private Boolean automaticPurchase;
}
