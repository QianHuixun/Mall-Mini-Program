package cn.tofocus.account.db.entity.org;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

/**
 * 
 * 机构
 * 机构自身管理
 * 
 * @author  wyw
 * @version  [版本号, 2018年8月13日]
 */
@Entity
@Table(name = "sys_org")
@Getter
@Setter
@Schema(description = "机构")
public class OrginazationEntity implements HasTreeIndex<String>, Serializable
{
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 2L;

    @Column(length = 40)
    @IndexInRedis
    @Schema(description = "所属域")
    private String domainid;
    
    @Id
    @Column(length = 40)
    @AutoUUID
    @Schema(description = "机构主键")
    private String orgid;

    @Schema(description = "上级机构主键")
    @Column(length = 40)
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
        return orgid;
    }

    @Override
    public void setPkey(String pkey)
    {
        orgid = pkey;
    }
    
}
