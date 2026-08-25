package cn.tofocus.lejia.domain.jdvop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.open.api.sdk.domain.vopxx.MsgRecordProvider.response.queryTransByVopNormal.VopBizTransMessage;
import com.jd.open.api.sdk.request.vopxx.VopMessageDeleteClientMsgByIdListRequest;
import com.jd.open.api.sdk.request.vopxx.VopMessageQueryTransByVopNormalRequest;
import com.jd.open.api.sdk.response.vopxx.VopMessageDeleteClientMsgByIdListResponse;
import com.jd.open.api.sdk.response.vopxx.VopMessageQueryTransByVopNormalResponse;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.domain.jdvop.bean.msg.JdVOPAddressChangeMsg;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东VOP - 消息接口
 */
@Slf4j
@Component
public class JdVOPMsgManager extends BaseJdVOPManager
{
    @Autowired
    private JdVOPMsgQueue jdVOPMsgQueue;
    
    /**
     * 订单相关消息状态流转
     * 1、订单自动取消消息：作废
     * 2、订单取消结果消息：作废（取消成功），状态不变（取消失败）
     * 3、订单支付失败消息：状态变回已退款，并退还热力豆
     * 4、支付成功新订单通知：状态不变（理论上是待发货）
     * 5、配送单生成成功通知：已发货
     * 6、订单等待确认收货消息（即将到货）：已发货（主要用于部分不是京东配送订单可能不会有“配送单生成成功通知”，进行发货状态变更的补充）
     * 7、订单妥投消息：已到货
     * 8、订单完成消息：已完成
     */
    public void consumeMsgTask()
    {
        List<VopBizTransMessage> list = null;
        do
        {
            list = queryTransByVopNormal(null);
            if (CollectionUtil.isEmpty(list))
            {
                log.info("[京东VOP]没有未处理的消息");
                break;
            }
            List<Long> consumedIds = new ArrayList<>();
            for (VopBizTransMessage msg : list)
            {
                try
                {
                    if (msg.getYn() == 1)
                    {
                        switch (msg.getType())
                        {
                            case 1:
                            case 5:
                            case 10:
                            case 12:
                            case 14:
                            case 15:
                            case 18:
                            case 25:
                            case 31:
                            {
                                // 订单相关消息
                                String correlationId = jdVOPMsgQueue.orderMsg(msg);
                                log.info("[京东VOP]查询到有效消息[订单相关消息][{}]({})：{}",
                                    msg.getType(),
                                    correlationId,
                                    JsonUtil.toString(msg));
                                break;
                            }
                            case 2:
                            case 4:
                            case 6:
                            case 16:
                            case 48:
                            case 126:
                            {
                                // sku相关消息
                                String correlationId = jdVOPMsgQueue.skuMsg(msg);
                                log.info("[京东VOP]查询到有效消息[sku相关消息][{}]({})：{}",
                                    msg.getType(),
                                    correlationId,
                                    JsonUtil.toString(msg));
                                break;
                            }
                            case 104:
                            case 105:
                            case 119:
                            {
                                // 申请单环节变更消息
                                // {"id":推送id,"result":{"expectationChanged":期望发生变化,"thirdApplyId":三方申请单号,"pin":客户pin,"stepPassType":售后环节通过情况10:全部通过20:部分通过30:没有通过【注】applyStep为20或40的时候stepPassType有值,"isOffline":线上线下标识,"applyStep":申请环节标识10:申请20:审核30:收货40:处理50:待用户确认60:完成70:取消,"contractNumber":合同号,"orderId":京东订单号},"type":104,"time":推送时间}
                                String correlationId = jdVOPMsgQueue.afsMsg(msg);
                                log.info("[京东VOP]查询到有效消息[售后相关消息][{}]({})：{}",
                                    msg.getType(),
                                    correlationId,
                                    JsonUtil.toString(msg));
                                break;
                            }
                            case 50:
                            {
                                // 京东地址变更消息
                                // {"id":"推送id","result":{"areaId":"京东地址编码","areaName":"京东地址名称","parentId":"父京东ID编码","areaLevel":“地址等级(行政级别：国家(1)、省(2)、市(3)、县(4)、镇(5))”,"operateType":”操作类型(插入数据为1，更新时为2，删除时为3)}”,"time":"消息推送时间",“type":50 }
                                JdVOPAddressChangeMsg m =
                                    JsonUtil.getBean(msg.getContent(), JdVOPAddressChangeMsg.class);
                                String correlationId = jdVOPMsgQueue.addressChangeMsg(m);
                                log.info("[京东VOP]查询到有效消息[京东地址变更消息]({})：{}", correlationId, JsonUtil.toString(msg));
                                break;
                            }
                            default:
                                log.info("[京东VOP]查询到不予处理的有效消息[{}]：{}", msg.getType(), JsonUtil.toString(msg));
                                break;
                        }
                    }
                    consumedIds.add(msg.getId());
                }
                catch (Exception e)
                {
                    log.error("[京东VOP]查询到消息消费失败：{}", JsonUtil.toString(msg));
                }
            }
            // 消费消息
            deleteClientMsgByIdList(consumedIds);
        }
        while (CollectionUtil.isNotEmpty(list) && list.size() >= 100);
    }
    
    /**
     * 消息查询接口
     * @param typeSet 消息类型集合，最大支持100
     * @return 响应结果
     */
    public List<VopBizTransMessage> queryTransByVopNormal(Set<Long> typeSet)
    {
        try
        {
            VopMessageQueryTransByVopNormalRequest request = new VopMessageQueryTransByVopNormalRequest();
            // 消息读取操作类型，2:通用读取(默认)，3:独立读取，多type用通用读取，单个type用独立读取
            int readType = 2;
            if (typeSet != null)
            {
                request.setType(collection2Str(typeSet));
                if (typeSet.size() == 1)
                    readType = 3;
            }
            request.setReadType(readType);
            VopMessageQueryTransByVopNormalResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "消息查询接口";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 消息删除接口
     * @param recordIdList 消息ID列表，最大100
     * @return 响应结果
     */
    public Boolean deleteClientMsgByIdList(List<Long> recordIdList)
    {
        try
        {
            if (recordIdList == null)
                return true;
            VopMessageDeleteClientMsgByIdListRequest request = new VopMessageDeleteClientMsgByIdListRequest();
            request.setId(collection2Str(recordIdList));
            VopMessageDeleteClientMsgByIdListResponse response = jdClient().execute(request);
            if (!response.getVopOrderRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getVopOrderRpcResult().getResultMessage()));
            }
            return response.getVopOrderRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "消息删除接口";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
}
