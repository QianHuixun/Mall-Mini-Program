package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.lejia.bean.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorPointLineOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "商户 pkey")
    private Integer vendor;
    private String vendorName;
    private String vendorMobile;
    /**
     * 积分
     */
    @Schema(description = "积分")
    private Integer points;
    
    @Schema(description = "余额")
    private Integer balance;
    
    /**
     * 购买+/消费-/活动+/手动+-
     */
    @Schema(description = "积分来源")
    private SourceType source;
    
    @Schema(description = "关联流水")
    private Integer formId;
    
    @Schema(description = "支付会员")
    private Integer member;
	private String memberName;
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;
}
