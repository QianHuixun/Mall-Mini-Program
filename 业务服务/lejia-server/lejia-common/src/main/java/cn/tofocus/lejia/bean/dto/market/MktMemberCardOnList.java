package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户优惠券
 */
@Data
public class MktMemberCardOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;
	
    /**
    * 状态 初始/已使用/已过期
    */
	@Schema(description = "状态 初始/已使用/已过期", required = true)
	private CardStatus status;
	
	public String getStatusName()
	{
	    if(Boolean.TRUE.equals(invalid))
	        return "已失效";
	    return status.getName();
	}
	
    private Boolean invalid;
    
    /**
    * 会员
    */
	@Schema(description = "会员", required = true)
	private Integer member;
	private String memberName;
	@Schema(description = "手机")
    private String mobile;
    /**
    * 优惠券
    */
	@Schema(description = "优惠券", required = true)
	private Integer card;
	private String cardName;
    /**
    * 优惠券编码
    */
	@Schema(description = "优惠券编码", required = true)
	private String cardNumber;
	
	@Schema(description = "卡券价值")
	private BigDecimal cost;
	
	
	@Schema(description = "订单编号")
	private Integer orderId;

	@Schema(description = "使用市场")
	private String userFarmer;
	private String userFarmerName;

	@Schema(description = "使用时间")
	private Date userTime;
	
    @Schema(description = "领取时间")
    private Date createdTime;
}
