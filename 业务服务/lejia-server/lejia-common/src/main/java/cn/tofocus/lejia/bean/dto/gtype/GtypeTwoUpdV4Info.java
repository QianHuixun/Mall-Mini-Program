package cn.tofocus.lejia.bean.dto.gtype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeTwoUpdV4Info
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "排序")
    private Integer sort;
    
    private Integer gtype;
    
    @Schema(description = "关联运营端二级分类")
    private Integer sysTwoGtype;
}
