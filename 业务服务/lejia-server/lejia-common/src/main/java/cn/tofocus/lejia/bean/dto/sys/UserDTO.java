package cn.tofocus.lejia.bean.dto.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserDTO 
{
	
	@Schema(description = "pkey")
	private Integer pkey;

	@Schema(description = "手机号码")
	private String mobile;

	@Schema(description = "昵称")
	private String nickname;
	/**
	 * 市场
	 */
	@Schema(description = "市场")
	private String farmer;

	/**
	 * 公司
	 */
	@Schema(description = "公司")
	private String company;
	
	@Schema(description = "角色")
	private String roleKey;
	
	@Schema(description = "角色")
	private String roleKeyName;
}
