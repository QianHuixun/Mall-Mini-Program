package cn.tofocus.lejia.domain.pay;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsRefundQueryRequest;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsRefundQueryResponse;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsRefundRequest;
import cn.tofocus.lejia.domain.pay.bean.chinaums.ChinaUmsRefundResponse;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class ChinaUmsRefundManager extends ChinaUmsBaseManager
{
    
    public ChinaUmsRefundResponse chinaUmsRefund(String merOrderId, String refundOrderId, BigDecimal amt)
    {
        // 退款
        ChinaUmsRefundRequest request = new ChinaUmsRefundRequest();
        request.setRequestTimestamp(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        request.setMid(Constant.ZxConfig.TJ_MID);
        request.setTid(Constant.ZxConfig.TJ_TID);
        // "3EY5" + 
        request.setMerOrderId(merOrderId);
        request.setRefundAmount(amt.intValue());
        request.setRefundOrderId("3EY5" + refundOrderId);
        
        String res;
        try
        {
            res = postRequestRefund(JsonUtil.toString(request));
            ChinaUmsRefundResponse response = JsonUtil.getBean(res, ChinaUmsRefundResponse.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new ChinaUmsRefundResponse();
    }
    
    public String chinaUmsRefundQuery(String refundOrderId) throws Exception
    {
        // 退款查询
        ChinaUmsRefundQueryRequest queryRequest = new ChinaUmsRefundQueryRequest();
        queryRequest.setRequestTimestamp(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        queryRequest.setMid(Constant.ZxConfig.TJ_MID);
        queryRequest.setTid(Constant.ZxConfig.TJ_TID);
        // "3EY5" + 
        queryRequest.setMerOrderId(refundOrderId);
        
        String queryRes = postRequestRefundQuery(JsonUtil.toString(queryRequest));
        ChinaUmsRefundQueryResponse queryResponse = JsonUtil.getBean(queryRes, ChinaUmsRefundQueryResponse.class);
        System.out.println(JsonUtil.toString(queryResponse));
        return "";
    }
}
