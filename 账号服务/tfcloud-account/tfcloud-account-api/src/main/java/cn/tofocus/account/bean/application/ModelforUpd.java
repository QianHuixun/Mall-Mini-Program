package cn.tofocus.account.bean.application;

import javax.validation.constraints.NotNull;

import cn.tofocus.core.enums.ModelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ModelforUpd
{
    @NotNull
    private String pkey;
    
    @Schema(description = "名称")
    @NotNull
    private String name;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "状态：OnLine，OffLine，Disabled")
    @NotNull
    private ModelStatus status;
    
    @Schema(description = "是否默认开通")
    @NotNull
    private boolean defEnable;

}
