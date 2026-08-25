package cn.tofocus.lejia.bean.dto.app.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class MktAppMemberCardOnList {

	/**
	 * pkey
	 */
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 会员
	 */
	@Schema(description = "会员")
	private Integer member;

	/**
	 * 优惠券
	 */
	@Schema(description = "优惠券")
	private Integer card;

	/**
	 * 优惠券编码
	 */
	@Schema(description = "优惠券编码")
	private String cardNumber;

	/**
	 * 开始日期
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date startDate;
	
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
	
	/**
	 * 到期日期
	 */
	@Schema(description = "到期日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date endDate;

	private MktAppCardDetailsDTO detail;
}
