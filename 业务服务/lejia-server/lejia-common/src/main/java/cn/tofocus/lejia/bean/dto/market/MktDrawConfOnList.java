package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktDrawConfOnList 
{

	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;

	/**
	 * 支付积分
	 */
	@Schema(description = "支付积分", required = true)
	private Integer point;


	/**
	 * 启用标志
	 */
//	@Schema(description = "启用标志", hidden = true)
//	private Boolean enabled;

	/**
	 * 建档时间
	 */
//	@Schema(description = "建档时间", hidden = true)
//	private Date createdTime;
}
