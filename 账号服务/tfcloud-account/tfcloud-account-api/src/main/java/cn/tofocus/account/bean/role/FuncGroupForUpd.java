package cn.tofocus.account.bean.role;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FuncGroupForUpd
{
    @NotNull
    private String pkey;
    
    //名称
    @Schema(description = "名称")
    @NotNull
    private String name;
    
    @Schema(description = "应用分组")
    private String group;
    
    @Schema(description = "排序")
    private Integer sort;
}
