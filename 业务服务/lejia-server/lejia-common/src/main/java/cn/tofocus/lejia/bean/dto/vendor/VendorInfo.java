package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorInfo
{
    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "银行名称")
    private String bankname;
    
    @Schema(description = "银行卡号")
    private String bankcard;
    
    @Schema(description = "开户支行名称")
    private String bankBranchName;
    
    @Schema(description = "开户行大额行号")
    private String bankNo;
    
    @Schema(description = "开户人")
    private String bankuser;
    
    @Schema(description = "中信银行主键")
    private String zxUserId;
    
    @Schema(description = "中信-身份证号码")
    private String zxIdentity;
    
    @Schema(description = "银行卡绑定手机")
    private String bankuserMoblie;
    
    private BigDecimal commissionRate;
    
    @JsonIgnore
    private String farmer;
    
    @JsonIgnore
    private String company;
    
    @Schema(description = "佣金费率")
    public String getCommission()
    {
        if (commissionRate != null) return commissionRate.stripTrailingZeros().toPlainString() + "%";
        return "";
    }
}
