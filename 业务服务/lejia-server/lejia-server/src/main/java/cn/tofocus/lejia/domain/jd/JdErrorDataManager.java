package cn.tofocus.lejia.domain.jd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.SkuInfoOrderOpenResp;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.jd.JdSplitOrderLine;
import cn.tofocus.lejia.bean.dto.market.jd.JdOrderReport;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.enums.jd.OrderCorrelationStatus;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.domain.jdvop.JdVOPOrderManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdErrorDataManager
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
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private JdVOPOrderManager jdVOPOrderManager;
    
    @Autowired
    private JdOrderManager jdOrderManager;
    
    // 退款订单处理 退款明细没有主键处理
    public Boolean processRefund()
    {
        List<MktOrderRefundLine> list = orderRefundLineDao.select().isNull("refundPkey").exec();
        log.info("错误数据数量: {}", list.size());
        int i = 0;
        for(MktOrderRefundLine orl : list)
        {
            List<MktOrderLine> exec = orderLineDao.select().eq("pkey", orl.getOrderLinePkey()).exec();
            if(exec != null && exec.size() == 1)
            {
                List<MktOrderRefund> orList = orderRefundDao.select().eq("orderPkey", exec.get(0).getOrderPkey()).exec();
                if(orList != null && orList.size() == 1)
                {
                    orl.setRefundPkey(orList.get(0).getPkey());
                    i++;
                }
            }
            else
            {
                System.out.println("不是一个ol数量的订单: " + orl.getPkey());
            }
        }
        System.out.println("处理后的数量: " + i);
        orderRefundLineDao.updateAll(list);
        return true;
    }
    
    // 拆单 数量没拆 老数据问题处理
    public Boolean processOrderSplit()
    {
        // 找出没作废 又没有订单明细的数据
        List<MktOrder> byNotExists = orderDao.byNotExists();
        log.info("没有订单明细的数据有: {}条", byNotExists.size());
        Map<Integer,List<MktOrderLine>> orderMap = new HashMap<>();
        for(MktOrder o : byNotExists)
        {
            JdOrderCorrelation correlation = jdOrderCorrelationDao.get(o.getPkey());
            Long pOrder = correlation.getParentOrder();
            if(pOrder == null)
            {
                log.info("订单主键: {}找不到京东父类主键", o.getPkey());
                continue;
            }
            List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> queryOrderDetail =
                jdVOPOrderManager.queryOrderDetail(pOrder, null);
            for(com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp qoo : queryOrderDetail)
            {
                log.info("qoo: {}", JsonUtil.toString(qoo, true));
                List<Long> list = qoo.getChildJdOrderIdList();
                if(list != null && !list.isEmpty())
                {
                    List<JdOrderCorrelation> jocList = jdOrderCorrelationDao.select().in("jdCode", list).exec();
                    Map<Long, JdOrderCorrelation> jocMap = new HashMap<>();
                    List<Integer> orderKeyList = new ArrayList<>();
                    for(JdOrderCorrelation joc : jocList)
                    {
                        orderKeyList.add(joc.getPkey());
                        jocMap.put(joc.getJdCode(), joc);
                    }
                    List<MktOrderLine> exec = orderLineDao.select().in("orderPkey", orderKeyList).exec();
                    Map<Long,JdSplitOrderLine> map = new HashMap<>();
                    exec.forEach(e -> {
                       map.put(e.getSpace(), BeanUtil.beanFrom(JdSplitOrderLine.class, e));
                    });
                    Map<Long,List<JdSplitOrderLine>> mapList = new HashMap<>();
                    for(Long key : list)
                    {
                        List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> childOrderDetail =
                            jdVOPOrderManager.queryOrderDetail(key, null);
                        List<JdSplitOrderLine> v = new ArrayList<>();
                        mapList.put(key, v);
                        for(com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp cqoo : childOrderDetail)
                        {
                            List<SkuInfoOrderOpenResp> skuInfoList = cqoo.getSkuInfoList();
                            for(SkuInfoOrderOpenResp sioo : skuInfoList)
                            {
                                long skuId = sioo.getSkuId();
                                JdSplitOrderLine mktOrderLine = map.get(skuId);
                                if(sioo.getSkuNum() != mktOrderLine.getNum().intValue())
                                    mktOrderLine.setJdNum(sioo.getSkuNum());
                                mapList.get(key).add(mktOrderLine);
                            }
                        }
                    }
                    
                    
                    for(Long key : list)
                    {
                        if(mapList.containsKey(key))
                        {
                            List<JdSplitOrderLine> jsoList = mapList.get(key);
                            if(jocMap.containsKey(key))
                            {
                                JdOrderCorrelation joc = jocMap.get(key);
                                List<MktOrderLine> addOl = new ArrayList<>();
                                for(JdSplitOrderLine ol : jsoList)
                                {
                                    if(!orderMap.containsKey(joc.getPkey()))
                                    {
                                        orderMap.put(joc.getPkey(), new ArrayList<>());
                                    }
                                    if(ol.getJdNum() != null)
                                    {
                                        MktOrderLine add = BeanUtil.beanFrom(MktOrderLine.class, ol);
                                        add.setNum(ol.getJdNum());
                                        add.setCouponAmt(add.getPricen().multiply(BigDecimal.valueOf(add.getNum())));
                                        add.setOrderPkey(joc.getPkey());
                                        MktOrder mktOrder = orderDao.get(add.getOrderPkey());
                                        add.setStatus(mktOrder.getStatus());
                                        if(!ol.getOrderPkey().equals(joc.getPkey()))
                                        {
                                            add.setPkey(null);
                                        }
                                        addOl.add(add);
                                        orderMap.get(joc.getPkey()).add(add);
                                    }
                                }
                                orderLineDao.putAll(addOl);
                            }
                        }
                    }
                }
            }
        }
        log.info("需要处理的订单表梳理: {}", orderMap.keySet().size());
        for(Integer key : orderMap.keySet())
        {
            List<MktOrderLine> list = orderMap.get(key);
            MktOrder order = orderDao.get(key);
            BigDecimal amto = BigDecimal.ZERO;
            Integer num = 0;
            for(MktOrderLine ol : list)
            {
                amto = amto.add(ol.getCouponAmt());
                num = num + ol.getNum();
            }
            order.setAmto(amto);
            order.setAmtall(amto.add(order.getOldPostage()));
            order.setAmtn(amto.add(order.getPostage()));
            orderDao.update(order);
        }
        return true;
    }
    
    public void processOrderSplit(Integer pkey)
    {
        MktOrder o = orderDao.get(pkey);
        Map<Integer,List<MktOrderLine>> orderMap = new HashMap<>();
        JdOrderCorrelation correlation = jdOrderCorrelationDao.get(o.getPkey());
        Long pOrder = correlation.getParentOrder();
//        if(pOrder == null)
//        {
//            log.info("订单主键: {}找不到京东父类主键", o.getPkey());
//            continue;
//        }
        List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> queryOrderDetail =
            jdVOPOrderManager.queryOrderDetail(pOrder, null);
        for(com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp qoo : queryOrderDetail)
        {
            log.info("qoo: {}", JsonUtil.toString(qoo, true));
            List<Long> list = qoo.getChildJdOrderIdList();
            if(list != null && !list.isEmpty())
            {
                List<JdOrderCorrelation> jocList = jdOrderCorrelationDao.select().in("jdCode", list).exec();
                Map<Long, JdOrderCorrelation> jocMap = new HashMap<>();
                List<Integer> orderKeyList = new ArrayList<>();
                for(JdOrderCorrelation joc : jocList)
                {
                    orderKeyList.add(joc.getPkey());
                    jocMap.put(joc.getJdCode(), joc);
                }
                List<MktOrderLine> exec = orderLineDao.select().in("orderPkey", orderKeyList).exec();
                Map<Long,JdSplitOrderLine> map = new HashMap<>();
                exec.forEach(e -> {
                   map.put(e.getSpace(), BeanUtil.beanFrom(JdSplitOrderLine.class, e));
                });
                Map<Long,List<JdSplitOrderLine>> mapList = new HashMap<>();
                for(Long key : list)
                {
                    List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> childOrderDetail =
                        jdVOPOrderManager.queryOrderDetail(key, null);
                    List<JdSplitOrderLine> v = new ArrayList<>();
                    mapList.put(key, v);
                    for(com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp cqoo : childOrderDetail)
                    {
                        List<SkuInfoOrderOpenResp> skuInfoList = cqoo.getSkuInfoList();
                        for(SkuInfoOrderOpenResp sioo : skuInfoList)
                        {
                            long skuId = sioo.getSkuId();
                            JdSplitOrderLine mktOrderLine = map.get(skuId);
                            if(sioo.getSkuNum() != mktOrderLine.getNum().intValue())
                                mktOrderLine.setJdNum(sioo.getSkuNum());
                            mapList.get(key).add(mktOrderLine);
                        }
                    }
                }
                
                System.out.println("mapList: " + JsonUtil.toString(mapList, true));
                for(Long key : list)
                {
                    if(mapList.containsKey(key))
                    {
                        List<JdSplitOrderLine> jsoList = mapList.get(key);
                        if(jocMap.containsKey(key))
                        {
                            JdOrderCorrelation joc = jocMap.get(key);
                            List<MktOrderLine> addOl = new ArrayList<>();
                            for(JdSplitOrderLine ol : jsoList)
                            {
                                if(!orderMap.containsKey(joc.getPkey()))
                                {
                                    orderMap.put(joc.getPkey(), new ArrayList<>());
                                }
                                if(ol.getJdNum() != null)
                                {
                                    MktOrderLine add = BeanUtil.beanFrom(MktOrderLine.class, ol);
                                    add.setNum(ol.getJdNum());
                                    add.setCouponAmt(add.getPricen().multiply(BigDecimal.valueOf(add.getNum())));
                                    add.setOrderPkey(joc.getPkey());
                                    MktOrder mktOrder = orderDao.get(add.getOrderPkey());
                                    add.setStatus(mktOrder.getStatus());
                                    if(!ol.getOrderPkey().equals(joc.getPkey()))
                                    {
                                        add.setPkey(null);
                                    }
                                    addOl.add(add);
                                    orderMap.get(joc.getPkey()).add(add);
                                }
                            }
                            orderLineDao.putAll(addOl);
                        }
                    }
                }
            }
        }
        log.info("需要处理的订单表梳理: {}", orderMap.keySet().size());
        System.out.println("需要处理的订单表梳理: " + JsonUtil.toString(orderMap, true));
        for(Integer key : orderMap.keySet())
        {
            List<MktOrderLine> list = orderMap.get(key);
            MktOrder order = orderDao.get(key);
            BigDecimal amto = BigDecimal.ZERO;
            Integer num = 0;
            for(MktOrderLine ol : list)
            {
                amto = amto.add(ol.getCouponAmt());
                num = num + ol.getNum();
            }
            order.setAmto(amto);
            order.setAmtall(amto.add(order.getOldPostage()));
            order.setAmtn(amto.add(order.getPostage()));
            orderDao.update(order);
        }
    }
    
    public void jdRefundAmtError()
    {
        PageResult<JdOrderReport> pageResult = jdOrderManager.reportByOrder(0,20000,null,null,null);
        List<JdOrderReport> list = new ArrayList<>();
        for(JdOrderReport j : pageResult.getContent())
        {
            if(j.getRefundGoodsAmt().compareTo(BigDecimal.ZERO) > 0 
                &&j.getAmto().compareTo(j.getRefundGoodsAmt()) != 0)
            {
                list.add(j);
            }
        }
        log.info("数据不同的有{}条", list.size());
//        log.info("list: {}", JsonUtil.toString(list, true));
//        for(JdOrderReport j : list)
//        {
//            List<MktOrderRefund> refundList = orderRefundDao.listOrderPkey(j.getOrderPkey());
//            for(MktOrderRefund or : refundList)
//            {
//                List<MktOrderRefundLine> exec = orderRefundLineDao.select().eq("refundPkey", or.getPkey()).exec();
//                for(MktOrderRefundLine orl : exec)
//                {
//                    MktOrderLine orderLine = orderLineDao.get(orl.getOrderLinePkey());
//                    orl.setRefundAmt(orderLine.getPricen().multiply(new BigDecimal(orl.getRefundNum()).setScale(2, BigDecimal.ROUND_HALF_UP)));
//                }
//                orderRefundLineDao.updateAll(exec);
//                System.out.println("处理数据: " + exec.size());
//            }
//        }
    }
    
}
