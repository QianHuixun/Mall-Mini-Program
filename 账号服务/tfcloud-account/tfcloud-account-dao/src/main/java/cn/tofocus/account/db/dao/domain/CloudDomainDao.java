package cn.tofocus.account.db.dao.domain;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.domain.CloudDomainEntity;
import cn.tofocus.common.Constant;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class CloudDomainDao extends JpaNotifyedDao<String, CloudDomainEntity>
{

    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }

    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.DomainNameAccess;
    }

    public List<StrKeyName> listKeyName()
    {
        return this.select().execDto(StrKeyName.class);
    }
    
}
