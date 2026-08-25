package cn.tofocus.account.db.entity.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.security.AccessScopeType;
import cn.tofocus.db.IndexInRedis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Table(name = "sys_user_role")
@Schema(description = "用户的角色")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime"})
public class RoleInstance implements HasPkey<String>
{
    private static final long MillisofDay = 24L * 3600 * 1000;
    
    @Id
    @Column(length = 40)
    private String pkey;
    
    //用户id
    @Column(length = 40)
    @IndexInRedis
    @Schema(description = "用户主键")
    private String ownerid;

    @Column(length = 40)
    @Schema(description = "角色主键")
    private String value;

    @Schema(description = "授权类型")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private AccessScopeType scopeType;
    
    @Column(length = 40)
    @Schema(description = "授权范围")
    private String scope;

    @Schema(description = "过期时间")
    private Date expireTime;

    @Schema(description = "创建时间")
    private Date createdTime;

    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;
    
    public RoleInstance()
    {
        super();
    }
    
    public RoleInstance(String domainid, String value, Date expireTime)
    {
        this.value = value;
        this.domainid = domainid;
        this.expireTime = expireTime;
        this.createdTime = new Date();
        pkey = Util.getUUID();
    }

    public RoleInstance(String domainid, String value, int validDays)
    {
        this.value = value;
        this.domainid = domainid;
        if (validDays < 0)
            this.expireTime = null;
        else
            this.expireTime = new Date(System.currentTimeMillis() + validDays * MillisofDay);
        this.createdTime = new Date();
        pkey = Util.getUUID();
    }

    public RoleInstance(String domainid, String value, long expireTime, Date createTime)
    {
        this.value = value;
        this.domainid = domainid;
        this.expireTime = new Date(expireTime);
        this.createdTime = createTime;
        pkey = Util.getUUID();
    }

    public RoleInstance(String domainid, String value)
    {
        this.value = value;
        this.domainid = domainid;
        this.expireTime = null;
        this.createdTime = new Date();
        pkey = Util.getUUID();
    }

    public String getValue()
    {
        if (!isExpired())
            return value;
        else
            return null;
    }
    
    public Date getExpireTime()
    {
        return expireTime;
    }
    
    public boolean isExpired()
    {
        if (expireTime == null)
            return false;
        else
            return expireTime.getTime() < System.currentTimeMillis();
    }
    
}
