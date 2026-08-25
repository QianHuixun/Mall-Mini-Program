package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.enums.KryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktKryOrderOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
    private Integer pkey;

	@Schema(description = "商户名称")
	private String vendorName="";
	private Long uuid;
	/**
    * 订单号
    */
	@Schema(description = "订单号")
    private String code;

	/**
    * 订单状态 已完成/其他
    */
	@Schema(description = "订单状态 已完成/其他")
    private KryStatus status;

	/**
    * 订单来源
    */
	@Schema(description = "订单来源")
    private String source;

	/**
    * 商户实收金额
    */
	@Schema(description = "商户实收金额")
    private Long receivedAmount;

	/**
    * 用户实付金额
    */
	@Schema(description = "用户实付金额")
    private Long custRealPay;

	/**
    * 订单原始金额
    */
	@Schema(description = "订单原始金额")
    private Long tradeAmount;

	/**
    * 优惠总金额
    */
	@Schema(description = "优惠总金额")
    private Long privilegeAmount;

	/**
    * 顾客id
    */
	@Schema(description = "顾客id")
    private Long customerId;

	/**
    * 顾客昵称
    */
	@Schema(description = "顾客昵称")
    private String custmerName;

	/**
    * 会员id
    */
	@Schema(description = "会员id")
    private Long memberId;

	/**
    * 会员手机
    */
	@Schema(description = "会员手机")
    private String mobile;

	/**
    * 订单时间
    */
	@Schema(description = "订单时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date orderTime;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;
}
