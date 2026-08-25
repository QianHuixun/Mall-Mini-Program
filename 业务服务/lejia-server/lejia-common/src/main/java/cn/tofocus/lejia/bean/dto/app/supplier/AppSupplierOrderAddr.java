package cn.tofocus.lejia.bean.dto.app.supplier;

import cn.tofocus.core.json.MaskPhone;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppSupplierOrderAddr extends MktAppAddrDTO
{
    @MaskPhone
    @Schema(description = "收货人手机")
    private String mobile;
}
