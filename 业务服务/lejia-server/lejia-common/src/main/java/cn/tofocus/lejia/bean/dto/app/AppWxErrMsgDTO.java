package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppWxErrMsgDTO
{
	/**
	 * 错误码
	 */
	@Schema(description = "错误码")
	private String errcode;

	/**
	 * 错误信息
	 */
	@Schema(description = "错误信息")
	private String errmsg;

}
