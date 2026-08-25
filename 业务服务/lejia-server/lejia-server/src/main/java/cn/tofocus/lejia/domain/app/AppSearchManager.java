package cn.tofocus.lejia.domain.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.lejia.bean.dto.app.market.AppSearchAppOnList;
import cn.tofocus.lejia.bean.entity.market.MktSearch;
import cn.tofocus.lejia.bean.entity.market.MktSearchHot;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktSearchDao;
import cn.tofocus.lejia.dao.market.MktSearchHotDao;
import cn.tofocus.lejia.dao.market.MktSearchKeywordDao;

@Component
public class AppSearchManager
{
    @Autowired
    private MktSearchDao searchDao;
    
    @Autowired
    private MktSearchHotDao searchHotDao;
    
    @Autowired
    private MktSearchKeywordDao mktSearchKeywordDao;
    
    public AppSearchAppOnList queryAppSearch(Integer stype)
    {
        AppSearchAppOnList appSearchAppOnList = new AppSearchAppOnList();
        Integer ascription = MobileSession.appid();
        AggregationBuilder<Integer, MktSearch> builder = searchDao.aggregation()
            .groupby("descp")
            .eq("ascription", ascription)
            .max("createdTime", "createdTime")
            .page(0)
            .pagesize(8)
            //                .eq("idDel", false)
            .sort("createdTime", true);
        if (stype != null)
        {
            builder.eq("stype", stype);
        }
        
        Integer member = MobileSession.memberPkey();
        if (member != null)
        {
            builder.eq("member", member);
            PageResult<MktSearch> pageResult = builder.exec(MktSearch.class);
            appSearchAppOnList.setLines(pageResult.getContent());
        }
        appSearchAppOnList.setHotLines(queryAppSearchHot(stype).getContent());
        return appSearchAppOnList;
    }
    
    public PageResult<MktSearchHot> queryAppSearchHot(Integer stype)
    {
        Integer ascription = MobileSession.appid();
        SelectPageBuilder<Integer, MktSearchHot> builder = searchHotDao.selectPage()
            .page(0)
            .pagesize(8)
            .eq("ascription", ascription)
            //                .eq("idDel", false)
            .sort("createdTime", true);
        if (stype != null)
            builder.eq("stype", stype);
        PageResult<MktSearchHot> pageResult = builder.exec();
        return pageResult;
    }
    
    public Boolean delSearch()
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null)
            return false;
        List<MktSearch> list = searchDao.select().in("member", memberPkey).exec();
        boolean result = searchDao.removeAll(list);
        return result;
    }
    
    public List<String> listKeywords(SearchKeywordModule module)
    {
        Integer ascription = MobileSession.appid();
        String farmerPkey = null;
        if (module == SearchKeywordModule.SELF_MALL || module == SearchKeywordModule.BNYP)
            farmerPkey = Constant.Operation + ascription;
        else
            farmerPkey = MobileSession.farmerPkey();
        return mktSearchKeywordDao.listKeywords(ascription, farmerPkey, module);
    }
    
}
