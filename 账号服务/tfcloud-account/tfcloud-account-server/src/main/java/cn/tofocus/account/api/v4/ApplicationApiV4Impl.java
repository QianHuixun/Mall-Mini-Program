package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;

@RequestMapping("/v4/application")
@RestController
public class ApplicationApiV4Impl implements ApplicationApiV4
{
    @Autowired
    private ApplicationDao applicationDao;

    @Override
    public Result<List<StrKeyName>> listFrontEndAppName(String domain)
    {
        List<StrKeyName> list = applicationDao.listFrontEndAppName(domain);
        return new Result<>(list);
    }
    
}
