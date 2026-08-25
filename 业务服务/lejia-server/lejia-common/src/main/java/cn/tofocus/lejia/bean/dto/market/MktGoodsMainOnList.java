package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGoodsMainOnList
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
    private Integer pkey;

    /**
    * 分类
    */
 	@Schema(description = "分类", required = true)
    private Integer gtype;
 	
    /**
    * 分类
    */
 	@Schema(description = "分类名称", hidden = true)
    private String gtypeName;
 	
    /**
    * 名称
    */
 	@Schema(description = "名称", required = true)
    private String name;
 	
 	/**
    * 排序
    */
 	@Schema(description = "排序", required = true)
    private Integer sort;
 	
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
    * 建档时间
    */
 	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;

}
