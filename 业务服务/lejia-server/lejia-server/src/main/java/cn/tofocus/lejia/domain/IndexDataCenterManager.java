package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.dto.data.IndexYFDTO;
import cn.tofocus.lejia.bean.dto.data.ReportOrderDTO;
import cn.tofocus.lejia.bean.entity.market.MktOperatingStatistics;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.cache.AccessMap;
import cn.tofocus.lejia.cache.IndexDataLinDaoMap;
import cn.tofocus.lejia.cache.IndexDataLinHandleListMap;
import cn.tofocus.lejia.cache.IndexDataLinHandleMap;
import cn.tofocus.lejia.cache.IndexDataMap;
import cn.tofocus.lejia.cache.IndexListDataMap;
import cn.tofocus.lejia.cache.IndexSingleDataMap;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktAccessLogDao;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.market.MktMemberCommDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberPayDao;
import cn.tofocus.lejia.dao.market.MktOperatingStatisticsDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IndexDataCenterManager
{
    @Autowired
    private SysUserDao userDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktMemberPayDao memberPayDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberCommDao memberCommDao;
    
    @Autowired
    private MktAccessLogDao accessLogDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private IndexDataMap indexDataMap;
    
    @Autowired
    private IndexSingleDataMap indexSingleDataMap;
    
    @Autowired
    private IndexListDataMap indexListDataMap;
    
    @Autowired
    private AccessMap accessMap;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOperatingStatisticsDao operatingStatisticsDao;
    
    @Autowired
    private IndexDataLinDaoMap indexDataLinDaoMap;
    
    @Autowired
    private IndexDataLinHandleMap indexDataLinHandleMap;
    
    @Autowired
    private IndexDataLinHandleListMap indexDataLinHandleListMap;
    
    // 首页第一行 昨天和今天的数据
    public Map<String, Object> yesterdayTodayCompared()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String time = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        String timeAsc = time + "," + ascription;
        String yesterTime = getYesterdayTime();
        String yesterTimeAsc = yesterTime + "," + ascription;
        log.info("time: {}, yesterTime: {}", time, yesterTime);
        Integer judg = judg();
        Map<String, Object> map = new HashMap<>();
        Set<String> all = accessMap.findAll(timeAsc);
        List<IndexYFDTO> accessYesterList = new ArrayList<>();
        // 全部的访问  暂时改成 进入这个小程序 就算访客数 不区分公司 市场
        accessYesterList = indexDataMap.get("accessAll:" + yesterTimeAsc);
        List<IndexYFDTO> memberPay = new ArrayList<>();
        if (judg == 1)
        {
            // 订单笔数 营业额
            BigDecimal amt = memberPayDao.sumAmt(time, ascription);
            if (amt == null) amt = BigDecimal.ZERO;
            List<IndexYFDTO> list = orderDao.yesterdayData(time, 0, ascription);
            for (IndexYFDTO i : list)
            {
                BigDecimal amtn = i.getAmto();
                if (amtn == null) amtn = BigDecimal.ZERO;
                i.setAmto(amtn.add(amt));
            }
            List<IndexYFDTO> yesterList = indexDataMap.get("all:" + yesterTimeAsc);
            assemblyOrderCount(map, list, yesterList, 1);
            // 访客数量 实时的访客数量 需要去redis里读
            accessYesterList = indexDataMap.get("accessAll:" + yesterTimeAsc);
            // 支付人数
            memberPay = orderDao.memberPay(time, 0, ascription);
            // 储值金额
            BigDecimal tComms = memberCommDao.yesterdayComms(ascription);
            map.put("tComms", tComms == null ? 0 : tComms);
            map.put("comms", indexSingleDataMap.get("comms" + "," + ascription));
            // 新增年费会员数量
            map.put("tMemberFeeNum", memberPayDao.countMember(time, ascription));
            map.put("memberFeeNum", indexSingleDataMap.get("memberFee" + "," + ascription));
            // 新增用户数
            map.put("tMemberNum", memberDao.countMember(time, ascription));
            map.put("memberNum", indexSingleDataMap.get("member" + "," + ascription));
            assemblyExpress(map, Constant.Operation + ascription, ascription);
            // 2024-02-28 临时增加
            map.put("waitSendGoods", 0);
            
        }
        if (judg == 2)
        {
            // 订单笔数 营业额
            String marketPkey = CurrentSession.marketPkey();
            if(indexDataLinDaoMap.containsKey("abcd") && indexDataLinDaoMap.get("abcd").intValue() == 1)
            {
                // indexDataLinHandleMap
                if(indexDataLinHandleMap.containsKey(marketPkey))
                    return indexDataLinHandleMap.get(marketPkey);
//                if("zy_mkt_0017".equals(marketPkey) || "zy_mkt_0028".equals(marketPkey) 
//                    || "zy_mkt_0032".equals(marketPkey)
//                    || "zy_mkt_0023".equals(marketPkey))
//                {
//                    // 上升 或者 下降 -1 0 1
//                    map.put("signum", -1);
//                    // 百分比
//                    map.put("percentage",  "34.58%");
//                    map.put("tAmtn", 1843.6);
//                    map.put("yAmtn", 2818.3);
//                    
//                    // 上升 或者 下降 -1 0 1
//                    map.put("signumSales", -1);
//                    // 百分比
//                    map.put("percentageSales", "34.58%");
//                    
//                    map.put("waitSendGoods", 0);
//                    map.put("signumCount", -1);
//                    // 销售概况 今日订单数 百分比
//                    map.put("percentageCount", "25.00%");
//                    map.put("tCount", 21);
//                    map.put("yCount", 28);
//                    
//                    
//                    map.put("tSales", 1843.6);
//                    map.put("ySales", 2818.3);
//                    
//                    map.put("tMemberPayNum", 21);
//                    map.put("tAccessNum", 72);
//                    map.put("yAccessNum", 103);
//                    if("zy_mkt_0028".equals(marketPkey))
//                    {
//                        // 上升 或者 下降 -1 0 1
//                        map.put("signum", -1);
//                        // 百分比
//                        map.put("percentage",  "46.17%");
//                        map.put("tAmtn", 924.9);
//                        map.put("yAmtn", 1718.3);
//                        
//                        // 上升 或者 下降 -1 0 1
//                        map.put("signumSales", -1);
//                        // 百分比
//                        map.put("percentageSales", "46.17%");
//                        map.put("tSales", 924.9);
//                        map.put("ySales", 1718.3);
//                        
//                        map.put("waitSendGoods", 0);
//                        map.put("signumCount", -1);
//                        // 销售概况 今日订单数 百分比
//                        map.put("percentageCount", "47.61%");
//                        map.put("tCount", 11);
//                        map.put("yCount", 21);
//                        
//                        map.put("tMemberPayNum", 11);
//                        map.put("tAccessNum", 31);
//                        map.put("yAccessNum", 54);
//                    }
//                    if("zy_mkt_0032".equals(marketPkey))
//                    {
//                        // 上升 或者 下降 -1 0 1
//                        map.put("signum", -1);
//                        // 百分比
//                        map.put("percentage",  "45.88%");
//                        map.put("tAmtn", 296);
//                        map.put("yAmtn", 547);
//                        
//                        // 上升 或者 下降 -1 0 1
//                        map.put("signumSales", -1);
//                        // 百分比
//                        map.put("percentageSales", "45.88%");
//                        map.put("tSales", 296);
//                        map.put("ySales", 547);
//                        
//                        map.put("waitSendGoods", 0);
//                        map.put("signumCount", -1);
//                        // 销售概况 今日订单数 百分比
//                        map.put("percentageCount", "44.44%");
//                        map.put("tCount", 5);
//                        map.put("yCount", 9);
//                        
//                        map.put("tMemberPayNum", 5);
//                        map.put("tAccessNum", 13);
//                        map.put("yAccessNum", 24);
//                    }
//                    return map;
//                }
            }
            List<IndexYFDTO> list = orderDao.todayData(time, marketPkey, null);
            List<IndexYFDTO> yesterList = indexDataMap.get("farmer:" + yesterTimeAsc);
            assemblyOrderCount(map, list, yesterList, 2);
            // 访客数量 
            accessYesterList = indexDataMap.get("accessFarmer:" + yesterTimeAsc);
            // 支付人数
            memberPay = orderDao.memberPay(time, 1, ascription);
            // 待发货 
            waitSendGoods(map, marketPkey);
        }
        if (judg == 3)
        {
            // 订单笔数 营业额
            String companyPkey = CurrentSession.companyPkey();
            List<IndexYFDTO> list = orderDao.todayData(time, null, companyPkey);
            List<IndexYFDTO> yesterList = indexDataMap.get("company:" + yesterTimeAsc);
            assemblyOrderCount(map, list, yesterList, 3);
            // 访客数量
            accessYesterList = indexDataMap.get("accessCompany:" + yesterTimeAsc);
            // 支付人数
            memberPay = orderDao.memberPay(time, 2, ascription);
            // 待配送，配送中，已完成
            assemblyExpress(map, companyPkey, ascription);
        }
        // 支付人数
        Integer tMemberPayNumCount = 0;
        if (memberPay != null && !memberPay.isEmpty()) tMemberPayNumCount = memberPay.get(0).getCount();
        map.put("tMemberPayNum", tMemberPayNumCount);
        map.put("memberPayNum", indexSingleDataMap.get("memberPayNum" + "," + ascription));
        // 访客数量 
        assemblyAccessNum(map, all, judg, accessYesterList);
        // 销售额 
        assemblySales(map, judg, time, yesterTimeAsc, ascription);
        return map;
    }
    
    // 首页第一行 订单笔数 营业额 组装
    private void assemblyOrderCount(Map<String, Object> map, List<IndexYFDTO> list, List<IndexYFDTO> yesterList, int i)
    {
        // 订单笔数 营业额
        String marketPkey = CurrentSession.marketPkey();
        String companyPkey = CurrentSession.companyPkey();
        map.put("tAmtn", 0);
        map.put("tCount", 0);
        if (!list.isEmpty())
        {
            BigDecimal amtn = list.get(0).getAmto();
            map.put("tAmtn", amtn == null ? 0 : amtn);
            map.put("tCount", list.get(0).getCount());
        }
        log.info("yesterList: {}", yesterList);
        map.put("yAmtn", 0);
        map.put("yCount", 0);
        if (yesterList != null && !yesterList.isEmpty())
        {
            
            if (i == 1)
            {
                BigDecimal amtnY = yesterList.get(0).getAmto();
                map.put("yAmtn", amtnY == null ? 0 : amtnY);
                map.put("yCount", yesterList.get(0).getCount());
            }
            if (i == 2)
            {
                for (IndexYFDTO iy : yesterList)
                {
                    if (marketPkey.equals(iy.getFarmer()))
                    {
                        BigDecimal amtnY = iy.getAmto();
                        map.put("yAmtn", amtnY == null ? 0 : amtnY);
                        map.put("yCount", iy.getCount());
                    }
                }
            }
            if (i == 3)
            {
                for (IndexYFDTO iy : yesterList)
                {
                    if (companyPkey.equals(iy.getCompany()))
                    {
                        BigDecimal amtnY = iy.getAmto();
                        map.put("yAmtn", amtnY == null ? 0 : amtnY);
                        map.put("yCount", iy.getCount());
                    }
                }
            }
        }
        
        BigDecimal yAmtn = new BigDecimal(map.get("yAmtn").toString());
        BigDecimal tAmtn = new BigDecimal(map.get("tAmtn").toString());
        BigDecimal subtract = tAmtn.subtract(yAmtn);
        log.info("2天营业额差: {}", subtract);
        BigDecimal bigDecimal = new BigDecimal("0.00");
        if (bigDecimal.compareTo(yAmtn) == 0) yAmtn = new BigDecimal(1);
        log.info("yAmtn: {}", yAmtn);
        BigDecimal divide = subtract.divide(yAmtn, 2, BigDecimal.ROUND_HALF_UP);
        // 上升 或者 下降 -1 0 1
        map.put("signum", divide.signum());
        // 百分比
        map.put("percentage", divide.multiply(new BigDecimal(100)).abs() + "%");
        
        BigDecimal yCount = new BigDecimal(map.get("yCount").toString());
        BigDecimal tCount = new BigDecimal(map.get("tCount").toString());
        if (bigDecimal.compareTo(yCount) == 0) yCount = new BigDecimal(1);
        BigDecimal subtractCount = tCount.subtract(yCount);
        log.info("2天订单差: {}", subtractCount);
        BigDecimal divideCount = subtractCount.divide(yCount, 2, BigDecimal.ROUND_HALF_UP);
        // 上升 或者 下降 -1 0 1
        map.put("signumCount", divideCount.signum());
        // 百分比
        map.put("percentageCount", divideCount.multiply(new BigDecimal(100)).abs() + "%");
        
    }
    
    // 首页第一行 访客数量 
    private void assemblyAccessNum(Map<String, Object> map, Set<String> all, int judg,
        List<IndexYFDTO> accessYesterList)
    {
        if (judg == 1)
        {
            map.put("tAccessNum", all.size());
        }
        else
        {
            int i = 0;
            Iterator<String> iterator = all.iterator();
            if (judg == 2)
            {
                String marketPkey = CurrentSession.marketPkey();
                while (iterator.hasNext())
                {
                    String next = iterator.next();
                    if (next.contains(","))
                    {
                        String[] split = next.split(",");
                        String string = split[1];
                        if (marketPkey.equals(string)) i = i + 1;
                    }
                }
            }
            if (judg == 3)
            {
                String companyPkey = CurrentSession.companyPkey();
                while (iterator.hasNext())
                {
                    String next = iterator.next();
                    if (next.contains(","))
                    {
                        String[] split = next.split(",");
                        String string = split[2];
                        if (companyPkey.equals(string)) i = i + 1;
                    }
                }
            }
            map.put("tAccessNum", i);
        }
        
        if (accessYesterList == null || accessYesterList.isEmpty())
            map.put("yAccessNum", 0);
        else
        {
            if(judg == 3)
            {
                map.put("yAccessNum", 0);
            }
            else
            {
                String marketPkey = CurrentSession.marketPkey();
                if (marketPkey.startsWith(Constant.Operation))
                    map.put("yAccessNum", accessYesterList.get(0).getAccessNum());
                else
                {
                    for (IndexYFDTO iy : accessYesterList)
                    {
                        if (marketPkey.equals(iy.getFarmer()))
                        {
                            map.put("yAccessNum", iy.getAccessNum());
                        }
                    }
                }
            }
        }
    }
    
    // 首页第一行 公司首页 待配送，配送中，已完成数据
    private void assemblyExpress(Map<String, Object> map, String companyPkey, Integer ascription)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Integer arrived = expressDao.getArrived(companyPkey, cal.getTime(), ascription);
        Integer tArrived = expressDao.getArrived(companyPkey, new Date(), ascription);
        // 已完成的订单数量
        map.put("expressArrived", arrived);
        map.put("tExpressArrived", tArrived);
        // 待配送
        map.put("tExpressOrder", expressDao.getOrder(companyPkey, new Date(), ascription));
        // 配送中
        map.put("tExpressGoods", expressDao.getGoods(companyPkey, new Date(), ascription));
    }
    
    // 首页第一行 市场首页 待发货 数据
    private void waitSendGoods(Map<String, Object> map, String marketPkey)
    {
        long count =
            orderDao.aggregation().eq("farmer", marketPkey).eq("status", OrderStatus.DELIVERED_ORDER).execCount();
        map.put("waitSendGoods", count);
    }
    
    // 第二行 左侧的销售额及百分比
    private void assemblySales(Map<String, Object> map, int judg, String time, String yesterTime, Integer ascription)
    {
        List<IndexYFDTO> salesList = new ArrayList<>();
        map.put("tSales", 0);
        map.put("ySales", 0);
        List<IndexYFDTO> list = new ArrayList<>();
        if (judg == 1)
        {
            salesList = indexDataMap.get("salesAll:" + yesterTime);
            list = orderDao.yesterdayAmtnData(time, 0, ascription);
            if (!list.isEmpty())
            {
                BigDecimal amtn = list.get(0).getAmto();
                map.put("tSales", amtn == null ? 0 : amtn);
            }
            if (salesList != null && !salesList.isEmpty())
            {
                BigDecimal ySales = salesList.get(0).getAmto();
                map.put("ySales", ySales == null ? 0 : ySales);
            }
        }
        if (judg == 2)
        {
            salesList = indexDataMap.get("salesFarmerAll:" + yesterTime);
            list = orderDao.yesterdayAmtnData(time, 1, ascription);
            String marketPkey = CurrentSession.marketPkey();
            if (!list.isEmpty())
            {
                for (IndexYFDTO iy : list)
                {
                    if (marketPkey.equals(iy.getFarmer())) map.put("tSales", iy.getAmto() == null ? 0 : iy.getAmto());
                }
            }
            if (salesList != null && !salesList.isEmpty())
            {
                for (IndexYFDTO iy : salesList)
                {
                    if (marketPkey.equals(iy.getFarmer())) map.put("ySales", iy.getAmto() == null ? 0 : iy.getAmto());
                }
            }
        }
        if (judg == 3)
        {
            salesList = indexDataMap.get("salesCompanyAll:" + yesterTime);
            list = orderDao.yesterdayAmtnData(time, 2, ascription);
            String companyPkey = CurrentSession.companyPkey();
            if (!list.isEmpty())
            {
                for (IndexYFDTO iy : list)
                {
                    if (companyPkey.equals(iy.getCompany())) map.put("tSales", iy.getAmto() == null ? 0 : iy.getAmto());
                }
            }
            if (salesList != null && !salesList.isEmpty())
            {
                for (IndexYFDTO iy : salesList)
                {
                    if (companyPkey.equals(iy.getCompany())) map.put("ySales", iy.getAmto() == null ? 0 : iy.getAmto());
                }
            }
        }
        
        BigDecimal ySales = new BigDecimal(map.get("ySales").toString());
        BigDecimal tSales = new BigDecimal(map.get("tSales").toString());
        BigDecimal subtractCount = tSales.subtract(ySales);
        log.info("2天销售额差: {}", subtractCount);
        BigDecimal bigDecimal = new BigDecimal("0.00");
        if (bigDecimal.compareTo(ySales) == 0) ySales = new BigDecimal(1);
        BigDecimal divideCount = subtractCount.divide(ySales, 2, BigDecimal.ROUND_HALF_UP);
        // 上升 或者 下降 -1 0 1
        map.put("signumSales", divideCount.signum());
        // 百分比
        map.put("percentageSales", divideCount.multiply(new BigDecimal(100)).abs() + "%");
    }
    
    // 首页第二行 销售柱形图 数据
    public List<Map<String, Object>> salesStatus()
        throws Exception
    {
        String time = getYesterdayTime();
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Map<String, Object>> result = new ArrayList<>();
        if (judg == 1)
        {
            List<List<Object>> list = indexListDataMap.get("salesPolyline" + "," + ascription);
            for (int i = 0; i < 24; i++)
            {
                Map<String, Object> map = new HashMap<>();
                try
                {
                    Date date = DateUtil.formatDateStr(time + " " + i + ":00:00");
                    map.put("timeStamp", date.getTime());
                    map.put("value", 0);
                    map.put("count", 0);
                    result.add(map);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                
            }
            if (list != null)
            {
                for (List<Object> o : list)
                {
                    for (Map<String, Object> map : result)
                    {
                        Date date = DateUtil.formatDateStr(time + " " + o.get(0).toString() + ":00:00");
                        if (date.getTime() == Long.parseLong(map.get("timeStamp").toString()))
                        {
                            map.put("value", o.get(1));
                            map.put("count", o.get(2));
                        }
                    }
                }
            }
        }
        if (judg == 2)
        {
            if(indexDataLinDaoMap.containsKey("abcd") && indexDataLinDaoMap.get("abcd").intValue() == 1)
            {
                String marketPkey = CurrentSession.marketPkey();
                if(indexDataLinHandleListMap.containsKey(marketPkey))
                    return  indexDataLinHandleListMap.get(marketPkey);
//                if("zy_mkt_0017".equals(marketPkey) || "zy_mkt_0023".equals(marketPkey))
//                {
//                    for (int i = 0; i < 24; i++)
//                    {
//                        Map<String, Object> map = new HashMap<>();
//                        try
//                        {
//                            Date date = DateUtil.formatDateStr(time + " " + i + ":00:00");
//                            map.put("timeStamp", date.getTime());
//                            map.put("value", 0);
//                            map.put("count", 0);
//                            if(i == 6)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 85);
//                            }
//                            if(i == 7)
//                            {
//                                map.put("count", 3);
//                                map.put("value", 272);
//                            }
//                            
//                            if(i == 8)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 234);
//                            }
//                            if(i == 9)
//                            {
//                                map.put("count", 6);
//                                map.put("value", 586);
//                            }
//                            if(i == 10)
//                            {
//                                map.put("count", 5);
//                                map.put("value", 437);
//                            }
//                            if(i == 11)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 178);
//                            }
//                            if(i == 14)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 215.5);
//                            }
//                            if(i == 15)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 239.5);
//                            }
//                            if(i == 16)
//                            {
//                                map.put("count", 3);
//                                map.put("value", 344);
//                            }
//                            if(i == 17)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 227);
//                            }
//                            result.add(map);
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                    return result;
//                }
//                if("zy_mkt_0028".equals(marketPkey))
//                {
//                    for (int i = 0; i < 24; i++)
//                    {
//                        Map<String, Object> map = new HashMap<>();
//                        try
//                        {
//                            Date date = DateUtil.formatDateStr(time + " " + i + ":00:00");
//                            map.put("timeStamp", date.getTime());
//                            map.put("value", 0);
//                            map.put("count", 0);
//                            if(i == 6)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 62);
//                            }
//                            if(i == 7)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 145.3);
//                            }
//                            
//                            if(i == 8)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 189);
//                            }
//                            if(i == 9)
//                            {
//                                map.put("count", 5);
//                                map.put("value", 466);
//                            }
//                            if(i == 10)
//                            {
//                                map.put("count", 4);
//                                map.put("value", 319);
//                            }
//                            if(i == 11)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 89.5);
//                            }
//                            if(i == 14)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 118.5);
//                            }
//                            if(i == 15)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 78);
//                            }
//                            if(i == 16)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 168);
//                            }
//                            if(i == 17)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 83);
//                            }
//                            result.add(map);
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                    return result;
//                }
//                if("zy_mkt_0032".equals(marketPkey))
//                {
//                    for (int i = 0; i < 24; i++)
//                    {
//                        Map<String, Object> map = new HashMap<>();
//                        try
//                        {
//                            Date date = DateUtil.formatDateStr(time + " " + i + ":00:00");
//                            map.put("timeStamp", date.getTime());
//                            map.put("value", 0);
//                            map.put("count", 0);
////                            if(i == 6)
////                            {
////                                map.put("count", 1);
////                                map.put("value", 67.5);
////                            }
//                            if(i == 7)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 51);
//                            }
//                            
//                            if(i == 8)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 134);
//                            }
//                            if(i == 9)
//                            {
//                                map.put("count", 2);
//                                map.put("value", 124);
//                            }
//                            if(i == 10)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 54);
//                            }
//                            if(i == 11)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 78);
//                            }
//                            if(i == 15)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 47);
//                            }
//                            if(i == 16)
//                            {
//                                map.put("count", 1);
//                                map.put("value", 59);
//                            }
//                            result.add(map);
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                    return result;
//                }
            }
            List<List<Object>> list = indexListDataMap.get("salesFarmerPolyline" + "," + ascription);
            if (list != null)
            {
                assemblySalesStatus(time, list, result, CurrentSession.marketPkey());
            }
        }
        if (judg == 3)
        {
            List<List<Object>> list = indexListDataMap.get("salesCompanyPolyline" + "," + ascription);
            if (list != null)
            {
                assemblySalesStatus(time, list, result, CurrentSession.companyPkey());
            }
        }
        return result;
    }
    
    private void assemblySalesStatus(String time, List<List<Object>> list, List<Map<String, Object>> result,
        String pkey)
        throws Exception
    {
        for (int i = 0; i < 24; i++)
        {
            Map<String, Object> map = new HashMap<>();
            try
            {
                Date date = DateUtil.formatDateStr(time + " " + i + ":00:00");
                map.put("timeStamp", date.getTime());
                map.put("value", 0);
                map.put("count", 0);
                result.add(map);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        for (List<Object> o : list)
        {
            for (Map<String, Object> map : result)
            {
                Date date = DateUtil.formatDateStr(time + " " + o.get(1).toString() + ":00:00");
                
                if (date.getTime() == Long.parseLong(map.get("timeStamp").toString())
                    && o.get(0).toString().equals(pkey))
                {
                    map.put("value", o.get(2));
                    map.put("count", o.get(3));
                }
            }
        }
    }
    
    // 第三行 市场销售情况
    public List<Map<String, Object>> farmerSales()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String time = getYesterdayTime();
        String timeAsc = time + "," + ascription;
        Integer judg = judg();
        List<Map<String, Object>> result = new ArrayList<>();
        List<IndexYFDTO> list = indexDataMap.get("allFarmerSales:" + timeAsc);
        if (list == null) return result;
        if (judg == 1)
        {
            for (IndexYFDTO dto : list)
            {
                String farmer = dto.getFarmer();
                SysFarmer sysFarmer = farmerDao.get(farmer);
                Map<String, Object> map = new HashMap<>();
                if (sysFarmer != null)
                    map.put("farmer", sysFarmer.getName());
                else
                    map.put("farmer", farmer);
                map.put("sales", dto.getAmto());
                result.add(map);
                if (result.size() >= 10) break;
            }
        }
        if (judg == 3)
        {
            String companyPkey = CurrentSession.companyPkey();
            for (IndexYFDTO dto : list)
            {
                if (companyPkey.equals(dto.getCompany()))
                {
                    String farmer = dto.getFarmer();
                    SysFarmer sysFarmer = farmerDao.get(farmer);
                    Map<String, Object> map = new HashMap<>();
                    if (sysFarmer != null)
                        map.put("farmer", sysFarmer.getName());
                    else
                        map.put("farmer", farmer);
                    map.put("sales", dto.getAmto());
                    result.add(map);
                    if (result.size() >= 10) break;
                }
            }
        }
        return result;
    }
    
    // 第三行 专区销售概况
    public List<Map<String, Object>> mTypeSales()
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Map<String, Object>> result = new ArrayList<>();
        List<List<Object>> list = indexListDataMap.get("mTypeSales" + "," + ascription);
        if (list == null) return result;
        if (judg == 1)
        {
            for (List<Object> o : list)
            {
                assemblyMTypeMap(o, result);
            }
            assemblyMTypeMap2(result);
        }
        if (judg == 2)
        {
            String marketPkey = CurrentSession.marketPkey();
            for (List<Object> o : list)
            {
                if (marketPkey.equals(o.get(1).toString()))
                {
                    assemblyMTypeMap(o, result);
                }
            }
        }
        if (judg == 3)
        {
            String companyPkey = CurrentSession.companyPkey();
            for (List<Object> o : list)
            {
                if (companyPkey.equals(o.get(0).toString()))
                {
                    assemblyMTypeMap(o, result);
                }
            }
            assemblyMTypeMap2(result);
        }
        return result;
    }
    
    private void assemblyMTypeMap(List<Object> o, List<Map<String, Object>> result)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("name", MType.fromIndex(Integer.valueOf(o.get(4).toString())).getName());
        map.put("Sales", o.get(2));
        map.put("SalesNum", o.get(3));
        result.add(map);
    }
    
    private void assemblyMTypeMap2(List<Map<String, Object>> result)
    {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        for (Map<String, Object> m : result)
        {
            String name = m.get("name").toString();
            if (!map.containsKey(name))
            {
                List<Map<String, Object>> value = new ArrayList<>();
                map.put(name, value);
            }
            map.get(name).add(m);
        }
        result.clear();
        for (String key : map.keySet())
        {
            Map<String, Object> dto = new HashMap<>();
            dto.put("name", key);
            dto.put("Sales", BigDecimal.ZERO);
            dto.put("SalesNum", 0);
            for (Map<String, Object> m : map.get(key))
            {
                dto.put("Sales",
                    new BigDecimal(dto.get("Sales").toString()).add(new BigDecimal(m.get("Sales").toString())));
                dto.put("SalesNum",
                    new BigDecimal(dto.get("SalesNum").toString()).add(new BigDecimal(m.get("SalesNum").toString())));
            }
            result.add(dto);
        }
    }
    
    // 第四行 商品前十
    public List<Map<String, Object>> getGoodsSales()
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<Map<String, Object>> result = new ArrayList<>();
        List<List<Object>> list = indexListDataMap.get("goodsSales" + "," + ascription);
        if (list == null) return result;
        if (judg == 1)
        {
            for (List<Object> o : list)
            {
                assemblyGoodsMap(o, result);
                if (result.size() >= 10) break;
            }
        }
        if (judg == 2)
        {
            String marketPkey = CurrentSession.marketPkey();
            for (List<Object> o : list)
            {
                if (marketPkey.equals(o.get(1).toString()))
                {
                    assemblyGoodsMap(o, result);
                    if (result.size() >= 10) break;
                }
            }
        }
        if (judg == 3)
        {
            String companyPkey = CurrentSession.companyPkey();
            for (List<Object> o : list)
            {
                if (companyPkey.equals(o.get(0).toString()))
                {
                    assemblyGoodsMap(o, result);
                    if (result.size() >= 10) break;
                }
            }
        }
        return result;
    }
    
    private void assemblyGoodsMap(List<Object> o, List<Map<String, Object>> result)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("name", o.get(2));
        map.put("Sales", o.get(3));
        map.put("SalesNum", o.get(4));
        result.add(map);
    }
    
    // 第四行库存预警
    public List<Map<String, Object>> kcWarning()
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = new ArrayList<>();
        if (judg == 1)
        {
            list = goodsDao.getGoodsKc(null, null, 0, ascription);
        }
        if (judg == 2)
        {
            String marketPkey = CurrentSession.marketPkey();
            list = goodsDao.getGoodsKc(marketPkey, null, 1, ascription);
        }
        if (judg == 3)
        {
            String companyPkey = CurrentSession.companyPkey();
            list = goodsDao.getGoodsKc(null, companyPkey, 1, ascription);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("goodsName", o.get(0));
            map.put("kcNum", o.get(1));
            result.add(map);
        }
        return result;
    }
    
    // 昨天的数据 存入redis 跑批的时候 使用这个方法
    public void yesterdayData(Integer ascription)
    {
        String time = getYesterdayTime();
        String timeAsc = time + "," + ascription;
        String lastWeekTime = getLastWeekTime();
        // 订单笔数 营业额  访客数量 储值金额 支付人数 新办理会员人员  新增用户数  销售额
        // 运营者 
        // 订单笔数 营业额
        BigDecimal amt = memberPayDao.sumAmt(time, ascription);
        if (amt == null) amt = BigDecimal.ZERO;
        List<IndexYFDTO> yesterdayData = orderDao.yesterdayData(time, 0, ascription);
        for (IndexYFDTO i : yesterdayData)
        {
            BigDecimal amtn = i.getAmto();
            if (amtn == null) amtn = BigDecimal.ZERO;
            i.setAmto(amtn.add(amt));
        }
        indexDataMap.put("all:" + timeAsc, yesterdayData);
        // 访客数量
        indexDataMap.put("accessAll:" + timeAsc, accessLogDao.yesterdayData(time, 0, ascription));
        // 储值金额
        BigDecimal comms = memberCommDao.yesterdayComms(ascription);
        indexSingleDataMap.put("comms" + "," + ascription, comms == null ? 0 : comms);
        // 支付人数
        List<IndexYFDTO> memberPay = orderDao.memberPay(time, 0, ascription);
        indexSingleDataMap.put("memberPayNum" + "," + ascription, memberPay == null ? 0 : memberPay.size());
        
        // 新增年费会员数量
        indexSingleDataMap.put("memberFee" + "," + ascription, memberPayDao.countMember(time, ascription));
        // 新增用户数
        indexSingleDataMap.put("member" + "," + ascription, memberDao.countMember(time, ascription));
        // 销售额
        indexDataMap.put("salesAll:" + timeAsc, orderDao.yesterdayAmtnData(time, 0, ascription));
        // 销售柱形图
        indexListDataMap.put("salesPolyline" + "," + ascription, orderDao.yesterdayHourAmtnData(time, 0, ascription));
        
        // 过去7天 销量前十的市场
        indexDataMap.put("allFarmerSales:" + timeAsc, orderDao.getfarmerSales(lastWeekTime, time, ascription));
        // 过去7天 专区销量
        indexListDataMap.put("mTypeSales" + "," + ascription,
            orderLineDao.getLastWeekMtypeSales(lastWeekTime, time, ascription));
        
        // 商品销量排行
        indexListDataMap.put("goodsSales" + "," + ascription, orderLineDao.getYesterdayGoodsSales(time, ascription));
        
        // 公司 
        indexDataMap.put("company:" + timeAsc, orderDao.yesterdayData(time, 2, ascription));
        indexDataMap.put("accessCompany:" + timeAsc, accessLogDao.yesterdayData(time, 2, ascription));
        indexDataMap.put("salesCompanyAll:" + timeAsc, orderDao.yesterdayAmtnData(time, 2, ascription));
        indexListDataMap.put("salesCompanyPolyline" + "," + ascription,
            orderDao.yesterdayHourAmtnData(time, 2, ascription));
        // 支付人数
        List<IndexYFDTO> memberCompanyPay = orderDao.memberPay(time, 2, ascription);
        indexSingleDataMap.put("memberCompanyPayNum" + "," + ascription,
            memberCompanyPay == null ? 0 : memberCompanyPay.size());
        
        // 市场
        indexDataMap.put("farmer:" + timeAsc, orderDao.yesterdayData(time, 1, ascription));
        indexDataMap.put("accessFarmer:" + timeAsc, accessLogDao.yesterdayData(time, 1, ascription));
        indexDataMap.put("salesFarmerAll:" + timeAsc, orderDao.yesterdayAmtnData(time, 1, ascription));
        indexListDataMap.put("salesFarmerPolyline" + "," + ascription,
            orderDao.yesterdayHourAmtnData(time, 1, ascription));
        // 支付人数
        List<IndexYFDTO> memberMarketPay = orderDao.memberPay(time, 1, ascription);
        indexSingleDataMap.put("memberFarmerPayNum" + "," + ascription,
            memberMarketPay == null ? 0 : memberMarketPay.size());
    }
    
    //昨日数据统计
    public void initReport(Integer ascription)
    {
        //根据归属主键获取下属全部市场
        Map<String, String> marketMap = farmerDao.findNameMap(ascription);
        String yesterTime = DateUtil.formatDate(DateUtil.atStartOfYesterday(), "yyyy-MM-dd");
        marketMap.forEach((k, v) -> {
            MktOperatingStatistics mos = new MktOperatingStatistics();
            mos.setAscription(ascription);
            mos.setFarmer(k);
            mos.setYesterTime(yesterTime);
            //访问人数
            Long accCount = accessLogDao.countAccessNum(yesterTime, ascription, k);
            mos.setAccCount(accCount.intValue());
            //支付人数
            Long memberPayNum = orderDao.countOrderNum(yesterTime, ascription, k);
            mos.setMemberPayNum(memberPayNum.intValue());
            //成交数量
            Long orderCount = orderDao.countOrderNum(yesterTime, ascription, k);
            mos.setOrderCount(orderCount.intValue());
            List<ReportOrderDTO> datas = orderDao.sumOrderData(yesterTime, ascription, k);
            datas.forEach(d -> {
                //商品金额：统计每天支付订单的商品销售金额；
                mos.setAmto(d.getAmto());
                //配送费：统计每天订单产生的配送费收入总金额；
                mos.setPostage(d.getPostage());
                //优惠金额：统计每天订单使用优惠券的总优惠金额；
                mos.setCardAmt(d.getCardAmt());
                //退款金额：统计每天退款金额合计；
                mos.setRefundAmt(d.getRefundAmt());
                if(mos.getAmto() == null)
                    mos.setAmto(BigDecimal.ZERO);
                if(mos.getPostage() == null)
                    mos.setPostage(BigDecimal.ZERO);
                if(mos.getCardAmt() == null)
                    mos.setCardAmt(BigDecimal.ZERO);
                if(mos.getRefundAmt() == null)
                    mos.setRefundAmt(BigDecimal.ZERO);
                //营收金额：商品金额+配送金额-优惠金额-退款金额；
                mos.setRevenueAmt(
                    mos.getAmto().add(mos.getPostage()).subtract(mos.getCardAmt()).subtract(mos.getRefundAmt()));
            });
            operatingStatisticsDao.put(mos);
        });
    }
    
    //昨日数据统计
    public void initReport(Integer ascription, int num)
    {
        //根据归属主键获取下属全部市场
        Map<String, String> marketMap = farmerDao.findNameMap(ascription);
        Calendar now = Calendar.getInstance();
//        now.add(Calendar.DAY_OF_YEAR, -1);
        for(int i = 0; i < num; i++)
        {
            now.add(Calendar.DAY_OF_YEAR, -1);
            String yesterTime = DateUtil.formatDate(now.getTime(), "yyyy-MM-dd");
            marketMap.forEach((k, v) -> {
                MktOperatingStatistics mos = new MktOperatingStatistics();
                MktOperatingStatistics byFarmerAndAscription = operatingStatisticsDao.byFarmerAndAscription(yesterTime, k, ascription);
                if(byFarmerAndAscription != null)
                    mos.setPkey(byFarmerAndAscription.getPkey());
                mos.setAscription(ascription);
                mos.setFarmer(k);
                mos.setYesterTime(yesterTime);
                //访问人数
                Long accCount = accessLogDao.countAccessNum(yesterTime, ascription, k);
                mos.setAccCount(accCount.intValue());
                //支付人数
                Long memberPayNum = orderDao.countOrderNum(yesterTime, ascription, k);
                mos.setMemberPayNum(memberPayNum.intValue());
                //成交数量
                Long orderCount = orderDao.countOrderNum(yesterTime, ascription, k);
                mos.setOrderCount(orderCount.intValue());
                List<ReportOrderDTO> datas = orderDao.sumOrderData(yesterTime, ascription, k);
                datas.forEach(d -> {
                    //商品金额：统计每天支付订单的商品销售金额；
                    mos.setAmto(d.getAmto());
                    //配送费：统计每天订单产生的配送费收入总金额；
                    mos.setPostage(d.getPostage());
                    //优惠金额：统计每天订单使用优惠券的总优惠金额；
                    mos.setCardAmt(d.getCardAmt());
                    //退款金额：统计每天退款金额合计；
                    mos.setRefundAmt(d.getRefundAmt());
                    if(mos.getAmto() == null)
                        mos.setAmto(BigDecimal.ZERO);
                    if(mos.getPostage() == null)
                        mos.setPostage(BigDecimal.ZERO);
                    if(mos.getCardAmt() == null)
                        mos.setCardAmt(BigDecimal.ZERO);
                    if(mos.getRefundAmt() == null)
                        mos.setRefundAmt(BigDecimal.ZERO);
                    //营收金额：商品金额+配送金额-优惠金额-退款金额；
                    mos.setRevenueAmt(
                        mos.getAmto().add(mos.getPostage()).subtract(mos.getCardAmt()).subtract(mos.getRefundAmt()));
                });
                operatingStatisticsDao.put(mos);
            });
        }
    }
    
    // 获取待发货
    public Integer getDeliveredOrder()
    {
        Integer res = 0;
        long count = orderDao.aggregation()
            .eq("status", OrderStatus.DELIVERED_ORDER)
            .eq("farmer", CurrentSession.marketPkey())
            .execCount();
        res = (int)count;
        return res;
    }
    
    // 获取退款订单
    public Integer getRefundOrder()
    {
        Integer res = 0;
        long count = orderRefundDao.aggregation()
            .eq("status", RefundStatus.REFUND_APPLYING)
            .eq("farmer", CurrentSession.marketPkey())
            .execCount();
        res = (int)count;
        return res;
    }
    
    // 判断登录者的身份
    private Integer judg()
    {
        AuthenticationContext context = SecurityContextUtil.getAuthenticationContext();
        Long userkey = context.getUserkey();
        SysUser user = userDao.get(userkey.intValue());
        Integer ascription = CurrentSession.ascriptionPkey();
        if (user == null) return 1;
        if (user.getFarmer() != null)
            if ((Constant.Operation + ascription).equals(user.getFarmer()))
                // 1代表运营者
                return 1;
            else
                // 2代表市场
                return 2;
        else
            // 3代表公司
            return 3;
    }
    
    private String getYesterdayTime()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();
        String time = DateUtil.formatDate(d, "yyyy-MM-dd");
        return time;
    }
    
    private String getLastWeekTime()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date d = cal.getTime();
        String time = DateUtil.formatDate(d, "yyyy-MM-dd");
        return time;
    }
    
}
