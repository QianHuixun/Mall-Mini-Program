package cn.tofocus.account.api.v2.application;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.account.bean.application.ApplicationInfo;
import cn.tofocus.account.bean.application.CloudDomainInfo;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.aop.CheckAppPermission;
import cn.tofocus.core.user.SysFunctionEnum;
import cn.tofocus.account.db.dao.application.ApplicationDao;
import cn.tofocus.account.db.dao.domain.CloudDomainDao;
import cn.tofocus.account.db.entity.application.ApplicationEntity;
import cn.tofocus.account.db.entity.domain.CloudDomainEntity;

@RequestMapping("/v2")
@RestController
public class ApplicationApiImpl implements ApplicationApi
{
    @Autowired
    private CloudDomainDao domainCache;
    
    @Autowired
    private ApplicationDao appCache;
    
    @Override
    @CheckAppPermission(SysFunctionEnum.ManagerApplication)
    public Result<PageResult<CloudDomainInfo>> queryDomain(Integer page, Integer pagesize, String name)
    {
        PageResult<CloudDomainInfo> r =
            domainCache.selectPage().page(page).pagesize(pagesize).like("name", name).execDto(CloudDomainInfo.class);
        return new Result<>(r);
    }
    
    @Override
    @CheckAppPermission(SysFunctionEnum.ManagerApplication)
    public Result<Object> addDomain(@Valid CloudDomainInfo info)
    {
        if (domainCache.isExistKey(info.getPkey()))
        {
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        }
        else
        {
            CloudDomainEntity a = BeanUtil.beanFrom(CloudDomainEntity.class, info);
            domainCache.add(a);
            return new Result<>();
        }
    }
    
    @Override
    @CheckAppPermission(SysFunctionEnum.ManagerApplication)
    public Result<Object> delDomain(String pkey)
    {
        domainCache.removeById(pkey);
        return new Result<>();
    }
    
    @Override
    @CheckAppPermission(SysFunctionEnum.ManagerApplication)
    public Result<Object> addApp(@RequestBody @Valid ApplicationInfo app)
    {
        if (appCache.isExistKey(app.getPkey()))
        {
            throw TofocusException.of(SysErrCode.Cache.KEY_IS_EXIST);
        }
        else
        {
            ApplicationEntity a = BeanUtil.beanFrom(ApplicationEntity.class, app);
            appCache.add(a);
            return new Result<>();
        }
    }
    
    @Override
    @CheckAppPermission(SysFunctionEnum.ManagerApplication)
    public Result<PageResult<ApplicationInfo>> queryApp(Integer page, Integer pagesize, String name, String domain)
    {
        PageResult<ApplicationInfo> r = appCache.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("domainid", domain)
            .like("name", name)
            .execDto(ApplicationInfo.class);
        return new Result<>(r);
    }
    
    @Override
    @CheckAppPermission(SysFunctionEnum.ManagerApplication)
    public Result<Object> delApp(String pkey)
    {
        appCache.removeById(pkey);
        return new Result<>();
    }
    
}
