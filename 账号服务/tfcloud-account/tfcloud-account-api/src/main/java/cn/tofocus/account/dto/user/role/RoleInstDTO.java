package cn.tofocus.account.dto.user.role;

import java.io.Serializable;
import java.util.Date;

import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.db.dto.RoleName;
import lombok.Data;

@Data
public class RoleInstDTO implements Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    private String pkey;
    
    private String value;

    @RoleName(from = "value")
    private String rolename;

    private AccessScopeType scopeType;
    
    private String scope;

    private Boolean includeSub;

    private Date expireTime;
    
    private Date createdTime;
}
