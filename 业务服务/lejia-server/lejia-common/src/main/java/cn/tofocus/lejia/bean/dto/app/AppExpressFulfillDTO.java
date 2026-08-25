package cn.tofocus.lejia.bean.dto.app;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppExpressFulfillDTO {
	/**
	 * pkey
	 */
	@Schema(description = "pkey", required = true)
	private Integer pkey;

	/**
	 * 订单
	 */
	@Schema(description = "订单", required = true)
	private Integer orderId;

	@Schema(description = "单据号")
	private String code;

	@Schema(description = "小票码")
	private Integer smallTicket;

	@Schema(description = "收货地址")
	private String addr;
	
	/**
	 * 下单时间
	 */
	@Schema(description = "下单时间")
	private Date orderTime;
	
	@Schema(description = "重量")
	private BigDecimal weight;
	
	@Schema(description = "到货时间", required = false)
	private Date qrTime;
	
}
