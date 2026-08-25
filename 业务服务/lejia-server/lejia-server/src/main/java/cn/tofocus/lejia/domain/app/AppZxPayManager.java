package cn.tofocus.lejia.domain.app;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.XStream;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonObject;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.zx.bean.pay.Native;
import cn.tofocus.lejia.zx.pay.constants.Constants;
import cn.tofocus.lejia.zx.pay.util.Sign;

@Component
public class AppZxPayManager
{
    /** 报文头*/
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";
    
    //    private static final String httpsUrl = "http://totalpay.test.bank.ecitic.com/totalpay/unifyTrade.do";
    private static final String httpsUrl = "https://totalpay.citicbank.com/totalpay/unifyTrade.do";
    
    @Value("${zx.pay.notify.url}")
    private String notifyUrl;
    
    public WxPayData tradeNative(String openid, String payNumber, BigDecimal amtn, String body, BigDecimal longitude, BigDecimal latitude)
    {
        WxPayData postXml = null;
        BigDecimal hundred = new BigDecimal(100);
        Native n = new Native();
        n.setService("unified.trade.native");
        n.setMch_id("421010060000001");
        n.setMch_create_ip("127.0.0.1");
        n.setOut_trade_no(payNumber);
        n.setTotal_fee(amtn.multiply(hundred).setScale(0).toString());
        n.setNotify_url(notifyUrl + "v1/zx/pay/notify");
        
        if (StringUtil.isNotEmpty(body) && body.length() > 126) body = body.substring(0, 126);
        byte[] b;
        try
        {
            b = body.getBytes("GBK");
            n.setBody(b.toString());
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        n.setTrade_type("JSAPI");
        n.setSub_openid(openid);
        n.setSub_appid("wx23587f1c70f6b8da");
        n.setVersion("3.0.1");
        n.setTerminal_info(getTerminalInfo(longitude, latitude));
        n.setSign(getSign(n));
        postXml = postXml(Native.class, n);
        
        return postXml;
    }
    
    private String getTerminalInfo(BigDecimal longitude, BigDecimal latitude)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("device_type", "11");
        Object json = JSONObject.toJSON(map);
        return json.toString();
    }
    
    @SuppressWarnings("rawtypes")
    private <T> String getSign(T t)
    {
        Map map = JSON.parseObject(JSON.toJSONString(t), Map.class);
        String plain = Sign.sortByASCII(map);
        System.out.println("plain: " + plain);
        System.out.println("==========Sign and VerifySign==========");
        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("sign: " + sign);
        return sign;
    }
    
    private <T> WxPayData postXml(Class clazz, T t)
    {
        XStream xtreamData = new XStream();
        xtreamData.alias("stream", clazz);
        String restr = xtreamData.toXML(t).replace("__", "_");
        restr = XML_HEAD + restr;
        System.out.println("restr: " + restr);
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> formEntity = new HttpEntity<>(restr, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(httpsUrl, formEntity, String.class);
        System.out.println("entity: " + JsonUtil.toString(entity, true));
        
        org.dom4j.Document document;
        WxPayData payData = null;
        try
        {
            document = DocumentHelper.parseText(entity.getBody());
            org.dom4j.Element element = document.getRootElement();
            element.element("result_code").getText();
            String return_code = element.element("result_code").getText();
            if("FAIL".equals(return_code))
            {
                throw TofocusException.of(LejiaErrCode.WRONG_WEPAY);
            }
            if("SUCCESS".equals(return_code))
            {
                org.dom4j.Element context = element.element("wc_pay_data");
                String text = context.getText();
                JsonObject j = new JsonObject(text);
                payData = new WxPayData();
                payData.setAppId(j.get("appId").toString());
                payData.setTimeStamp(j.get("timeStamp").toString());
                payData.setNonceStr(j.get("nonceStr").toString());
                payData.setPack(j.get("package").toString());
                // .replace("prepay_id=", "")
                payData.setSignType(j.get("signType").toString());
                payData.setPaySign(j.get("paySign").toString());
            }
            
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        
        return payData;
    }
}
