package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktCookfdTypeOnList 
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
    private String name;

	/**
    * 排序
    */
	@Schema(description = "排序", required = true)
    private Integer sort;

	/**
    * 启用标志
    */
	@Schema(description = "启用标志", required = true)
    private Boolean enabled;

}
