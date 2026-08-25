package cn.tofocus.account.bean.user.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppRoleForUpd
{
    @Schema(description = "角色主键")
    private String pkey;
    
    //名称
    @Schema(description = "角色名称")
    private String name;
    
    //描述
    @Schema(description = "角色描述")
    private String description;
    
    @Schema(description = "角色分组标识")
    private String group;
    
    @Schema(description = "启用")
    private boolean enable;
}
