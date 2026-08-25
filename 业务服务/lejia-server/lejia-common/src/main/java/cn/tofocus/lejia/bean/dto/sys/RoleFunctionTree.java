package cn.tofocus.lejia.bean.dto.sys;

import cn.tofocus.core.data.TreeModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色的权限配置树")
public class RoleFunctionTree
{
    @Schema(description = "角色主键")
    private String rolePkey;

    @Schema(description = "权限配置树")
    private TreeModel<String, ?> data;
}
