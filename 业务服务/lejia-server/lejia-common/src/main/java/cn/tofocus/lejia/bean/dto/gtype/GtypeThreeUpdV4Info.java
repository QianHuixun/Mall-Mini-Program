package cn.tofocus.lejia.bean.dto.gtype;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeThreeUpdV4Info
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    private String photo;
    
    @Schema(description = "一级分类主键")
    private Integer gtype;
    
    @Schema(description = "二级分类主键")
    private Integer gtypeTwo;
    
    @Schema(description = "关联运营端二级分类")
    @JsonIgnore
    private Integer sysTwoGtype;
}
