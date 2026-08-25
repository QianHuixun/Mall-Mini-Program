package cn.tofocus.account.db.entity.role;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.security.OwnByDomain;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_app_function", indexes = {@Index(columnList = "domainid")})
@Getter
@Setter
@Schema(description = "应用自定义功能")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime", "updatedBy", "updatedTime"})
public class AppFunctionEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    private String pkey;
    
    //名称
    @Column(length = 40)
    @Name
    @Schema(description = "名称")
    private String name;
    
    //描述
    @Column(length = 100)
    @Schema(description = "描述")
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
    
    @Column(name = "group_name", length = 40)
    @Schema(description = "应用分组")
    private String group;

    @Column(length = 40)
    @Schema(description = "权限组")
    private String funcGroup;

    @Schema(description = "排序")
    private Integer sort;
    
    @Column(length = 40)
    @IndexInRedis
    @Schema(description = "域")
    @OwnByDomain
    private String domainid;
    
    public AppFunctionEntity()
    {
        super();
    }
    
    public AppFunctionEntity(String pkey, String name, String description)
    {
        super();
        this.setPkey(pkey);
        this.setName(name);
        this.setDescription(description);
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getPkey());
        builder.append(":");
        builder.append(getName());
        builder.append("(");
        String description = getDescription();
        if (description != null)
        {
            builder.append(",");
            builder.append(getDescription());
        }
        builder.append(")");
        builder.append("{");
        if (domainid != null)
        {
            builder.append("domainid=");
            builder.append(domainid);
        }
        builder.append("}");
        return builder.toString();
    }
    
}
