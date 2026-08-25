package cn.tofocus.lejia.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.domain.MsdCateringManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MsdCateringTask
{
    @Value("${catering.enabled:false}")
    private boolean enabled;
    
    @Value("${catering.task.syncMember.enabled:false}")
    private boolean syncMemberEnabled;
    
    @Autowired
    private MsdCateringManager msdCateringManager;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 10 1 * * ?") //每天1点10分跑批
    public void syncMemberTask()
    {
        if (enabled)
        {
            if (syncMemberEnabled)
            {
                log.info("-------开始同步第三方报餐系统会员跑批");
                msdCateringManager.syncMemberTask();
                log.info("-------完成同步第三方报餐系统会员跑批");
            }
            else
            {
                log.info("-------未开启同步第三方报餐系统会员跑批");
            }
        }
        else
        {
            log.info("-------未开启对接第三方报餐系统会员");
        }
    }
}
