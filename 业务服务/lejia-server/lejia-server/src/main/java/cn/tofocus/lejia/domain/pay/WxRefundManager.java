package cn.tofocus.lejia.domain.pay;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.common.RandomStringGenerator;
import com.wechat.pay.java.service.refund.model.Amount;
import com.wechat.pay.java.service.refund.model.AmountReq;

import cn.tofocus.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;

@Slf4j
@Component
public class WxRefundManager
{
    // 微信要求 固定字段
    private static String schema = "WECHATPAY2-SHA256-RSA2048";
    // 退款接口路径
    private static String refundsUrl = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";
    // 商户ID
//    private static String merchantId = "1575634231";
    // 证书序列号
//    private static String certificateSerialNo = "62486DBCF7941AAC643EB1D35D427DA672FBCEA4";
    
    // 发起退款请求  返回true 退款失败 需要处理
    public Boolean createRefundOrder(String transactionId, String outRefundNo, long refund, long total, 
        String merchantId, String certificateSerialNo, String configLocalpath)
    {
//        String merchantId = "1575634231";
//        String certificateSerialNo = "62486DBCF7941AAC643EB1D35D427DA672FBCEA4";
//        if(ascription != null && 9 == ascription.intValue())
//        {
//            merchantId = "1675130351";
//            certificateSerialNo = "738A7210EFAEB985B75A7BE861FE6E22080BE5EC";
//        }
//        if(ascription != null && 14 == ascription.intValue())
//        {
//            merchantId = "1680391395";
//            certificateSerialNo = "1C4D21D68BBE26A7CD9E80A95D6EC41F679893E0";
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("transaction_id", transactionId);
        map.put("out_refund_no", outRefundNo);
        AmountReq ar = new AmountReq();
        ar.setRefund(refund);
        ar.setTotal(total);
        ar.setCurrency("CNY");
        map.put("amount", ar);
        String body = JsonUtil.toString(map);
        log.info("传给微信退款接口的参数:" + body);
        HttpUrl httpurl = HttpUrl.parse(refundsUrl);
        String buildMessage;
        try
        {
            buildMessage = getToken("POST", httpurl, body, merchantId, certificateSerialNo, configLocalpath);
            log.info("组合后的数据: {}", buildMessage);
            return postWx(refundsUrl, body, buildMessage);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    // 发起退款请求
    public void createRefundOrderV2(String transactionId, String outRefundNo, long refund, long total, 
        String merchantId, String certificateSerialNo, String configLocalpath, long payerTotal, long payerRefund,
        long settlementTotal, long settlementRefund, long discountRefund)
    {
//        String merchantId = "1575634231";
//        String certificateSerialNo = "62486DBCF7941AAC643EB1D35D427DA672FBCEA4";
//        if(ascription != null && 9 == ascription.intValue())
//        {
//            merchantId = "1675130351";
//            certificateSerialNo = "738A7210EFAEB985B75A7BE861FE6E22080BE5EC";
//        }
//        if(ascription != null && 14 == ascription.intValue())
//        {
//            merchantId = "1680391395";
//            certificateSerialNo = "1C4D21D68BBE26A7CD9E80A95D6EC41F679893E0";
//        }
        Map<String, Object> map = new HashMap<>();
        map.put("transaction_id", transactionId);
        map.put("out_refund_no", outRefundNo);
        Amount ar = new Amount();
        ar.setTotal(total);
        ar.setRefund(refund);
        ar.setCurrency("CNY");
        
        
        ar.setPayerTotal(payerTotal);
        ar.setPayerRefund(payerRefund);
        ar.setSettlementTotal(settlementTotal);
        ar.setSettlementRefund(settlementRefund);
        ar.setDiscountRefund(discountRefund);
        
        
        map.put("amount", ar);
        String body = JsonUtil.toString(map);
        log.info("传给微信退款接口的参数:" + body);
        HttpUrl httpurl = HttpUrl.parse(refundsUrl);
        String buildMessage;
        try
        {
            buildMessage = getToken("POST", httpurl, body, merchantId, certificateSerialNo, configLocalpath);
            log.info("组合后的数据: {}", buildMessage);
            postWx(refundsUrl, body, buildMessage);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }
    
    private String getToken(String method, HttpUrl url, String body, String merchantId, String certificateSerialNo, String configLocalpath)
        throws UnsupportedEncodingException
    {
        String nonceStr = RandomStringGenerator.getRandomStringByLength(32).toUpperCase();
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"), merchantId, configLocalpath);
        return "mchid=\"" + merchantId + "\"," + "nonce_str=\"" + nonceStr + "\"," + "timestamp=\"" + timestamp
            + "\"," + "serial_no=\"" + certificateSerialNo + "\"," + "signature=\"" + signature + "\"";
    }
    
    private String sign(byte[] message, String merchantId, String configLocalpath)
    {
        Signature sign;
        PrivateKey privateKey = null;
        try
        {
            sign = Signature.getInstance("SHA256withRSA");
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(getCertStream(configLocalpath), merchantId.toCharArray());
            Enumeration<String> aliases = ks.aliases();
            if (aliases.hasMoreElements())
            {
                String alias = aliases.nextElement();
                privateKey = (PrivateKey)ks.getKey(alias, merchantId.toCharArray());
            }
            sign.initSign(privateKey);
            sign.update(message);
            
            return Base64.getEncoder().encodeToString(sign.sign());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 读取证书
     */
    private static InputStream getCertStream(String configLocalpath)
    {
        byte[] certData = null;
        try
        {
            //通过路径读取
            InputStream certStream = new FileInputStream(new File(configLocalpath));
            //放到项目resources目录读取
//            InputStream certStream =
//                Thread.currentThread().getContextClassLoader().getResourceAsStream(ascription + "/apiclient_cert.p12");
            certData = IOUtils.toByteArray(certStream);
            certStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ByteArrayInputStream certBis = new ByteArrayInputStream(certData);
        return certBis;
    }
    
    private String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body)
    {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null)
        {
            canonicalUrl += "?" + url.encodedQuery();
        }
        return method + "\n" + canonicalUrl + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
    }
    
    private Boolean postWx(String url, String body, String buildMessage)
    {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 参数
        StringEntity entity = new StringEntity(body, "UTF-8");
        
        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        
        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("Accept", "application/json;charset=utf8");
        httpPost.setHeader("Authorization", schema + " " + buildMessage);
        httpPost.setEntity(entity);
        
        // 响应模型
        CloseableHttpResponse response = null;
        try
        {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            
            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null)
            {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                String jsonStr = EntityUtils.toString(responseEntity);
                System.out.println("响应内容为:" + jsonStr);
                JSONObject jsonObj = JSON.parseObject(jsonStr);
                String status = jsonObj.get("status").toString();
                if(!"SUCCESS".equals(status) && !"PROCESSING".equals(status))
                {
                    return true;
                }
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // 释放资源
                if (httpClient != null)
                {
                    httpClient.close();
                }
                if (response != null)
                {
                    response.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public static void main(String[] args)
    {
//        WxRefundManager wr = new WxRefundManager();
//        wr.createRefundOrder("4200002436202407250629814135", "912507245680472", 150, 150, "1575634231", "62486DBCF7941AAC643EB1D35D427DA672FBCEA4", "/data/tofocus/server/zyysc/ys/apiclient_cert.p12");
    }
    
}
