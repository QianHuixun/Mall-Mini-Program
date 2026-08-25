package cn.tofocus.lejia.api.v1.market;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktAdvertOnList;
import cn.tofocus.lejia.bean.dto.market.MktCombinationAdviseInfo;
import cn.tofocus.lejia.bean.dto.market.MktCombinationAdviseOnList;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigInfo;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigOnList;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.market.AdvertManager;

@RequestMapping("/v1/market/img")
@RestController
public class LejiaAdvertApiImpl implements LejiaAdvertApi
{
    @Autowired
    private AdvertManager advertManager;
    
    @Override
    @LogApi(operation = "新增广告", format = "新增{entity.name}广告", resultFormat = "新增成功")
    public Result<MktAdvertOnList> insAdvert(MktAdvertOnList entity)
    {
        return new Result<>(advertManager.insAdvert(entity));
    }
    
    @Override
    public Result<MktAdvertOnList> insSpecialAdvert(MktAdvertOnList entity)
    {
        return new Result<>(advertManager.insSpecialAdvert(entity));
    }
    
    @Override
    public Result<MktAdvertOnList> getAdvert(Integer pkey)
    {
        return new Result<>(advertManager.getAdvert(pkey));
    }
    
    @Override
    public Result<PageResult<MktAdvertOnList>> queryAdvert(int page, int pagesize, AdvertPosition position)
    {
        return new Result<>(advertManager.queryAdvert(page, pagesize, position));
    }
    
    @Override
    public Result<PageResult<MktAdvertOnList>> querySpecialAdvert(int page, int pagesize, AdvertPosition position,
        List<String> farmers)
    {
        return new Result<>(advertManager.querySpecialAdvert(page, pagesize, position, farmers));
    }
    

    @Override
    @LogApi(operation = "修改广告", format = "修改{name}广告")
    public Result<MktAdvertOnList> updAdvert(Integer pkey, String name, AdvertPosition position, String positionObj,
        String photo, LinkType urlType, String objKey, Integer sort, List<String> farmers, LocationType locationType,
        List<Integer> targerKeys, MemberVisibleRange visibleRange)
    {
        return new Result<>(
            advertManager.updAdvert(pkey, name, position, positionObj, photo, urlType, objKey, sort, farmers,locationType,targerKeys,visibleRange));
    }
    
    @Override
    @LogApi(operation = "删除广告", format = "删除广告")
    public Result<Boolean> delAdvert(Integer pkey)
    {
        return new Result<>(advertManager.delAdvert(pkey));
    }
    
    @Override
    @LogApi(operation = "启动广告", format = "启动广告")
    public Result<Boolean> startAdvert(Integer pkey)
    {
        return new Result<>(advertManager.enabledAdvert(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止广告", format = "停止广告")
    public Result<Boolean> stopAdvert(Integer pkey)
    {
        return new Result<>(advertManager.enabledAdvert(pkey, false));
    }
    
  

  

    @Override
    public Result<Boolean> enableFunMenu(Integer pkey, Boolean enabled)
    {
      
        return  new Result<>(advertManager.enableFunMenu(pkey,enabled));
    }

    @Override
    public Result<PageResult<MktFunMenuConfigOnList>> queryFunMenuConfig(int page, int pagesize)
    {
        String marketPkey = CurrentSession.marketPkey();
        return  new Result<>(advertManager.queryFunMenuConfig(page,pagesize,Collections.singletonList(marketPkey)));
    }

    @Override
    public Result<MktFunMenuConfigInfo> getFunMenuConfig(Integer pkey)
    {
   
        return  new Result<>(advertManager.getFunMenuConfig(pkey));
    }

    @Override
    public Result<Boolean> updFunMenuConfig(@Valid MktFunMenuConfigInfo info)
    {
        String marketPkey = CurrentSession.marketPkey();
        return  new Result<>(advertManager.updFunMenuConfig(info,marketPkey));
    }

    @Override
    public Result<Boolean> delFunMenuConfig(Integer pkey)
    {

        return new Result<>(advertManager.delFunMenuConfig(pkey));
    }

//    @Override
//    public Result<MktAdvertOnList> updAdvert(Integer pkey, String name, AdvertPosition position, String positionObj,
//        String photo, LinkType urlType, String objKey, Integer sort, List<String> farmers, LocationType locationType,
//        List<Integer> targerKeys, MemberVisibleRange visibleRange)
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }
    
}
