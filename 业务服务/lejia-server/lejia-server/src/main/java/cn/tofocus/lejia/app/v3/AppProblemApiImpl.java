package cn.tofocus.lejia.app.v3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.v3.ProblemAppOnList;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.v3.ProblemManager;

@RequestMapping("/v3/app/problem")
@RestController
public class AppProblemApiImpl implements AppProblemApi
{
    @Autowired
    private ProblemManager manager;
    
    @Override
    public Result<List<ProblemAppOnList>> list()
    {
        Integer appid = MobileSession.appid();
        return new Result<>(manager.getAppProblem(appid));
    }
    
}
