package cn.tofocus.lejia.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.lejia.bean.dto.sys.SysAccountShieldVersion;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.dao.sys.AccountDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccountManager
{
    @Autowired
    private AccountDao accountDao;
    
    public SysAccountShieldVersion getShieldVersion(Integer ascription)
    {
        List<AccountEntity> accounts =
            accountDao.list(ascription, Lists.newArrayList(AccountType.USER, AccountType.COURIER, AccountType.VENDOR));
        
        SysAccountShieldVersion shieldVersion = new SysAccountShieldVersion();
        for (AccountEntity account : accounts)
        {
            if (account.getAccountType() == null) continue;
            switch (account.getAccountType())
            {
                case USER:
                    shieldVersion.setUserVersion(account.getShieldVersion());
                    break;
                case COURIER:
                    shieldVersion.setCourierVersion(account.getShieldVersion());
                    break;
                case VENDOR:
                    shieldVersion.setVendorVersion(account.getShieldVersion());
                    break;
                default:
            }
        }
        return shieldVersion;
    }
    
    public boolean saveShieldVersion(Integer ascription, SysAccountShieldVersion shieldVersion)
    {
        accountDao.updateShieldVersion(ascription, AccountType.USER, shieldVersion.getUserVersion());
        accountDao.updateShieldVersion(ascription, AccountType.COURIER, shieldVersion.getCourierVersion());
        accountDao.updateShieldVersion(ascription, AccountType.VENDOR, shieldVersion.getVendorVersion());
        return true;
    }
}
