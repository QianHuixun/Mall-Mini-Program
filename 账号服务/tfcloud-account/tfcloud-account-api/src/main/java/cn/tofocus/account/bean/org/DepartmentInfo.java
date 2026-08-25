package cn.tofocus.account.bean.org;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import cn.tofocus.common.cachemap.bean.HasTreeIndex;
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
public class DepartmentInfo implements HasTreeIndex<String>, Serializable
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 2L;

    @NotBlank
    private String orgid;
    
    private String deptid;
    
    private String parentid;
    
    private String name;
    
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
