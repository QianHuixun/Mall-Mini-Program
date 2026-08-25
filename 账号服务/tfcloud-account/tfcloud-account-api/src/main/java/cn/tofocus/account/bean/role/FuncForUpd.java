package cn.tofocus.account.bean.role;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FuncForUpd
{
    @NotNull
    private String pkey;
    
    //名称
    @Schema(description = "名称")
    @NotNull
    private String name;
    
    //描述
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "应用分组")
    private String group;

    @Schema(description = "权限组")
    private String funcGroup;

    @Schema(description = "排序")
    private Integer sort;
    
}
