package cn.tofocus.lejia.api.v3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v3.ProblemOnInfo;
import cn.tofocus.lejia.bean.dto.v3.ProblemTypeOnInfo;
import cn.tofocus.lejia.domain.v3.ProblemManager;

@RequestMapping("/v3/sys/market/problem")
@RestController
public class ProblemApiImpl implements ProblemApi
{
    @Autowired
    private ProblemManager manager;
    
    @Override
    public Result<PageResult<ProblemOnInfo>> queryProblem(int page, int pagesize, List<Integer> types, String content)
    {
        return new Result<>(manager.queryProblem(page, pagesize, types, content));
    }

    @Override
    public Result<Boolean> insProblem(ProblemOnInfo dto)
    {
        return new Result<>(manager.insProblem(dto));
    }

    @Override
    public Result<Boolean> updProblem(ProblemOnInfo dto)
    {
        return new Result<>(manager.updProblem(dto));
    }

    @Override
    public Result<Boolean> enabled(Integer pkey, Boolean enabled)
    {
        return new Result<>(manager.enabled(pkey, enabled));
    }

    @Override
    public Result<Boolean> delProblem(Integer pkey)
    {
        return new Result<>(manager.delProblem(pkey));
    }

    @Override
    public Result<PageResult<ProblemTypeOnInfo>> queryProblemType(int page, int pagesize)
    {
        return new Result<>(manager.queryProblemType(page, pagesize));
    }
    
    @Override
    public Result<List<ProblemTypeOnInfo>> listProblemType()
    {
        return new Result<>(manager.listProblemType());
    }

    @Override
    public Result<Boolean> insProblemType(ProblemTypeOnInfo dto)
    {
        return new Result<>(manager.insProblemType(dto));
    }

    @Override
    public Result<Boolean> updProblemType(ProblemTypeOnInfo dto)
    {
        return new Result<>(manager.updProblemType(dto));
    }

    @Override
    public Result<Boolean> delProblemType(Integer pkey)
    {
        return new Result<>(manager.delProblemType(pkey));
    }
    
}
