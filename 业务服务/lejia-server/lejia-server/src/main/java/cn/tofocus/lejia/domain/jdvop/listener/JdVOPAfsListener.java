package cn.tofocus.lejia.domain.jdvop.listener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getAfsOutline.AfsOutLineOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getAfsOutline.WareSummaryInfoOpenResp;
import com.jd.open.api.sdk.domain.vopxx.MsgRecordProvider.response.queryTransByVopNormal.VopBizTransMessage;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.dao.jd.JdOrderCorrelationDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundExtendDao;
import cn.tofocus.lejia.dao.refund.MktOrderRefundLineDao;
import cn.tofocus.lejia.domain.OrderRefundManager;
import cn.tofocus.lejia.domain.jd.JdAppOrderManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPAfsManager;
import cn.tofocus.lejia.domain.jdvop.bean.msg.JdVOPRefundStepChangeMsg;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdVOPAfsListener implements MsgListener<VopBizTransMessage, String>
{
    public static final String PIPE_NAME = "zyysc.jd.vop.msg.afs";
    
    @Autowired
    private JdAppOrderManager jdAppOrderManager;
    
    @Autowired
    private OrderRefundManager orderRefundManager;
    
    @Autowired
    private JdOrderCorrelationDao jdOrderCorrelationDao;
    
    @Autowired
    private MktOrderRefundDao orderRefundDao;
    
    @Autowired
    private MktOrderRefundLineDao orderRefundLineDao;
    
    @Autowired
    private MktOrderRefundExtendDao orderRefundExtendDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    @Autowired
    private JdVOPAfsManager jdVOPAfsManager;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleMessage(String pipeId, String correlationId, VopBizTransMessage msg)
        throws Exception
    {
        switch (msg.getType())
        {
            case 104:
            {
                // 申请单环节变更消息
                // {"id":推送id,"result":{"expectationChanged":期望发生变化,"thirdApplyId":三方申请单号,"pin":客户pin,"stepPassType":售后环节通过情况10:全部通过20:部分通过30:没有通过【注】applyStep为20或40的时候stepPassType有值,"isOffline":线上线下标识,"applyStep":申请环节标识10:申请20:审核30:收货40:处理50:待用户确认60:完成70:取消,"contractNumber":合同号,"orderId":京东订单号},"type":104,"time":推送时间}
                JdVOPRefundStepChangeMsg m = JsonUtil.getBean(msg.getContent(), JdVOPRefundStepChangeMsg.class);
                refundStepChangeMsg(m);
                break;
            }
            case 105:
            {
                // 订单维度售后完成消息
                // {"id":推送id,"result":{"pin":京东pin,"orderId":京东子订单号,"batchIds":[三方申请单号1,三方申请单号2]，"type":105,"time":推送时间}
                log.info("[京东VOP-消息队列]暂时仅打印[订单维度售后完成]消息：{}", JsonUtil.toString(msg));
                break;
            }
            case 119:
            {
                // 申请单维度售后退款完成消息
                // {"id":推送id,"result":{"customerId":"企业PIN","kaApplyId":"京东售后申请单号","modifyDate":"退款时间（以最后的退款单为准）","originalOrderId":"订单号","outApplyId":"客户售后申请单号（同申请售后中的thirdApplyId）","refundAmount":"退款总额","refundDetail":[{"id":"主键","refundType":"申请类型","refundAmount":"退款⾦额","refundJdBankId":"退款平台编号（⽀付枚举值）","refundJdBankName":"退款银⾏名称（⼯商银⾏，招商银⾏等）","payid":"支付单号"}],"refundPaymentInfos":[{"id":"主键","refundSourceId":"退款明细单编号","payId":"⽀付单号","payType":"⽀付类型","payEnum":"商品类型","refundableAmount":"退款业务占⽤本⽀付单的⾦额","b2bPayType":"混合⽀付必填（中台的⽀付类型）",}],"refundWaresInfos":[{"skuId":"商品编号","skuName":"商品名称","wareNumber":"商品数量"}]},"type":119,"time":推送时间}
                log.info("[京东VOP-消息队列]暂时仅打印[申请单维度售后退款完成消息]消息：{}", JsonUtil.toString(msg));
                break;
            }
        }
        return "ok";
    }
    
    private static final String REFUND_STEP_CHANGE_MSG_NAME = "申请单环节变更";
    
    private void refundStepChangeMsg(JdVOPRefundStepChangeMsg msg)
    {
        try
        {
            startHandleLog(REFUND_STEP_CHANGE_MSG_NAME, msg);
            JdOrderCorrelation joc = jdOrderCorrelationDao.getByJdCode(msg.getOrderId());
            MktOrderRefund or = orderRefundDao.byOutRefundNo(msg.getThirdApplyId());
            if (or == null)
                throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到售后单");
            switch (msg.getApplyStep())
            {
                // 20审核完成待收货；
                case 20:
                {
                    boolean afterSign = or.getStatus().getIndex() > RefundStatus.JD_PENDING_APPROVAL.getIndex();
                    // 10:全部通过
                    if (msg.getStepPassType() == 10)
                    {
                        if (afterSign)
                            break;
                        or.setStatus(RefundStatus.JD_APPROVAL_ACCEPTED);
                        orderRefundDao.update(or);
                    }
                    // 20:部分通过
                    else if (msg.getStepPassType() == 20)
                    {
                        if (afterSign)
                            break;
                        partlyAccept(msg, or, RefundStatus.JD_APPROVAL_ACCEPTED);
                    }
                    // 30:没有通过
                    else
                    {
                        // 防止先收到60消息，才通知没通过
                        if (afterSign && or.getStatus() != RefundStatus.JD_CONFIRMED)
                            break;
                        orderRefundManager.rollbackRefundJd(msg.getOrderId(),
                            msg.getThirdApplyId(),
                            RefundStatus.JD_APPROVAL_REJECTED);
                    }
                    break;
                }
                // 30收货完成待处理；
                case 30:
                {
                    if (or.getStatus().getIndex() >= RefundStatus.JD_RECEIPTED.getIndex())
                        break;
                    or.setStatus(RefundStatus.JD_RECEIPTED);
                    orderRefundDao.update(or);
                    break;
                }
                // 40处理完成（如需退款则等待退款）；
                case 40:
                {
                    boolean afterSign = or.getStatus().getIndex() > RefundStatus.JD_RECEIPTED.getIndex();
                    // 10:全部通过
                    if (msg.getStepPassType() == 10)
                    {
                        if (afterSign)
                            break;
                        or.setStatus(RefundStatus.JD_PROCESSED_SUCCESS);
                        orderRefundDao.update(or);
                    }
                    // 20:部分通过
                    else if (msg.getStepPassType() == 20)
                    {
                        if (afterSign)
                            break;
                        partlyAccept(msg, or, RefundStatus.JD_PROCESSED_SUCCESS);
                    }
                    // 30:没有通过
                    else
                    {
                        // 防止先收到60消息，才通知没通过
                        if (afterSign && or.getStatus() != RefundStatus.JD_CONFIRMED)
                            break;
                        orderRefundManager.rollbackRefundJd(msg.getOrderId(),
                            msg.getThirdApplyId(),
                            RefundStatus.JD_PROCESSED_FAILED);
                    }
                    break;
                }
                // 50待用户确认；
                case 50:
                {
                    MktOrder order = orderDao.get(joc.getPkey());
                    RefundJdType jdType = or.getJdType();
                    String remark = "订单退款";
                    if (RefundJdType.RETURN_GOODS.equals(jdType))
                    {
                        remark = "订单退货";
                    }
                    
                    if (!RefundJdType.EXCHANGE.equals(jdType))
                    {
                        if (order.getRefundAmt() != null)
                            order.setRefundAmt(order.getRefundAmt().add(or.getAmtre()));
                        else
                            order.setRefundAmt(or.getAmtre());
                        BigDecimal refundJd = orderRefundLineDao.aggSumRefundJd(or.getPkey());
                        if (order.getRefundJd() != null)
                            order.setRefundJd(order.getRefundJd().add(refundJd));
                        else
                            order.setRefundJd(refundJd);
                        // 查询已完成的退款商品金额，判断是否退完
                        BigDecimal haveRefundFinalGoodsAmt =
                            orderRefundDao.aggRefundGoodsAmt(order.getPkey(), RefundStatus.refundedStatus(), or.getPkey());
                        if (haveRefundFinalGoodsAmt == null)
                            haveRefundFinalGoodsAmt = BigDecimal.ZERO;
                        if (order.getAmto().compareTo(haveRefundFinalGoodsAmt.add(or.getRefundGoodsAmt())) <= 0)
                        {
                            order.setStatus(OrderStatus.REFUNDED_ORDER);
                        }
                        //                        order.setRefundAmt(haveRefundFinalGoodsAmt.add(or.getAmtre()));
                        orderDao.update(order);
                        BigDecimal amtnMsd = or.getAmtre();
                        if(PayType.ORDER_WEIXIN.equals(order.getPayType()))
                        {
                            jdAppOrderManager.refundWxPay(order);
                        }
                        else
                        {
                            if(PayType.MSD_COMBINATION.equals(order.getPayType()))
                            {
                                amtnMsd = or.getRefundOtherAmt();
                                jdAppOrderManager.refundWxPay(order);
//                            memberMsdManager.updMsdPayFail(order.getMember(), null, amtnMsd, order.getAscription());
                            }
                            // 退热力豆
                            memberMsdManager.updMsdBalance(order.getMember(),
                                null,
                                true,
                                amtnMsd,
                                MsdOperationType.REFUND,
                                order.getCode() + remark,
                                order.getCode(),
                                or.getAscription(),
                                true);
                        }
                        
                    }
                    if (or.getStatus().getIndex() >= RefundStatus.JD_PENDING_CONFIRM.getIndex())
                        break;
                    or.setStatus(RefundStatus.JD_PENDING_CONFIRM);
                    orderRefundDao.update(or);
                    break;
                }
                // 60售后结束
                case 60:
                {
                    if (RefundStatus.JD_APPROVAL_REJECTED.equals(or.getStatus())
                        || RefundStatus.JD_PROCESSED_FAILED.equals(or.getStatus()))
                        break;
                    or.setStatus(RefundStatus.JD_CONFIRMED);
                    or.setOutProcessing(false);
                    orderRefundDao.update(or);
                    break;
                }
                // 70取消
                case 70:
                {
                    orderRefundManager.rollbackRefundJd(msg.getOrderId(), msg.getThirdApplyId(), RefundStatus.JD_CANCELED);
                    break;
                }
                default:
                    log.warn("[京东VOP-消息队列]{}消息的ApplyStep不合法：{}", REFUND_STEP_CHANGE_MSG_NAME, msg.getApplyStep());
            }
        }
        catch (Exception e)
        {
            exceptionLog(REFUND_STEP_CHANGE_MSG_NAME, e);
        }
    }
    
    // 部分通过
    private void partlyAccept(JdVOPRefundStepChangeMsg msg, MktOrderRefund or, RefundStatus status)
    {
        List<AfsOutLineOpenResp> list = jdVOPAfsManager.getAfsOutline(msg.getOrderId(), msg.getThirdApplyId(), 1L);
        if (CollectionUtil.isEmpty(list))
            throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "找不到售后商品明细");
        Map<Long, WareSummaryInfoOpenResp> map = new HashMap<>();
        for (AfsOutLineOpenResp item : list)
        {
            WareSummaryInfoOpenResp wareSummaryInfo = item.getWareSummaryInfo();
            if (wareSummaryInfo == null)
            {
                log.error("[京东VOP-消息队列]查询售后（{}）商品明细返回数据异常：{}", msg.getThirdApplyId(), JsonUtil.toString(list));
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "查询售后商品明细返回数据异常");
            }
            map.put(wareSummaryInfo.getWareId(), wareSummaryInfo);
        }
        List<MktOrderRefundLine> lines = orderRefundLineDao.listRefundPkey(or.getPkey());
        List<MktOrderLine> toUpdOrderLines = new ArrayList<>();
        BigDecimal refundGoodsAmt = BigDecimal.ZERO;
        for (MktOrderRefundLine line : lines)
        {
            WareSummaryInfoOpenResp wareInfo = map.get(line.getGoods());
            if (wareInfo == null)
            {
                log.error("[京东VOP-消息队列]售后（{}）商品明细无法匹配：{}", msg.getThirdApplyId(), JsonUtil.toString(list));
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "售后商品明细无法匹配");
            }
            int acceptNum = wareInfo.getApplyNum() - wareInfo.getWareCancelNum();
            // 有新产生的拒绝
            int originRefundNum = line.getRefundNum();
            if (acceptNum > originRefundNum)
            {
                BigDecimal originRefundAmt = line.getRefundAmt();
                int cancelNum = originRefundNum - acceptNum;
                MktOrderLine ol = orderLineDao.get(line.getOrderLinePkey());
                line.setRefundNum(acceptNum);
                line.setRefundAmt(ol.getPricen().multiply(new BigDecimal(acceptNum)));
                StringBuilder sb = new StringBuilder();
                if (StringUtil.isNotBlank(line.getRemark()))
                    sb.append(line.getRemark()).append("，");
                if (status == RefundStatus.JD_APPROVAL_ACCEPTED)
                    sb.append("有").append(cancelNum).append("件商品审核不通过");
                else if (status == RefundStatus.JD_PROCESSED_SUCCESS)
                    sb.append("有").append(cancelNum).append("件商品处理失败");
                line.setRemark(sb.toString());
                
                ol.setRefundNum(ol.getRefundNum() - cancelNum);
                ol.setRefundAmt(ol.getRefundAmt().subtract(originRefundAmt).add(line.getRefundAmt()));
                toUpdOrderLines.add(ol);
            }
            line.setRefundJd(wareInfo.getShouldRefundAmount());
            refundGoodsAmt = refundGoodsAmt.add(line.getRefundAmt());
        }
        if (refundGoodsAmt.compareTo(or.getAmtre()) != 0)
        {
            or.setAmtre(refundGoodsAmt);
        }
        or.setStatus(status);
        orderRefundLineDao.updateAll(lines);
        orderRefundDao.update(or);
        if (CollectionUtil.isNotEmpty(toUpdOrderLines))
            orderLineDao.updateAll(toUpdOrderLines);
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
}
