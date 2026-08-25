package cn.tofocus.lejia.dao.sys;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity.F;
import cn.tofocus.lejia.bean.enums.AccountType;

@Component
public class AccountDao extends JpaSpecificationDelegate<Integer, AccountEntity>
{
    public AccountEntity get(Integer ascription, AccountType accountType)
    {
        return this.selectOne().eq(F.ascription, ascription).eq(F.accountType, accountType).exec();
    }
    
    public List<AccountEntity> list(Integer ascription, List<AccountType> accountTypes)
    {
        return this.select().eq(F.ascription, ascription).in(F.accountType, accountTypes).exec();
    }
    
    public void updateShieldVersion(Integer ascription, AccountType accountType, String shieldVersion)
    {
        this.select()
            .strict(true)
            .eq(F.ascription, ascription)
            .eq(F.accountType, accountType)
            .update(F.shieldVersion, shieldVersion);
    }
}
