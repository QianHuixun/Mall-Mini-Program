package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberCardDTO 
{

	
	
	@Schema(description = "订单号")
	private String code;
	private String cardName;
	@Schema(description = "使用市场")
	private String userFarmerName;
    /**
    * 使用时间
    */
	@Schema(description = "使用时间")
	private String userTime;
}
