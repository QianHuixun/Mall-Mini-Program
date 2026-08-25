package cn.tofocus.lejia.bean.dto.zx;

import javax.validation.constraints.NotBlank;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.annotation.ValidStringIn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BaseZxUserInfoForUpdBank
{
    // 仅工会类型保存
    @Schema(description = "账户名称")
    private String name;
    
    @Schema(description = "用户类型, 1-个人 2-企业 3-个体工商户")
    private String userType;

    @Schema(description = "中信银行主键")
    @JsonIgnore
    private String zxUserId;

    @Schema(description = "是否注册")
    public boolean getRegistered()
    {
        return StringUtil.isNotBlank(zxUserId);
    }
    
    @NotBlank(message = "开户银行联行号不能为空")
    @Schema(description = "开户银行联行号")
    private String panNum;
    
    @NotBlank(message = "银行账号不能为空")
    @Schema(description = "银行账号")
    private String pan;
    
    @ValidStringIn(values = {"1", "2", "3", "4", "5", "6"}, message = "银行账户类型不合法")
    @Schema(description = "银行账户类型")
    private String acctType;
    
    @NotBlank(message = "银行预留手机号不能为空")
    @Schema(description = "银行预留手机号")
    private String bankPhone;
}
