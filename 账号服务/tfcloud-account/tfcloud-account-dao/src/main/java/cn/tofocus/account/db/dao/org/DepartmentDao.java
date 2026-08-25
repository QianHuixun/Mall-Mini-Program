package cn.tofocus.account.db.dao.org;

import java.util.List;
import org.springframework.stereotype.Component;

import cn.tofocus.account.bean.AccountConstant;
import cn.tofocus.account.db.entity.org.DepartmentEntity;
import cn.tofocus.account.db.entity.org.DepartmentEntity.F;
import cn.tofocus.common.Constant;
import cn.tofocus.db.jpa.dao.JpaNotifyedDao;

@Component
public class DepartmentDao extends JpaNotifyedDao<String, DepartmentEntity>
{
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }
    
    @Override
    protected String notifyedCacheName()
    {
        return AccountConstant.DeptNameAccess;
    }
    
    public List<String> listByOrg(String orgid)
    {
        return this.select().strict(true).eq(F.orgid, orgid).execDto(F.deptid, String.class);
    }
}
