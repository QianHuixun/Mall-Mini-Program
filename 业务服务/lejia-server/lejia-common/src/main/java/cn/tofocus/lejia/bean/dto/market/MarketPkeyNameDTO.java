package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MarketPkeyNameDTO
{
	/**
	 * 主键
	 */
	@Schema(description = "pkey")
	private String pkey;

	/**
	 * 市场名称
	 */
	@Schema(description = "市场名称")
	private String name;
}
