package cn.tofocus.lejia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cn.tofocus.common.util.CsvUtil;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.domain.market.VendorOrderManager;

public class ManualDistributePayCommTest
{
    private static BigDecimal postage = BigDecimal.ZERO;
    
    private static BigDecimal cardAmt = BigDecimal.ZERO;
    
    private static BigDecimal sum = BigDecimal.ZERO;
    
    private static BigDecimal zero = new BigDecimal("0.00");
    
    // 辅助方法：创建测试订单
    private MktVendorOrder createOrder(Integer vendor, BigDecimal totalPrice)
    {
        MktVendorOrder order = new MktVendorOrder();
        order.setVendor(vendor);
        order.setAmt(totalPrice);
        order.setCommissions(BigDecimal.ZERO);
        return order;
    }
    
    // 辅助方法：计算列表总手续费
    private BigDecimal sumPayComm(List<MktVendorOrder> orders)
    {
        return orders.stream().map(MktVendorOrder::getPayComm).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 辅助方法：检查手续费倒挂
    private void assertNoCommissionInversion(List<MktVendorOrder> orders)
    {
        Map<Integer, BigDecimal> vendorTotalPrice = new HashMap<>();
        Map<Integer, BigDecimal> vendorTotalPayComm = new HashMap<>();
        
        // 按vendor分组计算总金额和总手续费
        for (MktVendorOrder order : orders)
        {
            Integer vendor = order.getVendor();
            BigDecimal price = order.getAmt() != null ? order.getAmt() : BigDecimal.ZERO;
            BigDecimal comm = order.getPayComm() != null ? order.getPayComm() : BigDecimal.ZERO;
            
            vendorTotalPrice.merge(vendor, price, BigDecimal::add);
            vendorTotalPayComm.merge(vendor, comm, BigDecimal::add);
        }
        
        // 检查是否总金额高的组手续费也高
        vendorTotalPrice.forEach((vendor, totalPrice) -> {
            BigDecimal totalComm = vendorTotalPayComm.get(vendor);
            
            vendorTotalPrice.forEach((otherVendor, otherTotalPrice) -> {
                if (totalPrice.compareTo(otherTotalPrice) > 0)
                {
                    BigDecimal otherTotalComm = vendorTotalPayComm.get(otherVendor);
                    assertTrue(totalComm.compareTo(otherTotalComm) >= 0,
                        "商户" + vendor + "总金额(" + totalPrice + ")高于商户" + otherVendor + "，但手续费(" + totalComm + ")低于对方("
                            + otherTotalComm + ")");
                }
            });
        });
    }
    
    // 测试用例1：空列表
    @Test
    void testEmptyList()
    {
        List<MktVendorOrder> orders = new ArrayList<>();
        BigDecimal payCommission = BigDecimal.ZERO;
        VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
        assertEquals(0, orders.size());
    }
    
    // 测试用例2：单个订单
    @Test
    void testSingleOrder()
    {
        List<MktVendorOrder> orders = new ArrayList<>();
        orders.add(createOrder(1, new BigDecimal("100.00")));
        
        BigDecimal payCommission = new BigDecimal("10.00");
        VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
        
        // 验证结果
        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("10.00"), orders.get(0).getPayComm());
        assertEquals(payCommission, sumPayComm(orders));
        assertTrue(orders.get(0).getPayComm().compareTo(BigDecimal.ZERO) >= 0);
    }
    
