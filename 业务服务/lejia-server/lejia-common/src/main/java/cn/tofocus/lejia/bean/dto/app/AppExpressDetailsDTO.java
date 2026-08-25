package cn.tofocus.lejia.bean.dto.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppExpressDetailsDTO 
{
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

	/**
	 * 下单时间
	 */
	@Schema(description = "下单时间")
	private Date orderTime;
	
	@Schema(description = "重量")
	private BigDecimal weight;
	
	@Schema(description = "收货地址")
	private String addr;

	@Schema(description = "收货人", required = false)
	private String name;

	@Schema(description = "收货人手机", required = false)
	private String mobile;
	
	private String remark;
	
	private List<Map<String,Object>> orderLines = new ArrayList<>(); 
}
