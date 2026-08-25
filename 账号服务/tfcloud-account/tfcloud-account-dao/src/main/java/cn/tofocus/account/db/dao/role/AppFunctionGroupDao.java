package cn.tofocus.account.db.dao.role;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.role.FuncGroupInfo;
import cn.tofocus.account.db.entity.role.AppFunctionGroupEntity.F;
import cn.tofocus.common.Constant;
import cn.tofocus.account.db.entity.role.AppFunctionGroupEntity;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class AppFunctionGroupDao extends JpaSpecificationDelegate<String, AppFunctionGroupEntity>
{
    
    public PageResult<FuncGroupInfo> queryFuncGroup(Integer page, Integer pagesize, String domain)
    {
        if (Constant.NULLID.equals(domain))
            return this.selectPage()
                .page(page)
                .pagesize(pagesize)
                .isNull(F.domainid)
                .sort(F.group)
                .sort(F.sort)
                .execDto(FuncGroupInfo.class);
        else
            return this.selectPage()
                .page(page)
                .pagesize(pagesize)
                .strict(true)
                .eq(F.domainid, domain)
                .sort(F.group)
                .sort(F.sort)
                .execDto(FuncGroupInfo.class);
    }
    
    public List<StrKeyName> listFunctionGroup(String domain, String group)
    {
        if (Constant.NULLID.equals(domain))
            return this.select()
                .isNull(F.domainid)
                .eq(F.group, group)
                .sort(F.group)
                .sort(F.sort)
                .execDto(StrKeyName.class);
        else
            return this.select()
                .strict(true)
                .eq(F.domainid, domain)
                .eq(F.group, group)
                .sort(F.group)
                .sort(F.sort)
                .execDto(StrKeyName.class);
    }
}
