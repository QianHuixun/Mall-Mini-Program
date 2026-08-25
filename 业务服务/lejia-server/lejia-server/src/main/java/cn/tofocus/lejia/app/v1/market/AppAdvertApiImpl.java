package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.market.AppAdvertOnList;
import cn.tofocus.lejia.bean.dto.market.MktCombinationAdviseInfo;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigOnList;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.app.AppAdvertManager;


@RequestMapping("/v1/app/market/img")
@RestController
public class AppAdvertApiImpl implements AppAdvertApi 
{
    
    @Autowired
    private AppAdvertManager advertManager;

    @Override
    public Result<AppAdvertOnList> getAdvert(Integer pkey) {
        return new Result<>(advertManager.getAdvert(pkey));
    }

    @Override
    public Result<List<AppAdvertOnList>> queryAdvert(AdvertPosition position, String positionObj)
    {
        return new Result<>(advertManager.queryAppAdvert(position, positionObj));
    }

	@Override
	public Result<List<MktIndexAdvertOnList>> listIndexAdvert() {
		return new Result<>(advertManager.listIndexAdvertV2());
	}

    @Override
    public Result<Boolean> notDisplayIndexAdvert(Integer pkey)
    {
        return new Result<>(advertManager.notDisplayIndexAdvert(pkey));
    }



    @Override
    public Result<List<MktFunMenuConfigOnList>> listFunMenuConfig()
    {
        String marketPkey =MobileSession.farmerPkey();
        Integer member=MobileSession.memberPkey();
        Integer appid=MobileSession.appid();
        return new Result<>(advertManager.listFunMenuConfig(marketPkey,member,appid));
    }


}
