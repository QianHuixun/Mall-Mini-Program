package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktLogisticsOnList 
{
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;

    /**
    * 名称
    */
	@Schema(description = "名称", required = true)
    private String name;

    /**
    * 描述
    */
	@Schema(description = "描述")
    private String descp;
	
	/**
    * 启用标志
    */
	@Schema(description = "启用标志", hidden = true)
    private Boolean enabled;
	
    /**
    * 建档时间
    */
	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;

}
