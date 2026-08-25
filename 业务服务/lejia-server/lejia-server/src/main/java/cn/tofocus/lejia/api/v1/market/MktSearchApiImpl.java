package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktSearchKeywordInfo;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import cn.tofocus.lejia.domain.market.MktSearchManager;
import cn.tofocus.lejia.exception.LejiaErrCode;

@RequestMapping("/v1/market/search")
@RestController
public class MktSearchApiImpl implements MktSearchApi
{
    @Autowired
    private MktSearchManager mktSearchManager;
    
    @Override
    public Result<PageResult<MktSearchKeywordInfo>> query(int page, int pagesize, SearchKeywordModule module,
        String keyword)
    {
        PageResult<MktSearchKeywordInfo> res = mktSearchManager.query(page, pagesize, module, keyword);
        return new Result<>(res);
    }
    
    @Override
    public Result<MktSearchKeywordInfo> get(Integer pkey)
    {
        MktSearchKeywordInfo res = mktSearchManager.get(pkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> add(MktSearchKeywordInfo info)
    {
        boolean sign = mktSearchManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> upd(MktSearchKeywordInfo info)
    {
        if (info.getPkey() == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "主键不能为空");
        boolean sign = mktSearchManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> del(Integer pkey)
    {
        boolean sign = mktSearchManager.del(pkey);
        return new Result<>(sign);
    }
}
