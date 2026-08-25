package cn.tofocus.domain.user.def;

import java.util.Date;

import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.db.dto.JoinDTO;
import lombok.Data;

@Data
public class RoleInstanceDTO
{
    //主键
    private String pkey;

    private String ownerid;
    
    //角色主键
    private String value;

    //范围类型
    private AccessScopeType scopeType;
    
    //范围(* 为所有)
    private String scope;

    //过期时间
    private Date expireTime;

    @JoinDTO(dataQuery = "appRoleDao", from = "value")
    private AppRoleInfo role;
}
