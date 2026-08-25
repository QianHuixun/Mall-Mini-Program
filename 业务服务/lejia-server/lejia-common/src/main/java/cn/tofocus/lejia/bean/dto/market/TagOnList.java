package cn.tofocus.lejia.bean.dto.market;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TagOnList
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "true:已打上标签")
    private Boolean enabled = false;
}
