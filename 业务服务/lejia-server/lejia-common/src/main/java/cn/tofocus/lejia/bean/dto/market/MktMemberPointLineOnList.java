package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.lejia.bean.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktMemberPointLineOnList 
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
    * 借贷标志 借(-)/贷(+)
    */
	@Schema(description = "借贷标志 借(-)/贷(+)")
    private Boolean direct;

	@Schema(description = "积分值")
    private Integer points;

    /**
    * 余额
    */
	@Schema(description = "余额")
    private Integer balance;

	@Schema(description = "积分来源 购买+/消费-/手动+-")
    private SourceType source;
	private String sourceName;

	@Schema(description = "其他信息")
	private String remark;
	
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
}
