package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppCardDTO {

	
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
    * 到期日期
    */
	@Schema(description = "到期日期")
	private Date endDate;
}
