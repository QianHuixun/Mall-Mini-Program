package cn.tofocus.account.db.dao.role;

import java.util.List;

import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.bean.user.app.AppRoleInfo;
import cn.tofocus.account.bean.user.app.AppRoleInfoOnPage;
import cn.tofocus.account.db.entity.role.AppRoleEntity.F;
import cn.tofocus.account.db.entity.role.AppRoleEntity;
import cn.tofocus.common.Constant;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class AppRoleDao extends JpaNotifyedDao<String, AppRoleEntity>
{
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }
    
    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.RoleNameAccess;
    }
    
    public List<AppRoleEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public boolean isPkeyUsed(String excludeDomain, List<String> keyList)
    {
        return this.selectOne().strict(true).notEq(F.domainid, excludeDomain).in(F.pkey, keyList).exec() != null;
    }
    
    public PageResult<AppRoleInfoOnPage> queryRole(Integer page, Integer pagesize, String domain, boolean onlySysRole)
    {
        // @formatter:off
        if (Constant.NULLID.equals(domain))
            return this.selectPage().page(page).pagesize(pagesize).isNull(F.domainid).execDto(AppRoleInfoOnPage.class);
        else
            return this.selectPage()
                .page(page)
                .pagesize(pagesize)
                .strict(true)
                .eq(F.domainid, domain)
                .iF(onlySysRole)
                  .isNull(F.orgid)
                  .isNull(F.deptid)
                .endIf()
                .execDto(AppRoleInfoOnPage.class);
        // @formatter:on
    }
    
    public List<AppRoleInfo> listRoleInDept(String domain, String group, String deptid)
    {
        // @formatter:off
        return this.select()
            .and()
              .eq(F.domainid, domain)  //域条件
              .iF(group == null)       //分组条件
                .isNull(F.group)
              .eLse()
                .eq(F.group, group)
              .endIf()
              .or()
                .and()                 //加入系统角色
                  .isNull(F.orgid)
                  .isNull(F.deptid)
                .close()
                .eq(F.deptid, deptid) //加入部门角色
              .close()
            .close()
            .done()
            .sort(F.deptid, false, NullHandling.NULLS_FIRST)
            .sort(F.createdTime, false)
            .execDto(AppRoleInfo.class);
        // @formatter:on
    }
}
