package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 对账中心 每日数据
 * @author Administrator
 *
 */
@Data
public class PayDayDTO 
{
	@Schema(description = "日期")
	private String day;

	@Schema(description = "收入")
	private String amt;
	
	@Schema(description = "收入笔数")
	private String amtNum;
	
	@Schema(description = "手续费")
	private String handlingFee;
	
	@Schema(description = "支出")
	private String expenditure;
	
	@Schema(description = "支出笔数")
	private String expenditureNum;
	
	@Schema(description = "收益")
	private String income;

}
