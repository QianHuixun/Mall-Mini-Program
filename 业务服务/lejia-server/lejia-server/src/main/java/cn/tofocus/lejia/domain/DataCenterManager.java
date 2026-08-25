package cn.tofocus.lejia.domain;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.prod;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.DateUtils;
import com.google.common.collect.Lists;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.join.db.SelectOps;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.data.SpecialAreaOnPage;
import cn.tofocus.lejia.bean.dto.goods.GoodsLineSum;
import cn.tofocus.lejia.bean.dto.goods.GoodsLineSummary;
import cn.tofocus.lejia.bean.dto.market.CommsDetailOnPage;
import cn.tofocus.lejia.bean.dto.market.MktSupplierSaleSummary;
import cn.tofocus.lejia.bean.dto.order.MktGoodsOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.order.MktGoodsOrderLineSummary;
import cn.tofocus.lejia.bean.dto.order.MktSupplierOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.sys.FarmerOption;
import cn.tofocus.lejia.bean.entity.market.*;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPointLineDao;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataCenterManager
{
    @Autowired
    private SysUserDao userDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private SysFarmerConfigDao farmerConfigDao;
    
    @Autowired
    private MktDrawWinDao drawWinDao;
    
    @Autowired
    private MktMemberPayDao memberPayDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private MktVendorPointLineDao vendorPointLineDao;
    
    @Autowired
    private MktLogisticsDao logisticsDao;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private MktMemberCommLineDao memberCommLineDao;
    
    @Autowired
    private MktAccessLogDao accessLogDao;

    @Autowired
    private MktSupplierDao supplierDao;

    @Autowired
    private SysAscriptionDao ascriptionDao;

    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;

    @Autowired
    private MktOrderTagDao orderTagDao;
    
    private static Integer FIXED_PAGE = 0;
    
    private static Integer FIXED_PAGESIZE = 10000;
    
    // 各专区报表
    public PageResult<SpecialAreaOnPage> mTypeData(String marketPkey, String companyPkey, String startTime,
        String endTime, int page, int pagesize)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (StringUtils.isBlank(marketPkey))
        {
            marketPkey = null;
            if (!(Constant.Operation + ascription).equals(CurrentSession.marketPkey())) marketPkey = CurrentSession.marketPkey();
        }
        if (StringUtils.isBlank(companyPkey))
        {
            companyPkey = null;
            if (!(Constant.Operation + ascription).equals(CurrentSession.companyPkey())) companyPkey = CurrentSession.companyPkey();
        }
        
        AggregationBuilder<Integer, MktOrder> builder = orderDao.aggregation()
            .page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .sum("amto", "Sales")
            .count("orderType", "SalesNum")
            .groupby("orderType", "orderType")
            .notIn("status",
                OrderStatus.UNPAID_ORDER,
                OrderStatus.VOID_ORDER,
                OrderStatus.REFUNDED_ORDER,
                OrderStatus.REFUND_APPLICATION_ORDER);
        if (StringUtils.isNotBlank(companyPkey)) builder.eq("company", companyPkey);
        if (StringUtils.isNotBlank(marketPkey)) builder.eq("farmer", marketPkey);
        if (StringUtils.isNotBlank(startTime))
        {
            if (StringUtils.isBlank(endTime))
                endTime = "2100-01-01";
            else
                endTime = endTime + " 23:59:59";
        }
        else
            startTime = null;
        if (StringUtils.isNotBlank(startTime)) builder.between("createdTime", startTime, endTime);
        PageResult<SpecialAreaOnPage> result = builder.exec(SpecialAreaOnPage.class);
        return result;
    }
    
    // 各商品报表
    public PageResult<Map<String, Object>> goodsData(String marketPkey, String companyPkey, String startTime,
        String endTime, int page, int pagesize)
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = new ArrayList<>();
        if (StringUtils.isBlank(marketPkey)) marketPkey = null;
        if (StringUtils.isBlank(companyPkey)) companyPkey = null;
        Integer size = null;
        if (judg == 1)
        {
            list = orderLineDao.getGoodsSales(marketPkey, companyPkey, startTime, endTime, page, pagesize, ascription);
            size = orderLineDao.getGoodsSales(marketPkey, companyPkey, startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                .size();
        }
        if (judg == 2)
        {
            list = orderLineDao.getGoodsSales(CurrentSession
                .marketPkey(), CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size =
                orderLineDao
                    .getGoodsSales(CurrentSession
                        .marketPkey(), CurrentSession.companyPkey(), startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                    .size();
        }
        if (judg == 3)
        {
            list = orderLineDao
                .getGoodsSales(marketPkey, CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size = orderLineDao
                .getGoodsSales(marketPkey, CurrentSession.companyPkey(), startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                .size();
        }
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("Sales", o.get(1));
            map.put("SalesNum", o.get(2));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(size);
        result.setContent(content);
        return result;
    }
    
    // 各商品分析
    public List<Map<String, Object>> goodsAnalysis(Integer goodsPkey, String startTime, String endTime)
    {
        List<List<Object>> list = new ArrayList<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        list = orderLineDao.getGoodsAnalysis(goodsPkey, startTime, endTime, ascription);
        log.info("list:siez: {}", list.size());
        List<Map<String, Object>> result = new ArrayList<>();
        try
        {
            Calendar calendar = new GregorianCalendar();
            Date parseDate = DateUtils.parseDate(endTime, "yyyy-MM-dd");
            calendar.setTime(DateUtils.parseDate(startTime, "yyyy-MM-dd"));
            
            while (true)
            {
                Date date = calendar.getTime();
                Map<String, Object> map = new HashMap<>();
                map.put("timeStamp", date.getTime());
                map.put("value", 0);
                result.add(map);
                calendar.add(Calendar.DATE, 1);
                
                if (date.getTime() == parseDate.getTime()) break;
            }
            
            for (List<Object> o : list)
            {
                for (Map<String, Object> map : result)
                {
                    Date date = DateUtils.parseDate(o.get(1).toString(), "yyyy-MM-dd");
                    Long object = (Long)map.get("timeStamp");
                    if (date.getTime() == object) map.put("value", o.get(0));
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // 异常货品分析
    public PageResult<Map<String, Object>> goodsAbnormal(int page, int pagesize)
    {
        String marketPkey = CurrentSession.marketPkey();
        SysFarmerConfig farmerConfig = farmerConfigDao.get(marketPkey);
        Integer abnormalNum = farmerConfig.getAbnormalNum();
        if (abnormalNum == null) abnormalNum = 10000;
        List<List<Object>> list = orderLineDao.getGoodsAbnormal(marketPkey, abnormalNum, page, pagesize);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("Sales", o.get(1));
            map.put("SalesNum", o.get(2));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(
            orderLineDao.getGoodsAbnormal(marketPkey, abnormalNum, FIXED_PAGE, FIXED_PAGESIZE).size());
        result.setContent(content);
        return result;
    }
    
    // 奖品报表
    public List<Map<String, Object>> drawWin()
    {
        List<List<Object>> list = drawWinDao.getDrawWin(CurrentSession.ascriptionPkey());
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("type", PrizeType.fromIndex(Integer.valueOf(o.get(1).toString())).getName());
            map.put("num", o.get(2));
            result.add(map);
        }
        return result;
    }
    
    // 时间段明细 折线图
    public List<Map<String, Object>> goodsHourAnalysis(Integer goodsPkey, String time)
    {
        List<List<Object>> list = new ArrayList<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        list = orderLineDao.getgoodsHourAnalysis(goodsPkey, time, ascription);
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 24; i++)
        {
            Map<String, Object> map = new HashMap<>();
            try
            {
                Date date = DateUtil.formatDateStr(time + " " + i + ":00:00");
                map.put("timeStamp", date.getTime());
                map.put("value", 0);
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
                try
                {
                    Date date = DateUtil.formatDateStr(time + " " + o.get(0).toString() + ":00:00");
                    if (date.getTime() == Long.parseLong(map.get("timeStamp").toString()))
                    {
                        map.put("value", o.get(1));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                
            }
        }
        return result;
    }
    
    // 时间段明细表
    public PageResult<Map<String, Object>> goodsHourDetail(Integer goodsPkey, String time, int page, int pagesize)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = orderLineDao.getgoodsHourDetail(goodsPkey, time, page, pagesize, ascription);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("num", o.get(1));
            map.put("pricen", o.get(2));
            map.put("kcCode", o.get(3));
            Date date = (Date)o.get(4);
            map.put("createdTime", DateUtil.formatDate(date));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(orderLineDao.getgoodsHourDetail(goodsPkey, time, FIXED_PAGE, FIXED_PAGESIZE, ascription).size());
        result.setContent(content);
        return result;
    }
    
    // 年费会员办卡数量报表
    public List<Map<String, Object>> annualMemberPay(String startTime, String endTime)
    {
        List<List<Object>> list = memberPayDao.getMemberPay(startTime, endTime, CurrentSession.ascriptionPkey());
        log.info("list: {}", list);
        List<Map<String, Object>> result = new ArrayList<>();
        try
        {
            Calendar calendar = new GregorianCalendar();
            Date parseDate = DateUtils.parseDate(endTime, "yyyy-MM-dd");
            calendar.setTime(DateUtils.parseDate(startTime, "yyyy-MM-dd"));
            
            while (true)
            {
                Date date = calendar.getTime();
                Map<String, Object> map = new HashMap<>();
                map.put("timeStamp", date.getTime());
                map.put("value", 0);
                result.add(map);
                calendar.add(Calendar.DATE, 1);
                
                if (date.getTime() == parseDate.getTime()) break;
            }
            
            for (List<Object> o : list)
            {
                for (Map<String, Object> map : result)
                {
                    Date date = DateUtils.parseDate(o.get(0).toString(), "yyyy-MM-dd");
                    Long object = (Long)map.get("timeStamp");
                    if (date.getTime() == object) map.put("value", o.get(1));
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    // 积分兑换统计报表
    public PageResult<Map<String, Object>> goodsIntegralSales(String startTime, String endTime, int page, int pagesize)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = orderLineDao.getgoodsIntegralSales(startTime, endTime, page, pagesize, ascription);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("Sales", o.get(1));
            map.put("SalesNum", o.get(2));
            map.put("pointn", o.get(3));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        int size = orderLineDao.getgoodsIntegralSales(startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription).size();
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 付费会员消费分析
    public PageResult<Map<String, Object>> memberGoodsSales(String startTime, String endTime, int page, int pagesize)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktMember> exec = memberDao.select().eq("ascription", ascription).eq("level", LevelType.PAID_MEMBER).exec();
        List<Integer> memberPkeys = new ArrayList<>();
        for (MktMember m : exec)
            memberPkeys.add(m.getPkey());
        if (memberPkeys.isEmpty()) return null;
        List<List<Object>> list = new ArrayList<>();
        list = orderLineDao.getMemberGoodsSales(memberPkeys, startTime, endTime, page, pagesize);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("Sales", o.get(1));
            map.put("SalesNum", o.get(2));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        int size = orderLineDao.getMemberGoodsSales(memberPkeys, startTime, endTime, 0, 10000).size();
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 商场用户访问报表
    public List<Map<String, Object>> getMallAccessNum(String startTime, String endTime)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = accessLogDao.mallAccessNum(startTime, endTime, ascription);
        log.info("list: {}", list);
        List<Map<String, Object>> result = new ArrayList<>();
        try
        {
            Calendar calendar = new GregorianCalendar();
            Date parseDate = DateUtils.parseDate(endTime, "yyyy-MM-dd");
            calendar.setTime(DateUtils.parseDate(startTime, "yyyy-MM-dd"));
            
            while (true)
            {
                Date date = calendar.getTime();
                Map<String, Object> map = new HashMap<>();
                map.put("timeStamp", date.getTime());
                map.put("value", 0);
                result.add(map);
                calendar.add(Calendar.DATE, 1);
                
                if (date.getTime() == parseDate.getTime()) break;
            }
            
            for (List<Object> o : list)
            {
                for (Map<String, Object> map : result)
                {
                    Date date = DateUtils.parseDate(o.get(0).toString(), "yyyy-MM-dd");
                    Long object = (Long)map.get("timeStamp");
                    if (date.getTime() == object) map.put("value", o.get(1));
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    // 商场新增用户
    public List<Map<String, Object>> getAddMemberCount(String startTime, String endTime)
    {
        List<List<Object>> list = memberDao.getAddMemberCount(startTime, endTime, CurrentSession.ascriptionPkey());
        log.info("list: {}", list);
        List<Map<String, Object>> result = new ArrayList<>();
        try
        {
            Calendar calendar = new GregorianCalendar();
            Date parseDate = DateUtils.parseDate(endTime, "yyyy-MM-dd");
            calendar.setTime(DateUtils.parseDate(startTime, "yyyy-MM-dd"));
            
            while (true)
            {
                Date date = calendar.getTime();
                Map<String, Object> map = new HashMap<>();
                map.put("timeStamp", date.getTime());
                map.put("value", 0);
                result.add(map);
                calendar.add(Calendar.DATE, 1);
                
                if (date.getTime() == parseDate.getTime()) break;
            }
            
            for (List<Object> o : list)
            {
                for (Map<String, Object> map : result)
                {
                    Date date = DateUtils.parseDate(o.get(0).toString(), "yyyy-MM-dd");
                    Long object = (Long)map.get("timeStamp");
                    if (date.getTime() == object) map.put("value", o.get(1));
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    // 优惠券使用统计
    public PageResult<Map<String, Object>> queryFarmerCardCount(String marketPkey, String companyPkey, String startTime,
        String endTime, int page, int pagesize)
    {
        Integer judg = judg();
        List<String> marketPkeys = new ArrayList<>();
        if (judg == 2)
        {
            marketPkey = CurrentSession.marketPkey();
            companyPkey = CurrentSession.companyPkey();
        }
        if (judg == 3)
        {
            companyPkey = CurrentSession.companyPkey();
        }
        if (StringUtils.isNotBlank(marketPkey)) marketPkeys.add(marketPkey);
        if (StringUtils.isNotBlank(companyPkey) && StringUtils.isBlank(marketPkey))
        {
            List<SysFarmer> exec = farmerDao.select().eq("org", companyPkey).exec();
            if (exec.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            exec.forEach(e -> {
                marketPkeys.add(e.getPkey());
            });
        }
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktMemberCard> list = memberCardDao.queryFarmerCardCount2(marketPkeys, startTime, endTime, ascription);
        if (list.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<String> marketKeys = new ArrayList<>();
        list.forEach(e -> {
            if (StringUtils.isNotBlank(e.getFarmer())) marketKeys.add(e.getFarmer());
        });
        List<SysFarmer> exec = farmerDao.select().eq("ascription", ascription).in("pkey", marketKeys.toArray()).exec();
        Map<String, String> farMap = new HashMap<>();
        exec.forEach(e -> {
            farMap.put(e.getPkey(), e.getName());
        });
        List<Map<String, Object>> content = new ArrayList<>();
        for (MktMemberCard o : list)
        {
            Map<String, Object> map = new HashMap<>();
            if (farMap.containsKey(o.getFarmer())) map.put("name", farMap.get(o.getFarmer()));
            map.put("num", o.getPkey());
            map.put("cardPrice", o.getCost());
            content.add(map);
        }
        return PageUtil.page(content, PageParameter.of(page, pagesize));
    }
    
    // 菜品类别销售统计
    public PageResult<Map<String, Object>> goodsTypeSales(String marketPkey, String companyPkey, String startTime,
        String endTime, int page, int pagesize)
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = new ArrayList<>();
        if (StringUtils.isBlank(marketPkey)) marketPkey = null;
        if (StringUtils.isBlank(companyPkey)) companyPkey = null;
        int size = 0;
        if (judg == 1)
        {
            list = orderLineDao.getGoodsTypeSales(marketPkey, companyPkey, startTime, endTime, page, pagesize, ascription);
            size =
                orderLineDao.getGoodsTypeSales(marketPkey, companyPkey, startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                    .size();
        }
        if (judg == 2)
        {
            list = orderLineDao.getGoodsTypeSales(CurrentSession
                .marketPkey(), CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size =
                orderLineDao
                    .getGoodsTypeSales(CurrentSession
                        .marketPkey(), CurrentSession.companyPkey(), startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                    .size();
        }
        if (judg == 3)
        {
            list = orderLineDao
                .getGoodsTypeSales(marketPkey, CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size =
                orderLineDao
                    .getGoodsTypeSales(marketPkey,
                        CurrentSession.companyPkey(),
                        startTime,
                        endTime,
                        FIXED_PAGE,
                        FIXED_PAGESIZE, 
                        ascription)
                    .size();
        }
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0).toString());
            map.put("Sales", o.get(1));
            map.put("SalesNum", o.get(2));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 积分商户销售额统计报表
    public PageResult<Map<String, Object>> vendorSales(String vendorName, String startTime, String endTime, int page,
        int pagesize)
    {
        if (StringUtils.isBlank(vendorName)) vendorName = null;
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = new ArrayList<>();
        list = vendorPointLineDao.getVendorSales(vendorName, startTime, endTime, page, pagesize, ascription);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("mobile", o.get(1));
            map.put("pointSum", o.get(2));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        int size = vendorPointLineDao.getVendorSales(vendorName, startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription).size();
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 市场销售统计报表
    public PageResult<Map<String, Object>> getFarmerSales(String marketPkey, String companyPkey, String startTime,
        String endTime, int page, int pagesize)
    {
        Integer judg = judg();
        List<List<Object>> list = new ArrayList<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        if (StringUtils.isBlank(marketPkey)) marketPkey = null;
        if (StringUtils.isBlank(companyPkey)) companyPkey = null;
        int size = 0;
        if (judg == 1)
        {
            list = orderLineDao.getFarmerSales(marketPkey, companyPkey, startTime, endTime, page, pagesize, ascription);
            size = orderLineDao.getFarmerSales(marketPkey, companyPkey, startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                .size();
        }
        if (judg == 2)
        {
            list = orderLineDao.getFarmerSales(CurrentSession
                .marketPkey(), CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size =
                orderLineDao
                    .getFarmerSales(CurrentSession
                        .marketPkey(), CurrentSession.companyPkey(), startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                    .size();
        }
        if (judg == 3)
        {
            list = orderLineDao
                .getFarmerSales(marketPkey, CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size =
                orderLineDao
                    .getFarmerSales(marketPkey,
                        CurrentSession.companyPkey(),
                        startTime,
                        endTime,
                        FIXED_PAGE,
                        FIXED_PAGESIZE, 
                        ascription)
                    .size();
        }
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("farmerName", o.get(0).toString());
            map.put("companyName", o.get(1));
            map.put("Sales", o.get(2));
            map.put("SalesNum", o.get(3));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 公司销售统计报表
    public PageResult<Map<String, Object>> getCompanySales(String companyPkey, String startTime, String endTime,
        int page, int pagesize)
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = new ArrayList<>();
        if (StringUtils.isBlank(companyPkey)) companyPkey = null;
        int size = 0;
        if (judg == 1)
        {
            list = orderLineDao.getCompanySales(companyPkey, startTime, endTime, page, pagesize, ascription);
            size = orderLineDao.getCompanySales(companyPkey, startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription).size();
        }
        if (judg == 3)
        {
            list = orderLineDao.getCompanySales(CurrentSession.companyPkey(), startTime, endTime, page, pagesize, ascription);
            size = orderLineDao
                .getCompanySales(CurrentSession.companyPkey(), startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription)
                .size();
        }
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", o.get(0));
            map.put("Sales", o.get(1));
            map.put("SalesNum", o.get(2));
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 配送员绩效表
    public PageResult<Map<String, Object>> getExpressCourierCount(String startTime, String endTime, int page,
        int pagesize)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<List<Object>> list = new ArrayList<>();
        list = expressDao.getExpressCourierCount(marketPkey, 3, startTime, endTime, page, pagesize, ascription);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : list)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("successNum", o.get(1));
            content.add(map);
        }
        List<List<Object>> list2 =
            expressDao.getExpressCourierCount(marketPkey, null, startTime, endTime, page, pagesize, ascription);
        for (List<Object> o : list2)
        {
            String name = o.get(0).toString();
            for (Map<String, Object> m : content)
            {
                if (m.get("name").equals(name)) m.put("orderNum", o.get(1));
            }
        }
        
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        int size =
            expressDao.getExpressCourierCount(marketPkey, 3, startTime, endTime, FIXED_PAGE, FIXED_PAGESIZE, ascription).size();
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 运费报表
    public List<Map<String, Object>> getPostageCount(String startTime, String endTime)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        List<MktLogistics> exec = logisticsDao.select().exec();
        for (MktLogistics bean : exec)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", bean.getName());
            map.put("count", 0);
            map.put("postageSum", BigDecimal.ZERO);
            result.add(map);
        }
        Integer ascription = CurrentSession.ascriptionPkey();
        PageResult<MktOrderDesc> pageResult = orderDescDao.aggreLogisticeCount(startTime, endTime, ascription);
        for (Map<String, Object> map : result)
        {
            for (MktOrderDesc od : pageResult.getContent())
            {
                if (map.get("name").equals(od.getLogistics()))
                {
                    map.put("count", od.getPkey());
                    
                }
            }
        }
        List<Map<String, Object>> list = orderDescDao.aggreLogisticeSum(startTime, endTime, ascription);
        for (Map<String, Object> map : result)
        {
            for (Map<String, Object> o : list)
            {
                if (map.get("name").equals(o.get("name"))) map.put("postageSum", o.get("postageSum"));
            }
        }
        List<MktDrawWin> dwLogisticeSum = drawWinDao.aggreLogisticeSum(startTime, endTime, ascription);
        for (Map<String, Object> map : result)
        {
            for (MktDrawWin dw : dwLogisticeSum)
            {
                if (map.get("name").equals(dw.getLogistics()))
                    map.put("count", Integer.valueOf(map.get("count").toString()) + dw.getPkey());
            }
        }
        
        return result;
    }
    
    // 佣金达人
    public PageResult<Map<String, Object>> getComms(String memberName, int page, int pagesize)
    {
        String marketPkey = CurrentSession.marketPkey();
        List<List<Object>> commsNumList = memberCommLineDao.getCommsNum(marketPkey, memberName, page, pagesize);
        List<List<Object>> commsList = memberCommLineDao.getComms(marketPkey, memberName, page, pagesize);
        PageResult<Map<String, Object>> result = new PageResult<>();
        List<Map<String, Object>> content = new ArrayList<>();
        for (List<Object> o : commsNumList)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", o.get(0));
            map.put("goodsNum", o.get(1));
            map.put("buyNum", o.get(2));
            for (List<Object> o2 : commsList)
            {
                if (o.get(0).toString().equals(o2.get(0).toString())) map.put("comms", o2.get(1));
            }
            content.add(map);
        }
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        int size = memberCommLineDao.getCommsNum(marketPkey, memberName, FIXED_PAGE, FIXED_PAGESIZE).size();
        result.setTotalElements(Long.parseLong(size + ""));
        result.setContent(content);
        return result;
    }
    
    // 佣金明细
    public PageResult<CommsDetailOnPage> getCommsDetail(String startTime, String endTime, int page, int pagesize)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        String start = null;
        String end = null;
        if (StringUtils.isNotBlank(startTime))
        {
            start = startTime + " 00:00:00";
            end = endTime + " 23:59:59";
        }
        List<MktOrder> exec = orderDao.select()
            .eq("farmer", marketPkey)
            .eq("ascription", ascription)
            .eq("status", OrderStatus.CONFIRM_ORDER)
            .isNotNull("tjr")
            .between("createdTime", start, end)
            .exec();
        if (exec == null || exec.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<String> formIds = new ArrayList<>();
        List<Integer> memberKeys = new ArrayList<>();
        Map<String, MktOrder> map = new HashMap<>();
        exec.forEach(e -> {
            formIds.add(e.getCode());
            map.put(e.getCode(), e);
            memberKeys.add(e.getMember());
            memberKeys.add(e.getTjr());
        });
        List<MktMember> memExec = memberDao.select().in("pkey", memberKeys).exec();
        Map<Integer, String> memberMap = new HashMap<>();
        memExec.forEach(e -> {
            memberMap.put(e.getPkey(), e.getName());
        });
        PageResult<MktMemberCommLine> pageResult = memberCommLineDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .eq("source", CommSourceType.COMM_SHARE)
            .in("formId", formIds)
            .sort("pkey")
            .exec();
        List<CommsDetailOnPage> content = new ArrayList<>();
        for (MktMemberCommLine c : pageResult.getContent())
        {
            CommsDetailOnPage cd = new CommsDetailOnPage();
            cd.setKcCode(c.getFormId());
            cd.setComms(c.getComms().toString());
            cd.setCommsTime(DateUtil.formatDate(c.getCreatedTime()));
            if (map.containsKey(c.getFormId()))
            {
                MktOrder order = map.get(c.getFormId());
                cd.setBuyTime(DateUtil.formatDate(order.getCreatedTime()));
                cd.setBuyAmtn(order.getAmtn().toString());
                if (memberMap.containsKey(order.getMember())) cd.setBuyMember(memberMap.get(order.getMember()));
                if (memberMap.containsKey(order.getTjr())) cd.setTjr(memberMap.get(order.getTjr()));
            }
            content.add(cd);
        }
        PageResult<CommsDetailOnPage> res = BeanUtil.beanPageFrom(CommsDetailOnPage.class, pageResult);
        res.setContent(content);
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
    
    public Map<String, Object> getForeignDetail(String startTime, String endTime)
    {
        if (StringUtils.isNotBlank(startTime) && startTime.length() == 10)
        {
            startTime = startTime + " 00:00:00";
        }
        if (StringUtils.isNotBlank(endTime) && endTime.length() == 10)
        {
            endTime = endTime + " 23:59:59";
        }
        Integer ascription = CurrentSession.ascriptionPkey();
        if(ascription == null)
            ascription = 2;
        Map<String, Object> map = new HashMap<>();
        // 累计开通市场数
        long count = farmerDao.aggregation().eq("ascription", ascription).notEq("pkey", (Constant.Operation + ascription)).execCount();
        map.put("openMarketCount", count);
        // 本期新增市场数
        long count2 = farmerDao.aggregation().eq("ascription", ascription).between("createdTime", startTime, endTime).notEq("pkey", (Constant.Operation + ascription)).execCount();
        map.put("newMarketCount", count2);
        // 累计注册消费者数
        long mCount = memberDao.aggregation().eq("ascription", ascription).execCount();
        map.put("allUser", mCount);
        // 本期新增消费者数
        long mCount2 = memberDao.aggregation().eq("ascription", ascription).between("createdTime", startTime, endTime).execCount();
        map.put("newUser", mCount2);
        return map;
    }
    
    public Map<String, Object> getForeignDetailOrder()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if(ascription == null)
            ascription = 2;
        List<OrderStatus> statuss = new ArrayList<>();
        statuss.add(OrderStatus.DELIVERED_ORDER);
        statuss.add(OrderStatus.SHIPPED_ORDER);
        statuss.add(OrderStatus.WAIT_ARRIVAL_ORDER);
        statuss.add(OrderStatus.WAIT_WRITEOFF_ORDER);
        statuss.add(OrderStatus.ARRIVED_ORDER);
        statuss.add(OrderStatus.CONFIRM_ORDER);
        //        statuss.add(OrderStatus.REFUND_APPLICATION_ORDER);
        PageResult<MktOrder> result = orderDao.aggregation()
            .in("status", statuss.toArray())
            .eq("ascription", ascription)
            .sum("amto", "amto")
            .count("pkey", "pkey")
            .execDto(MktOrder.class);
        Map<String, Object> res = new HashMap<>();
        res.put("allOrderAmtn", "0");
        res.put("allOrderCount", "0");
        if (!result.getContent().isEmpty())
        {
            MktOrder order = result.getContent().get(0);
            res.put("allOrderAmtn", order.getAmto());
            res.put("allOrderCount", order.getPkey());
        }
        long count = goodsDao.aggregation().eq("ascription", ascription).eq("idDel", false).eq("enabled", true).execCount();
        res.put("allGoodsCount", count);
        return res;
    }

    public PageResult<MktSupplierSaleSummary> getSupplierSales(int page, int pagesize, String startTime, String endTime,
        String supplierName)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<Integer> suppliers = null;
        if (StringUtil.isNotBlank(supplierName))
        {
            suppliers = supplierDao.findPkeys(ascription, supplierName, null);
            if (suppliers.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        PageResult<MktSupplierSaleSummary> pageResult = orderDao.aggregation()
            .page(page)
            .pagesize(pagesize)
            .eq(MktOrder.F.ascription, ascription)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.supplier, suppliers)
            .isNotNull(MktOrder.F.supplier)
            .groupby(MktOrder.F.supplier)
            .count("*", "orderCount")
            .sum(MktOrder.F.pointn, "pointnSum")
            .sum(MktOrder.F.refundPoint, "refundPoint")
            .sum(MktOrder.F.amto, "amtoSum")
            .sum(MktOrder.F.postage, "postageSum")
            .sum(MktOrder.F.amtn, "amtnSum")
            .sort("amtnSum")
            .execDto(MktSupplierSaleSummary.class);
        if (!pageResult.getContent().isEmpty())
        {
            List<Integer> resSuppliers =
                pageResult.stream().map(MktSupplierSaleSummary::getSupplier).collect(Collectors.toList());
            // 查这几个供应商的商品销售数量
            List<MktSupplierSaleSummary> lineAggList = orderLineDao.joinSelect()
                .sum(MktOrderLine.F.num, "goodsCount")
                .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
                .eq(MktOrder.F.ascription, ascription)
                .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
                .in(MktOrder.F.status, OrderStatus.summaryStatus())
                .ge(MktOrder.F.createdTime, start)
                .lt(MktOrder.F.createdTime, end)
                .in(MktOrder.F.supplier, resSuppliers)
                .isNotNull(MktOrder.F.supplier)
                .groupby(MktOrder.F.supplier)
                .endJoin()
                .exec(MktSupplierSaleSummary.class);
            Map<Integer, Long> map = new HashMap<>();
            for (MktSupplierSaleSummary summary : lineAggList)
            {
                if (summary.getGoodsCount() != null) map.put(summary.getSupplier(), summary.getGoodsCount());
            }
            
            List<MktOrder> list = orderDao.select()
                .eq(MktOrder.F.ascription, ascription)
                .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
                .in(MktOrder.F.status, OrderStatus.summaryStatus())
                .ge(MktOrder.F.createdTime, start)
                .lt(MktOrder.F.createdTime, end)
                .in(MktOrder.F.supplier, suppliers)
                .isNotNull(MktOrder.F.supplier)
                .exec();
            Map<Integer,List<Integer>> omap = new HashMap<>();
            list.forEach(e -> 
            {
                if(!omap.containsKey(e.getSupplier()))
                {
                    List<Integer> v = new ArrayList<>();
                    omap.put(e.getSupplier(), v);
                }
                omap.get(e.getSupplier()).add(e.getPkey());
            });
            
            for (MktSupplierSaleSummary summary : pageResult)
            {
                summary.setGoodsCount(map.getOrDefault(summary.getSupplier(), 0L));
                summary.setRefundPostage(BigDecimal.ZERO);
                summary.setRefundAmt(BigDecimal.ZERO);
                if(omap.containsKey(summary.getSupplier()))
                {
                    BigDecimal refundPostageAmt = orderRefundDao.aggRefundPostageAmt(omap.get(summary.getSupplier()));
                    summary.setRefundPostage(refundPostageAmt);
                    BigDecimal refundAmt = orderRefundDao.aggRefundAmt(omap.get(summary.getSupplier()));
                    summary.setRefundAmt(refundAmt);
                }
                summary.setAmtnSum(summary.getAmtnSum().subtract(summary.getRefundAmt()).subtract(summary.getRefundPostage()));
                if(summary.getRefundPoint() == null)
                    summary.setRefundPoint(0);
            }
        }
        return pageResult;
    }
    
    public MktSupplierSaleSummary sumSupplierSales(String startTime, String endTime, String supplierName)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<Integer> suppliers = null;
        if (StringUtil.isNotBlank(supplierName))
        {
            suppliers = supplierDao.findPkeys(ascription, supplierName, null);
            if (suppliers.isEmpty()) return MktSupplierSaleSummary.empty();
        }
        List<MktSupplierSaleSummary> list = orderDao.aggregation()
            .eq(MktOrder.F.ascription, ascription)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.supplier, suppliers)
            .isNotNull(MktOrder.F.supplier)
            .sum(MktOrder.F.pointn, "pointnSum")
            .sum(MktOrder.F.refundPoint, "refundPoint")
            .sum(MktOrder.F.amto, "amtoSum")
            .sum(MktOrder.F.postage, "postageSum")
            .sum(MktOrder.F.amtn, "amtnSum")
//            .sum(MktOrder.F.refundAmt, "refundAmt")
            .execList(MktSupplierSaleSummary.class);
        MktSupplierSaleSummary summary;
        if (CollectionUtil.isEmpty(list))
            summary = MktSupplierSaleSummary.empty();
        else
            summary = list.get(0).fillDefault();
        
        List<MktOrder> orders = orderDao.select()
            .eq(MktOrder.F.ascription, ascription)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.supplier, suppliers)
            .isNotNull(MktOrder.F.supplier)
            .exec();
        List<Integer> v = CollectionUtil.keyList(orders);
        BigDecimal refundPostageAmt = orderRefundDao.aggRefundPostageAmt(v);
        summary.setRefundPostage(refundPostageAmt);
        BigDecimal refundAmt = orderRefundDao.aggRefundAmt(v);
        summary.setRefundAmt(refundAmt);
        summary.setAmtoSum(summary.getAmtoSum().subtract(summary.getRefundAmt()));
        summary.setPostageSum(summary.getPostageSum().subtract(summary.getRefundPostage()));
        summary.setAmtnSum(summary.getAmtoSum().add(summary.getPostageSum()));
        if(summary.getRefundPoint() != null)
            summary.setPointnSum(summary.getPointnSum() - summary.getRefundPoint());
        return summary;
    }
    
    public PageResult<MktSupplierOrderLineOnPage> querySupplierOrderLine(int page, int pagesize, String startTime,
        String endTime, String kcCode, String supplierName, String goodsName, 
        List<PayType> payTypes, List<Integer> tags)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<Integer> suppliers = null;
        if (StringUtil.isNotBlank(supplierName))
        {
            suppliers = supplierDao.findPkeys(ascription, supplierName, null);
            if (suppliers.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        List<Integer> orderKeys = new ArrayList<>();
        if(tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascription);
            if(list == null || list.isEmpty())
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            list.forEach(e -> orderKeys.add(e.getOrderPkey()));
        }
        
        PageResult<MktSupplierOrderLineOnPage> result = orderLineDao.joinSelectPage()
            .as(MktOrderLine.F.pkey)
            .as(MktOrderLine.F.orderPkey)
            .as(MktOrderLine.F.goods)
            .as(MktOrderLine.F.space)
            .as(MktOrderLine.F.goodsName)
            .as(MktOrderLine.F.num)
            .as(MktOrderLine.F.pricen)
            .as(MktOrderLine.F.refundAmt)
            .as(MktOrderLine.F.couponAmt)
            .as(MktOrderLine.F.point)
            .like(MktOrderLine.F.goodsName, goodsName)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .as(MktOrder.F.code, "kcCode")
            .as(MktOrder.F.member)
            .as(MktOrder.F.supplier)
            .as(MktOrder.F.createdTime)
            .as(MktOrder.F.payType)
            .eq(MktOrder.F.ascription, ascription)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.supplier, suppliers)
            .in(MktOrder.F.pkey, orderKeys)
            .in(MktOrder.F.payType, payTypes)
            .isNotNull(MktOrder.F.supplier)
            .like(MktOrder.F.code, kcCode)
            .endJoin()
            .sort(0, MktOrder.F.createdTime)
            .sort(0, MktOrder.F.pkey)
            .sort(MktOrderLine.F.pkey)
            .page(page)
            .pagesize(pagesize)
            .exec(MktSupplierOrderLineOnPage.class);
        for(MktSupplierOrderLineOnPage d : result)
        {
            if(d.getRefundAmt() != null && d.getRefundAmt().compareTo(d.getCouponAmt()) == 0)
            {
                List<MktOrderRefundLine> listOrderLinePkey = orderRefundLineDao.listOrderLinePkey(d.getPkey());
                Integer rp = 0;
                for(MktOrderRefundLine orl : listOrderLinePkey)
                {
                    if (orl.getRefundPoint() != null)
                    {
                        MktOrderRefund or = orderRefundDao.get(orl.getRefundPkey());
                        if (RefundStatus.REFUND_FINAL.equals(or.getStatus()))
                            rp += (orl.getRefundPoint());
                    }
                }
                d.setRefundPoint(rp);
            }
            else
            {
                d.setRefundPoint(0);
            }
            d.setTagName(orderTagDao.getTagName(d.getOrderPkey()));
        }
        return result;
    }
    
    public BigDecimal sumSupplierOrderLine(String startTime, String endTime, String kcCode, String supplierName,
        String goodsName, List<PayType> payTypes, List<Integer> tags)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<Integer> suppliers = null;
        if (StringUtil.isNotBlank(supplierName))
        {
            suppliers = supplierDao.findPkeys(ascription, supplierName, null);
            if (suppliers.isEmpty()) return BigDecimal.ZERO;
        }
        List<Integer> orderKeys = new ArrayList<>();
        if(tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascription);
            if(list == null || list.isEmpty())
                return BigDecimal.ZERO;
            list.forEach(e -> orderKeys.add(e.getOrderPkey()));
        }
        
        List<AmtAgg> list = orderLineDao.joinSelect()
            .sum(prod(f(MktOrderLine.F.num), f(MktOrderLine.F.pricen)), "amt")
            .like(MktOrderLine.F.goodsName, goodsName)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.ascription, ascription)
            .in(MktOrder.F.pkey, orderKeys)
            .in(MktOrder.F.payType, payTypes)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.supplier, suppliers)
            .isNotNull(MktOrder.F.supplier)
            .like(MktOrder.F.code, kcCode)
            .endJoin()
            .exec(AmtAgg.class);
        if (list.isEmpty()) return BigDecimal.ZERO;
        
        List<AmtAgg> refundList = orderLineDao.joinSelect()
            .sum("refundAmt", "amt")
            .like(MktOrderLine.F.goodsName, goodsName)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.ascription, ascription)
            .eq(MktOrder.F.orderOir, OrderOir.POINTS_MALL)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.supplier, suppliers)
            .in(MktOrder.F.pkey, orderKeys)
            .in(MktOrder.F.payType, payTypes)
            .isNotNull(MktOrder.F.supplier)
            .like(MktOrder.F.code, kcCode)
            .endJoin()
            .exec(AmtAgg.class);
        BigDecimal res = list.get(0).getAmt();
        if(res == null)
            res = BigDecimal.ZERO;
        if(!refundList.isEmpty())
        {
            BigDecimal amt = refundList.get(0).getAmt();
            if(amt == null)
                amt = BigDecimal.ZERO;
            res = res.subtract(amt);
        }
        return res;
    }
    
    @Data
    public static class AmtAgg
    {
        private BigDecimal amt;
    }
    
    public List<FarmerOption> listFarmerOptions()
    {
        Integer judg = judg();
        Integer ascription = CurrentSession.ascriptionPkey();
        List<FarmerOption> list;
        // 运营端
        switch (judg)
        {
            // 运营端
            case 1:
                list = farmerDao.listValidFarmer(ascription, null, null, FarmerOption.class);
                // 插入运营端名字
                SysAscription ascriptionBean = ascriptionDao.get(ascription);
                FarmerOption ascriptionOp = new FarmerOption();
                ascriptionOp.setPkey(Constant.Operation + ascription);
                ascriptionOp.setName(ascriptionBean.getName());
                list.add(ascriptionOp);
                break;
            // 市场端
            case 2:
                list = farmerDao.listValidFarmer(ascription,
                    CurrentSession.companyPkey(),
                    CurrentSession.marketPkey(),
                    FarmerOption.class);
                break;
            // 公司端
            case 3:
                list = farmerDao.listValidFarmer(ascription, CurrentSession.companyPkey(), null, FarmerOption.class);
                break;
            default:
                list = new ArrayList<>();
        }
        return list;
    }
    
    public PageResult<GoodsLineSummary> goodsLineSummary(int page, int pagesize, String startTime, String endTime,
        String goodsName, String farmer)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<String> farmerPkeys = listAllowedFarmerPkeys();
        if (CollectionUtil.isEmpty(farmerPkeys))
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<GoodsLineSummary> hasCoupon =
            goodsLineSumAgg(true, false, ascription, start, end, goodsName, farmer, farmerPkeys, GoodsLineSummary.class);
        Map<String, GoodsLineSummary> map =
            hasCoupon.stream().collect(Collectors.toMap(GoodsLineSummary::getKey, Function.identity()));
        List<GoodsLineSummary> noCoupon =
            goodsLineSumAgg(true, true, ascription, start, end, goodsName, farmer, farmerPkeys, GoodsLineSummary.class);
        for (GoodsLineSummary line : noCoupon)
        { 
            if (map.containsKey(line.getKey()))
            {
                GoodsLineSummary summary = map.get(line.getKey());
                summary.add(line);
            }
            else
            {
                // 没优惠后金额的，将优惠后金额设为商品总价
                line.setCouponAmtSum(line.getAmtSum());
                line.fillDefault();
                map.put(line.getKey(), line);
            }
        }
        List<GoodsLineSummary> list = new ArrayList<>(map.values());
        return PageUtil.page(list, PageParameter.of(page, pagesize));
    }
    
    public GoodsLineSum goodsLineSum(String startTime, String endTime, String goodsName, String farmer)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<String> farmerPkeys = listAllowedFarmerPkeys();
        if (CollectionUtil.isEmpty(farmerPkeys)) return GoodsLineSum.empty();
        
        List<GoodsLineSum> hasCouponList =
            goodsLineSumAgg(false, false, ascription, start, end, goodsName, farmer, farmerPkeys, GoodsLineSum.class);
        List<GoodsLineSum> noCouponList =
            goodsLineSumAgg(false, true, ascription, start, end, goodsName, farmer, farmerPkeys, GoodsLineSum.class);
        
        GoodsLineSum hasCoupon =
            CollectionUtil.isEmpty(hasCouponList) ? GoodsLineSum.empty() : hasCouponList.get(0);
            GoodsLineSum noCoupon =
            CollectionUtil.isEmpty(noCouponList) ? GoodsLineSum.empty() : noCouponList.get(0);
        hasCoupon.add(noCoupon);
        return hasCoupon;
    }
    
    private <T> List<T> goodsLineSumAgg(boolean groupby, boolean isCouponAmtNull, Integer ascription,
        Date start, Date end, String goodsName, String farmer, List<String> farmerPkeys, Class<T> clazz)
    {
        AggregationBuilder<Integer, MktOrderLine> builder = orderLineDao.aggregation()
            .eq(MktOrderLine.F.ascription, ascription)
            .ge(MktOrderLine.F.createdTime, start)
            .lt(MktOrderLine.F.createdTime, end)
            .in(MktOrderLine.F.farmer, farmerPkeys)
            .eq(MktOrderLine.F.farmer, farmer)
            .like(MktOrderLine.F.goodsName, goodsName)
            .notEq(MktOrderLine.F.status, OrderStatus.VOID_ORDER);
        if (isCouponAmtNull)
            builder.isNull(MktOrderLine.F.couponAmt);
        else
            builder.isNotNull(MktOrderLine.F.couponAmt);
        if (groupby)
            builder.groupby(MktOrderLine.F.goods).groupby(MktOrderLine.F.goodsName).groupby(MktOrderLine.F.space);
        return builder.count("*", "orderCount")
            .sum(MktOrderLine.F.num, "goodsCount")
            .sum(prod(f(MktOrderLine.F.num), f(MktOrderLine.F.pricen)), "amtSum")
            .sum(MktOrderLine.F.couponAmt, "couponAmtSum")
            .sum(MktOrderLine.F.refundAmt, "refundAmtSum")
            .sum(MktOrderLine.F.weight, "weight")
            .execListDto(clazz);
    }
    
    public PageResult<MktGoodsOrderLineOnPage> queryGoodsOrderLine(int page, int pagesize, String startTime,
        String endTime, String kcCode, String memberMobile, OrderStatus status, Integer deliveryType, Integer goods,
        String goodsName, Integer space)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<String> farmerPkeys = listAllowedFarmerPkeys();
        if (CollectionUtil.isEmpty(farmerPkeys))
            return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<Integer> memberPkeys = null;
        if (StringUtil.isNotBlank(memberMobile))
        {
            memberPkeys = memberDao.listPkeys(ascription, memberMobile);
            if (CollectionUtil.isEmpty(memberPkeys))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        List<DistributionType> distributionTypes = null;
        if (deliveryType != null)
        {
            distributionTypes = convertDeliveryType(deliveryType);
            if (CollectionUtil.isEmpty(distributionTypes))
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        }
        return orderLineDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktOrderLine.F.pkey)
            .as(MktOrderLine.F.orderPkey)
            .as(MktOrderLine.F.goods)
            .as(MktOrderLine.F.space)
            .as(MktOrderLine.F.goodsName)
            .as(MktOrderLine.F.num)
            .as(MktOrderLine.F.pricen)
            .as(MktOrderLine.F.couponAmt)
            .as(MktOrderLine.F.refundAmt)
            .eq(MktOrderLine.F.goods, goods)
            .like(MktOrderLine.F.goodsName, goodsName)
            .eq(MktOrderLine.F.space, space)
            .notEq(MktOrderLine.F.status, OrderStatus.VOID_ORDER)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .as(MktOrder.F.code, "kcCode")
            .as(MktOrder.F.member)
            .as(MktOrder.F.createdTime)
            .as(MktOrder.F.distributionType)
            .as(MktOrder.F.status)
            .eq(MktOrder.F.ascription, ascription)
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.farmer, farmerPkeys)
            .like(MktOrder.F.code, kcCode)
            .in(MktOrder.F.member, memberPkeys)
            .eq(MktOrder.F.status, status)
            .in(MktOrder.F.distributionType, distributionTypes)
            .endJoin()
            .sort(MktOrder.F.createdTime)
            .sort(MktOrderLine.F.pkey)
            .exec(MktGoodsOrderLineOnPage.class);
    }
    
    public MktGoodsOrderLineSummary sumGoodsOrderLine(String startTime, String endTime, String kcCode,
        String memberMobile, OrderStatus status, Integer deliveryType, Integer goods, String goodsName, Integer space)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date start = DateUtil.atStartOfDay(startTime);
        Date end = DateUtil.atStartOfNextDay(endTime);
        List<String> farmerPkeys = listAllowedFarmerPkeys();
        if (CollectionUtil.isEmpty(farmerPkeys)) return MktGoodsOrderLineSummary.empty();
        List<Integer> memberPkeys = null;
        if (StringUtil.isNotBlank(memberMobile))
        {
            memberPkeys = memberDao.listPkeys(ascription, memberMobile);
            if (CollectionUtil.isEmpty(memberPkeys)) return MktGoodsOrderLineSummary.empty();
        }
        List<DistributionType> distributionTypes = null;
        if (deliveryType != null)
        {
            distributionTypes = convertDeliveryType(deliveryType);
            if (CollectionUtil.isEmpty(distributionTypes)) return MktGoodsOrderLineSummary.empty();
        }
        MktGoodsOrderLineSummary hasCoupon = sumGoodsOrderLineAgg(false,
            ascription,
            start,
            end,
            farmerPkeys,
            kcCode,
            memberPkeys,
            status,
            distributionTypes,
            goods,
            goodsName,
            space);
        MktGoodsOrderLineSummary noCoupon = sumGoodsOrderLineAgg(true,
            ascription,
            start,
            end,
            farmerPkeys,
            kcCode,
            memberPkeys,
            status,
            distributionTypes,
            goods,
            goodsName,
            space);
        hasCoupon.add(noCoupon);
        return hasCoupon;
    }
    
    private MktGoodsOrderLineSummary sumGoodsOrderLineAgg(boolean isCouponAmtNull, Integer ascription, Date start,
        Date end, List<String> farmerPkeys, String kcCode, List<Integer> memberPkeys, OrderStatus status,
        List<DistributionType> distributionTypes, Integer goods, String goodsName, Integer space)
    {
        SelectOps builder = orderLineDao.joinSelect()
            .sum(MktOrderLine.F.num, "goodsCount")
            .sum(prod(f(MktOrderLine.F.num), f(MktOrderLine.F.pricen)), "amtSum")
            .sum(MktOrderLine.F.couponAmt, "couponAmtSum")
            .sum(MktOrderLine.F.refundAmt, "refundAmtSum")
            .eq(MktOrderLine.F.goods, goods)
            .like(MktOrderLine.F.goodsName, goodsName)
            .eq(MktOrderLine.F.space, space)
            .notEq(MktOrderLine.F.status, OrderStatus.VOID_ORDER);
        if (isCouponAmtNull)
            builder.isNull(MktOrderLine.F.couponAmt);
        else
            builder.isNotNull(MktOrderLine.F.couponAmt);
        List<MktGoodsOrderLineSummary> list = builder.join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .count(MktOrder.F.pkey, "orderCount")
            .eq(MktOrder.F.ascription, ascription)
            .ge(MktOrder.F.createdTime, start)
            .lt(MktOrder.F.createdTime, end)
            .in(MktOrder.F.farmer, farmerPkeys)
            .like(MktOrder.F.code, kcCode)
            .in(MktOrder.F.member, memberPkeys)
            .eq(MktOrder.F.status, status)
            .in(MktOrder.F.distributionType, distributionTypes)
            .endJoin()
            .exec(MktGoodsOrderLineSummary.class);
        MktGoodsOrderLineSummary summary;
        if (CollectionUtil.isEmpty(list))
            summary = MktGoodsOrderLineSummary.empty();
        else
            summary = list.get(0).fillDefault();
        return summary;
    }
    
    private List<String> listAllowedFarmerPkeys()
    {
        List<FarmerOption> farmers = listFarmerOptions();
        return farmers.stream().map(FarmerOption::getPkey).collect(Collectors.toList());
    }
    
    private List<DistributionType> convertDeliveryType(Integer deliveryType)
    {
        // 配送方式（1：配送，2：自提）
        if (deliveryType == 1)
            return DistributionType.delivery();
        else if (deliveryType == 2)
            return Lists.newArrayList(DistributionType.PICKUP);
        else
            return null;
    }
    
}
