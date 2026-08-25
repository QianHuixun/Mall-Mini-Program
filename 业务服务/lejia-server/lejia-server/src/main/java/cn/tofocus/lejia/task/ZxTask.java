package cn.tofocus.lejia.task;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.TjZxManager;
import cn.tofocus.lejia.zx.utilV2.Constants;

@Component
public class ZxTask
{
    private static final Logger logger = LoggerFactory.getLogger(OrderTask.class);
    
    @Value("${task.tj.zx.settlementQf.enabled:false}")
    private boolean tjZxSettlementQfTaskEnabled;
    
    @Value("${task.tj.zx.vendorOrderZero.enabled:false}")
    private boolean tjZxVendorOrderZeroTaskEnabled;
    
    @Autowired
    private TjZxManager tjZxManager;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Value("${tofocus.pay.options:false}")
    private Boolean payFlag;
    
    //    public void runFileUpload()
    //    {
    //        logger.info("-------生成结算报表开始-----------");
    //        LocalDate taskLocalDate = LocalDate.now();
    //        int n = -3;
    //        // 市场的清分时间
    //        SysAscription sa = sysAscriptionDao.get(Constants.ascription);
    //        if (sa != null)
    //        {
    //            if (sa.getZxQf() != null)
    //            {
    //                n = -sa.getZxQf();
    //            }
    //        }
    //        LocalDate tradeLocalDate = taskLocalDate.plusDays(n);
    //        String tradeDate = tradeLocalDate.toString();
    //        logger.info("-------跑批数据对应的时间:  " + tradeDate + " -----------");
    //        if (payFlag)
    //        {
    //            if (tradeDate.equals("2025-07-14"))
    //            {
    //                logger.info("这个日期不跑数据");
    //            }
    //            else
    //            {
    //                DayOfWeek dayOfWeek = taskLocalDate.getDayOfWeek();
    //                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
    //                {
    //                    logger.info("周末不跑批");
    //                }
    //                else if (dayOfWeek == DayOfWeek.MONDAY)
    //                {
    //                    logger.info("周一 跑三天的数据");
    //                    
    //                    logger.info("周六的数据");
    //                    tradeDate = tradeLocalDate.plusDays(-2).toString();
    //                    tjZxManager.runSettle(tradeDate, Constants.ascription, null);
    //                    
    //                    logger.info("周日的数据");
    //                    tradeDate = tradeLocalDate.plusDays(-1).toString();
    //                    tjZxManager.runSettle(tradeDate, Constants.ascription, null);
    //                    
    //                    logger.info("周一的数据");
    //                    tradeDate = tradeLocalDate.toString();
    //                    tjZxManager.runSettle(tradeDate, Constants.ascription, null);
    //                }
    //                else
    //                {
    //                    tjZxManager.runSettle(tradeDate, Constants.ascription, null);
    //                }
    //            }
    //        }
    //        logger.info("-------生成结算报表结束----------");
    //    }
    
    @Scheduled(cron = "0 0 16 * * ? ")
    public void runFileUploadT()
    {
        if (tjZxSettlementQfTaskEnabled)
        {
            int n = -3;
            SysAscription sa = sysAscriptionDao.get(Constants.ascription);
            if (sa != null)
            {
                if (sa.getZxQf() != null)
                {
                    n = -sa.getZxQf();
                }
                runFileUploadH(n, false);
                n = -30;
                if (sa.getZxQfSys() != null)
                {
                    n = -sa.getZxQfSys();
                }
                runFileUploadH(n, true);
            }
        }
        else
        {
            logger.info("-------未开启天津结算清分跑批");
        }
    }
    
    public void runFileUploadH(Integer n, Boolean flag)
    {
        logger.info("-------生成结算报表开始-----------");
        LocalDate taskLocalDate = LocalDate.now();
        LocalDate tradeLocalDate = taskLocalDate.plusDays(n);
        String tradeDate = tradeLocalDate.toString();
        logger.info("-------跑批数据对应的时间:  " + tradeDate + " -----------");
        if (payFlag)
        {
            logger.info("跑批交易日期：{}", tradeDate);
            tjZxManager.runSettle(tradeDate, Constants.ascription, flag);
        }
        logger.info("-------生成结算报表结束----------");
    }
    
    @Scheduled(cron = "0 0 20 * * ? ")
    public void zxVendorOrderZero()
    {
        if (tjZxVendorOrderZeroTaskEnabled)
        {
            logger.info("-------天津退款商户订单跑批开始-----------");
            tjZxManager.zxVendorOrderZero();
            logger.info("-------天津退款商户订单跑批结束-----------");
        }
        else
        {
            logger.info("-------未开启天津退款商户订单跑批");
        }
    }
}
