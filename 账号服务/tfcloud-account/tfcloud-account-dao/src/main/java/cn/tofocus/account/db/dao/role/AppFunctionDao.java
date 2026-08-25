package cn.tofocus.account.db.dao.role;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.role.AppFunctionEntity.F;
import cn.tofocus.account.db.entity.role.AppFunctionEntity;
import cn.tofocus.common.Constant;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class AppFunctionDao  extends JpaNotifyedDao<String, AppFunctionEntity>
{
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }

    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.FuncNameAccess;
    }

    public List<AppFunctionEntity> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
}
