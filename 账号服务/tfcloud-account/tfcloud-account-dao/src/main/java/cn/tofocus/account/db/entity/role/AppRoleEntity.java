package cn.tofocus.account.db.entity.role;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.security.OwnByDept;
import cn.tofocus.core.security.OwnByDomain;
import cn.tofocus.core.security.OwnByOrg;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_app_role")
@Getter
@Setter
@Schema(description = "角色")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime", "updatedBy", "updatedTime"})
public class AppRoleEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    @Schema(description = "角色主键")
    private String pkey;
    
    //名称
    @Column(length = 40)
    @Name
    @Schema(description = "角色名称")
    private String name;
    
    //描述
    @Column(length = 100)
    @Schema(description = "角色描述")
    private String description;
    
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    
    @LastModifiedDate
    @Column(name = "updated_time")
    private Date updatedTime;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(length = 40)
    @IndexInRedis
    @OwnByDomain
    @Schema(description = "角色所属域")
    private String domainid;
    
    @Column(length = 40)
    @IndexInRedis
    @OwnByOrg
    @Schema(description = "角色所属机构")
    private String orgid;
    
    @Column(length = 40)
    @IndexInRedis
    @OwnByDept
    @Schema(description = "角色所属部门")
    private String deptid;
    
    @Column(name = "group_name", length = 40)
    @Schema(description = "角色分组标识")
    private String group;
    
    @Schema(description = "启用")
    private boolean enable = true;
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", getPkey()=");
        builder.append(getPkey());
        builder.append("]");
        return builder.toString();
    }
}
