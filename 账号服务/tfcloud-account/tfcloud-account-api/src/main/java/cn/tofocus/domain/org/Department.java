package cn.tofocus.domain.org;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import cn.tofocus.common.cachemap.bean.HasTreeIndex;
import cn.tofocus.db.Name;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 部门
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年8月15日]
 */
@Getter
@Setter
@Schema(description = "部门")
public class Department implements HasTreeIndex<String>, Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 2L;

    @Schema(description = "所属域")
    private String domainid;
    
    @NotBlank
    @Schema(description = "所属机构")
    private String orgid;
    
    @Schema(description = "部门主键")
    private String deptid;
    
    @Schema(description = "上级部门")
    private String parentid;
    
    @Name
    @Schema(description = "名称")
    private String name;

    private Date createdTime;
    
    private Date updatedTime;
    
    private Long createdBy;
    
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
