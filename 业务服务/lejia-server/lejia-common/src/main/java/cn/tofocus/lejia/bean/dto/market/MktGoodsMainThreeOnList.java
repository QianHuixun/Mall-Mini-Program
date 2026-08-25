package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGoodsMainThreeOnList
{
	@Schema(description = "pkey", hidden = true)
    private Integer pkey;
 	
 	@Schema(description = "二级分类")
    private Integer twoGtype;
 	
 	@Schema(description = "二级分类名称", hidden = true)
 	private String twoGtypeName;
 	
 	private Integer gtype;
 	
 	@Schema(description = "一级分类名称", hidden = true)
 	private String gtypeName;
 	
 	@Schema(description = "名称", required = true)
    private String name;
 	
 	@Schema(description = "排序", required = true)
    private Integer sort;
 	
 	@Schema(description = "备注")
    private String remark;

 	@Schema(description = "启用标志", required = true)
    private Boolean enabled;

 	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;

}
