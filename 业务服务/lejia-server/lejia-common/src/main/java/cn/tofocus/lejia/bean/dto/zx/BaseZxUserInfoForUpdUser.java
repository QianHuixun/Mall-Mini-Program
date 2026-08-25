package cn.tofocus.lejia.bean.dto.zx;

import javax.validation.constraints.NotBlank;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.annotation.ValidStringIn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BaseZxUserInfoForUpdUser
{
    // 仅工会类型保存
    @Schema(description = "账户名称")
    private String name;

    @Schema(description = "中信银行主键")
    @JsonIgnore
    private String zxUserId;

    @Schema(description = "是否注册")
    public boolean getRegistered()
    {
        return StringUtil.isNotBlank(zxUserId);
    }
    
    @NotBlank(message = "用户类型不能为空")
    @ValidStringIn(values = {"1", "2", "3"}, message = "用户类型不合法")
    @Schema(description = "用户类型, 1-个人 2-企业 3-个体工商户")
    private String userType;
    
    @NotBlank(message = "用户名称不能为空")
    @Schema(description = "用户名称")
    private String userNm;
    
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String userPhone;
    
    @NotBlank(message = "证件类型不能为空")
    @ValidStringIn(values = {"01", "22", "23", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
        "37", "38", "39", "02", "03", "04", "05", "06", "07", "08"}, message = "证件类型不合法")
    @Schema(description = "证件类型")
    private String userIdType;
    
    @NotBlank(message = "证件号码不能为空")
    @Schema(description = "证件号码")
    private String userIdNo;
    
    @Schema(description = "法人姓名")
    private String corpNm;
    
    @Schema(description = "法人证件类型")
    private String corpIdType;
    
    @Schema(description = "法人证件号码")
    private String corpIdNo;
}
