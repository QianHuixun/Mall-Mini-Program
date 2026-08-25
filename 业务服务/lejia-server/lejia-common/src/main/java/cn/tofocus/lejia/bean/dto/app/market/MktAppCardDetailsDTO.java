package cn.tofocus.lejia.bean.dto.app.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MktAppCardDetailsDTO {

	/**
	 * pkey
	 */
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;

	/**
	 * 价值
	 */
	@Schema(description = "价值")
	private BigDecimal cost;

	/**
	 * 最低消费
	 */
	@Schema(description = "最低消费")
	private BigDecimal limitCost;

	/**
	 * 有效期(天)
	 */
	@Schema(description = "有效期(天)")
	private Integer effective;

	/**
	 * 到期日期
	 */
	@Schema(description = "到期日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date endDate;

	/**
	 * 介绍
	 */
	@Schema(description = "介绍")
	private String content;
}
