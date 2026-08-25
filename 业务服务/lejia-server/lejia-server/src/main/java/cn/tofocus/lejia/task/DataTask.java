package cn.tofocus.lejia.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.IndexDataCenterManager;
import cn.tofocus.lejia.domain.IterateManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataTask
{
    @Value("${task.dataCenter.enabled:false}")
    private boolean dataCenterTaskEnabled;
    
    @Value("${task.initKc.enabled:false}")
    private boolean initKcTaskEnabled;
    
    @Value("${task.initReport.enabled:false}")
    private boolean initReportTaskEnabled;
    
    @Autowired
    private IndexDataCenterManager manager;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private IterateManager iterateManager;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 10 0 * * ?")
    public void runDataCenter()
    {
        if (dataCenterTaskEnabled)
        {
            log.info("-------开始首页数据跑批");
            long k1 = System.currentTimeMillis();
            List<SysAscription> list = sysAscriptionDao.findAll();
            for (SysAscription as : list)
            {
                manager.yesterdayData(as.getPkey());
            }
            log.info("-------完成首页数据跑批，总耗时：{}", System.currentTimeMillis() - k1);
        }
        else
        {
            log.info("-------未开启首页数据跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 20 0 * * ?")
    public void runInitKc()
    {
        if (initKcTaskEnabled)
        {
            log.info("-------开始商品规格库存调整跑批");
            long k1 = System.currentTimeMillis();
            List<SysAscription> list = sysAscriptionDao.findAll();
            for (SysAscription as : list)
            {
                iterateManager.initKc(as.getPkey());
            }
            log.info("-------完成商品规格库存调整跑批，总耗时：{}", System.currentTimeMillis() - k1);
        }
        else
        {
            log.info("-------未开启商品规格库存调整跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 30 0 * * ?")
    public void runInitReport()
    {
        if (initReportTaskEnabled)
        {
            log.info("-------开始日统计跑批");
            long k1 = System.currentTimeMillis();
            List<SysAscription> list = sysAscriptionDao.findAll();
            for (SysAscription as : list)
            {
                manager.initReport(as.getPkey(), 6);
            }
            log.info("-------完成日统计跑批，总耗时：{}", System.currentTimeMillis() - k1);
        }
        else
        {
            log.info("-------未开启日统计跑批");
        }
    }
    
}
