package cn.tofocus.lejia.bean.dto.zx;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ZxUserInfoForUpdVendorBank extends BaseZxUserInfoForUpdBank
{
    @NotNull(message = "商户主键不能为空")
    @Schema(description = "商户主键")
    private Integer vendor;
}
