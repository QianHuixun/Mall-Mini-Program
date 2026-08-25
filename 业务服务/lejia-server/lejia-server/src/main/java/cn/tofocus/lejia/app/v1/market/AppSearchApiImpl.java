package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.market.AppSearchAppOnList;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import cn.tofocus.lejia.domain.app.AppSearchManager;

@RequestMapping("/v1/app/market/search")
@RestController
public class AppSearchApiImpl implements AppSearchApi
{
    @Autowired
    private AppSearchManager searchManager;
    
    @Override
    public Result<AppSearchAppOnList> getSearch(Integer stype)
    {
        return new Result<>(searchManager.queryAppSearch(stype));
    }
    
    @Override
    public Result<Boolean> delSearch()
    {
        return new Result<>(searchManager.delSearch());
    }
    
    @Override
    public Result<List<String>> listKeywords(SearchKeywordModule module)
    {
        return new Result<>(searchManager.listKeywords(module));
    }
}
