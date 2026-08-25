package cn.tofocus.lejia.bean.dto.app.vendor;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderInfoV2
{
    @Schema(description = "未结营收")
    private String awaitSettlement;
    
    @Schema(description = "已结营收")
    private String alreadySettlement;
    
    @Schema(description = "订单明细")
    private PageResult<VendorOrderOnPage> lines;
}
