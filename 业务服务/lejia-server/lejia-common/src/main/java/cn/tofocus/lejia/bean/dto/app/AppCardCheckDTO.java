package cn.tofocus.lejia.bean.dto.app;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppCardCheckDTO 
{
	
	@JsonIgnore
	private Integer card;
	/**
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;
	/**
	 * 使用时间
	 */
	@Schema(description = "使用时间")
	private Date userTime;
	
}
