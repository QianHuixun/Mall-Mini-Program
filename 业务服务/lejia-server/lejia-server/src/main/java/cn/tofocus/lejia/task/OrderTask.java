package cn.tofocus.lejia.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.MarketManager;
import cn.tofocus.lejia.domain.market.OrderManager;
import cn.tofocus.lejia.domain.market.VendorOrderManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.vendor.VendorWalletManager;

/**
 * @Auther: pty
 * @Date: 2020/7/6 14:24
 * @Description:
 */
@Component
public class OrderTask
{
    private static final Logger logger = LoggerFactory.getLogger(OrderTask.class);
    
    @Value("${task.order.checkUnpaid.enabled:false}")
    private boolean checkUnpaidOrderTaskEnabled;
    
    @Value("${task.order.finish.enabled:false}")
    private boolean finishOrderTaskEnabled;
    
    @Value("${task.order.settlementWallet.enabled:false}")
    private boolean settlementWalletTaskEnabled;
    
    @Value("${task.order.resetCourierAndSupply.enabled:false}")
    private boolean resetCourierAndSupplyTaskEnabled;
    
    @Value("${task.tj.order.presaleWarning.enabled:false}")
    private boolean tjPresaleOrderWarningTaskEnabled;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private MarketManager marketManager;
    
    @Autowired
    private VendorOrderManager vendorOrderManager;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private VendorWalletManager vendorWalletManager;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer ascription;
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0/10 * * * ?") //每10分钟跑一次
    public void checkPayOrder()
    {
        if (checkUnpaidOrderTaskEnabled)
        {
            logger.info("-------待支付订单处理跑批");
            appOrderManager.runCheckOrder();
            logger.info("-------完成待支付订单处理跑");
        }
        else
        {
            logger.info("-------未开启待支付订单处理跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 10 0 * * ?") //每天零点10分跑批
    public void checkRunOrder()
    {
        if (finishOrderTaskEnabled)
        {
            logger.info("-------已完成订单确认处理跑批");
            List<SysAscription> list = sysAscriptionDao.findAll();
            for (SysAscription as : list)
            {
                appOrderManager.runDrOrder(as.getPkey());
            }
            logger.info("-------完成已完成订单确认处理跑批");
        }
        else
        {
            logger.info("-------未开启已完成订单确认处理跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 15 0 * * ?") //每天零点15分跑批
    public void runSettlementWallet()
    {
        if (settlementWalletTaskEnabled)
        {
            logger.info("-------商户订单自动结算,结算3天前的数据跑批-------开始-------");
            // 2月29日订单 3月3日结算
            vendorWalletManager.runSettlementWallet(2, ascription);
            logger.info("-------商户订单自动结算,结算3天前的数据跑批-------结束-------");
        }
        else
        {
            logger.info("-------未开启商户订单自动结算跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 0 * * ?") //每天零点跑批
    public void reset()
    {
        if (resetCourierAndSupplyTaskEnabled)
        {
            logger.info("-------自动派单和自动采购重置跑批开始-------");
            marketManager.runDispatchCourier();
            List<SysAscription> list = sysAscriptionDao.findAll();
            for (SysAscription as : list)
            {
                vendorOrderManager.resetSupplyOrder(as.getPkey());
            }
            logger.info("-------自动派单和自动采购重置跑批结束-------");
        }
        else
        {
            logger.info("-------未开启自动派单和自动采购重置跑批");
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 20 0 * * ?") //每天零点20分跑批
    public void aggPresaleOrder()
    {
        if (tjPresaleOrderWarningTaskEnabled)
        {
            logger.info("-------开始  天津查询 当天是否超过200元的预售订单 跑批 -------");
            orderManager.presaleOrder();
            logger.info("-------结算  天津查询 当天是否超过200元的预售订单 跑批 -------");
        }
        else
        {
            logger.info("-------未开启天津预售订单预警跑批");
        }
    }
    
}
