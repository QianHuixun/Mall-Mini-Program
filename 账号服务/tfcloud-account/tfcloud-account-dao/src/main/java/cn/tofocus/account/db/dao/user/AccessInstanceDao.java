package cn.tofocus.account.db.dao.user;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.account.db.entity.user.AccessInstance.F;
import cn.tofocus.account.db.entity.user.AccessInstance;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;

@Component
public class AccessInstanceDao extends JpaSpecificationDelegate<String, AccessInstance>
{
    
    public List<AccessInstance> listByDomain(String domain)
    {
        return this.select().strict(true).eq(F.domainid, domain).exec();
    }
    
    public boolean isFuncUsed(String funcid)
    {
        return this.selectOne().strict(true).eq(F.funcKey, funcid).exec() != null;
    }
}
