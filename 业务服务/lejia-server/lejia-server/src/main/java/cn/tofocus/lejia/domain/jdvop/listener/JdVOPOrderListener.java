package cn.tofocus.lejia.domain.jdvop.listener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import cn.tofocus.common.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jd.open.api.sdk.domain.vopxx.MsgRecordProvider.response.queryTransByVopNormal.VopBizTransMessage;

import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo.LogisticInfo;
import cn.tofocus.lejia.bean.dto.app.jd.JdOrderDeliveryInfo.TrackInfo;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.jd.JdOrderRefundManager;
import cn.tofocus.lejia.domain.jdvop.bean.msg.*;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.util.NumberUtils;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdVOPOrderListener implements MsgListener<VopBizTransMessage, String>
{
    public static final String PIPE_NAME = "zyysc.jd.vop.msg.order";
    
    @Autowired
    private JdAppOrderManager jdAppOrderManager;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;

    private ConcurrentMap<String, Timeout> timeoutMap = new ConcurrentHashMap<String, Timeout>();
    
    @Autowired
    private JdOrderRefundManager jdOrderRefundManager;
    
    private Timer timer;
    
    @PostConstruct
    public void init()
    {
        if (timer == null)
        {
            timer = new HashedWheelTimer();
        }
    }
    
    public void close()
    {
        try
        {
            long start = System.currentTimeMillis();
            while (!timeoutMap.isEmpty())
            {
                long now = System.currentTimeMillis();
                if (now - start > TimeUnit.SECONDS.toMillis(90))
                    break;
                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleMessage(String pipeId, String correlationId, VopBizTransMessage msg)
        throws Exception
    {
        switch (msg.getType())
        {
            case 1:
            {
                // 订单拆分消息
                // {"id":推送id, "result" : {"pOrder" :父订单id} , "type": 1, "time":推送时间}
                // 京东订单可能会被多次拆单； 例如：订单1 首先被拆成订单2、订单3；然后订单2有继续被拆成订单4、订单5；最终订单1的子单是订单3、订单4、订单5；每拆一次单我们都会发送一次拆单消息，但父订单号只会传递订单1（原始单），需要通过查询接口获取到最新所有子单，进行相关更新；
                JdVOPOrderSplitMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderSplitMsg.class);
                orderSplitMsg(m);
                break;
            }
            case 5:
            {
                // 订单妥投消息
                // {"id":推送id, "result":{"orderId":"京东订单编号", "state":"1是妥投，2是拒收"}, "type" : 5, "time":推送时间}
                JdVOPOrderDeliveryStateChangeMsg m =
                    JsonUtil.getBean(msg.getContent(), JdVOPOrderDeliveryStateChangeMsg.class);
                orderDeliveryStateChangeMsg(m);
                break;
            }
            case 10:
            {
                // 订单取消消息
                // {"id":推送id, "result":{"orderId": 京东订单编号 ,"state": 取消结果(0:失败,1:成功)}, "type" : 10, "time":推送时间}
                // state字段, 0:取消失败,1:取消成功
                JdVOPOrderCancelMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderCancelMsg.class);
                orderCancelMsg(new RetriableMsg<>(Util.getUUID(), 0, m));
                break;
            }
            case 12:
            {
                // 配送单生成成功消息
                // {"id":推送id, "result":{"orderId": 京东订单编号 }, "type" : 12, "time":推送时间}
                JdVOPOrderDeliveryInfoMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderDeliveryInfoMsg.class);
                orderDeliveryInfoMsg(m);
                break;
            }
            case 14:
            {
                // 支付失败消息
                // {"id":推送id, "result":{"orderId": 京东订单编号}, "type" : 14, "time":推送时间}
                JdVOPOrderPayFailedMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderPayFailedMsg.class);
                orderPayFailedMsg(m);
                break;
            }
            case 15:
            {
                // 7天未支付取消消息/未确认取消消息
                // {"id":推送id, "result":{"orderId": 京东订单编号, "cancelType": 取消类型}， "type" : 15, "time":推送时间}
                // cancelType, 1: 7天未支付取消消息; 2: 未确认取消
                JdVOPOrderAutoCancelMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderAutoCancelMsg.class);
                orderAutoCancelMsg(new RetriableMsg<>(Util.getUUID(), 0, m));
                break;
            }
            case 18:
            {
                // 订单等待确认收货消息
                // {"id":推送id, "result":{"orderId":京东订单号} "type" : 18, "time":推送时间}
                JdVOPOrderWaitingConfirmReceiptMsg m =
                    JsonUtil.getBean(msg.getContent(), JdVOPOrderWaitingConfirmReceiptMsg.class);
                orderWaitingConfirmReceiptMsg(m);
                break;
            }
            case 25:
            {
                // 新订单消息
                // {"id":推送id, "result":{"orderId":京东订单号, "pin":"京东账号"} "type" : 25, "time":推送时间(订单创建时间)}
                JdVOPOrderPaidMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderPaidMsg.class);
                orderPaidMsg(m);
                break;
            }
            case 31:
            {
                // 订单完成消息
                // {"id":推送id,"result":{"jdOrderState":19,"pin":客户PIN,"completeTime":"2023-07-25 08:21:23","orderId":订单号},"type":31,"time":推送时间}
                JdVOPOrderFinishMsg m = JsonUtil.getBean(msg.getContent(), JdVOPOrderFinishMsg.class);
                orderFinishMsg(m);
                break;
            }
        }
        return "ok";
    }
    
    private static final String ORDER_SPLIT_NAME = "订单拆分";
    
    private void orderSplitMsg(JdVOPOrderSplitMsg msg)
    {
        try
        {
            startHandleLog(ORDER_SPLIT_NAME, msg);
            jdAppOrderManager.orderSplit(msg.getPOrder());
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_SPLIT_NAME, e);
        }
    }
    
    private static final String ORDER_DELIVERY_STATE_CHANGE_NAME = "订单妥投";
    
    private void orderDeliveryStateChangeMsg(JdVOPOrderDeliveryStateChangeMsg msg)
    {
        try
        {
            startHandleLog(ORDER_DELIVERY_STATE_CHANGE_NAME, msg);
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    if (order.getStatus() != OrderStatus.ARRIVED_ORDER
                        && order.getStatus() != OrderStatus.CONFIRM_ORDER)
                    {
                        order.setStatus(OrderStatus.ARRIVED_ORDER);
                        orderDao.update(order);
                        orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                        log.info("[京东VOP-消息队列]订单（{}）已妥投", order.getCode());
                    }
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_DELIVERY_STATE_CHANGE_NAME, e);
        }
    }
    
    private static final String ORDER_CANCEL_NAME = "订单主动取消";
    
    private void orderCancelMsg(RetriableMsg<JdVOPOrderCancelMsg> retriableMsg)
    {
        try
        {
            JdVOPOrderCancelMsg msg = retriableMsg.getMsg();
            startHandleLog(ORDER_CANCEL_NAME, msg);
            if (msg.getState() == null)
                return;
            switch (msg.getState())
            {
                // 取消失败
                case 0:
                {
                    log.warn("[京东VOP-消息队列]订单主动取消失败：{}", msg.getOrderId());
                    break;
                }
                // 取消成功
                case 1:
                {
                    JdOrderCorrelation joc = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
                    if (joc != null)
                    {
                        String outRefundNo = numberUtils.createRefundOrderNumber();
                        MktOrderRefund or = orderRefundDao.byJdOrderCodeHandle(joc.getOrderCode());
                        MktOrder order = orderDao.get(joc.getPkey());
                        if (order == null)
                        {
                            log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
                            break;
                        }
                        if(or == null)
                        {
                            // 正常逻辑不会走这里
                            log.warn("走这里有问题");
                            JdOrderCorrelation pjoc = jdOrderCorrelationDao.getByJdCode(joc.getParentOrder());
                            or = jdOrderRefundManager.assemblyRefund(order, pjoc.getOrderCode(), outRefundNo);
                        }
                        else
                        {
                            or.setOutRefundNo(outRefundNo);
                            or.setDelTime(new Date());
                            or.setStatus(RefundStatus.REFUND_FINAL);
                            // 京东订单直接取消,邮费直接退回  TODO
                            or.setRefundPostage(or.getPostage());
                            orderRefundDao.update(or);
                        }
                       
                        order.setStatus(OrderStatus.REFUNDED_ORDER);
                        if (order.getRefundAmt() != null)
                            order.setRefundAmt(order.getRefundAmt().add(or.getAmtre()));
                        else
                            order.setRefundAmt(or.getAmtre());
                        if(PayType.MSD_COMBINATION.equals(order.getPayType()))
                        {
                            if(order.getRefundWeixinAmt() == null)
                                order.setRefundWeixinAmt(BigDecimal.ZERO);
                            order.setRefundWeixinAmt(order.getRefundWeixinAmt().add(or.getRefundWeixinAmt()));
                            if(order.getRefundOtherAmt() == null)
                                order.setRefundOtherAmt(BigDecimal.ZERO);
                            order.setRefundOtherAmt(order.getRefundOtherAmt().add(or.getRefundOtherAmt()));
                        }
                        order.setRefundJd(order.getPayDetailMoney());
                        orderDao.update(order);
                        BigDecimal amtnMsd = order.getAmtn();
                        if(PayType.ORDER_WEIXIN.equals(order.getPayType()))
                        {
                            jdAppOrderManager.refundWxPay(order);
                        }
                        else
                        {
                            if(PayType.MSD_COMBINATION.equals(order.getPayType()))
                            {
                                amtnMsd = order.getOtherAmt();
//                            memberMsdManager.updMsdPayFail(order.getMember(), null, amtnMsd, order.getAscription());
                                jdAppOrderManager.refundWxPay(order);
                            }
                            
                            // 退还热力豆
                            memberMsdManager.updMsdBalance(order.getMember(),
                                null,
                                true,
                                amtnMsd,
                                MsdOperationType.REFUND,
                                order.getCode() + "订单退款",
                                order.getCode(),
                                order.getAscription(),
                                true);
                        }
                            
                        log.info("[京东VOP-消息队列]订单（{}）已主动取消成功", order.getCode());
                    }
                    else
                    {
                        log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
                        if (retriableMsg.getTimes() == 0)
                        {
                            RetriableMsg<JdVOPOrderCancelMsg> newRetriableMsg = new RetriableMsg<>(Util.getUUID(), retriableMsg.getTimes() + 1, msg);
                            Timeout timeout = timer.newTimeout(new OrderCancelTask(newRetriableMsg), 5, TimeUnit.SECONDS);
                            timeoutMap.put(newRetriableMsg.getId(), timeout);
                        }
                        else if (retriableMsg.getTimes() == 1)
                        {
                            RetriableMsg<JdVOPOrderCancelMsg> newRetriableMsg = new RetriableMsg<>(Util.getUUID(), retriableMsg.getTimes() + 1, msg);
                            Timeout timeout = timer.newTimeout(new OrderCancelTask(newRetriableMsg), 60, TimeUnit.SECONDS);
                            timeoutMap.put(newRetriableMsg.getId(), timeout);
                        }
                        else
                        {
                            log.warn("[京东VOP-消息队列]重试处理[{}]消息超过最大次数：{}", ORDER_CANCEL_NAME, JsonUtil.toString(msg));
                        }
                    }
                    break;
                }
                default:
                    log.warn("[京东VOP-消息队列]订单主动取消消息的state不合法：{}", msg.getState());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_CANCEL_NAME, e);
        }
    }
    
    private static final String ORDER_DELIVERY_INFO_NAME = "配送单生成成功";
    
    private void orderDeliveryInfoMsg(JdVOPOrderDeliveryInfoMsg msg)
    {
        try
        {
            startHandleLog(ORDER_DELIVERY_INFO_NAME, msg);
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    if (order.getStatus() != OrderStatus.SHIPPED_ORDER && order.getStatus() != OrderStatus.ARRIVED_ORDER
                        && order.getStatus() != OrderStatus.CONFIRM_ORDER)
                    {
                        order.setStatus(OrderStatus.SHIPPED_ORDER);
                        orderDao.update(order);
                        orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                        log.info("[京东VOP-消息队列]订单（{}）已发货", order.getCode());
                    }
                    MktOrderDesc orderDesc = orderDescDao.get(order.getPkey());
                    if(orderDesc != null)
                    {
                        try
                        {
                            JdOrderDeliveryInfo jod = jdAppOrderManager.queryDeliveryInfo(order.getPkey());
                            if(jod.getLogisticInfoList() != null && !jod.getLogisticInfoList().isEmpty())
                            {
                                LogisticInfo logisticInfo = jod.getLogisticInfoList().get(0);
                                orderDesc.setLogistics(logisticInfo.getDeliveryCarrier());
                                orderDesc.setKdCode(logisticInfo.getDeliveryOrderId());
                            }
                            if(jod.getTrackInfoList() != null && !jod.getTrackInfoList().isEmpty())
                            {
                                TrackInfo trackInfo = jod.getTrackInfoList().get(jod.getTrackInfoList().size() - 1);
                                orderDesc.setFhTime(trackInfo.getTrackMsgTime());
                            }
                            orderDescDao.update(orderDesc);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_DELIVERY_INFO_NAME, e);
        }
    }
    
    private static final String ORDER_PAY_FAILED_NAME = "订单支付失败";
    
    private void orderPayFailedMsg(JdVOPOrderPayFailedMsg msg)
    {
        try
        {
            startHandleLog(ORDER_PAY_FAILED_NAME, msg);
            // 查订单
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    // 改状态（作废）
                    order.setStatus(OrderStatus.VOID_ORDER);
                    orderDao.update(order);
                    orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                    BigDecimal amtnMsd = order.getAmtn();
                    // 该流程不确定,用户的热力豆状态
                    if(PayType.MSD_COMBINATION.equals(order.getPayType()))
                    {
                        amtnMsd = order.getOtherAmt();
                        memberMsdManager.updMsdPayFail(order.getMember(), null, amtnMsd, order.getAscription());
                        jdAppOrderManager.refundWxPay(order);
                    }
                    else if(PayType.ORDER_WEIXIN.equals(order.getPayType()))
                    {
                        jdAppOrderManager.refundWxPay(order);
                    }
                    else
                    {
                        // 退热力豆
                        memberMsdManager.updMsdBalance(order.getMember(),
                            null,
                            true,
                            amtnMsd,
                            MsdOperationType.REFUND,
                            order.getCode() + "京东订单支付失败退回",
                            order.getCode(),
                            order.getAscription(),
                            true);
                    }
                    log.info("[京东VOP-消息队列]支付失败订单（{}）已作废， 热力豆已退回", order.getCode());
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_PAY_FAILED_NAME, e);
        }
    }
    
    private static final String ORDER_AUTO_CANCEL_NAME = "订单自动取消";
    
    private void orderAutoCancelMsg(RetriableMsg<JdVOPOrderAutoCancelMsg> retriableMsg)
    {
        try
        {
            JdVOPOrderAutoCancelMsg msg = retriableMsg.getMsg();
            startHandleLog(ORDER_AUTO_CANCEL_NAME, msg);
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    order.setStatus(OrderStatus.VOID_ORDER);
                    orderDao.update(order);
                    orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                    log.info("[京东VOP-消息队列]订单（{}）已自动取消", order.getCode());
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
                if (retriableMsg.getTimes() == 0)
                {
                    RetriableMsg<JdVOPOrderAutoCancelMsg> newRetriableMsg = new RetriableMsg<>(Util.getUUID(), retriableMsg.getTimes() + 1, msg);
                    Timeout timeout = timer.newTimeout(new OrderAutoCancelTask(newRetriableMsg), 5, TimeUnit.SECONDS);
                    timeoutMap.put(newRetriableMsg.getId(), timeout);
                }
                else if (retriableMsg.getTimes() == 1)
                {
                    RetriableMsg<JdVOPOrderAutoCancelMsg> newRetriableMsg = new RetriableMsg<>(Util.getUUID(), retriableMsg.getTimes() + 1, msg);
                    Timeout timeout = timer.newTimeout(new OrderAutoCancelTask(newRetriableMsg), 60, TimeUnit.SECONDS);
                    timeoutMap.put(newRetriableMsg.getId(), timeout);
                }
                else
                {
                    log.warn("[京东VOP-消息队列]重试处理[{}]消息超过最大次数：{}", ORDER_AUTO_CANCEL_NAME, JsonUtil.toString(msg));
                }
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_AUTO_CANCEL_NAME, e);
        }
    }
    
    private static final String ORDER_WAITING_CONFIRM_RECEIPT_NAME = "订单等待确认收货";
    
    private void orderWaitingConfirmReceiptMsg(JdVOPOrderWaitingConfirmReceiptMsg msg)
    {
        try
        {
            startHandleLog(ORDER_WAITING_CONFIRM_RECEIPT_NAME, msg);
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    if (order.getStatus() != OrderStatus.SHIPPED_ORDER && order.getStatus() != OrderStatus.ARRIVED_ORDER
                        && order.getStatus() != OrderStatus.CONFIRM_ORDER)
                    {
                        order.setStatus(OrderStatus.SHIPPED_ORDER);
                        orderDao.update(order);
                        orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                        log.info("[京东VOP-消息队列]订单（{}）即将到货，等待确认收货", order.getCode());
                    }
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_WAITING_CONFIRM_RECEIPT_NAME, e);
        }
    }
    
    private static final String ORDER_PAID_NAME = "新订单";
    
    private void orderPaidMsg(JdVOPOrderPaidMsg msg)
    {
        try
        {
            startHandleLog(ORDER_PAID_NAME, msg);
            // 查订单
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    if (OrderStatus.PAYING_ORDER.equals(order.getStatus()))
                    {
                        order.setStatus(OrderStatus.DELIVERED_ORDER);
                        orderDao.update(order);
                        orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                    }
                    log.info("[京东VOP-消息队列]订单（{}）的京东订单（{}）支付成功", order.getCode(), correlation.getJdCode());
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_PAID_NAME, e);
        }
    }
    
    private static final String ORDER_FINISH_NAME = "订单完成";
    
    private void orderFinishMsg(JdVOPOrderFinishMsg msg)
    {
        try
        {
            startHandleLog(ORDER_FINISH_NAME, msg);
            JdOrderCorrelation correlation = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            if (correlation != null)
            {
                MktOrder order = orderDao.get(correlation.getPkey());
                if (order != null)
                {
                    if (order.getStatus() != OrderStatus.CONFIRM_ORDER)
                    {
                        order.setStatus(OrderStatus.CONFIRM_ORDER);
                        orderDao.update(order);
                        orderLineDao.updStatusByOrderPkey(order.getPkey(), order.getStatus());
                        log.info("[京东VOP-消息队列]订单（{}）已完成", order.getCode());
                    }
                }
                else
                {
                    log.warn("[京东VOP-消息队列]根据京东订单找不到系统订单：{}", JsonUtil.toString(correlation));
                }
            }
            else
            {
                log.warn("[京东VOP-消息队列]找不到京东订单：{}", msg.getOrderId());
            }
        }
        catch (Exception e)
        {
            exceptionLog(ORDER_FINISH_NAME, e);
        }
    }
    
    private void startHandleLog(String name, Object msg)
    {
        log.info("[京东VOP-消息队列]开始处理[{}]消息：{}", name, JsonUtil.toString(msg));
    }
    
    private void exceptionLog(String name, Exception e)
    {
        log.error("[京东VOP-消息队列]处理[{}]消息异常：{}", name, e.getMessage());
        log.error("堆栈：", e);
    }
    
    @Override
    public void handleResult(String pipeId, String correlationId, Result<String> result)
        throws Exception
    {
        
    }
    
    @AllArgsConstructor
    private class OrderCancelTask implements TimerTask
    {
        private RetriableMsg<JdVOPOrderCancelMsg> msg;
        
        @Override
        public void run(Timeout timeout)
            throws Exception
        {
            log.info("[京东VOP-消息队列]第{}次重试处理[{}]消息：{}",
                msg.getTimes(),
                ORDER_CANCEL_NAME,
                JsonUtil.toString(msg.getMsg()));
            orderCancelMsg(msg);
            timeoutMap.remove(msg.getId());
        }
    }
    
    @AllArgsConstructor
    private class OrderAutoCancelTask implements TimerTask
    {
        private RetriableMsg<JdVOPOrderAutoCancelMsg> msg;
        
        @Override
        public void run(Timeout timeout)
            throws Exception
        {
            log.info("[京东VOP-消息队列]第{}次重试处理[{}]消息：{}",
                msg.getTimes(),
                ORDER_AUTO_CANCEL_NAME,
                JsonUtil.toString(msg.getMsg()));
            orderAutoCancelMsg(msg);
            timeoutMap.remove(msg.getId());
        }
    }
    
    @Data
    @AllArgsConstructor
    private static class RetriableMsg<T>
    {
        private String id;

        private int times;
        
        private T msg;
    }
}
