package cn.tofocus.lejia.bean.dto.vendor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BankInfo
{
    @JsonIgnore
    private Integer pkey;
    
    @Schema(description = "银行名称")
    private String bankname;
    
    @Schema(description = "银行卡号")
    private String bankcard;
    
    @Schema(description = "开户人")
    private String bankuser;
    
    @Schema(description = "中信-身份证号码")
    private String zxIdentity;
    
    @Schema(description = "银行卡绑定手机")
    private String bankuserMoblie;
}