    // 测试用例3：多个订单同商户
    @Test
    void testSameVendor()
    {
        List<MktVendorOrder> orders = new ArrayList<>();
        orders.add(createOrder(1, new BigDecimal("100.00"))); // 33.33%
        orders.add(createOrder(1, new BigDecimal("200.00"))); // 66.67%
        
        BigDecimal payCommission = new BigDecimal("30.00");
        VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
        
        // 验证结果
        assertEquals(payCommission, sumPayComm(orders));
        assertTrue(orders.get(0).getPayComm().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(orders.get(1).getPayComm().compareTo(BigDecimal.ZERO) >= 0);
        
        // 金额比例应与手续费比例一致
        BigDecimal ratio1 = orders.get(0).getPayComm().divide(payCommission, 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal ratio2 = orders.get(1).getPayComm().divide(payCommission, 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal expectedRatio1 = new BigDecimal("0.3333");
        BigDecimal expectedRatio2 = new BigDecimal("0.6667");
        
        assertTrue(ratio1.compareTo(expectedRatio1) == 0 || ratio1.compareTo(expectedRatio1) == -1);
        assertTrue(ratio2.compareTo(expectedRatio2) == 0 || ratio2.compareTo(expectedRatio2) == 1);
    }
    
    private static Map<Integer, List<MktVendorOrder>> map = new HashMap<>();
    
    private static Map<Integer, BigDecimal> payCommissionMap = new HashMap<>();
    
    private static BigDecimal payCommissionRate = Constant.ZxConfig.TJ_COMMISSION_RATE;
    
    @BeforeAll
    public static void load()
    {
        try
        {
            List<MktVendorOrder> list =
                CsvUtil.importCsv("D:\\Users\\czy\\Desktop\\mkt_vendor_order.csv", MktVendorOrder.class);
            for (MktVendorOrder vo : list)
            {
                map.computeIfAbsent(vo.getOrderPkey(), k -> new ArrayList<>()).add(vo);
            }
            for (Integer orderPkey : map.keySet())
            {
                List<MktVendorOrder> orders = map.get(orderPkey);
                BigDecimal sum = orders.stream()
                    .map(vo -> (vo.getAmt() != null ? vo.getAmt() : BigDecimal.ZERO)
                        .add(vo.getCommissions() != null ? vo.getCommissions() : BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sum.compareTo(BigDecimal.ZERO) == 0)
                    map.remove(orderPkey);
                else
                    payCommissionMap.put(orderPkey, sum.multiply(payCommissionRate).setScale(2, RoundingMode.HALF_UP));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // 测试用例4：不同商户
    @Test
    void testDifferentVendors()
    {
        for (Integer orderPkey : map.keySet())
        {
            List<MktVendorOrder> orders = map.get(orderPkey);
            BigDecimal payCommission = payCommissionMap.get(orderPkey);
            VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
            
            // 验证结果
            assertEquals(payCommission, sumPayComm(orders));
            orders.forEach(order -> assertTrue(order.getPayComm().compareTo(BigDecimal.ZERO) >= 0));
            assertNoCommissionInversion(orders); // 验证无倒挂
        }
    }
    
    // 测试用例5：总手续费为零
    @Test
    void testZeroCommission()
    {
        for (Integer orderPkey : map.keySet())
        {
            List<MktVendorOrder> orders = map.get(orderPkey);
            BigDecimal payCommission = zero;
            VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
            
            // 验证结果
            assertEquals(payCommission, sumPayComm(orders));
            orders.forEach(order -> assertEquals(zero, order.getPayComm()));
        }
    }
    
    // 测试用例6：订单金额为零
    @Test
    void testZeroAmountOrders()
    {
        List<MktVendorOrder> orders = new ArrayList<>();
        orders.add(createOrder(1, BigDecimal.ZERO));
        orders.add(createOrder(2, new BigDecimal("400.00")));
        orders.add(createOrder(3, BigDecimal.ZERO));
        
        BigDecimal payCommission = new BigDecimal("50.00");
        VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
        
        // 验证结果
        assertEquals(payCommission, sumPayComm(orders));
        orders.forEach(order -> assertTrue(order.getPayComm().compareTo(BigDecimal.ZERO) >= 0));
        
        // 金额为0的订单手续费应为0
        assertEquals(zero, orders.get(0).getPayComm());
        assertEquals(zero, orders.get(2).getPayComm());
        assertEquals(payCommission, orders.get(1).getPayComm()); // 全部手续费由有金额的订单承担
    }
    
    // 测试用例7：手续费精确度（小数处理）
    @Test
    void testPrecisionHandling()
    {
        List<MktVendorOrder> orders = new ArrayList<>();
        orders.add(createOrder(1, new BigDecimal("100.00")));
        orders.add(createOrder(1, new BigDecimal("100.00")));
        orders.add(createOrder(1, new BigDecimal("100.00")));
        
        BigDecimal payCommission = new BigDecimal("1.00");
        VendorOrderManager.distributePayComm(orders, postage, cardAmt, payCommission, sum);
        
        // 验证结果
        BigDecimal total = sumPayComm(orders);
        assertEquals(payCommission, total);
        
        // 手续费应平均分配（允许0.01的误差）
        BigDecimal expected = new BigDecimal("0.33");
        orders.forEach(order -> {
            BigDecimal comm = order.getPayComm();
            assertTrue(comm.equals(expected) || comm.equals(new BigDecimal("0.34")), "手续费应在0.33-0.34之间，实际：" + comm);
        });
    }
}
