package cn.tofocus.lejia.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.domain.wanli.WanliManager;

@Component
public class WanliTask
{
    private static final Logger logger = LoggerFactory.getLogger(WanliTask.class);
    
    @Value("${sec.courier.wanli.task:false}")
    private Boolean task;
    
//    @Autowired
//    private WanliManager wanliManager;
    
//    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 */2 * * * ?")
//    public void runExpiredMemberCard()
//    {
//        if(task)
//        {
//            logger.info("---请求第三方派送---");
//            wanliManager.runOrderQuery();
//        }
//    }
}
