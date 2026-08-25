package cn.tofocus.lejia.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.v2.GiftV2Manager;
import cn.tofocus.lejia.repository.market.MktMemberCardRepository;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class CardTask
{
    private static final Logger logger = LoggerFactory.getLogger(CardTask.class);
    
    @Value("${task.card.enabled:false}")
    private boolean enabled;
    
    @Autowired
    private MktMemberCardRepository memcardRepository;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private GiftV2Manager giftManager;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 1 * * ?") // 每天零晨1点跑批
    public void runOrderDay()
    {
        if (enabled)
        {
            Date taskDate = DateUtil.atStartOfToday();
            logger.info("-------开始卡券到期跑批");
            memcardRepository.disableExpiredCards(taskDate);
            logger.info("-------完成卡券到期跑批");
            logger.info("-------开始礼品券到期跑批");
            giftManager.expiredTask(taskDate);
            logger.info("-------完成礼品券到期跑批");
        }
        else
        {
            logger.info("-------未开启卡券相关跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 10 * * ?") // 每天10点跑批
    public void runMaturityCard()
    {
        if (enabled)
        {
            logger.info("-------开始卡券快到期提醒跑批");
            cardManager.maturityNews();
            logger.info("-------完成卡券快到期提醒跑批");
        }
        else
        {
            logger.info("-------未开启卡券相关跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 1 * * ?") // 每天1点跑批
    public void runExpiredMemberCard()
    {
        if (enabled)
        {
            logger.info("---开始---已经过期7天卡券删除跑批---");
            //		cardManager.removeExpiredMemberCard();
            logger.info("---完成---已经过期7天卡券删除跑批---");
        }
        else
        {
            logger.info("-------未开启卡券相关跑批");
        }
    }
}
