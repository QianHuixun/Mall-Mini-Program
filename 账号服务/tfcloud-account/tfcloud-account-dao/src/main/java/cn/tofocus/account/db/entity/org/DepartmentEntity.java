package cn.tofocus.account.db.entity.org;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasTreeIndex;
import cn.tofocus.db.AutoUUID;
import cn.tofocus.db.IndexInRedis;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * 
 * 部门
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年8月15日]
 */
@Entity
@Table(name = "sys_org_dept")
@Getter
@Setter
@Schema(description = "部门")
@FieldNameConstants(innerTypeName = "F")
public class DepartmentEntity implements HasTreeIndex<String>, Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 2L;

    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;
    
    @Column(length = 40)
    @IndexInRedis
    @NotBlank
    @Schema(description = "所属机构")
    private String orgid;
    
    @Id
    @Column(length = 40)
    @AutoUUID
    @Schema(description = "部门主键")
    private String deptid;
    
    @Column(length = 40)
    @Schema(description = "上级部门")
    private String parentid;
    
    @Name
    @Column(length = 100)
    @Schema(description = "名称")
    private String name;

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
    
    @Override
    public String getPkey()
    {
        return deptid;
    }

    @Override
    public void setPkey(String pkey)
    {
        deptid = pkey;
    }

}
