package cn.tofocus.lejia.domain.jd;

import static cn.tofocus.core.query.exp.ExpUtil.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.goods.JdGwcOnInfo;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDetails;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.jd.JdOrderExcel;
import cn.tofocus.lejia.bean.dto.jd.JdOrderLineExcel;
import cn.tofocus.lejia.bean.dto.market.jd.JdOrderGoodsReport;
import cn.tofocus.lejia.bean.dto.market.jd.JdOrderOnPage;
import cn.tofocus.lejia.bean.dto.market.jd.JdOrderReport;
import cn.tofocus.lejia.bean.dto.market.jd.JdOrderTotal;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.*;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;

@Component
public class JdOrderManager
{
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private MktOrderTagDao orderTagDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;

    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;

    @Autowired
    private MktOrderDescDao descDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private JdAppOrderManager jdAppOrderManager;
    
    @Value("${zyysc.app.pickup.write.off.url:https://small.xinanshizu.com/writeOffIntegralPresale}")
    private String pickupWriteOffUrl;
    
    public <T extends JdOrderOnPage> PageResult<T> queryOrder(int page, int pagesize, String startDate, String endDate,
        OrderStatus status, String code, String mobile, List<Integer> tags, Class<T> clazz)
    {
        PageResult<T> result = PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        
        List<Integer> orderIds = new ArrayList<>();
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktOrderDesc> exec = descDao.select().like("mobile", mobile).exec();
            if (exec.isEmpty())
                return result;
            for (MktOrderDesc od : exec)
                orderIds.add(od.getPkey());
        }
        if (tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascriptionPkey);
            if (list == null || list.isEmpty())
                return result;
            for (MktOrderTag ot : list)
                orderIds.add(ot.getOrderPkey());
        }
        if (!orderIds.isEmpty())
        {
            orderIds = orderIds.stream().distinct().collect(Collectors.toList());
        }
        PageResult<T> pageResult = orderDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .in("pkey", orderIds)
            .eq("orderType", OrderType.INTEGRAL_JD_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("status", status)
            .eq("ascription", ascriptionPkey)
            .like("code", code)
            .iF(startDate != null)
            .between("createdTime", startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .sort("createdTime")
            .sort("pkey")
            .execDto(clazz);
        List<T> content = pageResult.getContent();
        if (!content.isEmpty())
        {
            List<Integer> orderPkeys = new ArrayList<>();
            for (T jo : content)
                orderPkeys.add(jo.getPkey());
            // 批量聚合退款金额与标签名，避免逐单 N+1 查询（aggRefundAmtre / getTagName 每单各 1 次）
            Map<Integer, BigDecimal> refundMap = orderRefundDao.mapAggRefundAmtre(orderPkeys);
            Map<Integer, String> tagMap = orderTagDao.mapTagName(orderPkeys);
            for (T jo : content)
            {
                jo.setRefundAmt(refundMap.getOrDefault(jo.getPkey(), BigDecimal.ZERO));
                jo.setTagName(tagMap.getOrDefault(jo.getPkey(), ""));
                jo.setGoodsPrice(jo.getAmto());
            }
        }
        return pageResult;
    }
    
    public JdOrderTotal orderSum(String startDate, String endDate, OrderStatus status, String code, String mobile,
        List<Integer> tags)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        JdOrderTotal res = new JdOrderTotal();
        res.setCount(0l);
        res.setSum(BigDecimal.ZERO);
        List<Integer> orderIds = new ArrayList<>();
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktOrderDesc> exec = descDao.select().like("mobile", mobile).exec();
            if (exec.isEmpty())
                return res;
            for (MktOrderDesc od : exec)
                orderIds.add(od.getPkey());
        }
        if (tags != null && !tags.isEmpty())
        {
            List<MktOrderTag> list = orderTagDao.listTag(tags, ascriptionPkey);
            if (list == null || list.isEmpty())
                return res;
            for (MktOrderTag ot : list)
                orderIds.add(ot.getOrderPkey());
        }
        if (!orderIds.isEmpty())
        {
            orderIds = orderIds.stream().distinct().collect(Collectors.toList());
        }
        PageResult<JdOrderTotal> execDto = orderDao.aggregation()
            .in("pkey", orderIds)
            .eq("orderType", OrderType.INTEGRAL_JD_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("ascription", ascriptionPkey)
            .like("code", code)
            .iF(startDate != null)
            .between("createdTime", startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .sum("amtn", "sum")
            .count("pkey", "count")
            .execDto(JdOrderTotal.class);
        return execDto.getContent().get(0);
    }
    
    public List<JdOrderExcel> exportOrder(String startDate, String endDate, OrderStatus status, String code,
        String mobile, List<Integer> tags)
    {
        // 用 JdOrderOnPage 而非 JdOrderFullOnPage：jdCode 改用 mapOrderCodeJdCode 批量解析，
        // 避免 @JoinProperty(referencedName=orderCode)（非 pkey）在大数据量下栈溢出
        PageResult<JdOrderOnPage> pageResult =
            queryOrder(0, 50000, startDate, endDate, status, code, mobile, tags, JdOrderOnPage.class);
        List<JdOrderOnPage> content = pageResult.getContent();
        Map<String, String> codeJdCodeMap = mapOrderCodeJdCode(content);
        List<JdOrderExcel> list = BeanUtil.beanListFrom(JdOrderExcel.class, content);
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).setJdOrderId(codeJdCodeMap.get(content.get(i).getCode()));
        }
        return list;
    }

    public List<JdOrderLineExcel> exportOrderLine(String startDate, String endDate, OrderStatus status, String code,
        String mobile, List<Integer> tags)
    {
        PageResult<JdOrderOnPage> pageResult =
            queryOrder(0, 50000, startDate, endDate, status, code, mobile, tags, JdOrderOnPage.class);
        List<JdOrderOnPage> content = pageResult.getContent();
        if (content.isEmpty())
            return new ArrayList<>();
        List<Integer> key = new ArrayList<>();
        Map<Integer, JdOrderOnPage> map = new HashMap<>();
        for (JdOrderOnPage jo : content)
        {
            key.add(jo.getPkey());
            map.put(jo.getPkey(), jo);
        }
        // 京东订单号：joinSelect 不支持跨表用 MktOrder.code 关联 JdOrderCorrelation.orderCode，故按订单 code 单独解析 jdCode
        Map<String, String> codeJdCodeMap = mapOrderCodeJdCode(content);
        // 关联 MktOrderDesc（收货信息/发货时间）与 JdGoods（一/二/三级分类）查出明细；
        // orderPkey 按 9000 分批，避免 .in() 超过框架 10000 参数上限
        List<JdOrderLineExcel> list = new ArrayList<>();
        for (List<Integer> batch : Lists.partition(key, 9000))
            list.addAll(queryOrderLineExcel(batch));
        List<JdOrderLineExcel> excelList = new ArrayList<>();
        for (JdOrderLineExcel e : list)
        {
            JdOrderOnPage jo = map.get(e.getOrderPkey());
            if (jo == null)
                continue;
            // 订单维度字段（订单号/状态/支付/金额等）由订单数据补齐
            BeanUtils.copyProperties(jo, e);
            e.setJdOrderId(codeJdCodeMap.get(jo.getCode()));
            if (e.getLineRefundAmt() == null || e.getLineRefundAmt().compareTo(BigDecimal.ZERO) <= 0)
                e.setLineRefundAmt(BigDecimal.ZERO);
            e.setSumGoodsAmt(e.getLineAmt().subtract(e.getLineRefundAmt()));
            if (e.getFhTime() != null)
                e.setPstime(DateUtil.formatDate(e.getFhTime()));
            excelList.add(e);
        }
        return excelList;
    }

    /**
     * 按一批 orderPkey 关联 MktOrderDesc、JdGoods 查出明细行。
     * 单批 orderPkey 数须 ≤ 9000，以满足框架 .in() 的 10000 参数上限；大批量由调用方分批后合并。
     */
    private List<JdOrderLineExcel> queryOrderLineExcel(List<Integer> orderPkeys)
    {
        if (orderPkeys == null || orderPkeys.isEmpty())
            return new ArrayList<>();
        return orderLineDao.joinSelect()
            .in(MktOrderLine.F.orderPkey, orderPkeys)
            .as(MktOrderLine.F.orderPkey)
            .as(MktOrderLine.F.goodsName)
            .as(MktOrderLine.F.spaceName)
            .as(MktOrderLine.F.num)
            .as(MktOrderLine.F.price, JdOrderLineExcel.F.salePrice)
            .as(MktOrderLine.F.pricen)
            .as(MktOrderLine.F.couponAmt, JdOrderLineExcel.F.lineAmt)
            .as(MktOrderLine.F.refundAmt, JdOrderLineExcel.F.lineRefundAmt)
            .join(MktOrderDesc.class, MktOrderLine.F.orderPkey, MktOrderDesc.F.pkey)
            .as(MktOrderDesc.F.logistics)
            .as(MktOrderDesc.F.name, JdOrderLineExcel.F.receiver)
            .as(MktOrderDesc.F.mobile, JdOrderLineExcel.F.receiverMobile)
            .as(MktOrderDesc.F.addr, JdOrderLineExcel.F.receiverAddr)
            .as(MktOrderDesc.F.fhTime)
            .join(JdGoods.class, MktOrderLine.F.space, JdGoods.F.pkey)
            .as(JdGoods.F.categoryName)
            .as(JdGoods.F.twoCategoryName)
            .as(JdGoods.F.threeCategoryName)
            .endJoin()
            .sort(MktOrderLine.F.orderPkey, false)
            .sort(MktOrderLine.F.pkey, false)
            .exec(JdOrderLineExcel.class);
    }

    /**
     * 按订单 code 关联 jd_order_correlation.order_code，建立 code -> jdCode(京东订单号, String) 映射
     */
    private Map<String, String> mapOrderCodeJdCode(List<JdOrderOnPage> orders)
    {
        Map<String, String> map = new HashMap<>();
        List<String> codes = new ArrayList<>();
        for (JdOrderOnPage o : orders)
        {
            if (StringUtils.isNotBlank(o.getCode()))
                codes.add(o.getCode());
        }
        if (codes.isEmpty())
            return map;
        // 框架 .in() 单次参数上限 10000，按 9000 分批查询后合并
        for (List<String> batch : Lists.partition(codes, 9000))
        {
            for (JdOrderCorrelation joc : jdOrderCorrelationDao.select()
                .in(JdOrderCorrelation.F.orderCode, batch).exec())
            {
                map.put(joc.getOrderCode(), joc.getJdCode() == null ? null : joc.getJdCode().toString());
            }
        }
        return map;
    }

    public JdOrderDetails loadOrder(int pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktOrder order = orderDao.selectOne().eq("pkey", pkey).exec();
        JdOrderDetails dto = new JdOrderDetails();
        BeanUtils.copyProperties(order, dto);
        JdOrderCorrelation joc = jdOrderCorrelationDao.getByCode(order.getCode());
        if (joc != null)
            dto.setJdOrderId(joc.getJdCode());
        if (dto.getOrderType().getIndex() != 0)
            dto.setPointn(0);
        if (dto.getPointn() == null)
            dto.setPointn(0);
        MktExpress e = expressDao.selectOne().eq("orderId", pkey).eq("code", order.getCode()).exec();
        if (e != null)
        {
            dto.setExpressStatus(e.getStatus());
        }
        
        MktAppAddrDTO addDto = new MktAppAddrDTO();
        dto.setPstime("");
        MktOrderDesc orderDesc = descDao.get(pkey);
        dto.setLogistics("");
        dto.setKdCode("");
        if (orderDesc != null)
        {
            addDto.setAddrDetail(orderDesc.getAddr());
            addDto.setMobile(orderDesc.getMobile());
            addDto.setName(orderDesc.getName());
            addDto.setEnabled(true);
            addDto.setDistance(orderDesc.getDistance());
            dto.setRemark(orderDesc.getRemark());
            dto.setAddr(addDto);
            String logistics = orderDesc.getLogistics();
            dto.setLogistics(logistics == null ? "" : logistics);
            String kdCode = orderDesc.getKdCode();
            dto.setKdCode(kdCode == null ? "" : kdCode);
            if (StringUtils.isBlank(dto.getPstime())
                && (Constant.Operation + ascription).equals(CurrentSession.marketPkey()))
                dto.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
            
        }
        
        List<JdGwcOnInfo> list = new ArrayList<>();
        List<MktOrderLine> lineList = orderLineDao.select().eq("orderPkey", pkey).exec();
        for (MktOrderLine line : lineList)
        {
            JdGwcOnInfo gwcDto = new JdGwcOnInfo();
            BeanUtils.copyProperties(line, gwcDto);
            gwcDto.setSpace(line.getSpace());
            gwcDto.setGoods(line.getGoods());
            JdGoods jdGoods = jdGoodsDao.get(line.getSpace());
            gwcDto.setPrice(line.getPricen());
            if (jdGoods != null)
            {
                if (jdGoods.getPhoto1() != null && !jdGoods.getPhoto1().isEmpty())
                    gwcDto.setPhoto(jdGoods.getPhoto1().get(0));
            }
            
            list.add(gwcDto);
        }
        dto.setList1(list);
        if (order.getStatus().equals(OrderStatus.REFUND_APPLICATION_ORDER)
            || order.getStatus().equals(OrderStatus.REFUNDED_ORDER))
        {
            MktOrderRefund refund = orderRefundDao.selectOne().eq("orderPkey", pkey).exec();
            dto.setRefund(refund);
        }
        if (dto.getMember() != null)
        {
            MktMember mktMember = memberDao.get(dto.getMember());
            if (mktMember != null)
                dto.setMemberName(mktMember.getName());
        }
        
        dto.setPostFree(false);
        if (dto.getPickupAmt() != null && dto.getPickupAmt().compareTo(BigDecimal.ZERO) <= 0)
            dto.setPickupAmt(new BigDecimal("0.01"));
        dto.setPickupCode(null);
        return dto;
    }
    
    public PageResult<JdOrderReport> reportByOrder(int page, int pagesize, String startDate, String endDate,
        String code)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        PageResult<JdOrderReport> res = orderLineDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .sum(prod(f(MktOrderLine.F.price), f(MktOrderLine.F.num)), JdOrderReport.F.jdGoodsAmt)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .like(MktOrder.F.code, code)
            .iF(startDate != null && endDate != null)
            .between(MktOrder.F.createdTime, startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .groupby(MktOrder.F.pkey, JdOrderReport.F.orderPkey)
            .groupby(MktOrder.F.code)
            .groupby(MktOrder.F.amto)
            .groupby(MktOrder.F.amtn)
            .groupby(MktOrder.F.weixinAmt)
            .groupby(MktOrder.F.otherAmt)
            .groupby(MktOrder.F.refundWeixinAmt)
            .groupby(MktOrder.F.refundOtherAmt)
            .groupby(MktOrder.F.postage)
            .groupby(MktOrder.F.oldPostage)
            .groupby(MktOrder.F.refundAmt)
            .endJoin()
            .sort(0, MktOrder.F.pkey)
            .exec(JdOrderReport.class);
        // 收集有退款的订单
        List<Integer> refundOrderPkeys = new ArrayList<>();
        for (JdOrderReport line : res)
        {
            if (line.getRefundAmt() != null && line.getRefundAmt().compareTo(BigDecimal.ZERO) > 0)
                refundOrderPkeys.add(line.getOrderPkey());
        }

        if (!refundOrderPkeys.isEmpty())
        {
            // 批量查询所有退款单（内部自动分批，避免 N+1）
            Map<Integer, List<MktOrderRefund>> refundsByOrderPkey =
                orderRefundDao.mapOrderPkeyRefunds(refundOrderPkeys);

            // 按订单收集已退款状态的退款单 pkey，同时累加邮费退款
            Map<Integer, List<Integer>> refundPkeysByOrderPkey = new HashMap<>();
            List<RefundStatus> refundedStatuses = RefundStatus.refundedStatus();
            for (JdOrderReport line : res)
            {
                if (line.getRefundAmt() == null || line.getRefundAmt().compareTo(BigDecimal.ZERO) <= 0)
                    continue;
                List<MktOrderRefund> refundList =
                    refundsByOrderPkey.getOrDefault(line.getOrderPkey(), new ArrayList<>());
                BigDecimal refundPostage = BigDecimal.ZERO;
                BigDecimal refundJdPostage = BigDecimal.ZERO;
                List<Integer> finalRefundPkeyList = new ArrayList<>();
                for (MktOrderRefund refund : refundList)
                {
                    if (refundedStatuses.contains(refund.getStatus()))
                    {
                        refundPostage = refundPostage.add(refund.getRefundPostage());
                        if (refund.getRefundJdPostage() != null)
                            refundJdPostage = refundJdPostage.add(refund.getRefundJdPostage());
                        finalRefundPkeyList.add(refund.getPkey());
                    }
                }
                line.setRefundPostage(refundPostage);
                line.setRefundJdPostage(refundJdPostage);
                if (!finalRefundPkeyList.isEmpty())
                    refundPkeysByOrderPkey.put(line.getOrderPkey(), finalRefundPkeyList);
            }

            // 批量查询所有退款明细，按订单聚合退款商品金额
            if (!refundPkeysByOrderPkey.isEmpty())
            {
                // 构建 refundPkey -> orderPkey 映射
                Map<Integer, Integer> refundToOrder = new HashMap<>();
                for (Map.Entry<Integer, List<Integer>> entry : refundPkeysByOrderPkey.entrySet())
                {
                    for (Integer refundPkey : entry.getValue())
                        refundToOrder.put(refundPkey, entry.getKey());
                }

                List<Integer> allRefundPkeys = new ArrayList<>(refundToOrder.keySet());
                Map<Integer, BigDecimal> refundGoodsAmtByOrder = new HashMap<>();
                Map<Integer, BigDecimal> refundJdGoodsAmtByOrder = new HashMap<>();

                // 分批查询（每批 9000，避免 .in() 超过框架参数上限 10000）
                for (List<Integer> batch : Lists.partition(allRefundPkeys, 9000))
                {
                    List<MktOrderRefundLine> refundLines = orderRefundLineDao.listRefundPkeys(batch);
                    for (MktOrderRefundLine rfl : refundLines)
                    {
                        Integer orderPkey = refundToOrder.get(rfl.getRefundPkey());
                        if (orderPkey != null)
                        {
                            if (rfl.getRefundAmt() != null)
                                refundGoodsAmtByOrder.merge(orderPkey, rfl.getRefundAmt(), BigDecimal::add);
                            if (rfl.getRefundJd() != null)
                                refundJdGoodsAmtByOrder.merge(orderPkey, rfl.getRefundJd(), BigDecimal::add);
                        }
                    }
                }

                // 填充退款商品金额到各订单报表
                for (JdOrderReport line : res)
                {
                    if (refundPkeysByOrderPkey.containsKey(line.getOrderPkey()))
                    {
                        line.setRefundGoodsAmt(
                            refundGoodsAmtByOrder.getOrDefault(line.getOrderPkey(), BigDecimal.ZERO));
                        line.setRefundJdGoodsAmt(
                            refundJdGoodsAmtByOrder.getOrDefault(line.getOrderPkey(), BigDecimal.ZERO));
                    }
                }
            }
        }

        // 计算合计金额
        for (JdOrderReport line : res)
        {
            BigDecimal amto = line.getAmto() == null ? BigDecimal.ZERO : line.getAmto();
            BigDecimal postage = line.getPostage() == null ? BigDecimal.ZERO : line.getPostage();
            BigDecimal refundGoodsAmt = line.getRefundGoodsAmt() == null ? BigDecimal.ZERO : line.getRefundGoodsAmt();
            BigDecimal refundPostage = line.getRefundPostage() == null ? BigDecimal.ZERO : line.getRefundPostage();
            line.setAmt(amto.add(postage).subtract(refundGoodsAmt).subtract(refundPostage));

            BigDecimal jdGoodsAmt = line.getJdGoodsAmt() == null ? BigDecimal.ZERO : line.getJdGoodsAmt();
            BigDecimal oldPostage = line.getOldPostage() == null ? BigDecimal.ZERO : line.getOldPostage();
            BigDecimal refundJdGoodsAmt =
                line.getRefundJdGoodsAmt() == null ? BigDecimal.ZERO : line.getRefundJdGoodsAmt();
            BigDecimal refundJdPostage =
                line.getRefundJdPostage() == null ? BigDecimal.ZERO : line.getRefundJdPostage();
            line.setJdAmt(jdGoodsAmt.add(oldPostage).subtract(refundJdGoodsAmt).subtract(refundJdPostage));
            boolean isOld = false;
            if(line.getWeixinAmt() == null && line.getOtherAmt() == null)
            {
                isOld = true;
            }
            if(line.getWeixinAmt() == null)
                line.setWeixinAmt(BigDecimal.ZERO);
            if(line.getOtherAmt() == null)
                line.setOtherAmt(BigDecimal.ZERO);
            if(line.getRefundWeixinAmt() == null)
                line.setRefundWeixinAmt(BigDecimal.ZERO);
            if(line.getRefundOtherAmt() == null)
                line.setRefundOtherAmt(BigDecimal.ZERO);
            line.setAmt(line.getAmt().subtract(line.getWeixinAmt()));
            line.setAmt(line.getAmt().add(line.getRefundWeixinAmt()));
            if(isOld)
                line.setOtherAmt(line.getAmt());
        }
        return res;
    }
    
    public JdOrderReport sumReportByOrder(String startDate, String endDate, String code)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        
        List<JdOrderReport> orderLineAgg = orderLineDao.joinSelect()
            .sum(prod(f(MktOrderLine.F.price), f(MktOrderLine.F.num)), JdOrderReport.F.jdGoodsAmt)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .like(MktOrder.F.code, code)
            .iF(startDate != null && endDate != null)
            .between(MktOrder.F.createdTime, startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .endJoin()
            .exec(JdOrderReport.class);
        
        List<JdOrderReport> orderAgg = orderDao.aggregation()
            .sum(MktOrder.F.amto, MktOrder.F.amto)
            .sum(MktOrder.F.postage, MktOrder.F.postage)
            .sum(MktOrder.F.oldPostage, MktOrder.F.oldPostage)
            .sum(MktOrder.F.refundAmt, MktOrder.F.refundAmt)
            .sum(MktOrder.F.refundJd, MktOrder.F.refundJd)
            .sum(MktOrder.F.weixinAmt, MktOrder.F.weixinAmt)
            .sum(MktOrder.F.refundWeixinAmt, MktOrder.F.refundWeixinAmt)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .in(MktOrder.F.status, OrderStatus.summaryStatus())
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .like(MktOrder.F.code, code)
            .iF(startDate != null && endDate != null)
            .between(MktOrder.F.createdTime, startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .execListDto(JdOrderReport.class);

        JdOrderReport orderLineSum = CollectionUtil.isEmpty(orderLineAgg) ? new JdOrderReport() : orderLineAgg.get(0);
        JdOrderReport orderSum = CollectionUtil.isEmpty(orderAgg) ? new JdOrderReport() : orderAgg.get(0);
        JdOrderReport report = BeanUtil.beanFrom(JdOrderReport.class, orderSum);
        report.setJdGoodsAmt(orderLineSum.getJdGoodsAmt());

        BigDecimal amto = report.getAmto() == null ? BigDecimal.ZERO : report.getAmto();
        BigDecimal postage = report.getPostage() == null ? BigDecimal.ZERO : report.getPostage();
        BigDecimal refundAmt = report.getRefundAmt() == null ? BigDecimal.ZERO : report.getRefundAmt();
        report.setAmt(amto.add(postage).subtract(refundAmt));
        BigDecimal weixinAmt = report.getWeixinAmt() == null ? BigDecimal.ZERO : report.getWeixinAmt();
        BigDecimal refundWeixinAmt = report.getRefundWeixinAmt() == null ? BigDecimal.ZERO : report.getRefundWeixinAmt();
        report.setAmt(report.getAmt().subtract(weixinAmt).add(refundWeixinAmt));
        
        BigDecimal jdGoodsAmt = report.getJdGoodsAmt() == null ? BigDecimal.ZERO : report.getJdGoodsAmt();
        BigDecimal oldPostage = report.getOldPostage() == null ? BigDecimal.ZERO : report.getOldPostage();
        BigDecimal refundJd = report.getRefundJd() == null ? BigDecimal.ZERO : report.getRefundJd();
        report.setJdAmt(jdGoodsAmt.add(oldPostage).subtract(refundJd));
        return report;
    }
    
    public PageResult<JdOrderGoodsReport> reportByGoods(int page, int pagesize, String startDate, String endDate,
        String goodsName)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        return orderLineDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .groupby(MktOrderLine.F.space, JdOrderGoodsReport.F.pkey)
            .groupby(MktOrderLine.F.goodsName, JdOrderGoodsReport.F.goodsName)
            .groupby(MktOrderLine.F.spaceName, JdOrderGoodsReport.F.spaceName)
            .count(MktOrderLine.F.pkey, JdOrderGoodsReport.F.orderCount)
            .sum(diff(f(MktOrderLine.F.num), f(MktOrderLine.F.refundNum)), JdOrderGoodsReport.F.goodsCount)
            .sum(diff(f(MktOrderLine.F.couponAmt), f(MktOrderLine.F.refundAmt)), JdOrderGoodsReport.F.amt)
            .like(MktOrderLine.F.goodsName, goodsName)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .in(MktOrder.F.status, OrderStatus.summaryStatusWithoutRefunded())
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .iF(startDate != null && endDate != null)
            .between(MktOrder.F.createdTime, startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .endJoin()
            .exec(JdOrderGoodsReport.class);
    }
    
    public JdOrderGoodsReport sumReportByGoods(String startDate, String endDate, String goodsName)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        List<JdOrderGoodsReport> list = orderLineDao.joinSelect()
            .sum(diff(f(MktOrderLine.F.couponAmt), f(MktOrderLine.F.refundAmt)), JdOrderGoodsReport.F.amt)
            .like(MktOrderLine.F.goodsName, goodsName)
            .join(MktOrder.class, MktOrderLine.F.orderPkey, MktOrder.F.pkey)
            .eq(MktOrder.F.orderType, OrderType.INTEGRAL_JD_ORDER)
            .in(MktOrder.F.status, OrderStatus.summaryStatusWithoutRefunded())
            .eq(MktOrder.F.ascription, ascriptionPkey)
            .iF(startDate != null && endDate != null)
            .between(MktOrder.F.createdTime, startDate + " 00:00:00", endDate + " 23:59:59")
            .endIf()
            .endJoin()
            .exec(JdOrderGoodsReport.class);
        if (CollectionUtil.isEmpty(list))
            return new JdOrderGoodsReport();
        return list.get(0);
    }
    
    public JdOrderDeliveryInfo queryDeliveryInfo(Integer pkey)
    {
        return jdAppOrderManager.queryDeliveryInfo(pkey);
    }
}
