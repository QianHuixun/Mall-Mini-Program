package cn.tofocus.account.db.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.account.db.entity.role.RoleAccessInstance;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.domain.user.def.AccessInstanceInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 访问权限
 * 
 * @author wyw
 * 
 */
@Entity
@Table(name = "sys_user_acl")
@Data
@Schema(description = "用户的直接配置的权限")
@FieldNameConstants(innerTypeName = "F")
public class AccessInstance implements AccessInstanceInterface<String>
{
    @Id
    @Column(length = 40)
    private String pkey;
    
    //用户id
    @Column(length = 40)
    @IndexInRedis
    @Schema(description = "用户主键")
    private String ownerid;
    
    //权限ID
    @Column(length = 40)
    @Schema(description = "功能主键")
    private String funcKey;

    @Schema(description = "授权类型")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private AccessScopeType scopeType;
    
    @Column(length = 40)
    @Schema(description = "授权范围")
    private String scope;

    //允许或拒绝
    @Schema(description = "允许或拒绝")
    private boolean accept;

    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;

    public AccessInstance()
    {
        pkey = Util.getUUID();
    }

    public AccessInstance(String domainid, String ownerid, String funcKey, boolean accept)
    {
        pkey = Util.getUUID();
        this.domainid = domainid;
        this.ownerid = ownerid;
        this.funcKey = funcKey;
        this.accept = accept;
    }

    public AccessInstance(String domainid, AccessInstance at)
    {
        pkey = at.pkey;
        this.domainid = domainid;
        this.ownerid = at.ownerid;
        this.funcKey = at.funcKey;
        this.accept = at.accept;
        this.scopeType = at.scopeType;
        this.scope = at.scope;
    }

    public AccessInstance(String domainid, RoleAccessInstance at, RoleInstance ur, String userid)
    {
        pkey = Util.getUUID();
        this.domainid = domainid;
        this.ownerid = userid;
        this.funcKey = at.getFuncKey();
        this.accept = at.isAccept();
        this.scopeType = ur.getScopeType();
        this.scope = ur.getScope();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AccessInstance [");
        builder.append(pkey);
        builder.append(":");
        builder.append(ownerid);
        builder.append(".");
        builder.append(funcKey);
        builder.append("=");
        builder.append(accept);
        builder.append("]");
        return builder.toString();
    }
}
