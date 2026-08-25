package cn.tofocus.lejia.domain.jdvop;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.createAfsApply.ApplyAfterSaleOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.updateSendInfo.UpdateAfterSaleWayBillOpenReq;
import com.jd.open.api.sdk.domain.vopsh.OperaAfterSaleOpenProvider.request.updateSendInfo.WaybillInfoVoOpenReq;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.findRefundInfo.RefundInfoResultOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getAfsOutline.AfsOutLineOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.getGoodsAttributes.SupportedInfoOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.queryAfsAddressInfos.AfsAddressInfoOpenResp;
import com.jd.open.api.sdk.domain.vopsh.QueryAfterSaleOpenProvider.response.queryLogicticsInfo.WayBillInfoOpenResp;
import com.jd.open.api.sdk.request.vopsh.*;
import com.jd.open.api.sdk.response.vopsh.*;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东VOP - 售后接口
 */
@Slf4j
@Component
public class JdVOPAfsManager extends BaseJdVOPManager
{
    /**
     * 批量查询订单下商品售后权益
     * @param jdOrderId 京东子订单号，必填
     * @param skuIdList 商品ID，不能超过200
     * @return 响应结果
     */
    public List<SupportedInfoOpenResp> getGoodsAttributes(Long jdOrderId, List<Long> skuIdList)
    {
        try
        {
            VopAfsGetGoodsAttributesRequest request = new VopAfsGetGoodsAttributesRequest();
            request.setWareId(collection2Str(skuIdList));
            request.setOrderId(jdOrderId);
            VopAfsGetGoodsAttributesResponse response = jdClient().execute(request);
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
            String operate = "批量查询订单下商品售后权益";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 申请售后
     * @param apply 售后申请入参
     * @return 响应结果
     */
    public Boolean createAfsApply(ApplyAfterSaleOpenReq apply)
    {
        try
        {
            VopAfsCreateAfsApplyRequest request = new VopAfsCreateAfsApplyRequest();
            request.setApplyAfterSaleOpenReq(apply);
            log.info("申请售后参数: {}", JsonUtil.toString(apply, true));
            VopAfsCreateAfsApplyResponse response = jdClient().execute(request);
            log.info("申请售后返回结果: {}", JsonUtil.toString(response, true));
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
            String operate = "申请售后";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询售后概要
     * @param jdOrderId 原单子订单号
     * @param thirdApplyId 第三方申请单号，thirdApplyId=1 orderId下所有申请单概要列表
     * @param wareId 商品编号，wareId= 1 orderId下所有商品概要列表
     * @return 响应结果
     */
    public List<AfsOutLineOpenResp> getAfsOutline(Long jdOrderId, String thirdApplyId, Long wareId)
    {
        try
        {
            VopAfsGetAfsOutlineRequest request = new VopAfsGetAfsOutlineRequest();
            request.setPageIndex(1);
            request.setPageSize(100);
            request.setOrderId(jdOrderId);
            request.setThirdApplyId(thirdApplyId);
            request.setWareId(wareId);
            VopAfsGetAfsOutlineResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            if (response.getOpenRpcResult().getResult() == null)
                return null;
            return response.getOpenRpcResult().getResult().getItems();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询售后概要";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询客户寄回地址
     * @param jdOrderId 京东订单号
     * @param thirdApplyId 售后申请的三方申请单号
     * @param customerPin 用户pin
     * @return 响应结果
     */
    public List<AfsAddressInfoOpenResp> queryAfsAddressInfos(Long jdOrderId, String thirdApplyId, String customerPin)
    {
        try
        {
            VopAfsQueryAfsAddressInfosRequest request = new VopAfsQueryAfsAddressInfosRequest();
            request.setThirdApplyId(thirdApplyId);
            request.setOrderId(jdOrderId);
            request.setCustomerPin(customerPin);
            VopAfsQueryAfsAddressInfosResponse response = jdClient().execute(request);
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
            String operate = "查询客户寄回地址";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 填写运单信息
     * @param waybills 运单信息集合，最多20个
     * @param jdOrderId 京东订单号
     * @param thirdApplyId 售后申请的三方申请单号
     * @return 响应结果
     */
    public Boolean updateSendInfo(List<WaybillInfoVoOpenReq> waybills, Long jdOrderId, String thirdApplyId)
    {
        try
        {
            VopAfsUpdateSendInfoRequest request = new VopAfsUpdateSendInfoRequest();
            UpdateAfterSaleWayBillOpenReq updateAfterSaleWayBillOpenReq = new UpdateAfterSaleWayBillOpenReq();
            updateAfterSaleWayBillOpenReq.setThirdApplyId(thirdApplyId);
            updateAfterSaleWayBillOpenReq.setOrderId(jdOrderId);
            updateAfterSaleWayBillOpenReq.setWaybillInfoVoOpenReqList(waybills);
            request.setUpdateAfterSaleWayBillOpenReq(updateAfterSaleWayBillOpenReq);
            VopAfsUpdateSendInfoResponse response = jdClient().execute(request);
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
            String operate = "填写运单信息";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询售后单物流信息
     * @param jdOrderId 京东订单号
     * @param thirdApplyId 售后申请的三方申请单号
     * @return 响应结果
     */
    public List<WayBillInfoOpenResp> queryLogicticsInfo(Long jdOrderId, String thirdApplyId)
    {
        try
        {
            VopAfsQueryLogicticsInfoRequest request = new VopAfsQueryLogicticsInfoRequest();
            request.setThirdApplyId(thirdApplyId);
            request.setOriginalOrderId(jdOrderId);
            request.setPageNo(1);
            request.setPageSize(100);
            VopAfsQueryLogicticsInfoResponse response = jdClient().execute(request);
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
            String operate = "查询售后单物流信息";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询售后退款明细
     * @param jdOrderId 京东订单号
     * @param thirdApplyId 售后申请的三方申请单号
     * @return 响应结果
     */
    public RefundInfoResultOpenResp findRefundInfo(Long jdOrderId, String thirdApplyId)
    {
        try
        {
            VopAfsFindRefundInfoRequest request = new VopAfsFindRefundInfoRequest();
            request.setThirdApplyId(thirdApplyId);
            request.setOrderId(jdOrderId);
            VopAfsFindRefundInfoResponse response = jdClient().execute(request);
            if (!response.getReturnType().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getReturnType().getResultMessage()));
            }
            return response.getReturnType().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询售后退款明细";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 售后申请单取消
     * @param jdOrderId 京东订单号
     * @param thirdApplyId 售后申请的三方申请单号
     * @param remark 备注
     * @return 响应结果
     */
    public Boolean cancelAfsApply(Long jdOrderId, String thirdApplyId, String remark)
    {
        try
        {
            VopAfsCancelAfsApplyRequest request = new VopAfsCancelAfsApplyRequest();
            request.setThirdApplyId(thirdApplyId);
            request.setRemark(remark);
            request.setOrderId(jdOrderId);
            VopAfsCancelAfsApplyResponse response = jdClient().execute(request);
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
            String operate = "售后申请单取消";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 确认售后完成接口
     * @param jdOrderId 京东订单号
     * @param thirdApplyId 售后申请的三方申请单号
     * @return 响应结果
     */
    public Boolean confirmAfsOrder(Long jdOrderId, String thirdApplyId, String remark)
    {
        try
        {
            VopAfsConfirmAfsOrderRequest request = new VopAfsConfirmAfsOrderRequest();
            request.setThirdApplyId(thirdApplyId);
            request.setOrderId(jdOrderId);
            VopAfsConfirmAfsOrderResponse response = jdClient().execute(request);
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
            String operate = "确认售后完成接口";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
}
