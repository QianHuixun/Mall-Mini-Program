package cn.tofocus.account.db.entity.role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.util.Util;
import cn.tofocus.db.AutoUUID;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.domain.user.def.AccessInstanceInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 访问权限
 * 
 * @author wyw
 * 
 */
@Entity
@Table(name = "sys_role_acl")
@Data
@Schema(description = "角色的权限")
@FieldNameConstants(innerTypeName = "F")
@NoArgsConstructor
public class RoleAccessInstance implements AccessInstanceInterface<String>
{

    @Id
    @AutoUUID
    @Column(length = 40)
    private String pkey;
    
    //用户id
    @Column(length = 40)
    @IndexInRedis
    @Schema(description = "角色主键")
    private String ownerid;
    
    //权限ID
    @Column(length = 40)
    @Schema(description = "功能主键")
    private String funcKey;
    
    //允许或拒绝
    @Schema(description = "允许还是拒绝")
    private boolean accept;

    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;

    public RoleAccessInstance(String domainid, String pkey, String ownerid, String funcKey, boolean accept)
    {
        super();
        this.pkey = pkey;
        this.domainid = domainid;
        this.ownerid = ownerid;
        this.funcKey = funcKey;
        this.accept = accept;
    }

    public RoleAccessInstance(RoleAccessInstance at)
    {
        this.pkey = at.pkey;
        this.domainid = at.domainid;
        this.ownerid = at.ownerid;
        this.funcKey = at.funcKey;
        this.accept = at.accept;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("RoleAccessInstance [");
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
