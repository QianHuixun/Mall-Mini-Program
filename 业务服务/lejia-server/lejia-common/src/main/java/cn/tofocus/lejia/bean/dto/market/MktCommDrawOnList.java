package cn.tofocus.lejia.bean.dto.market;


import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  提现表
* @author zdw 2020-09-22
*/

@Data
public class MktCommDrawOnList 
{

	/**
	 * pkey
	 */
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 订单号
    */
	@Schema(description = "订单号", required = true)
    private String orderNumber;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
	@JsonIgnore
    private Integer member;

	@Schema(description = "提现银行卡")
	private String custCard;
	
	@Schema(description = "提现银行卡用户名")
	private String custName;
	
	@Schema(description = "提现银行卡 开户行")
	private String accountBank;
	
	/**
    * 佣金值
    */
	@Schema(description = "佣金值", required = true)
    private BigDecimal comms;

	/**
    * 状态 初始/已发
    */
	@Schema(description = "状态 初始/已发/拒绝", required = true)
    private CommDrawStatus status;
	private String statusName;

	/**
    * 流水号
    */
	@Schema(description = "流水号", required = false)
    private String bankCode;

	/**
    * 备注
    */
	@Schema(description = "备注", required = false)
    private String remark;

	/**
    * 确认时间
    */
	@Schema(description = "确认时间", required = false)
    private Date checkTime;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
    private Date createdTime;

   

}