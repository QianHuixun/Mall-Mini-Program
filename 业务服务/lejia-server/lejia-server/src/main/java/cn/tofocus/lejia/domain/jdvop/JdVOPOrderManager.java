package cn.tofocus.lejia.domain.jdvop;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.request.submitOrder.SubmitOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.OperaOrderOpenProvider.response.submitOrder.QueryOrderOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryBalanceOpenProvider.response.checkAccountBalance.CheckAccountBalanceOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.request.querySkuFreight.AreaBaseInfoOpenReq;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.request.querySkuFreight.FreightQueryOpenReq;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.request.querySkuFreight.SkuInfoOrderOpenReq;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryDeliveryInfo.DeliveryInfoQueryOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.querySkuFreight.FreightQueryOpenResp;
import com.jd.open.api.sdk.domain.vopdd.QueryPromiseOpenProvider.response.predictSkuPromise.PredictSkuPromiseOpenResp;
import com.jd.open.api.sdk.request.vopdd.*;
import com.jd.open.api.sdk.response.vopdd.*;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPSkuNum;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东VOP - 订单接口
 */
@Slf4j
@Component
public class JdVOPOrderManager extends BaseJdVOPManager
{
    /**
     * 提单前查询商品运费
     * @param skuNumInfoList sku和数量信息列表，最大支持100
     * @param areaInfo 区域信息
     * @param paymentType 支付方式,4余额，5对公转账、101金采支付
     * @return 响应结果
     */
    public FreightQueryOpenResp querySkuFreight(List<JdVOPSkuNum> skuNumInfoList, JdVOPAreaInfo areaInfo,
        Integer paymentType)
    {
        try
        {
            VopOrderQuerySkuFreightRequest request = new VopOrderQuerySkuFreightRequest();
            FreightQueryOpenReq freightQueryOpenReq = new FreightQueryOpenReq();
            freightQueryOpenReq.setPaymentType(paymentType);
            freightQueryOpenReq.setSkuInfoList(BeanUtil.beanListFrom(SkuInfoOrderOpenReq.class, skuNumInfoList));
            freightQueryOpenReq.setAreaInfo(BeanUtil.beanFrom(AreaBaseInfoOpenReq.class, areaInfo));
            request.setFreightQueryOpenReq(freightQueryOpenReq);
            VopOrderQuerySkuFreightResponse response = jdClient().execute(request);
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
            String operate = "提单前查询商品运费";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 提交预占订单服务
     * @return 响应结果
     */
    public QueryOrderOpenResp submitOrder(SubmitOrderOpenReq submitOrderOpenReq)
    {
        try
        {
            VopOrderSubmitOrderRequest request = new VopOrderSubmitOrderRequest();
            request.setSubmitOrderOpenReq(submitOrderOpenReq);
            VopOrderSubmitOrderResponse response = jdClient().execute(request);
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
            String operate = "提交预占订单服务";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 确认预占订单
     * @param jdOrderId 京东订单号，必填
     * @param thirdOrderId 第三方订单号
     * @return 响应结果
     */
    public Boolean confirmOrder(Long jdOrderId, String thirdOrderId)
    {
        try
        {
            VopOrderConfirmOrderRequest request = new VopOrderConfirmOrderRequest();
            request.setThirdOrderId(thirdOrderId);
            request.setJdOrderId(jdOrderId);
            VopOrderConfirmOrderResponse response = jdClient().execute(request);
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
            String operate = "确认预占订单";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询订单详情
     * @param jdOrderId 京东订单号
     * @param thirdOrderId 第三方订单号
     * @return 响应结果
     */
    public List<com.jd.open.api.sdk.domain.vopdd.QueryOrderOpenProvider.response.queryOrderDetail.QueryOrderOpenResp> queryOrderDetail(
        Long jdOrderId, String thirdOrderId)
    {
        try
        {
            VopOrderQueryOrderDetailRequest request = new VopOrderQueryOrderDetailRequest();
            request.setThirdOrderId(thirdOrderId);
            request.setJdOrderId(jdOrderId);
            VopOrderQueryOrderDetailResponse response = jdClient().execute(request);
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
            String operate = "查询订单详情";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询商品预计送达时间
     * @return 响应结果
     */
    public PredictSkuPromiseOpenResp predictSkuPromise(Long skuId, Integer skuNum, JdVOPAreaInfo areaInfo)
    {
        try
        {
            VopOrderPredictSkuPromiseRequest request = new VopOrderPredictSkuPromiseRequest();
            request.setSkuNum(skuNum);
            request.setSkuId(skuId);
            request.setProvinceId(areaInfo.getProvinceId());
            request.setCityId(areaInfo.getCityId());
            request.setCountyId(areaInfo.getCountyId());
            request.setTownId(areaInfo.getTownId());
            VopOrderPredictSkuPromiseResponse response = jdClient().execute(request);
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
            String operate = "查询商品预计送达时间";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询配送信息
     * @param jdOrderId 京东订单号，必填
     * @param thirdOrderId 第三方订单号
     * @return 响应结果
     */
    public DeliveryInfoQueryOpenResp queryDeliveryInfo(Long jdOrderId, String thirdOrderId)
    {
        try
        {
            VopOrderQueryDeliveryInfoRequest request = new VopOrderQueryDeliveryInfoRequest();
            request.setThirdOrderId(thirdOrderId);
            request.setJdOrderId(jdOrderId);
            VopOrderQueryDeliveryInfoResponse response = jdClient().execute(request);
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
            String operate = "查询配送信息";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 订单确认收货
     * @param jdOrderId 京东订单号，必填
     * @param thirdOrderId 第三方订单号
     * @return 响应结果
     */
    public Boolean confirmReceiveByOrder(Long jdOrderId, String thirdOrderId)
    {
        try
        {
            VopOrderConfirmReceiveByOrderRequest request = new VopOrderConfirmReceiveByOrderRequest();
            request.setThirdOrderId(thirdOrderId);
            request.setJdOrderId(jdOrderId);
            VopOrderConfirmReceiveByOrderResponse response = jdClient().execute(request);
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
            String operate = "订单确认收货";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 取消订单
     * @param jdOrderId 京东订单号，必填
     * @param thirdOrderId 第三方订单号
     * @param cancelReason 取消原因，长度限制 200
     * @return 响应结果
     */
    public Boolean cancelOrder(Long jdOrderId, String thirdOrderId, String cancelReason)
    {
        try
        {
            VopOrderCancelOrderRequest request = new VopOrderCancelOrderRequest();
            request.setThirdOrderId(thirdOrderId);
            request.setJdOrderId(jdOrderId);
            request.setCancelReason(cancelReason);
            VopOrderCancelOrderResponse response = jdClient().execute(request);
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
            String operate = "取消订单";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 查询账户余额
     * @return 响应结果
     */
    public CheckAccountBalanceOpenResp checkAccountBalance()
    {
        try
        {
            VopOrderCheckAccountBalanceRequest request = new VopOrderCheckAccountBalanceRequest();
            VopOrderCheckAccountBalanceResponse response = jdClient().execute(request);
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
            String operate = "查询账户余额";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
}
