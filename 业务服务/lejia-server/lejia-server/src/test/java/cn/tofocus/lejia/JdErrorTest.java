package cn.tofocus.lejia;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.domain.jd.JdErrorDataManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class JdErrorTest
{
    @Autowired
    private JdErrorDataManager manager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Test
    public void processRefund()
    {
        manager.processRefund();
    }
    
    @Test
    public void processOrderSplit()
    {
        manager.processOrderSplit();
    }
    
    @Test
    public void test1()
    {
        List<MktOrder> list = orderDao.byNotExists();
        System.out.println("list: " + JsonUtil.toString(list, true));
        System.out.println("list.size(): " + list.size());
    }
    
    @Test
    public void jdRefundAmtError()
    {
        manager.jdRefundAmtError();
    }
}
