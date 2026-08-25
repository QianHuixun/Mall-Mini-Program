package cn.tofocus.lejia.app.v1.sys;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.AppSaasInfo;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.sys.AccountDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;

@RequestMapping("/v1/app/saas")
@RestController
public class SaasApiImpl
{
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private AccountDao accountDao;
    
    @RequestMapping(value = "/get")
    @ResponseBody
    public Result<AppSaasInfo> getSaasInfo()
    {
        Integer appid = MobileSession.appid();
        SysAscription sysAscription = ascriptionDao.get(appid);
        AppSaasInfo res = new AppSaasInfo();
        res.setPhoto(sysAscription.getPhoto());
        List<AccountEntity> list = accountDao.select().eq("ascription", appid).exec();
        for(AccountEntity ac : list)
        {
            AccountType type = ac.getAccountType();
            if(AccountType.USER.equals(type))
                res.setUserName(ac.getAccountName());
            if(AccountType.COURIER.equals(type))
                res.setCourierName(ac.getAccountName());
            if(AccountType.VENDOR.equals(type))
                res.setVendorName(ac.getAccountName());
            if(AccountType.WX.equals(type))
                res.setWxName(ac.getAccountName());
        }
        return new Result<>(res);
    }
    
}
