package cn.tofocus.lejia.bean.dto.sys;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysLogOnList 
{
	@Schema(description = "编号")
	private Long pkey;

	@Schema(description = "用户主键")
	private Integer userPkey;

	@Schema(description = "用户ID")
	private String userId;

	@Schema(description = "用户名称")
	private String userName;

	@Schema(description = "手机号码")
	private String mobile;

	@Schema(description = "应用ID")
	private String appId;

	@Schema(description = "远端地址")
	private String remoteAddress;

	@Schema(description = "操作")
	private String operation;

	@Schema(description = "开始时间")
	private Date beginTime;

	@Schema(description = "处理用时")
	private Integer procMillisecond;

	@Schema(description = "是否成功")
	private Boolean success;

	@Schema(description = "操作内容")
	private String content;

	@Schema(description = "结果")
	private String result;

	@Schema(description = "市场")
	private String market;

	@Schema(description = "公司")
	private String company;
}
