package cn.tofocus.account.api.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.db.dao.domain.CloudDomainDao;
import cn.tofocus.common.Constant;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.StrKeyName;

@RequestMapping("/v4/domain")
@RestController
public class DomainApiV4Impl implements DomainApiV4
{
    @Autowired
    private CloudDomainDao domainDao;

    @Override
    public Result<List<StrKeyName>> listDomainName(boolean includeNull)
    {
        List<StrKeyName> list = domainDao.listKeyName();
        if (includeNull)
            list.add(0, new StrKeyName(Constant.NULLID, "无"));
        return new Result<>(list);
    }

    
}
