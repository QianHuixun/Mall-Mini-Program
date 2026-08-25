package cn.tofocus.lejia.dao.market;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.BaseSelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.aggs.AggregationBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.data.IndexYFDTO;
import cn.tofocus.lejia.bean.dto.data.ReportOrderDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberConsumption;
import cn.tofocus.lejia.bean.dto.order.MktOrderCountAmt;
import cn.tofocus.lejia.bean.dto.v2.screen.RealTimeSalesOnList;
import cn.tofocus.lejia.bean.dto.vendor.SettlementTotalInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorSettleDateInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrder.F;
import cn.tofocus.lejia.bean.entity.market.MktOrderGroup;
import cn.tofocus.lejia.bean.enums.*;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.repository.market.MktOrderRepository;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class MktOrderDao extends JpaSpecificationDelegate<Integer, MktOrder>
{
    @Autowired
    private MktOrderRepository repository;
    
    @Autowired
    private MktOrderGroupDao orderGroupDao;

    public PageResult<MktMemberConsumption> queryMemberConsumption(Integer memberPkey, int page, int pagesize)
    {
        PageResult<MktMemberConsumption> result = new PageResult<>();
        List<MktMemberConsumption> content = new ArrayList<MktMemberConsumption>();
        List<List<Object>> list = repository.queryMemberConsumption(memberPkey, page * pagesize, pagesize);
        for (List<Object> o : list)
        {
            MktMemberConsumption dto = new MktMemberConsumption();
            dto.setCode(o.get(0).toString());
            dto.setFarmer(o.get(1).toString());
            dto.setConsumption(new BigDecimal(o.get(2).toString()));
            dto.setGoodsName(o.get(3).toString());
            Date date = (Date)o.get(4);
            dto.setCreatedTime(date);
            content.add(dto);
        }
        result.setContent(content);
        PageParameter pageParamter = new PageParameter(page, pagesize);
        result.setPageable(pageParamter);
        result.setTotalElements(repository.queryMemberConsumptionCount(memberPkey));
        return result;
    }
    
    public <T> PageResult<T> queryOrder(int page, int pagesize, OrderOir orderOir, String startDate, String endDate,
        OrderStatus status, String code, List<Integer> orderIds, OrderType orderType, List<Integer> members,
        PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, String farmer, Boolean falg,
        ExpressType expressType, Integer ascriptionPkey, List<PayType> payTypes, DistributionType distributionType,
        Class<T> clazz)
    {
        SelectPageBuilder<Integer, MktOrder> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("farmer", farmer)
            .eq("ascription", ascriptionPkey)
            .eq("expressType", expressType)
            .eq("distributionType", distributionType)
            .in("payType", payTypes)
            .notEq("orderType", OrderType.INTEGRAL_JD_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("orderOir", orderOir)
            .in("member", members)
            .eq("purchaseStatus", purchaseStatus)
            .like("pickupCode", vrifyCode)
            .sort("pkey", true);
        assemblyBuilder(builder, orderOir, startDate, endDate, status, code, orderIds, orderType, groupPkey, falg);
        return builder.execDto(clazz);
    }
    
    public BaseSelectBuilder assemblyBuilder(BaseSelectBuilder builder, OrderOir orderOir, String startDate,
        String endDate, OrderStatus status, String code, List<Integer> orderIds, OrderType orderType, Integer groupPkey,
        Boolean falg)
    {
        if (orderIds.size() > 0) builder.in("pkey", orderIds.toArray());
        if (StringUtils.isNotBlank(code)) builder.like("code", code);
        if (status != null) builder.eq("status", status);
        if (orderType != null)
            builder.eq("orderType", orderType);
        else
        {
            if (!falg) builder.notEq("orderType", OrderType.COLLAGE_ORDER);
        }
        if (StringUtil.isNotEmpty(startDate)) builder.ge("createdTime", startDate + " 00:00:00");
        if (StringUtil.isNotEmpty(endDate))
        {
            builder.le("createdTime", endDate + " 23:59:59");
        }
        if (groupPkey != null)
        {
            MktOrderGroup group = orderGroupDao.get(groupPkey);
            if (group == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
            List<String> orderList = group.getOrderList();
            if (orderList.size() > 0) builder.in("pkey", orderList.toArray());
        }
        return builder;
    }
    
    public List<IndexYFDTO> yesterdayData(String time, int i, Integer ascription)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().eq("ascription", ascription)
            .sum("amto", "amto")
            .count("pkey", "count")
            .isNotNull("amto")
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between("createdTime", time + " 00:00:00", time + " 23:59:59");
        if (i == 1) return builder.groupby("farmer", "farmer").exec(IndexYFDTO.class).getContent();
        if (i == 2) return builder.groupby("company", "company").exec(IndexYFDTO.class).getContent();
        return builder.exec(IndexYFDTO.class).getContent();
    }
    
    public List<IndexYFDTO> todayData(String time, String marketPkey, String companyPkey)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().sum("amto", "amto")
            .count("pkey", "count")
            .isNotNull("amto")
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between("createdTime", time + " 00:00:00", time + " 23:59:59");
        if (StringUtils.isNotBlank(marketPkey)) builder.eq("farmer", marketPkey);
        if (StringUtils.isNotBlank(companyPkey)) builder.eq("company", companyPkey);
        return builder.exec(IndexYFDTO.class).getContent();
    }
    
    public List<IndexYFDTO> memberPay(String time, int i, Integer ascription)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().eq("ascription", ascription)
            .count("pkey", "count")
            .isNotNull("amto")
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between("createdTime", time + " 00:00:00", time + " 23:59:59");
        //				.groupby("member", "member");
        if (i == 1)
        {
            builder.eq("farmer", CurrentSession.marketPkey());
            return builder.groupby("farmer", "farmer").exec(IndexYFDTO.class).getContent();
        }
        if (i == 2)
        {
            builder.eq("company", CurrentSession.companyPkey());
            return builder.groupby("company", "company").exec(IndexYFDTO.class).getContent();
        }
        return builder.exec(IndexYFDTO.class).getContent();
    }
    
    public List<IndexYFDTO> yesterdayAmtnData(String time, int i, Integer ascription)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().sum("amto", "amto")
            .isNotNull("amto")
            .eq("ascription", ascription)
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between("createdTime", time + " 00:00:00", time + " 23:59:59");
        if (i == 1) return builder.groupby("farmer", "farmer").exec(IndexYFDTO.class).getContent();
        if (i == 2) return builder.groupby("company", "company").exec(IndexYFDTO.class).getContent();
        return builder.exec(IndexYFDTO.class).getContent();
    }
    
    public List<ReportOrderDTO> reportOrderData(String time, Integer ascription)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().sum(MktOrder.F.amto, MktOrder.F.amto)
            .sum(MktOrder.F.postage, MktOrder.F.postage)
            .sum(MktOrder.F.cardAmt, MktOrder.F.cardAmt)
            .sum(MktOrder.F.refundAmt, MktOrder.F.refundAmt)
            .isNotNull(MktOrder.F.amto) //排除积分订单？
            .eq(MktOrder.F.ascription, ascription)
            .in(MktOrder.F.status,
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between(MktOrder.F.createdTime, time + " 00:00:00", time + " 23:59:59");
        return builder.exec(ReportOrderDTO.class).getContent();
    }
    
    public List<List<Object>> yesterdayHourAmtnData(String time, int i, Integer ascription)
    {
        if (i == 1) return repository.yesterdayHourMarketData(time, ascription);
        if (i == 2) return repository.yesterdayHourCompanyData(time, ascription);
        return repository.yesterdayHourData(time, ascription);
    }
    
    public List<IndexYFDTO> getfarmerSales(String startTime, String endTime, Integer ascription)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().sum("amto", "amto")
            .in("status",
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .eq("ascription", ascription)
            .between("createdTime", startTime + " 00:00:00", endTime + " 23:59:59")
            .groupby("company", "company")
            .groupby("farmer", "farmer")
            .sort("amto", true);
        PageResult<IndexYFDTO> result = builder.exec(IndexYFDTO.class);
        return result.getContent();
    }
    
    public Integer getOrderCount(Integer goodsPkey)
    {
        List<List<Object>> orderCutNum = repository.getOrderCutNum(goodsPkey);
        return Integer.valueOf(orderCutNum.get(0).get(0).toString());
    }
    
    public Integer judgOrderCut(Integer goodsPkey, Integer member)
    {
        List<List<Object>> judgOrderCut = repository.judgOrderCut(goodsPkey, member);
        if (judgOrderCut != null && judgOrderCut.size() > 0 && judgOrderCut.get(0) != null
            && judgOrderCut.get(0).size() > 0) return Integer.valueOf(judgOrderCut.get(0).get(0).toString());
        return null;
    }
    
    public PageResult<IndexYFDTO> aggregationPay(List<String> codeList)
    {
        AggregationBuilder<Integer, MktOrder> builder = aggregation().sum("amtn", "amtn");
        if (codeList.size() > 0) builder.in("code", codeList.toArray());
        
        return builder.exec(IndexYFDTO.class);
    }
    
    public Map<Integer, MktOrderCountAmt> consumption(List<Integer> members)
    {
        List<MktOrderCountAmt> list = this.aggregation()
            .in(F.member, members)
            .in(F.status,
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .count("*", "count")
            .sum(F.amtn, "amt")
            .groupby(F.member, "pkey")
            .execList(MktOrderCountAmt.class);
        return list.stream().collect(Collectors.toMap(MktOrderCountAmt::getPkey, Function.identity(), (v1, v2) -> v2));
    }
    
    public BigDecimal getTodayAmt(Date date)
    {
        List<OrderStatus> status = new ArrayList<>();
        status.add(OrderStatus.DELIVERED_ORDER);
        status.add(OrderStatus.SHIPPED_ORDER);
        status.add(OrderStatus.ARRIVED_ORDER);
        status.add(OrderStatus.CONFIRM_ORDER);
        status.add(OrderStatus.REFUND_APPLICATION_ORDER);
        BigDecimal res = BigDecimal.ZERO;
        BigDecimal sum = (BigDecimal)this.aggregation()
            //            .eq("payType", PayType.ZXYW_WEIXIN)
            .in("status", status.toArray())
            .eq(substring(f("createdTime"), 1, 10), DateUtil.formatDate(date, "yyyy-MM-dd"))
            .execSum("amtn");
        if (sum != null) res = sum;
        return res;
    }
    
    public Map<String, BigDecimal> getTodayAmtMap(Date startDate, Date endDate)
    {
        List<MktOrder> list = this.aggregation()
            //            .eq("payType", PayType.ZXYW_WEIXIN)
            .in("status", listStatus().toArray())
            .ge("createdTime", startDate)
            .lt("createdTime", endDate)
            .groupby(substring(f("createdTime"), 1, 10), "pstime")
            .sum("amtn", "amtn")
            .execList(MktOrder.class);
        Map<String, BigDecimal> res = new HashMap<>();
        list.forEach(e -> {
            res.put(e.getPstime(), e.getAmtn());
        });
        return res;
    }
    
    public Map<String, SettlementTotalInfo> getTotalAmtMap(Date startDate, Date endDate)
    {
        List<SettlementTotalInfo> list = this.aggregation()
            //            .eq("payType", PayType.ZXYW_WEIXIN)
            .in("status", listStatus().toArray())
            .ge("createdTime", startDate)
            .lt("createdTime", endDate)
            .groupby(substring(f("createdTime"), 1, 10), "time")
            .sum("amtn", "amtn")
            .sum("cardAmt", "discountAmt")
            .sum("postage", "postage")
            .execList(SettlementTotalInfo.class);
        Map<String, SettlementTotalInfo> res = new HashMap<>();
        list.forEach(e -> {
            res.put(e.getTime(), e);
        });
        return res;
    }
    
    public List<BigDecimal> listAmtn(Date date)
    {
        List<MktOrder> list = this.select()
            //        .eq("payType", PayType.ZXYW_WEIXIN)
            .in("status", listStatus().toArray())
            .ge("createdTime", date)
            .lt("createdTime", DateUtil.atStartOfNextDay(date))
            .exec();
        return list.stream().map(MktOrder::getAmtn).collect(Collectors.toList());
    }
    
    public Boolean checkFarTimeConfirm(Date startDate, Date endDate, List<String> marketKeys)
    {
        long count = this.aggregation()
            .in("status", listStatus().toArray())
            .ge("createdTime", startDate)
            .lt("createdTime", endDate)
            .in("farmer", marketKeys)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .execCount();
        if (count > 0) return false;
        return true;
    }
    
    public List<Integer> querKeyList(Date startDate, Date endDate, Integer ascription, List<String> marketKeys)
    {
        List<MktOrder> exec = this.select()
            .in("status", listStatus().toArray())
            .ge("createdTime", startDate)
            .eq("ascription", ascription)
            .lt("createdTime", endDate)
            .in("farmer", marketKeys)
            .exec();
        List<Integer> res = new ArrayList<>();
        exec.forEach(e -> {
            res.add(e.getPkey());
        });
        return res;
    }
    
    private List<OrderStatus> listStatus()
    {
        List<OrderStatus> status = new ArrayList<>();
        status.add(OrderStatus.DELIVERED_ORDER);
        status.add(OrderStatus.SHIPPED_ORDER);
        status.add(OrderStatus.WAIT_ARRIVAL_ORDER);
        status.add(OrderStatus.WAIT_WRITEOFF_ORDER);
        status.add(OrderStatus.ARRIVED_ORDER);
        status.add(OrderStatus.CONFIRM_ORDER);
        status.add(OrderStatus.REFUND_APPLICATION_ORDER);
        return status;
    }
    
    public Map<Date, RealTimeSalesOnList> hourAmtn(Date startTime, Date endTime, Integer ascription)
    {
        List<List<Object>> list = repository.hourAmtn(startTime, endTime, ascription);
        Map<Date, RealTimeSalesOnList> map = new HashMap<>();
        list.forEach(e -> {
            RealTimeSalesOnList rt = new RealTimeSalesOnList();
            rt.setDate(DateUtil.formatDateStr(String.valueOf(e.get(0)), "yyyy-MM-dd HH"));
            rt.setOrderSales(new BigDecimal(String.valueOf(e.get(1))));
            rt.setTime(String.valueOf(e.get(0)));
            map.put(rt.getDate(), rt);
        });
        
        return map;
    }
    
    public Map<Date, RealTimeSalesOnList> dayAmtn(Date startTime, Date endTime, Integer ascription)
    {
        List<List<Object>> list = repository.dayAmtn(startTime, endTime, ascription);
        Map<Date, RealTimeSalesOnList> map = new HashMap<>();
        list.forEach(e -> {
            RealTimeSalesOnList rt = new RealTimeSalesOnList();
            rt.setDate(DateUtil.formatDateStr(String.valueOf(e.get(0)), "yyyy-MM-dd"));
            rt.setOrderSales(new BigDecimal(String.valueOf(e.get(1))));
            rt.setTime(String.valueOf(e.get(0)));
            map.put(rt.getDate(), rt);
        });
        
        return map;
    }
    
    public List<VendorSettleDateInfo> getNotPurchase(Integer ascription, List<String> marketKeys)
    {
        Map<String, Long> map = getNotPurchaseMap(ascription, marketKeys);
        List<VendorSettleDateInfo> res = new ArrayList<>();
        for (Entry<String, Long> e : map.entrySet())
        {
            VendorSettleDateInfo info = new VendorSettleDateInfo();
            info.setStart(DateUtil.formatDateStr(e.getKey(), "yyyy-MM-dd").getTime());
            info.setEnd(DateUtil.formatDateStr(e.getKey(), "yyyy-MM-dd").getTime());
            info.setColour(true);
            res.add(info);
        }
        return res;
    }
    
    public Map<String, Long> getNotPurchaseMap(Integer ascription, List<String> marketKeys)
    {
        return this.aggregation()
            .eq("ascription", ascription)
            .notEq("farmer", Constant.Operation + ascription)
            .notEq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
            .notIn("status", OrderStatus.UNPAID_ORDER, OrderStatus.REFUNDED_ORDER, OrderStatus.VOID_ORDER)
            .in("farmer", marketKeys)
            .lt("createdTime", DateUtil.atStartOfDay(new Date()))
            .execGroupByCount(substring(f("createdTime"), 1, 10));
    }
    
    public List<Integer> listKey(List<String> codes, List<String> marketKeys)
    {
        if (codes.isEmpty()) return new ArrayList<>();
        List<String> c = new ArrayList<>();
        codes.forEach(e -> {
            c.add(e + "1");
            c.add(e + "2");
            c.add(e + "3");
        });
        if (c.size() > 10000)
        {
            List<MktOrder> list = this.select()
                .in("farmer", marketKeys)
                .in("code", c.subList(0, 10000))
                .in("code", c.subList(10000, c.size()))
                .exec();
            return list.stream().map(MktOrder::getPkey).collect(Collectors.toList());
        }
        else
        {
            List<MktOrder> list = this.select().in("farmer", marketKeys).in("code", c).exec();
            return list.stream().map(MktOrder::getPkey).collect(Collectors.toList());
        }
    }
    
    public MktOrder getCodeLock(String code)
    {
        return repository.getCodeLock(code);
    }
    
    public Map<Integer, MktOrder> getMap(List<Integer> keys)
    {
        Map<Integer, MktOrder> res = new HashMap<>();
        if (keys.isEmpty()) return res;
        List<MktOrder> list = this.select().in("pkey", keys).exec();
        list.forEach(e -> res.put(e.getPkey(), e));
        return res;
    }
    
    public MktOrder getOrderByCodeAndThirdPartyNo(String code, String thirdPartOrderNo)
    {
        return this.selectOne().eq(F.code, code).eq(F.thirdPartyOrderNo, thirdPartOrderNo).exec();
    }
    
    public Integer getOrderPrintMaxNum(String farmer, Date start, Date end)
    {
        Number number = this.aggregation()
            .between("createdTime", start, end)
            .eq("farmer", farmer)
            .isNotNull("smallTicket")
            .notEq("distributionType", DistributionType.PICKUP)
            .execMax("smallTicket");
        int res = number.intValue();
        if (res == 0) res = 1000;
        return res;
        //        
        //        MktOrder order = this.selectOne()
        //            .ge("createdTime", date)
        //        .eq("farmer", farmer)
        //        .isNotNull("pickupCode")
        //        .notEq("distributionType", DistributionType.PICKUP)
        //        .sort("createdTime")
        //        .sort("pkey")
        //        .exec();
        //        if(order == null || StringUtil.isBlank(order.getPickupCode()))
        //            return 0;
        //        return Integer.valueOf(order.getPickupCode());
    }
    
    public List<String> listCode(List<Integer> keys)
    {
        List<MktOrder> list = this.select().in("pkey", keys).exec();
        return list.stream().map(MktOrder::getCode).collect(Collectors.toList());
    }
    
    public Boolean checkMemberCard(Integer member, Date start, Date end, Integer ascription, Integer card)
    {
        List<MktOrder> list = this.select()
            .eq("member", member)
            .notEq("status", OrderStatus.VOID_ORDER)
            .between("createdTime", start, end)
            .isNotNull("card")
            .eq("card", card)
            .eq("ascription", ascription)
            .exec();
        if (list != null && !list.isEmpty()) return true;
        return false;
    }
    
    public Map<String, Long> checkMemberCardV2(Integer member, Date start, Date end, Integer ascription)
    {
        return this.aggregation()
            .eq("member", member)
            .notEq("status", OrderStatus.VOID_ORDER)
            .between("createdTime", start, end)
            .isNotNull("card")
            .execGroupByCount("card", "pkey");
    }
    
    /** <统计支付人数>
     * <统计指定日期内市场支付人数>
     * @param       date    日期
     * @param ascription    归属
     * @param     farmer    市场
     * @return   支付人数
     */
    public Long countPayerNum(String date, Integer ascription, String farmer)
    {
        return this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .isNotNull(F.amto)
            .in(F.status,
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between(F.createdTime, DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date))
            .execCount(F.member);
    }
    
    /** <统计订单数>
     * <统计指定日期内市场支付成功订单>
     * @param       date    日期
     * @param ascription    归属
     * @param     farmer    市场
     * @return   支付人数
     */
    public Long countOrderNum(String date, Integer ascription, String farmer)
    {
        return this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .isNotNull(F.amto)
            .in(F.status,
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between(F.createdTime, DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date))
            .execCount(F.pkey);
    }
    
    /** <汇总金额相关>
     * <统计指定日期内市场金额>
     * @param       date    日期
     * @param ascription    归属
     * @param     farmer    市场
     * @return   支付人数
     */
    public List<ReportOrderDTO> sumOrderData(String date, Integer ascription, String farmer)
    {
        return this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .isNotNull(F.amto)
            .in(F.status,
                OrderStatus.DELIVERED_ORDER,
                OrderStatus.SHIPPED_ORDER,
                OrderStatus.WAIT_WRITEOFF_ORDER,
                OrderStatus.WAIT_ARRIVAL_ORDER,
                OrderStatus.ARRIVED_ORDER,
                OrderStatus.CONFIRM_ORDER)
            .between(F.createdTime, DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date))
            .sum(MktOrder.F.amto, MktOrder.F.amto)
            .sum(MktOrder.F.postage, MktOrder.F.postage)
            .sum(MktOrder.F.cardAmt, MktOrder.F.cardAmt)
            .sum(MktOrder.F.refundAmt, MktOrder.F.refundAmt)
            .execListDto(ReportOrderDTO.class);
    }
    
    public List<MktOrder> listCode(String code)
    {
        return this.select().like("code", code).exec();
    }
    
    public BigDecimal aggPresaleOrder(String pstime, Integer ascription)
    {
        Number number = this.aggregation()
            .isNotNull("pstime")
            .gt(substring(f("pstime"), 1, 10), pstime)
            .eq(F.ascription, ascription)
            .notEq(F.payType, PayType.ORDER_MSD)
            .notEq(F.status, OrderStatus.UNPAID_ORDER)
            .notEq(F.status, OrderStatus.REFUNDED_ORDER)
            .notEq(F.status, OrderStatus.VOID_ORDER)
            .between(F.createdTime, pstime + " 00:00:00", pstime + " 23:59:59")
            .execSum(F.amtn);
        if (number != null) return new BigDecimal(number.toString());
        return BigDecimal.ZERO;
    }
    
    public List<MktOrder> byNotExists()
    {
        return repository.byNotExists();
    }
}
