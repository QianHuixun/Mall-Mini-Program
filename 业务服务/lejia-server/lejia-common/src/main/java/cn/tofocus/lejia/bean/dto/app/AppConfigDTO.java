package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppConfigDTO 
{
	@Schema(description = "客服电话", required = true)
	private String tel;
	@Schema(description = "微信号", required = false)
	private String wechatNum;
	@Schema(description = "微信二维码", required = false)
	private String wechatCode;
}
