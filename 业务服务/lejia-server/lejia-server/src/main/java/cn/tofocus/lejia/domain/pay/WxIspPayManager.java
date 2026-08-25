package cn.tofocus.lejia.domain.pay;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.partnerpayments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Amount;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Payer;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayWithRequestPaymentResponse;

import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WxIspPayManager
{
    /** 商户API私钥路径 */
//    private String privateKeyPath = "D:\\gitclone\\watch\\distribution\\server\\farmWatch-2.3.3.Dev-SNAPSHOT\\apiclient_key.pem";
        private String privateKeyPath = "/data/tofocus/server/zyysc/sx/isp/apiclient_key.pem";
    
    /** 商户证书序列号 */
    private String merchantSerialNumber = "51E31F0DBF012208E5A04822403964E9F49D072B";
    
    /** 商户APIV3密钥 */
    private String apiV3key = "408a774e23b29f4ab7ddff3bd8a2b3d2";
    
    private Config config = null;
    public static void main(String[] args)
    {
        WxIspPayManager w = new WxIspPayManager();
        w.testPayV3("orOeX5bek-w1YMFyiRA_bXV3YMZ0", new BigDecimal("0.01"), "202308151142366235");
    }
    public WxPayData testPayV3(String openid, BigDecimal amt, String tradeNo)
    {
        if (config == null) config = new RSAAutoCertificateConfig.Builder().merchantId("1634874428")
            .privateKeyFromPath(privateKeyPath)
            .merchantSerialNumber(merchantSerialNumber)
            .apiV3Key(apiV3key)
            .build();
        int money = amt.multiply(new BigDecimal(100)).intValue();
        Amount amount = new Amount();
        amount.setTotal(money);
        amount.setCurrency("CNY");
        PrepayRequest request = new PrepayRequest();
        request.setSpAppid("wx4b5d65104121241a");
        request.setSpMchid("1634874428");
        request.setSubAppid("wxa93d94a36ffe7807");
        request.setSubMchid("1640343823");
        request.setAmount(amount);
        request.setDescription("test");
        Payer payer = new Payer();
        payer.setSubOpenid(openid);
        request.setPayer(payer);
        request.setOutTradeNo(tradeNo);
        request.setNotifyUrl("https://small.xinanshizu.com/zyysc/v1/wx/pay/reurl/isp");
        log.info("请求内容：{}", request);
        
        PrepayWithRequestPaymentResponse response = new JsapiServiceExtension
            .Builder()
            .config(config)
            .build()
            .prepayWithRequestPayment(request, "wx07f78782d1684dc1");
        try
        {
            WxPayData res = new WxPayData();
            System.out.println("response: " + response);
            res.setAppId(response.getAppId());
            res.setTimeStamp(response.getTimeStamp());
            res.setNonceStr(response.getNonceStr());
            res.setPack(response.getPackageVal());
            res.setPackageVal(response.getPackageVal());
            res.setSignType(response.getSignType());
            res.setPaySign(response.getPaySign());
            return res;
        }
        catch (HttpException e)
        { // 发送HTTP请求失败
          // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义
        }
        catch (ServiceException e)
        { // 服务返回状态小于200或大于等于300，例如500
          // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义
        }
        catch (MalformedMessageException e)
        { // 服务返回成功，返回体类型不合法，或者解析返回体失败
          // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义
        }
        return null;
    }
    
}
