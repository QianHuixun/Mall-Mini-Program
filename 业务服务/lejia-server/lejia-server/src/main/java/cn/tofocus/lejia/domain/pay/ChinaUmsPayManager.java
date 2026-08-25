package cn.tofocus.lejia.domain.pay;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsWxQueryRequest;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsWxQueryResponse;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsWxUnifiedOrderRequest;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsWxUnifiedOrderResponse;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class ChinaUmsPayManager extends ChinaUmsBaseManager
{

    public WxPayData chinaUmsPay(String subOpenId, String merOrderId, BigDecimal amt) throws Exception
    {
        WxPayData res = new WxPayData();
        String notifyUrl = "https://small.xinanshizu.com/zyysc/v1/chinaums/pay/notify";
        
        // 小程序支付
        ChinaUmsWxUnifiedOrderRequest request = new ChinaUmsWxUnifiedOrderRequest();
        request.setRequestTimestamp(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        request.setMerOrderId("3EY5" + merOrderId);
        request.setMid(Constant.ZxConfig.TJ_MID);
        request.setTid(Constant.ZxConfig.TJ_TID);
        request.setTotalAmount(amt.multiply(new BigDecimal("100")).intValue());
        request.setSubAppId(Constant.ZxConfig.TJ_SUBAPPID);
        request.setTradeType("MINI");
        request.setSubOpenId(subOpenId);
        // 15分钟后失效
        request.setExpireTime(DateUtil.formatDate(DateUtil.localDateTime2Date(LocalDateTime.now().plusMinutes(15)),
            "yyyy-MM-dd HH:mm:ss"));
        request.setNotifyUrl(notifyUrl);
        
        String postRequest = postRequest(JsonUtil.toString(request));
        
        ChinaUmsWxUnifiedOrderResponse response = JsonUtil.getBean(postRequest, ChinaUmsWxUnifiedOrderResponse.class);
        System.out.println("支付回调回来" + JsonUtil.toString(response));
        
        JSONObject jsonObject = (JSONObject)JSON.toJSON(response.getMiniPayRequest());
        res.setAppId(jsonObject.get((Object)"appId").toString());
        res.setTimeStamp(jsonObject.get((Object)"timeStamp").toString());
        res.setNonceStr(jsonObject.get((Object)"nonceStr").toString());
        res.setPack(jsonObject.get((Object)"package").toString());
        res.setSignType(jsonObject.get((Object)"signType").toString());
        res.setPaySign(jsonObject.get((Object)"paySign").toString());
        return res;
    }
    
    
    public ChinaUmsWxQueryResponse chinaUmsQuery(String merOrderId)
    {
        // 订单交易查询
        ChinaUmsWxQueryRequest queryRequest = new ChinaUmsWxQueryRequest();
        queryRequest.setRequestTimestamp(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        queryRequest.setMid(Constant.ZxConfig.TJ_MID);
        queryRequest.setTid(Constant.ZxConfig.TJ_TID);
        queryRequest.setMerOrderId("3EY5" + merOrderId);
        
        String queryRes;
        try
        {
            queryRes = postRequestQuery(JsonUtil.toString(queryRequest));
            ChinaUmsWxQueryResponse queryResponse = JsonUtil.getBean(queryRes, ChinaUmsWxQueryResponse.class);
            System.out.println(JsonUtil.toString(queryResponse));
            return queryResponse;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
