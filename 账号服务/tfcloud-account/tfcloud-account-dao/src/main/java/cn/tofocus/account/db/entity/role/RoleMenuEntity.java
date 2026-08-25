package cn.tofocus.account.db.entity.role;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_role_menu", indexes = {@Index(columnList = "ownerid")})
@Getter
@Setter
@Schema(description = "角色菜单配置")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime", "updatedTime"})
public class RoleMenuEntity implements HasPkey<String>
{
    @Id
    @AutoUUID
    @Column(length = 40)
    @Schema(description = "主键")
    private String pkey;
    
    @Column(length = 40)
    @Schema(description = "角色主键")
    private String ownerid;
    
    @Column(length = 40)
    @Schema(description = "菜单")
    private String menu;
    
    //允许或拒绝
    @Schema(description = "允许还是禁用")
    private boolean accept;
    
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    
    @LastModifiedDate
    @Column(name = "updated_time")
    private Date updatedTime;
    
    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;
}
