package cn.tofocus.lejia;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class ManualSysTest
{
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Test
    public void test1()
    {
        SysFarmer farmer = sysFarmerDao.get("lejia_mkt_0015");
        log.info("farmer: {}", farmer);
    }
    
    @Test
    public void test2()
    {
        try
        {
            BufferedImage createImage = FileUtil.createImage("http://www.baidu.com", 100, 100);
            log.info("aa: {}", createImage);
        }
        catch (Exception e)
        {
      
            e.printStackTrace();
        }
    }
    
    @Autowired
    private SysFarmerConfigDao farmerConfigDao;
    
    @Test
    public void test3()
    {
        SysFarmerConfig bean = new SysFarmerConfig();
        bean.setPkey("11");
        bean.setAddr("123");
        List<String> s = new ArrayList<>();
        s.add("1112");
        s.add("asfds");
        bean.setPsTime(s);
        bean.setDeliveryRange(new BigDecimal("1"));
        bean.setLatitude(new BigDecimal(1));
        bean.setLongitude(new BigDecimal(1));
        bean.setYjPos(11);
        bean.setYjTime("1231");
        bean.setYStatus(false);
        bean.setYytb("12312");
        bean.setYyte("2323");
        SysFarmerConfig add = farmerConfigDao.add(bean);
        System.out.println(add);
    }
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Test
    public void test4()
    {
        MktOrder order = orderDao.get(4951);
        //	    Integer maxNum = orderDao.getOrderPrintMaxNum(order.getFarmer(), DateUtil.atStartOfDay(new Date()));
        //	    System.out.println("maxNum:" + maxNum);
    }
    
}
