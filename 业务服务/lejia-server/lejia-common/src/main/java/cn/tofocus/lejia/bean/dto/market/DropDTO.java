package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DropDTO 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey", required = true)
	private Integer pkey;

	/**
	 * 名称
	 */
	@Schema(description = "名称", required = true)
	private String title;
}
