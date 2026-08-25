package cn.tofocus.lejia.bean.dto.gtype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GtypeUpdV4Info
{
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "图标")
    private String photo;
}
