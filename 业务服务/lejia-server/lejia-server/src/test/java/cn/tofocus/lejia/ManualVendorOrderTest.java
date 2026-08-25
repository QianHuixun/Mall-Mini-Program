package cn.tofocus.lejia;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;

@SpringBootTest
public class ManualVendorOrderTest
{
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Test
    public void testPayComm()
    {
        Integer pkey = 14530;
        MktOrder order = orderDao.get(pkey);
        BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
        BigDecimal orderAmt = order.getAmtn();
        if (order.getRefundAmt() != null)
            orderAmt = orderAmt.subtract(order.getRefundAmt());
        BigDecimal payCommission = orderAmt.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP);
        
        List<MktVendorOrder> list = vendorOrderDao.listOrder(order.getPkey());
        BigDecimal voSum = BigDecimal.ZERO;
        // 计算配送费和优惠券分配
        BigDecimal sum = BigDecimal.ZERO;
        for (MktVendorOrder vo : list)
        {
            voSum = voSum.add(vo.getAmt()).add(vo.getCommissions());
            sum = sum.add(vo.getTotalPrice());
        }
        if (sum.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal voPayCommission = payCommission;
            for (int i = 0; i < list.size(); i++)
            {
                MktVendorOrder vo = list.get(i);
                BigDecimal voAmt = vo.getAmt().add(vo.getCommissions());
                // 该订单应该分到的 手续费 五舍六入  (HALF_DOWN,四舍五入)
                BigDecimal voPc = voAmt.multiply(payCommission).divide(voSum, 2, RoundingMode.HALF_UP);
                if (i != list.size() - 1)
                {
                    voPayCommission = voPayCommission.subtract(voPc);
                }
                else
                {
                    voPc = voPayCommission;
                }
                vo.setPayComm(voPc);
            }
        }
        else
        {
            for (int i = 0; i < list.size(); i++)
            {
                MktVendorOrder vo = list.get(i);
                if (i != list.size() - 1)
                {
                    vo.setPayComm(BigDecimal.ZERO);
                }
                else
                {
                    vo.setPayComm(payCommission);
                }
            }
        }
        System.out.println(payCommission);
        BigDecimal sumPayCommission = BigDecimal.ZERO;
        for (MktVendorOrder vendorOrder : list)
        {
            System.out.println(vendorOrder.getPayComm());
            sumPayCommission = sumPayCommission.add(vendorOrder.getPayComm());
        }
        System.out.println(sumPayCommission);
    }
}
