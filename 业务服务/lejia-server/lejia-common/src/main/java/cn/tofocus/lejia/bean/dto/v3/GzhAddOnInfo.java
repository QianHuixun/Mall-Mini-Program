package cn.tofocus.lejia.bean.dto.v3;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GzhAddOnInfo
{
    @Schema(description = "手机")
    @NotEmpty(message = "手机不能为空")
    private String mobile;
    
    @Schema(description = "名称")
    @NotEmpty(message = "名称不能为空")
    private String name;
    
    @NotEmpty(message = "openid不能为空")
    private String openid;
    
    @NotEmpty(message = "头像地址")
    private String url;
    
    @NotNull(message = "ascription不能为空")
    private Integer ascription;
    
    @NotNull(message = "验证码")
    private String code;
}
