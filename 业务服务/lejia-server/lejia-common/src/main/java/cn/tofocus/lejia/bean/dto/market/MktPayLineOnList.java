package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktPayLineOnList 
{

	@Schema(description = "主键", required = true)
    private Integer pkey;

	@Schema(description = "订单号", required = false)
    private Integer orderNumber;

	@Schema(description = "流水号", required = false)
    private String code;
	
	private String mobile;

	@Schema(description = "支付类型 微信/支付宝/电子帐户", required = false)
    private PayType payType;
	private String payTypeName;

	@Schema(description = "支付金额", required = false)
    private BigDecimal amt;

	@Schema(description = "支付成功时间", required = false)
    private Date payTime;

}
