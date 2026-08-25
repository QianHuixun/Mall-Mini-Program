package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktCourierOnList 
{
	
	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
    private Integer pkey;

    /**
    * 姓名
    */
	@Schema(description = "姓名", required = true)
    private String name;

    /**
    * 手机
    */
	@Schema(description = "手机", required = true)
    private String mobile;

    /**
    * 备注
    */
	@Schema(description = "备注")
    private String remark;

    /**
    * 启用标志
    */
	@Schema(description = "启用标志", required = true)
    private Boolean enabled;

    /**
    * 市场
    */
	@Schema(description = "市场", hidden = true)
    private String farmer;

    /**
    * 公司
    */
	@Schema(description = "公司", hidden = true)
    private String company;

    /**
    * 建档时间
    */
	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;
}
