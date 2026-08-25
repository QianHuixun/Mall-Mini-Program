package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.lejia.bean.enums.ExpressStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktExpressOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey", required = true)
	private Integer pkey;

	/**
	 * 单据号
	 */
	@Schema(description = "单据号", required = true)
	private String code;

	/**
	 * 订单
	 */
	@Schema(description = "订单", required = true)
	private Integer orderId;

	/**
	 * 状态 初始/已派单/已拦货/已到货/拒收
	 */
	@Schema(description = "状态 初始/已派单/已拦货/已到货/拒收", required = true)
	private ExpressStatus status;
	private String statusName;
	/**
	 * 快递员
	 */
	@Schema(description = "快递员", required = false)
	private Integer courier;
	private String courierName;
	/**
	 * 派单时间
	 */
	@Schema(description = "派单时间", required = false)
	private Date pdTime;

	/**
	 * 接单时间
	 */
	@Schema(description = "接单时间", required = false)
	private Date jdTime;

	/**
	 * 到货时间
	 */
	@Schema(description = "到货时间", required = false)
	private Date qrTime;

	/**
	 * 市场
	 */
	@Schema(description = "市场", required = true)
	private String farmer;
	private String farmerName;

	/**
	 * 公司
	 */
	@Schema(description = "公司", required = true)
	private String company;
	private String companyName;

	/**
	 * 建档时间
	 */
	@Schema(description = "建档时间", required = true)
	private Date createdTime;

}
