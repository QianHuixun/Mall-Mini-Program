package cn.tofocus.lejia.domain.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v2.screen.BigScreenTopRightInfo;
import cn.tofocus.lejia.bean.dto.v2.screen.RealTimeSalesOnList;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRank2OnList;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankMarketOnPage;
import cn.tofocus.lejia.bean.dto.v2.screen.SalesRankOnList;
import cn.tofocus.lejia.bean.dto.v2.screen.TestOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.v2.TimeType;
import cn.tofocus.lejia.cache.AccessMap;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktAccessLogDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktOriTestDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BigScreenManager
{
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOriTestDao oriTestDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktAccessLogDao accessLogDao;
    
    @Autowired
    private AccessMap accessMap;
    
    public String runOrderLine()
    {
        long k1 = System.currentTimeMillis();
        List<MktOrderLine> lines = orderLineDao.findAll();
        List<MktOrder> orders = orderDao.findAll();
        List<MktGoods> goodsList = goodsDao.findAll();
        Map<Integer, Integer> map = new HashMap<>();
        goodsList.forEach(e -> {
            map.put(e.getPkey(), e.getGtype());
        });
        Map<Integer, MktOrder> orderMap = new HashMap<>();
        orders.forEach(e -> {
            orderMap.put(e.getPkey(), e);
        });
        
        for (MktOrderLine ol : lines)
        {
            if (map.containsKey(ol.getGoods()))
            {
                ol.setGtype(map.get(ol.getGoods()));
            }
            if (orderMap.containsKey(ol.getOrderPkey()))
            {
                MktOrder order = orderMap.get(ol.getOrderPkey());
                ol.setStatus(order.getStatus());
                ol.setCreatedTime(order.getCreatedTime());
            }
        }
        orderLineDao.updateAll(lines);
        long s1 = System.currentTimeMillis();
        String res = "本次数据跑批-orderline调整gtype字段,处理数量: " + lines.size() + "  耗时: " + (s1 - k1) + "  毫秒";
        return res;
    }
    
    /**
     * 销量分类金额排行 TOP10
     */
    public List<SalesRankOnList> listTypeSales(TimeType timeType)
    {
        Date startTime = getStartTime(timeType);
        Integer ascription = CurrentSession.ascriptionPkey();
        List<SalesRankOnList> res = new ArrayList<>();
        List<SalesRankOnList> list = orderLineDao.listTypeSales(startTime, new Date(), ascription);
        BigDecimal sum = BigDecimal.ZERO;
        for (SalesRankOnList sr : list)
        {
            sum = sum.add(sr.getOrderSales());
            MktGtype gtype = gtypeDao.get(sr.getGtype());
            if (gtype != null) sr.setTypeName(gtype.getName());
        }
        if (sum.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal hundred = new BigDecimal("100");
            for (SalesRankOnList sr : list)
            {
                sr.setPercentage(sr.getOrderSales().multiply(hundred).divide(sum, 2, BigDecimal.ROUND_HALF_UP));
                res.add(sr);
            }
        }
        return res;
    }
    
    /**
     * 销量商品金额排行榜 TOP20
     */
    public List<SalesRankOnList> listGoodsSales(TimeType timeType)
    {
        Date startTime = getStartTime(timeType);
        Integer ascription = CurrentSession.ascriptionPkey();
        List<SalesRankOnList> res = new ArrayList<>();
        List<SalesRankOnList> list = orderLineDao.listGoodsSales(startTime, new Date(), ascription);
        List<Integer> gkeys = new ArrayList<>();
        list.forEach(e -> {
            gkeys.add(e.getGoods());
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        for (SalesRankOnList sr : list)
        {
            if (goodsMap.containsKey(sr.getGoods())) sr.setName(goodsMap.get(sr.getGoods()).getTitle());
            res.add(sr);
        }
        return res;
    }
    
    /**
     * 销量分类笔数排行榜TOP10
     */
    public List<SalesRank2OnList> listTypeNum(TimeType timeType)
    {
        Date startTime = getStartTime(timeType);
        Integer ascription = CurrentSession.ascriptionPkey();
        System.out.println("ascription: " + ascription);
        System.out.println("CurrentSession: " + CurrentSession.marketPkey());
        
        List<SalesRank2OnList> res = new ArrayList<>();
        PageResult<SalesRank2OnList> pageResult = orderLineDao.aggregation()
            .page(0)
            .pagesize(10)
            .eq("ascription", ascription)
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .between("createdTime", startTime, new Date())
            .groupby("gtype", "gtype")
            .count("pkey", "num")
            .sort("num")
            .execDto(SalesRank2OnList.class);
        BigDecimal sum = BigDecimal.ZERO;
        for (SalesRank2OnList sr : pageResult.getContent())
        {
            sum = sum.add(new BigDecimal(sr.getNum()));
        }
        if (sum.compareTo(BigDecimal.ZERO) == 1)
        {
            BigDecimal hundred = new BigDecimal("100");
            for (SalesRank2OnList sr : pageResult.getContent())
            {
                sr.setPercentage(
                    new BigDecimal(sr.getNum()).multiply(hundred).divide(sum, 2, BigDecimal.ROUND_HALF_UP));
                res.add(sr);
            }
        }
        return res;
    }
    
    /**
     * 销售商品笔数排行榜
     */
    public List<SalesRankOnList> listGoodsNum(TimeType timeType)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date startTime = getStartTime(timeType);
        List<SalesRankOnList> res = new ArrayList<>();
        PageResult<SalesRankOnList> pageResult = orderLineDao.aggregation()
            .page(0)
            .pagesize(20)
            .eq("ascription", ascription)
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .between("createdTime", startTime, new Date())
            .groupby("goods", "goods")
            .count("pkey", "num")
            .sort("num")
            .execDto(SalesRankOnList.class);
        List<Integer> gkeys = new ArrayList<>();
        pageResult.getContent().forEach(e -> {
            gkeys.add(e.getGoods());
        });
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        for (SalesRankOnList sr : pageResult.getContent())
        {
            if (goodsMap.containsKey(sr.getGoods())) sr.setName(goodsMap.get(sr.getGoods()).getTitle());
            res.add(sr);
        }
        return res;
    }
    
    /**
     * 市场销售详情及中间地图数据
     */
    public List<SalesRankMarketOnPage> queryMarket(TimeType timeType)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date startTime = getStartTime(timeType);
        List<SalesRankMarketOnPage> result = orderDao.aggregation()
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .between("createdTime", startTime, new Date())
            .groupby("farmer", "market")
            .eq("ascription", ascription)
            .sum("amtall", "orderSales")
            .count("pkey", "num")
            .sort("orderSales")
            .execListDto(SalesRankMarketOnPage.class);
        List<SysFarmer> all = sysFarmerDao.findAll();
        Map<String, SysFarmer> map = new HashMap<>();
        all.forEach(e -> {
            map.put(e.getPkey(), e);
        });
        for (SalesRankMarketOnPage sr : result)
        {
            String market = sr.getMarket();
            if (map.containsKey(market))
            {
                SysFarmer farmer = map.get(market);
                sr.setMarketName(farmer.getName());
                SysFarmerConfig config = farmer.getConfig();
                if (config != null)
                {
                    sr.setLongitude(config.getLongitude());
                    sr.setLatitude(config.getLatitude());
                }
            }
            
        }
        return result;
    }
    
    /**
     * 右上角数据
     */
    public BigScreenTopRightInfo getTopRight(TimeType timeType)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        BigScreenTopRightInfo res = new BigScreenTopRightInfo();
        long farmerCount = sysFarmerDao.aggregation().eq("ascription", ascription).eq("idDel", false).execCount();
        res.setFarmerCount((int)farmerCount);
        Date startTime = getStartTime(timeType);
        List<BigScreenTopRightInfo> list = orderDao.aggregation()
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("ascription", ascription)
            .between("createdTime", startTime, new Date())
            .groupby("status", "status")
            .count("pkey", "orderNum")
            .sum("amtall", "orderSales")
            .execListDto(BigScreenTopRightInfo.class);
        BigDecimal orderSales = BigDecimal.ZERO;
        BigDecimal orderRefund = BigDecimal.ZERO;
        int orderNum = 0;
        for (BigScreenTopRightInfo bs : list)
        {
            orderSales = orderSales.add(bs.getOrderSales());
            orderNum = orderNum + bs.getOrderNum();
            if (bs.getStatus().equals(OrderStatus.REFUNDED_ORDER)) orderRefund = bs.getOrderSales();
        }
        String now = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        Set<String> set = accessMap.findAll(now + "," + ascription);
        int visitor = set.size();
        if (!timeType.equals(TimeType.THE_DAY))
        {
            long count = accessLogDao.aggregation()
                .eq("ascription", ascription)
                .between("accessTime", DateUtil.formatDate(startTime, "yyyy-MM-dd"), now)
                .execCount();
            visitor = visitor + (int)count;
        }
        
        if (visitor > 0)
        {
            BigDecimal hundred = new BigDecimal("100");
            // 支付用户数
            long payCount = orderDao.aggregation()
                .eq("ascription", ascription)
                .between("createdTime", startTime, new Date())
                .notEq("status", OrderStatus.UNPAID_ORDER)
                .notEq("status", OrderStatus.VOID_ORDER)
                .execCountDistinct("member");
            // 支付转化率  所选时间范围内，支付用户数/访问用户数
            BigDecimal conversion =
                new BigDecimal(payCount).multiply(hundred).divide(new BigDecimal(visitor), 2, BigDecimal.ROUND_HALF_UP);
            res.setConversion(conversion);
            
            // 客单价  所选时间范围内，商品产生的支付金额/支付用户数
            if (payCount > 0)
            {
                BigDecimal customer = orderSales.divide(new BigDecimal(payCount), 2, BigDecimal.ROUND_HALF_UP);
                res.setCustomer(customer);
            }
            
            // 复购率  所选时间范围内，重复购买的人数/所有购买的人数
            // 支付用户数 等于 所有购买的人数
            List<BigScreenTopRightInfo> listDto = orderDao.aggregation()
                .notEq("status", OrderStatus.UNPAID_ORDER)
                .notEq("status", OrderStatus.VOID_ORDER)
                .eq("ascription", ascription)
                .between("createdTime", startTime, new Date())
                .groupby("member", "orderNum")
                .count("pkey", "visitor")
                .execListDto(BigScreenTopRightInfo.class);
            int i = 0;
            for (BigScreenTopRightInfo bs : listDto)
            {
                if (bs.getVisitor() < 2) i++;
            }
            // 复购人数
            int repurchaseNum = listDto.size() - i;
            // 复购率
            BigDecimal repurchase = BigDecimal.ZERO;
            if (payCount > 0)
            {
                repurchase = new BigDecimal(repurchaseNum).multiply(hundred)
                    .divide(new BigDecimal(payCount), 2, BigDecimal.ROUND_HALF_UP);
            }
            res.setRepurchase(repurchase);
        }
        
        res.setVisitor(visitor);
        res.setOrderNum(orderNum);
        res.setOrderRefund(orderRefund);
        res.setOrderSales(orderSales);
        if (timeType.equals(TimeType.THE_DAY) || timeType.equals(TimeType.THREE_DAY))
        {
            List<RealTimeSalesOnList> hour = getHour(startTime);
            res.setRtsList(hour);
        }
        else
        {
            List<RealTimeSalesOnList> day = getDay(startTime);
            res.setRtsList(day);
        }
        
        return res;
    }
    
    /**
     * 检测信息
     */
    public PageResult<TestOnPage> queryTest(int page, int pagesize, TimeType timeType)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date startTime = getStartTime(timeType);
        return oriTestDao.selectPage()
            .page(page).pagesize(pagesize)
            .eq("ascription", ascription)
            .between("testDate", DateUtil.formatDate(startTime, "yyyy-MM-dd"), DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .execDto(TestOnPage.class);
    }
    
    private List<RealTimeSalesOnList> getHour(Date startTime)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        // 获取最早营业时间 和 最迟结束营业时间
        List<SysFarmerConfig> exec = sysFarmerConfigDao.select().eq("ascription", ascription).isNotNull("yytb").isNotNull("yyte").exec();
        Integer yytb = null;
        Integer yyte = null;
        for (SysFarmerConfig fc : exec)
        {
            String tb = fc.getYytb();
            String te = fc.getYyte();
            if (StringUtils.isNotBlank(tb))
            {
                String[] split = tb.split(":");
                Integer of = Integer.valueOf(split[0]);
                if (of > 23 || of < 0) throw TofocusException.of(LejiaErrCode.FARMER_TIME_ERROR);
                if (yytb == null) yytb = of;
                if (of < yytb) yytb = of;
            }
            
            if (StringUtils.isNotBlank(te))
            {
                String[] split2 = te.split(":");
                Integer of2 = Integer.valueOf(split2[0]);
                if (of2 > 23 || of2 < 0) throw TofocusException.of(LejiaErrCode.FARMER_TIME_ERROR);
                if (yyte == null) yyte = of2;
                if (of2 > yyte) yyte = of2;
            }
        }
        Date now = new Date();
        List<RealTimeSalesOnList> res = new ArrayList<>();
        Map<Date, RealTimeSalesOnList> map = orderDao.hourAmtn(startTime, now, ascription);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        while (true)
        {
            String hh = DateUtil.formatDate(cal.getTime(), "HH");
            if (Integer.valueOf(hh).equals(yytb))
            {
                break;
            }
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        System.out.println("开始时间: " + DateUtil.formatDate(cal.getTime()));
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(DateUtil.atStartOfDay(now));
        while (true)
        {
            String hh = DateUtil.formatDate(endCal.getTime(), "HH");
            if (Integer.valueOf(hh).equals(yyte))
            {
                break;
            }
            if (Integer.valueOf(hh) > yyte)
                endCal.add(Calendar.HOUR_OF_DAY, -1);
            else
                endCal.add(Calendar.HOUR_OF_DAY, 1);
        }
        System.out.println("结束时间: " + DateUtil.formatDate(endCal.getTime()));
        
        while (true)
        {
            if (cal.getTime().after(endCal.getTime())) break;
            if (map.containsKey(cal.getTime()))
            {
                res.add(map.get(cal.getTime()));
            }
            else
            {
                RealTimeSalesOnList rs = new RealTimeSalesOnList();
                rs.setDate(cal.getTime());
                rs.setOrderSales(BigDecimal.ZERO);
                rs.setTime(DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd HH"));
                res.add(rs);
            }
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        return res;
    }
    
    private List<RealTimeSalesOnList> getDay(Date startTime)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date now = new Date();
        List<RealTimeSalesOnList> res = new ArrayList<>();
        Map<Date, RealTimeSalesOnList> map = orderDao.dayAmtn(startTime, now, ascription);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(DateUtil.atStartOfDay(now));
        while (true)
        {
            if (cal.getTime().after(endCal.getTime())) break;
            if (map.containsKey(cal.getTime()))
            {
                res.add(map.get(cal.getTime()));
            }
            else
            {
                RealTimeSalesOnList rs = new RealTimeSalesOnList();
                rs.setDate(cal.getTime());
                rs.setOrderSales(BigDecimal.ZERO);
                rs.setTime(DateUtil.formatDate(cal.getTime(), "yyyy-MM-dd"));
                res.add(rs);
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        return res;
    }
    
    // 获取时间
    private Date getStartTime(TimeType timeType)
    {
        Date date = null;
        Calendar cal = Calendar.getInstance();
        switch (timeType)
        {
            case THE_DAY:
                
                break;
            case THREE_DAY:
                cal.add(Calendar.DAY_OF_YEAR, -2);
                break;
            case WEEK:
                cal.add(Calendar.DAY_OF_YEAR, -6);
                break;
            case MONTH:
                cal.add(Calendar.DAY_OF_MONTH, -29);
                break;
            
            default:
                break;
        }
        date = DateUtil.atStartOfDay(cal.getTime());
        log.info("getStartTime-data: {}", DateUtil.formatDate(date));
        return date;
    }
    
}
