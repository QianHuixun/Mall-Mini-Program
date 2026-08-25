package cn.tofocus.lejia.api.v3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v3.PromoteOnPage;
import cn.tofocus.lejia.bean.dto.v3.PromoteUpdDto;
import cn.tofocus.lejia.domain.v3.PromoteV3Manager;

@RequestMapping("/v3/sys/promote")
@RestController
public class PromoteApiImpl implements PromoteApi
{
    @Autowired
    private PromoteV3Manager manager;
    
    @Override
    public Result<Integer> ins(PromoteUpdDto dto)
    {
        return new Result<>(manager.ins(dto));
    }

    @Override
    public Result<Boolean> upd(PromoteUpdDto dto)
    {
        return new Result<>(manager.upd(dto));
    }

    @Override
    public Result<Boolean> del(Integer pkey)
    {
        return new Result<>(manager.del(pkey));
    }

    @Override
    public Result<Boolean> enabledStart(Integer pkey)
    {
        return new Result<>(manager.enabled(pkey, true));
    }

    @Override
    public Result<Boolean> enabledStop(Integer pkey)
    {
        return new Result<>(manager.enabled(pkey, false));
    }
    
    @Override
    public Result<PageResult<PromoteOnPage>> query(int page, int pagesize, String title, String content)
    {
        return new Result<>(manager.query(page, pagesize, title, content));
    }


    
}
