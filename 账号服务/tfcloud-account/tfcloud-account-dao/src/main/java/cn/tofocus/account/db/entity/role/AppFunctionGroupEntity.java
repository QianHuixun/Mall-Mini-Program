package cn.tofocus.account.db.entity.role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.security.OwnByDomain;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_app_function_group", indexes = {@Index(columnList = "domainid")})
@Getter
@Setter
@Schema(description = "功能组")
@FieldNameConstants(innerTypeName = "F")
public class AppFunctionGroupEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    private String pkey;
    
    //名称
    @Column(length = 40)
    @Name
    @Schema(description = "名称")
    private String name;
    
    @Column(name = "group_name", length = 40)
    @Schema(description = "应用分组")
    private String group;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Column(length = 40)
    @Schema(description = "域")
    @OwnByDomain
    private String domainid;
    
    public AppFunctionGroupEntity()
    {
        super();
    }
    
}
