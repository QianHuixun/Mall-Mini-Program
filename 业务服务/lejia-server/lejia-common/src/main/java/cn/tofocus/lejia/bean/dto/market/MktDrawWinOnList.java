package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.enums.PrizeStatus;
import cn.tofocus.lejia.bean.enums.PrizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktDrawWinOnList {

	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;

	/**
	 * 用户
	 */
	@Schema(description = "用户", hidden = true)
	private Integer member;
	/**
	 * 用户
	 */
	@Schema(description = "用户", hidden = true)
	private String memberName;

	/**
	 * 状态 初始/已发
	 */
	@Schema(description = "状态 初始/已发")
	private PrizeStatus status;

	/**
	 * 礼品类型 积分/优惠券/礼品券/礼品/
	 */
	@Schema(description = "礼品类型 积分/优惠券/实物/谢谢惠顾")
	private PrizeType pType;

	/**
	 * 奖品id
	 */
	@Schema(description = "奖品id")
	private Integer prize;

	/**
	 * 奖品名称
	 */
	@Schema(description = "奖品名称")
	private String name;
	
	@Schema(description = "中奖描述")
	private String descp;

	/**
	 * 收货地址
	 */
	@Schema(description = "收货地址")
	private String addr;

	@Schema(description = "发奖时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
	private Date sendTime;
	
	@Schema(description = "快递公司")
	private String logistics;
	@Schema(description = "快递单号")
	private String express;
	
	/**
	 * 中奖时间
	 */
	@Schema(description = "中奖时间")
	private Date createdTime;
}
