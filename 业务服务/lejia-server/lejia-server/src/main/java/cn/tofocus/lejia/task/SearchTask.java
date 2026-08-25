package cn.tofocus.lejia.task;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.bean.entity.market.MktSearchHot;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.enums.SearchType;
import cn.tofocus.lejia.dao.market.MktSearchHotDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.repository.market.MktSearchRepository;

/**
 * @Auther: pty
 * @Date: 2020/7/6 14:24
 * @Description:
 */
@Component
public class SearchTask
{
    private static final Logger logger = LoggerFactory.getLogger(SearchTask.class);
    
    @Value("${task.search.hot.enabled:false}")
    private boolean searchHotTaskEnabled;
    
    @Autowired
    private MktSearchHotDao searchHotDao;
    
    @Autowired
    private MktSearchRepository searchRepository;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0/120 * * * ?")
    public void refreshSearchHotTask()
    {
        if (searchHotTaskEnabled)
        {
            List<MktSearchHot> allList = searchHotDao.select().exec();
            searchHotDao.removeAll(allList);
            
            List<MktSearchHot> searchHotList = new ArrayList<>();
            SearchType[] searchTypes = SearchType.values();
            
            logger.info("-------开始每2小时热门搜索跑批");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 2);
            String sTime = DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
            String eTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            logger.info("time:{}----{}", sTime, eTime);
            
            List<SysAscription> list = sysAscriptionDao.findAll();
            for (SysAscription as : list)
            {
                for (SearchType searchType : searchTypes)
                {
                    List<Map<String, Object>> rs =
                        searchRepository.queryGroupByDescp(sTime, eTime, as.getPkey(), searchType.getIndex());
                    for (Map<String, Object> map : rs)
                    {
                        MktSearchHot mktSearchHot = new MktSearchHot();
                        mktSearchHot.setStype(searchType);
                        mktSearchHot.setDescp(map.get("descp").toString());
                        mktSearchHot.setCreatedTime(new Date());
                        searchHotList.add(mktSearchHot);
                    }
                }
            }
            searchHotDao.addAll(searchHotList);
            logger.info("-------结束每2小时热门搜索跑批");
        }
        else
        {
            logger.info("-------未开启每2小时热门搜索跑批");
        }
    }
}
