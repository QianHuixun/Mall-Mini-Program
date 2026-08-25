package cn.tofocus.account.db.dao.application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.application.ApplicationEntity;
import cn.tofocus.account.db.entity.application.ApplicationEntity.F;
import cn.tofocus.common.Constant;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.core.enums.AppTypeEnum;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class ApplicationDao extends JpaNotifyedDao<String, ApplicationEntity>
{
    private static final Set<String> needNotifyProperty = new HashSet<>(Arrays.asList(F.pkey, F.name, F.domainid));
    
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }
    
    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.AppNameAccess;
    }
    
    @Override
    protected Set<String> needNotifyProperty()
    {
        return needNotifyProperty;
    }
    
    public List<StrKeyName> listFrontEndAppName(String domain)
    {
        if (domain.equals(Constant.NULLID))
            return this.select().isNull(F.domainid).eq(F.apptype, AppTypeEnum.Vue).execDto(StrKeyName.class);
        else
            return this.select().eq(F.domainid, domain).eq(F.apptype, AppTypeEnum.Vue).execDto(StrKeyName.class);
    }
}
