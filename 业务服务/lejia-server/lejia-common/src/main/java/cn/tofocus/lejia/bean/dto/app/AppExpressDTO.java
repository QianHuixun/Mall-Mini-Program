package cn.tofocus.lejia.bean.dto.app;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppExpressDTO {
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

	@Schema(description = "期望送达时间")
	private String pstime;

	@Schema(description = "状态名称")
	private String statusName;
	
	@Schema(description = "下单时间")
	private Date orderTime;
	
	@Schema(description = "重量")
	private BigDecimal weight;
	
	@Schema(description = "收货地址")
	private String addr;
	
	@Schema(description = "经度")
    private BigDecimal longitude;

	@Schema(description = "纬度")
    private BigDecimal latitude;

	@Schema(description = "收货人", required = false)
	private String name;

	@Schema(description = "收货人手机", required = false)
	private String mobile;
	
}
