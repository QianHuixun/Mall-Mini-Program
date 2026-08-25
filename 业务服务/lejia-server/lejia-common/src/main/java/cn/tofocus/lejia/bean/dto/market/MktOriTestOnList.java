package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOriTestOnList 
{
	
	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;

	/**
	 * 检测商户
	 */
	@Schema(description = "检测商户", required = true)
	private String merchant;
	
	/**
	 * 检测商品
	 */
	@Schema(description = "检测商品", required = true)
	private String goods;

	/**
	 * 检测项目
	 */
	@Schema(description = "检测项目", required = true)
	private String entry;

	/**
	 * 检测结果
	 */
	@Schema(description = "检测结果", required = true)
	private Boolean testResult;

	/**
	 * 检测日期
	 */
	@Schema(description = "检测日期", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date testDate;

	/**
	 * 市场
	 */
	@Schema(description = "市场")
	private String farmer;
	
	/**
	 * 录入员
	 */
	@Schema(description ="录入员", hidden = true)
	private Integer createdBy;
	private String createdByName = "";
}
