package cn.tofocus.lejia.bean.dto.gtype;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeV4OnList
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "图标")
    private String photo;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "分类等级")
    private Integer level;
    
    @Schema(description = "上级pkey")
    private Integer higherLevelPkey;
    
    @Schema(description = "一级pkey")
    private Integer gtype;
    
    @Schema(description = "关联运营端二级分类")
    private Integer sysTwoGtype;
    
    @Schema(description = "关联运营端二级分类名称")
    private String sysTwoGtypeName;
    
    @Schema(description = "下级分类")
    private List<GtypeV4OnList> gtypeLowerList;
    
}
