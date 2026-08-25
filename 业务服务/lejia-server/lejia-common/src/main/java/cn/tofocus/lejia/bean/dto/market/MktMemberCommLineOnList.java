package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.lejia.bean.enums.CommSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberCommLineOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 用户
    */
	@Schema(description = "用户")
    private Integer member;
	private String memberName;
	private String memberMobile;

    /**
    * 借贷标志 false：借(-)/true：贷(+)
    */
	@Schema(description = "借贷标志 false：借(-)/true：贷(+)")
    private Boolean direct;

    /**
    * 佣金值
    */
    @Schema(description = "佣金值")
    private BigDecimal comms;
    
    /**
    * 余额
    */
	@Schema(description = "余额")
    private BigDecimal balance;

    /**
    * 积分来源 购买+/消费-/手动+-
    */
	@Schema(description = "积分来源 购买+/消费-/手动+-")
    private CommSourceType source;
	private String sourceName;
	
    /**
    * 来源单据
    */
	@Schema(description = "来源单据")
    private String formId;
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;

    @Schema(description = "交易金额")
    private String amtStr;

    public String getAmtStr()
    {
        if (direct == null || comms == null) return null;
        return direct ? "+" + comms : "-" + comms;
    }
}
