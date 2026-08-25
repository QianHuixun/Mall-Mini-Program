package cn.tofocus.lejia.bean.dto.app.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorWalletBankInfo
{
    @Schema(description = "银行卡号")
    private String bankcard;
    
    @Schema(description = "所属银行")
    private String bankname;
    
    @Schema(description = "开户支行")
    private String bankBranchName;
    
    @Schema(description = "持卡人姓名")
    private String bankuser;
    
    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "验证码")
    private String code;

    @Schema(description = "是否允许编辑")
    private boolean allowedUpd = true;
}
