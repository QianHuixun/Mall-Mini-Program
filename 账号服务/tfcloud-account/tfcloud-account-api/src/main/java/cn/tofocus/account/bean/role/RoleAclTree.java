package cn.tofocus.account.bean.role;

import java.util.List;

import cn.tofocus.core.data.TreeModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoleAclTree
{
    @Schema(description = "角色")
    private String pkey;
    
    @Schema(description = "要配置的权限分组")
    private String funcGroup;
    
    @Schema(description = "权限列表")
    private List<TreeModel<String, String>> data;
}
