package cn.tofocus.lejia.bean.entity.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  sys_company
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name = "sys_company")
public class SysCompany implements HasPkey<String>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "sys_company", strPrefix = "zyysc_company_", strMinimumIntegerDigits = 4)
    @Schema(description = "pkey")
    private String pkey;
    
    /**
    * 名称
    */
    @Schema(description = "名称")
    private String name;
    
    /**
    * 管理员主键
    */
    @Schema(description = "管理员主键")
    private Long managerUser;
    
    /**
    * 管理员
    */
    @Schema(description = "管理员")
    private String manager;
    
    /**
    * 登陆帐号
    */
    @Schema(description = "登陆帐号")
    private String mobile;
    
    /**
    * 地址
    */
    @Schema(description = "地址")
    private String addr;
    
    /**
    * 启用标志
    */
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    /**
    * 是否已删除
    */
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    /**
    * 建档员
    */
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    /**
    * 版本
    */
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}