package cn.tofocus.lejia.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.GoodsBoxManager;
import cn.tofocus.lejia.domain.h5.H5GoodsManager;
import cn.tofocus.lejia.repository.market.MktGoodsRepository;

/**
 * @Auther: pty
 * @Date: 2020/7/6 14:24
 * @Description:
 */
@Component
public class GoodsTask
{
    private static final Logger logger = LoggerFactory.getLogger(GoodsTask.class);
    
    @Value("${task.goods.expired.enabled:false}")
    private boolean goodsExpiredTaskEnabled;
    
    @Value("${task.goods.box.enabled:false}")
    private boolean goodsBoxTaskEnabled;
    
    @Autowired
    private MktGoodsRepository goodsRepository;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private GoodsBoxManager goodsBoxManager;
    
    @Autowired
    private H5GoodsManager h5GoodsManager;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 5 0 * * ?")
    public void cleanGoods()
    {
        if (goodsExpiredTaskEnabled)
        {
            logger.info("-------开始商品到期启停跑批");
            List<SysAscription> list = sysAscriptionDao.findAll();
            Calendar cal = Calendar.getInstance();
            String formatDate = DateUtil.formatDate(cal.getTime());
            Date now = DateUtil.formatDateStr(formatDate, "yyyy-MM-dd");
            cal.add(Calendar.DATE, -1);
            for (SysAscription as : list)
            {
                goodsRepository.enableExpiredGoods(now, as.getPkey());
                goodsRepository.disableExpiredGoods(cal.getTime(), as.getPkey());
            }
            logger.info("-------结束商品到期启停跑批");
        }
        else
        {
            logger.info("-------未开启商品到期启停跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 6 0 * * ?")
    public void runAddGoodsBoxSpace()
    {
        if (goodsBoxTaskEnabled)
        {
            logger.info("-------开始包厢商品规格跑批");
            goodsBoxManager.runAddGoodsBoxSpace();
            logger.info("-------结束包厢商品规格跑批");
            h5GoodsManager.runAddGoodsSpace();
            logger.info("-------结束H5包厢商品规格跑批");
        }
        else
        {
            logger.info("-------未开启包厢商品相关跑批");
        }
    }
    
    // 下午3点1分
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "00 1 15 * * ?")
    public void reduceKcNum()
    {
        if (goodsBoxTaskEnabled)
        {
            logger.info("-------开始包厢商品规格当天失效跑批");
            goodsBoxManager.reduceKcNum();
            logger.info("-------结束包厢商品规格当天失效跑批");
            h5GoodsManager.reduceKcNum();
            logger.info("-------结束H5包厢商品规格当天失效跑批");
        }
        else
        {
            logger.info("-------未开启包厢商品相关跑批");
        }
    }
    
    // 晚上9点1分跑批
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 1 21 * * ?")
    public void reduceKcNumNine()
    {
        if (goodsBoxTaskEnabled)
        {
            logger.info("-------开始包厢商品规格当天失效跑批");
            goodsBoxManager.reduceKcNum();
            logger.info("-------结束包厢商品规格当天失效跑批");
            h5GoodsManager.reduceKcNum();
            logger.info("-------结束H5包厢商品规格当天失效跑批");
        }
        else
        {
            logger.info("-------未开启包厢商品相关跑批");
        }
    }
    
}
