package cn.tofocus.account.db.entity.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.enums.ModelStatus;
import cn.tofocus.core.security.OwnByDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_model", indexes = {@Index(columnList = "domainid")})
@Getter
@Setter
@Schema(description = "系统模块")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime", "updatedTime"})
public class ModelEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    private String pkey;
    
    @Schema(description = "所属域")
    @Column(length = 40)
    @OwnByDomain
    private String domainid;
    
    @Schema(description = "名称")
    @Column(length = 40)
    private String name;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Column
    @Schema(description = "状态：OnLine，OffLine，Disabled")
    private ModelStatus status;
    
    @Column
    @Schema(description = "是否默认开通")
    private boolean defEnable;

    @Column
    @Schema(description = "模块下的菜单是否默认显示")
    private boolean defShowMenu = true;

    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    
    @LastModifiedDate
    @Column(name = "updated_time")
    private Date updatedTime;
}
