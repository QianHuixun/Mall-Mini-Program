package cn.tofocus.lejia.domain.pay;

import com.chinaums.open.api.OpenApiCache;
import com.chinaums.open.api.OpenApiContext;
import com.chinaums.open.api.constants.ConfigBean;
import com.chinaums.open.api.internal.util.http.HttpTransport;

import cn.tofocus.common.util.Util;

public abstract class ChinaUmsBaseManager
{
    private final static String URL = "https://api-mop.chinaums.com/v1/netpay/wx/unified-order";
    
    private final static String QUERY_URL = "https://api-mop.chinaums.com/v1/netpay/query";
    
    private static final String REFUND_URL = "https://api-mop.chinaums.com/v1/netpay/refund";
    
    private static final String REFUND_QUERY_URL = "https://api-mop.chinaums.com/v1/netpay/refund-query";
    
    //开发者ID
    private final static String APP_ID = "8a81c1be96cf23aa019768554d860698";
    
    //开发者秘钥
    private final static String APP_KEY = "4db742e1206642beb6b098eaabe39220";
    
    static String postRequest(String request)
        throws Exception
    {
        ConfigBean configBean = new ConfigBean();
        OpenApiContext context = new OpenApiContext();
        context.setStartTime(System.currentTimeMillis());
        context.setRequestId(Util.getUUID());
        context.setOpenServUrl(URL.split("/v")[0].concat("/"));
        context.setApiServiceUrl(URL);
        context.setVersion(URL.split("/")[3]);
        context.setAppId(APP_ID);
        context.setAppKey(APP_KEY);
        context.setConfigBean(configBean);
        context.setServiceCode(URL.split("/v")[1].substring(1));
        OpenApiCache.getCurrentToken(context);
        return HttpTransport.getInstance().doPost(context, request);
    }
    
    static String postRequestQuery(String request)
        throws Exception
    {
        ConfigBean configBean = new ConfigBean();
        OpenApiContext context = new OpenApiContext();
        context.setStartTime(System.currentTimeMillis());
        context.setRequestId(Util.getUUID());
        context.setOpenServUrl(QUERY_URL.split("/v")[0].concat("/"));
        context.setApiServiceUrl(QUERY_URL);
        context.setVersion(QUERY_URL.split("/")[3]);
        context.setAppId(APP_ID);
        context.setAppKey(APP_KEY);
        context.setConfigBean(configBean);
        context.setServiceCode(QUERY_URL.split("/v")[1].substring(1));
        OpenApiCache.getCurrentToken(context);
        return HttpTransport.getInstance().doPost(context, request);
    }
    
    static String postRequestRefund(String request)
        throws Exception
    {
        ConfigBean configBean = new ConfigBean();
        OpenApiContext context = new OpenApiContext();
        context.setStartTime(System.currentTimeMillis());
        context.setRequestId(Util.getUUID());
        context.setOpenServUrl(REFUND_URL.split("/v")[0].concat("/"));
        context.setApiServiceUrl(REFUND_URL);
        context.setVersion(REFUND_URL.split("/")[3]);
        context.setAppId(APP_ID);
        context.setAppKey(APP_KEY);
        context.setConfigBean(configBean);
        context.setServiceCode(REFUND_URL.split("/v")[1].substring(1));
        OpenApiCache.getCurrentToken(context);
        return HttpTransport.getInstance().doPost(context, request);
    }
    
    static String postRequestRefundQuery(String request)
        throws Exception
    {
        ConfigBean configBean = new ConfigBean();
        OpenApiContext context = new OpenApiContext();
        context.setStartTime(System.currentTimeMillis());
        context.setRequestId(Util.getUUID());
        context.setOpenServUrl(REFUND_QUERY_URL.split("/v")[0].concat("/"));
        context.setApiServiceUrl(REFUND_QUERY_URL);
        context.setVersion(REFUND_QUERY_URL.split("/")[3]);
        context.setAppId(APP_ID);
        context.setAppKey(APP_KEY);
        context.setConfigBean(configBean);
        context.setServiceCode(REFUND_QUERY_URL.split("/v")[1].substring(1));
        OpenApiCache.getCurrentToken(context);
        return HttpTransport.getInstance().doPost(context, request);
    }
}
