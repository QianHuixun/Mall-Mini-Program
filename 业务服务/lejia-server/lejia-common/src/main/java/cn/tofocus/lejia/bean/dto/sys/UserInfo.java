package cn.tofocus.lejia.bean.dto.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfo 
{

	private Long pkey;
	
    @Schema(description = "登录账号")
    private String loginName;
    
    @Schema(description = "名称")
    private String name;
    
	@Schema(description = "手机号码")
    private String mobile;
}
