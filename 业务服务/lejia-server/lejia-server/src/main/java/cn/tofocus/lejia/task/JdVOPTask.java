package cn.tofocus.lejia.task;

import cn.tofocus.lejia.domain.jd.JdGoodsManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPAddrManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.domain.jdvop.JdVOPCommonManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPMsgManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdVOPTask
{
    @Value("${task.jd.vop.enabled:false}")
    private boolean enabled;
    
    @Autowired
    private JdVOPCommonManager requestManager;
    
    @Autowired
    private JdVOPMsgManager msgManager;

    @Autowired
    private JdVOPAddrManager addrManager;

    @Autowired
    private JdGoodsManager jdGoodsManager;

    // 每8小时执行一次更新token
    @Scheduled(initialDelay = 0, fixedRate = 28800000)
    public void runUpdateAccessToken()
    {
        if (enabled)
        {
            log.info("--------[京东VOP定时任务] 开始更新access_token--------");
            try
            {
                requestManager.updateAccessTokenTask();
            }
            catch (Exception e)
            {
                log.error("[京东VOP定时任务] 更新access_token出错", e);
            }
            log.info("--------[京东VOP定时任务] 结束更新access_token--------");
        }
        else
            log.info("--------未开启京东VOP定时任务--------");
    }
    
    // 启动1分钟后开始执行，每1分钟执行一次
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void runConsumeMsgTask()
    {
        if (enabled)
        {
            log.info("--------[京东VOP定时任务] 开始处理京东消息--------");
            try
            {
                msgManager.consumeMsgTask();
            }
            catch (Exception e)
            {
                log.error("[京东VOP定时任务] 处理京东消息出错", e);
            }
            log.info("--------[京东VOP定时任务] 结束处理京东消息--------");
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void runSyncLowestBuy4AllSku()
    {
        if (enabled)
        {
            log.info("--------[京东VOP定时任务] 开始同步最低起购量--------");
            try
            {
                jdGoodsManager.syncLowestBuy4AllSku();
            }
            catch (Exception e)
            {
                log.error("[京东VOP定时任务] 同步最低起购量出错", e);
            }
            log.info("--------[京东VOP定时任务] 结束同步最低起购量--------");
        }
        else
            log.info("--------未开启京东VOP定时任务--------");
    }

    @Scheduled(cron = "0 0 2 1 * ?") // 每个月1号凌晨2点执行
    public void runSyncJdAddrTask()
    {
        if (enabled)
        {
            log.info("--------[京东VOP定时任务] 开始同步京东地址--------");
            try
            {
                addrManager.syncJdAddrTask();
            }
            catch (Exception e)
            {
                log.error("[京东VOP定时任务] 同步京东地址出错", e);
            }
            log.info("--------[京东VOP定时任务] 结束同步京东地址--------");
        }
        else
            log.info("--------未开启京东VOP定时任务--------");
    }
    
}
