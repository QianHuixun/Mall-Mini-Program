package cn.tofocus.lejia.task;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.domain.market.MemberManager;

/**
 * @Auther: pty
 * @Date: 2020/7/6 14:24
 * @Description:
 */
@Component
public class MemberTask
{
    private static final Logger logger = LoggerFactory.getLogger(MemberTask.class);
    
    @Value("${task.member.enabled:false}")
    private boolean enabled;
    
    @Autowired
    private MemberManager memberManager;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 10 0 * * ?") //每天零点10分跑批
    public void checkRunOrder()
    {
        if (enabled)
        {
            logger.info("-------会员到期处理跑批");
            memberManager.runCheckMember();
            logger.info("-------会员到期处理跑批完成");
        }
        else
        {
            logger.info("-------未开启会员相关跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 15 0 * * ?") //每天零点15分跑批
    public void runLogOutMember()
    {
        if (enabled)
        {
            logger.info("-------用户注销跑批开始");
            memberManager.runLogOutMember();
            logger.info("-------用户注销跑批完成");
        }
        else
        {
            logger.info("-------未开启会员相关跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 5 0 * * ?") //每天零点5分跑批
    public void accessNum()
    {
        if (enabled)
        {
            try
            {
                logger.info("-------会员访问处理跑批");
                memberManager.accessNum();
                logger.info("-------完成会员访问处理跑批");
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            logger.info("-------未开启会员相关跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 1 0 * * ?") //每天零点1分跑批
    public void runClearPoint()
    {
        if (enabled)
        {
            logger.info("-------积分清理跑批开始--------");
            logger.info("-------暂时没跑,后续调整--------");
            // memberPointManager.runClearPoint();
            logger.info("-------积分清理跑批结束--------");
        }
        else
        {
            logger.info("-------未开启会员相关跑批");
        }
    }
}
