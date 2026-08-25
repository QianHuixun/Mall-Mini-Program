package cn.tofocus.lejia.dao.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankOnList;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine.F;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.repository.market.MktOrderLineRepository;

@Component
public class MktOrderLineDao extends JpaSpecificationDelegate<Integer, MktOrderLine>
{
    @Autowired
    private MktOrderLineRepository repository;
    
    // 专区统计
    public List<List<Object>> getMtypeSales(String marketPkey, String companyPkey, String startTime, String endTime,
        int page, int pagesize, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales =
            repository.getMtypeSales(marketPkey, companyPkey, startTime, endTime, page * pagesize, pagesize, ascription);
        return sales;
    }
    
    public List<List<Object>> getGoodsSales(String marketPkey, String companyPkey, String startTime, String endTime,
        int page, int pagesize, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales =
            repository.getGoodsSales(marketPkey, companyPkey, startTime, endTime, page * pagesize, pagesize, ascription);
        return sales;
    }
    
    public List<List<Object>> getGoodsAnalysis(Integer goodsPkey, String startTime, String endTime, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales = repository.getGoodsAnalysis(goodsPkey, startTime, endTime, ascription);
        return sales;
    }
    
    public List<List<Object>> getGoodsAbnormal(String marketPkey, Integer abnormalNum, int page, int pagesize)
    {
        List<List<Object>> sales = repository.getGoodsAbnormal(marketPkey, abnormalNum, page * pagesize, pagesize);
        return sales;
    }
    
    public List<List<Object>> getgoodsHourAnalysis(Integer goodsPkey, String time, Integer ascription)
    {
        return repository.getgoodsHourAnalysis(goodsPkey, time + " 00:00:00", time + " 23:59:59", ascription);
    }
    
    public List<List<Object>> getgoodsHourDetail(Integer goodsPkey, String time, int page, int pagesize, Integer ascription)
    {
        return repository
            .getgoodsHourDetail(goodsPkey, time + " 00:00:00", time + " 23:59:59", page * pagesize, pagesize, ascription);
    }
    
    public List<List<Object>> getgoodsIntegralSales(String startTime, String endTime, int page, int pagesize, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        return repository.getgoodsIntegralSales(startTime, endTime, page * pagesize, pagesize, ascription);
    }
    
    public List<List<Object>> getMemberGoodsSales(List<Integer> memberPkeys, String startTime, String endTime, int page,
        int pagesize)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales =
            repository.getMemberGoodsSales(memberPkeys, startTime, endTime, page * pagesize, pagesize);
        return sales;
    }
    
    public List<List<Object>> getGoodsTypeSales(String marketPkey, String companyPkey, String startTime, String endTime,
        int page, int pagesize, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales =
            repository.getGoodsTypeSales(marketPkey, companyPkey, startTime, endTime, page * pagesize, pagesize, ascription);
        return sales;
    }
    
    public List<List<Object>> getFarmerSales(String marketPkey, String companyPkey, String startTime, String endTime,
        int page, int pagesize, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales =
            repository.getFarmerSales(marketPkey, companyPkey, startTime, endTime, page * pagesize, pagesize, ascription);
        return sales;
    }
    
    public List<List<Object>> getCompanySales(String companyPkey, String startTime, String endTime, int page,
        int pagesize, Integer ascription)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        List<List<Object>> sales =
            repository.getCompanySales(companyPkey, startTime, endTime, page * pagesize, pagesize, ascription);
        return sales;
    }
    
    private void timeProcess(String startTime, String endTime)
    {
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
    }
    
    public List<List<Object>> getLastWeekMtypeSales(String startTime, String endTime, Integer ascription)
    {
        timeProcess(startTime, endTime);
        return repository.getLastWeekMtypeSales(startTime, endTime, ascription);
    }
    
    public List<List<Object>> getYesterdayGoodsSales(String time, Integer ascription)
    {
        return repository.getYesterdayGoodsSales(time + " 23:59:59", ascription);
    }
    
    public List<SalesRankOnList> listTypeSales(Date startTime, Date endTime, Integer ascription)
    {
        List<SalesRankOnList> res = new ArrayList<>();
        List<List<Object>> list = repository.listTypeSales(startTime, endTime, ascription);
        for (List<Object> o : list)
        {
            SalesRankOnList sr = new SalesRankOnList();
            sr.setGtype(Integer.valueOf(String.valueOf(o.get(0))));
            sr.setOrderSales(new BigDecimal(String.valueOf(o.get(1))));
            res.add(sr);
        }
        return res;
    }
    
    public List<SalesRankOnList> listGoodsSales(Date startTime, Date endTime, Integer ascription)
    {
        List<SalesRankOnList> res = new ArrayList<>();
        List<List<Object>> list = repository.listGoodsSales(startTime, endTime, ascription);
        for (List<Object> o : list)
        {
            SalesRankOnList sr = new SalesRankOnList();
            sr.setGoods(Integer.valueOf(String.valueOf(o.get(0))));
            sr.setOrderSales(new BigDecimal(String.valueOf(o.get(1))));
            res.add(sr);
        }
        return res;
    }
    
    public Map<Integer,List<MktOrderLine>> mapOrderPkey(List<Integer> orderKeys)
    {
        Map<Integer,List<MktOrderLine>> map = new HashMap<>();
        if(orderKeys == null || orderKeys.isEmpty())
            return map;
        List<MktOrderLine> list = this.select().in("orderPkey", orderKeys.toArray()).exec();
        for(MktOrderLine ol : list)
        {
            Integer orderPkey = ol.getOrderPkey();
            if(!map.containsKey(orderPkey))
            {
                List<MktOrderLine> value = new ArrayList<>();
                map.put(orderPkey, value);
            }
            map.get(orderPkey).add(ol);
        }
        return map;
    }
    
    // 是否有销售出去
    public Boolean checkSalesRecord(Integer goods)
    {
        long count = this.aggregation()
        .eq("goods", goods)
        .notEq("status", OrderStatus.UNPAID_ORDER)
        .notEq("status", OrderStatus.VOID_ORDER)
        .notEq("status", OrderStatus.REFUNDED_ORDER)
        .execCount();
        return count > 0;
    }
    
    public List<MktOrderLine> listOrder(Integer orderPkey)
    {
        return this.select().eq("orderPkey", orderPkey).exec();
    }
    
    public List<MktOrderLine> listOrders(List<Integer> orderPkeys)
    {
        return this.select().in("orderPkey", orderPkeys).exec();
    }
    
    public void updStatusByOrderPkey(Integer orderPkey, OrderStatus status)
    {
        this.select().strict(true).eq(F.orderPkey, orderPkey).update(F.status, status);
    }
}
