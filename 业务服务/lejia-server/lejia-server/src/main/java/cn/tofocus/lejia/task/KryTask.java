//package cn.tofocus.lejia.task;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import cn.tofocus.lejia.bean.entity.sys.SysAscription;
//import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
//import cn.tofocus.lejia.domain.market.keruyun.KeruyunManager;
//
///**
// * @Auther: pty
// * @Date: 2020/7/6 14:24
// * @Description:
// */
//@Component
//public class KryTask {
//    
//    private static final Logger logger = LoggerFactory.getLogger(KryTask.class);
//    @Autowired
//    private KeruyunManager kryManger;
//    
//    @Autowired
//    private SysAscriptionDao sysAscriptionDao;
//    
//  
//    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 0 1 * * ?")//每天零晨1点跑批
//    public void runOrderDay() {
//    	logger.info("-------开始客如云跑批");
//        List<SysAscription> list = sysAscriptionDao.findAll();
//        for (SysAscription as : list)
//        {
//            kryManger.runOrderDay(as.getPkey());
//        }
//    	logger.info("-------完成客如云跑批");
//    }
//    
//    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron = "0 30 1 * * ?")//每天零晨1点跑批
//    public void runPointDay() {
//    	logger.info("-------开始客如云会员积分跑批");
//        List<SysAscription> list = sysAscriptionDao.findAll();
//        for (SysAscription as : list)
//        {
//            kryManger.runPointDay(as.getPkey());
//        }
//    	logger.info("-------完成客如云会员积分跑批");
//    }
//}
