package cn.tofocus.lejia.bean.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppCourierDTO 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 姓名
	 */
	@Schema(description = "姓名")
	private String name;

	/**
	 * 手机
	 */
	@Schema(description = "手机")
	private String mobile;


	/**
	 * 市场
	 */
	@Schema(description = "市场")
	private String farmer;
	private String farmerName;
	@Schema(description = "今日接单")
	private Long orderToday;
	@Schema(description = "历史接单")
	private Long orderHistory;
}
