package cn.tofocus.lejia.bean.dto.app.vendor;


import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppWalletBillOnInfo
{
    @Schema(description = "未结算列表")
    private List<AppVendorBillOnList> noSettlement;
    
    @Schema(description = "已结算列表")
    private List<AppVendorBillOnList> settlement;
}
