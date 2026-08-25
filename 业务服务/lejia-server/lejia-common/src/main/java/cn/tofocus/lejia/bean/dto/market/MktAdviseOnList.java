package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAdviseOnList {

	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
    private Integer pkey;

    /**
    * 正文
    */
	@Schema(description = "正文")
    private String content;

    /**
    * 提交人
    */
	@Schema(description = "提交人")
    private Integer member;
	
	@Schema(description = "提交人姓名", hidden = true)
	private String memberName;
	
    /**
    * 提交人手机
    */
	@Schema(description = "提交人手机")
    private String mobile;
    /**
     * 市场
     */
    @Schema(description = "市场")
    @JsonIgnore
    private String farmer;
    private String farmerName;
    /**
    * 建档时间
    */
	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;
}
