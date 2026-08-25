package cn.tofocus.domain.org;

import java.io.Serializable;
import java.util.Date;

import cn.tofocus.common.cachemap.bean.HasTreeIndex;
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
@Getter
@Setter
@Schema(description = "机构")
public class Orginazation implements HasTreeIndex<String>, Serializable
{
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 2L;

    @Schema(description = "所属域")
    private String domainid;
    
    @Schema(description = "机构主键")
    private String orgid;

    @Schema(description = "上级机构主键")
    private String parentid;

    @Schema(description = "名称")
    private String name;

    private Date createdTime;
    
    private Date updatedTime;
    
    private Long createdBy;
    
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
