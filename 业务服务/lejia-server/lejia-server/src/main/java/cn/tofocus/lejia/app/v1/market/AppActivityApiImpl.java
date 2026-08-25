package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppActivityDistributeOnPage;
import cn.tofocus.lejia.bean.dto.app.market.AppActivityInfo;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.domain.app.AppActivityManager;

@RequestMapping("/v1/app/market")
@RestController
public class AppActivityApiImpl implements AppActivityApi
{
    @Autowired
    private AppActivityManager activityManager;
    
    @Override
    public Result<AppActivityInfo> get(Integer pkey)
    {
        AppActivityInfo info = activityManager.get(pkey);
        return new Result<>(info);
    }
    
    @Override
    public Result<WxPayData> join(Integer pkey)
    {
        WxPayData result = activityManager.join(pkey);
        return new Result<>(result);
    }
    
    @Override
    public Result<PageResult<AppActivityDistributeOnPage>> queryDistributeActivity(int page, int pagesize)
    {
        PageResult<AppActivityDistributeOnPage> result = activityManager.queryDistributeActivity(page, pagesize);
        return new Result<>(result);
    }

    @Override
    public Result<List<AppActivityInfo>> listWelfare()
    {
        return new Result<>(activityManager.listWelfare());
    }
}
