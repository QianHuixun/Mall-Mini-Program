package cn.tofocus.account.bean.role;

import java.util.List;

import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.enums.MenuType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoleMenuTree
{
    @Schema(description = "对象主键")
    private String pkey;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "树节点")
    private List<AppMenuTree> data;
    
    @Data
    public static class AppMenuTree
    {
        @Schema(description = "对象主键")
        private String pkey;

        @Schema(description = "名称")
        private String name;
        
        @Schema(description = "下级节点")
        private List<TreeModel<String, MenuType>> sub;
    }
}
