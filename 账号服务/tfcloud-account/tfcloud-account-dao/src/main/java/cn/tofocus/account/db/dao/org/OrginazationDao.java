package cn.tofocus.account.db.dao.org;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.org.OrginazationEntity;
import cn.tofocus.common.Constant;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class OrginazationDao extends JpaNotifyedDao<String, OrginazationEntity>
{
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }

    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.OrgNameAccess;
    }
    
}
