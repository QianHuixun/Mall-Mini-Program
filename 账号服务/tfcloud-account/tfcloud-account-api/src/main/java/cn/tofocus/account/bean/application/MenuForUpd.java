package cn.tofocus.account.bean.application;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MenuForUpd
{
    @NotNull
    private String pkey;
    
    @Schema(description = "名称")
    @NotNull
    private String name;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "所属模块")
    private String modelId;
    
    @Schema(description = "上级菜单")
    private String parentid;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "启用")
    private boolean enable = true;
}
