package cn.tofocus.lejia.app.v1.market.goods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.market.*;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.domain.app.AppGoodsManager;

@RequestMapping("/v1/app/market/goods")
@RestController
public class AppGoodsApiImpl implements AppGoodsApi
{
    
    @Autowired
    private AppGoodsManager goodsManager;
    
    @Override
    public Result<PageResult<AppGoodsAppOnList>> queryAppGoods(Integer page, Integer pagesize, Integer gtype,
        Integer goodsMain, MType mType, String title, Integer hotSort, Integer priceSort, String date,
        Boolean isOnPresale, Boolean guessLike, Integer vendor, Integer topGoods)
    {
        int realPage = page;
        int realPagesize = pagesize;
        if (!MType.SPECIAL_GOODS.equals(mType))
        {
            realPage = 0;
            realPagesize = 10000;
        }
        return new Result<>(goodsManager.queryAppGoods(realPage,
            realPagesize,
            gtype,
            goodsMain,
            mType,
            title,
            hotSort,
            priceSort,
            date,
            isOnPresale,
            guessLike,
            vendor,
            topGoods,
            null));
    }
    
    @Override
    public Result<PageResult<AppGoodsAppOnList>> queryAppGuessLikeGoods(Integer page, Integer pagesize)
    {
        return new Result<>(goodsManager.queryAppGuessLikeGoods(page, pagesize));
    }
    
    @Override
    public Result<PageResult<AppGoodsAppOnList>> queryAppMemberGoods(Integer page, Integer pagesize)
    {
        return new Result<>(goodsManager.queryAppMemberGoods(page, pagesize));
    }
    
    @Override
    public Result<AppGoodsDetailsDTO> getGoods(Integer pkey)
    {
        return new Result<>(goodsManager.getAppGoods(pkey));
    }

    @Override
    public Result<AppGoodsDetailsDTO> getMemberGoods(Integer pkey)
    {
        return new Result<>(goodsManager.getMemberGoods(pkey));
    }
    
    @Override
    public Result<PageResult<AppGoodsCommentOnList>> queryGoodsComments(Integer page, Integer pagesize, Integer pkey)
    {
        return new Result<>(goodsManager.queryGoodsComments(page, pagesize, pkey));
    }
    
    @Override
    public Result<List<MktAppSpecialGoodsSellDateDTO>> getSpecialGoodsSellDate()
    {
        String[] states = {"已结束", "已结束", "抢购中", "即将开始", "即将开始"};
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
        Date yesterday = cal.getTime();
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
        Date dayBeforeYesterday = cal.getTime();
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 3);
        Date tomorrow = cal.getTime();
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
        Date dayAfterTomorrow = cal.getTime();
        Date[] dates = {dayBeforeYesterday, yesterday, today, tomorrow, dayAfterTomorrow};
        
        List<MktAppSpecialGoodsSellDateDTO> list = new ArrayList<>();
        for (int i = 0; i < states.length; i++)
        {
            MktAppSpecialGoodsSellDateDTO bean = new MktAppSpecialGoodsSellDateDTO();
            bean.setDate(dates[i]);
            bean.setState(states[i]);
            bean.setTimeDate(dates[i]);
            list.add(bean);
        }
        
        return new Result<>(list);
    }

    @Override
    public Result<List<GoodsProcessOnInfo>> listGoodsProcessOnInfo(Integer pkey)
    {
        return new Result<>(goodsManager.listGoodsProcessOnInfo(pkey));
    }
    
    @Override
    public Result<PageResult<AppRecommendGoodsOnPage>> queryAppGoodsRecommend(Integer page, Integer pagesize,
        GoodsRecommendZone zone, Integer sourceGoods)
    {
        return new Result<>(goodsManager.queryAppGoodsRecommend(page, pagesize, zone, sourceGoods));
    }
    
    @Override
    public Result<PageResult<AppMallGoodsOnPage>> queryMallGoods(Integer page, Integer pagesize, MType mtype,
        Integer gtype, Integer goodsMain, String title, Integer hotSort, Integer priceSort)
    {
        return new Result<>(
            goodsManager.queryMallGoods(page, pagesize, mtype, gtype, goodsMain, hotSort, priceSort, title));
    }

    @Override
    public Result<AppMsdGoodsOnScroll> searchMsdGoods(String title, Integer offset, Integer limit)
    {
        return new Result<>(goodsManager.searchMsdGoods(title, offset, limit));
    }
    
    //	@Override
    //	public Result<PageResult<AppGoodsAppOnList>> queryAppGoodsPopular(Integer page, Integer pagesize, MType mType) {
    //		return new Result<>(goodsManager.queryAppGoodsPopular(page, pagesize, mType));
    //	}
    
}
