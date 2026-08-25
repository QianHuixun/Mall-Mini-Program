package cn.tofocus.account.db.entity.org;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.util.security.MD5;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "sys_org_menu", indexes = {@Index(columnList = "domainid"), @Index(columnList = "orgid,menuAppid")})
@Getter
@Setter
@Schema(description = "公司菜单配置")
@FieldNameConstants(innerTypeName = "F")
@EqualsAndHashCode(exclude = {"createdTime", "updatedTime"})
public class OrgMenuEntity implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    @Schema(description = "主键")
    private String pkey;
    
    @Column(length = 40)
    @Schema(description = "所属域")
    private String domainid;
    
    @Column(length = 40)
    @Schema(description = "所属机构")
    private String orgid;
    
    @Column(length = 40)
    @Schema(description = "菜单所属应用")
    private String menuAppid;
    
    @Column
    @Schema(description = "菜单所属模块")
    private String menuModel;
    
    @Column(length = 40)
    @Schema(description = "菜单")
    private String menu;
    
    @Column
    @Schema(description = "是否启用")
    private boolean enable;
    
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    private Date createdTime;
    
    @LastModifiedDate
    @Column(name = "updated_time")
    private Date updatedTime;
    
    public static String genenateKey(String orgid, String menu)
    {
        return MD5.getMD5(orgid + menu);
    }
}
