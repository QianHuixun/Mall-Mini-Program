package cn.tofocus.lejia.domain.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.bean.entity.market.MktSearch;
import cn.tofocus.lejia.bean.enums.SearchType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktSearchDao;

@Component
public class SearchManager {
    @Autowired
    private MktSearchDao searchDao;

    public void insSearch(SearchType stype, String descp) {
        Integer member = MobileSession.memberPkey();
        if (member != null) {
            MktSearch mktSearch = new MktSearch();
            mktSearch.setMember(member);
            mktSearch.setStype(stype);
            mktSearch.setDescp(descp);
            searchDao.add(mktSearch);
        }
    }

}
