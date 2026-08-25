package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOriVenOnList 
{
	
	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;

	/**
	 * 溯源商户
	 */
	@Schema(description = "溯源商户", required = true)
	private String merchant;
	
	/**
	 * 溯源商品
	 */
	@Schema(description = "溯源商品", required = true)
	private String goods;

	/**
	 * 供应商
	 */
	@Schema(description = "供应商", required = true)
	private String vendor;

	/**
	 * 进货日期
	 */
	@Schema(description = "进货日期", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date oriDate;

	/**
	 * 建档时间
	 */
	@Schema(description = "建档时间", hidden = true)
	private Date createdTime;
}
