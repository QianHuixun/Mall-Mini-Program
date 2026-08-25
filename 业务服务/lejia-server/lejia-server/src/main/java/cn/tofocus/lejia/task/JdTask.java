package cn.tofocus.lejia.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.domain.jd.JdGoodsManager;

@Component
public class JdTask
{
    private static final Logger logger = LoggerFactory.getLogger(JdTask.class);
    
    @Value("${task.jd.goods.sync.enabled:false}")
    private boolean enabled;
    
    @Autowired
    private JdGoodsManager manager;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 15 1 * * ?")
    public void aggPresaleOrder()
    {
        if (enabled)
        {
            logger.info("-------京东商品同步跑批开始-------");
            manager.runJdGoodsInfo(null);
            logger.info("-------京东商品同步跑批结束-------");
        }
        else
            logger.info("--------未开启京东商品同步跑批--------");
    }
}
