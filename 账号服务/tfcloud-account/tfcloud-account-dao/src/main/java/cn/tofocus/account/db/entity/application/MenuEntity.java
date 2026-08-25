package cn.tofocus.account.db.entity.application;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.security.OwnByDomain;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_app_menu")
@Getter
@Setter
@Schema(description = "菜单")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime", "updatedTime"})
public class MenuEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    private String pkey;
    
    //名称
    @Name
    @Column(length = 40)
    @Schema(description = "名称")
    private String name;
    
    //描述
    @Column(length = 100)
    @Schema(description = "描述")
    private String description;
    
    @Column(length = 40)
    @Schema(description = "所属域")
    @OwnByDomain
    private String domainid;
    
    @Column
    @Schema(description = "所属模块")
    private String modelId;
    
    @IndexInRedis
    @Column(length = 40)
    @Schema(description = "所属应用")
    private String appid;
    
    @Column(length = 40)
    @Schema(description = "上级菜单")
    private String parentid;
    
    @Schema(description = "类型")
    private MenuType type;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "启用")
    private boolean enable = true;
    
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    
    @LastModifiedDate
    @Column(name = "updated_time")
    private Date updatedTime;
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getPkey());
        builder.append(":");
        builder.append(getName());
        builder.append("(");
        String description = getDescription();
        builder.append(type);
        if (description != null)
        {
            builder.append(",");
            builder.append(getDescription());
        }
        builder.append(")");
        builder.append("{");
        if (appid != null)
        {
            builder.append("appid=");
            builder.append(appid);
            builder.append(", ");
        }
        if (parentid != null)
        {
            builder.append("parentid=");
            builder.append(parentid);
            builder.append(", ");
        }
        builder.append("sort=");
        builder.append(sort);
        builder.append("}");
        return builder.toString();
    }
    
}
