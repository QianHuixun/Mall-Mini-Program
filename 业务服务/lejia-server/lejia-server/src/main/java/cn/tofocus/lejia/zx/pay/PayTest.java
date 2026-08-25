package cn.tofocus.lejia.zx.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.ctc.wstx.util.DataUtil;
import com.sun.istack.Nullable;
import com.sun.xml.bind.v2.model.core.Element;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.txw2.Document;
import com.tencent.common.XMLParser;
import com.thoughtworks.xstream.XStream;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.json.JsonObject;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.zx.bean.pay.BillApply;
import cn.tofocus.lejia.zx.bean.pay.Micropay;
import cn.tofocus.lejia.zx.bean.pay.Native;
import cn.tofocus.lejia.zx.bean.pay.PayQuery;
import cn.tofocus.lejia.zx.bean.pay.Refund;
import cn.tofocus.lejia.zx.pay.constants.Constants;
import cn.tofocus.lejia.zx.pay.util.Sign;
import cn.tofocus.lejia.zx.util.HttpsPost;


public class PayTest
{
    /** 报文头*/
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";
    //测试
    private static final String httpsUrl = "http://totalpay.test.bank.ecitic.com/totalpay/unifyTrade.do";
    // 正式
//    private static final String httpsUrl = "https://totalpay.citicbank.com/totalpay/unifyTrade.do";
   
    public static void main(String[] args)
    {
        PayTest pt = new PayTest();
        pt.tradeNative();
//       pt.billApply();
        
    }
    
    // 商户对账单申请
    public void billApply()
    {
        BillApply ba = new BillApply();
        ba.setMCHNO("421010060000001");
//        ba.setMCHNO("023200010000001");
        ba.setSETTLEDATE("2022-03-31");
        ba.setVERSION("3.0.0");
        ba.setSIGN(getSign(ba));
        XStream xtreamData = new XStream();
        xtreamData.alias("stream", BillApply.class);
        String restr = xtreamData.toXML(ba)
            .replace("__", "_");
        restr = XML_HEAD + restr;
        System.out.println("restr: " + restr);
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> formEntity = new HttpEntity<>(restr, headers);
        // 下载
        ResponseEntity<String> entity = restTemplate.postForEntity("https://merchant.citicbank.com/dw/downLoadMchtBill.do", formEntity, String.class);
        // 查询
//        ResponseEntity<String> entity = restTemplate.postForEntity("https://merchant.citicbank.com/dw/applyMchtBill.do", formEntity, String.class);
//        ResponseEntity<String> entity = restTemplate.postForEntity("http://totalpay.test.bank.ecitic.com/dw/downLoadApl.do", formEntity, String.class);
        System.out.println("entity: " + JsonUtil.toString(entity.getBody(), true));
    }
    
    // 退款
    public void refund()
    {
        Refund r = new Refund();
        r.setService("unified.trade.refund");
        r.setMch_id("023200010000001");
        r.setOut_trade_no("7777D020122510481092923");
        r.setOut_refund_no("7777D0201225104810929231111");
        r.setTotal_fee("1");
        r.setRefund_fee("1");
        Map map = JSON.parseObject(JSON.toJSONString(r), Map.class);
        String plain = Sign.sortByASCII(map);
        System.out.println("plain: " + plain);

        System.out.println("\n==========Sign and VerifySign==========");

        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("sign: " + sign);
        r.setSign(sign);
        postXml(Refund.class, r);
    }
    
    public void payQuery()
    {
        PayQuery q = new PayQuery();
        q.setService("unified.trade.query");
        q.setMch_id("023200010000001");
        q.setOut_trade_no("7777D020122510481092923");
        Map map = JSON.parseObject(JSON.toJSONString(q), Map.class);
        String plain = Sign.sortByASCII(map);
        System.out.println("plain: " + plain);

        System.out.println("\n==========Sign and VerifySign==========");

        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("sign: " + sign);
        q.setSign(sign);
        postXml(PayQuery.class, q);
    }
    
    public WxPayData tradeNative() 
    {
        Random r = new Random();
        
        Native n = new Native();
        n.setService("unified.trade.native");
        n.setMch_id("421010060000001");
        n.setOut_trade_no(r.nextInt() + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
        n.setTotal_fee("1");
        n.setMch_create_ip("127.0.0.1");
        n.setNotify_url("https://ymkt.xinanshizu.com/zyysc/v1/zx/pay/notify");
        String body = "中文";
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
//        n.setOpenid("oeRPG6Henwen2U7neeRcKlMOnbz0");
        n.setSub_openid("oeRPG6Henwen2U7neeRcKlMOnbz0");
        n.setSub_appid("wxd085f7c6482122a5");
//        n.setSub_openid("ovehA5SkSQayrnXpqhe1d3uatQcI");
//        n.setSub_appid("wx23587f1c70f6b8da");
        Map map = JSON.parseObject(JSON.toJSONString(n), Map.class);
        String plain = Sign.sortByASCII(map);
        System.out.println("plain: " + plain);

        System.out.println("\n==========Sign and VerifySign==========");

        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("sign: " + sign);
        n.setSign(sign);
        
        XStream xtreamData = new XStream();
        xtreamData.alias("stream", Native.class);
        String restr = xtreamData.toXML(n)
            .replace("__", "_");
        restr = XML_HEAD + restr;
        System.out.println("restr: " + restr);
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> formEntity = new HttpEntity<>(restr, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(httpsUrl, formEntity, String.class);
        System.out.println("entity: " + JsonUtil.toString(entity.getBody()));
        
        org.dom4j.Document document;
        WxPayData payData = null;
        try
        {
            document = DocumentHelper.parseText(entity.getBody());
            org.dom4j.Element element = document.getRootElement();
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
    
    private void test11(String s)
    {
        try
        {
            org.dom4j.Document document = DocumentHelper.parseText(s);
            org.dom4j.Element element = document.getRootElement();
            org.dom4j.Element context = element.element("wc_pay_data");
            
            System.out.println("path: " + context.getText());
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
    }
    
//    private Document parseXmlFile(String in) {
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputSource is = new InputSource(new StringReader(in));
//            return db.parse(is);
//        } catch (ParserConfigurationException e) {
//            throw new RuntimeException(e);
//        } catch (SAXException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    
    
    /**
     * 统一支付
     * 需要填写 付款码 微信或者支付宝
     */
    public void micropay()
    {
//        String httpsUrl = "http://totalpay.test.bank.ecitic.com/totalpay/unifyTrade.do";
        Micropay m = new Micropay();
        m.setService("unified.trade.micropay");
        m.setMch_id("023200010000001");
        m.setOut_trade_no("7777D020122510481092923");
        m.setBody("aaa");
        m.setTotal_fee("1");
        m.setMch_create_ip("127.0.0.1");
        m.setAuth_code("282511719225113715");//付款码
        
        Map<String, String> info = new HashMap<>(16);
        info.put("service", "unified.trade.micropay");
        info.put("mch_id", "023200010000001");
        info.put("out_trade_no", "7777D020122510481092923");
        info.put("body", "aaa");
        info.put("total_fee", "1");
        info.put("mch_create_ip", "127.0.0.1");
        info.put("auth_code", "282511719225113715");
//        info.put("Chinese", "���ģ�����");

        String plain = Sign.sortByASCII(info);
        System.out.println("plain: " + plain);

        System.out.println("\n==========Sign and VerifySign==========");

        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("sign: " + sign);
        m.setSign(sign);
        
        XStream xtreamData = new XStream();
        xtreamData.alias("stream", Micropay.class);
        String restr = xtreamData.toXML(m)
            .replace("__", "_");
        restr = XML_HEAD + restr;
        
        System.out.println("restr: " + restr);
        
//        HttpUtils.parsePostData(len, in)
       // String doPost = doPost(httpsUrl, restr);
        
        //发送请求获得响应数据
//        String resStr = HttpsPost.post(httpsUrl, restr);

       // System.out.println("doPost: " + doPost);
    }
    
    public static String doPost(String httpUrl, @Nullable String param) {
        StringBuffer result = new StringBuffer();
        //连接
        HttpURLConnection connection = null;
        OutputStream os = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            //创建连接对象
            URL url = new URL(httpUrl);
            //创建连接
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("POST");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //DoOutput设置是否向httpUrlConnection输出，DoInput设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
            //设置是否可读取
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            //拼装参数
            if (null != param && param.equals("")) {
                //设置参数
                os = connection.getOutputStream();
                //拼装参数
                os.write(param.getBytes("UTF-8"));
            }
            //设置权限
            //设置请求头等
            //开启连接
            //connection.connect();
            //读取响应
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "GBK"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                        result.append("\r\n");
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            connection.disconnect();
        }
        return result.toString();
    }
    
    private static <T> String getSign(T t)
    {
        Map map = JSON.parseObject(JSON.toJSONString(t), Map.class);
        String plain = Sign.sortByASCII(map);
        System.out.println("plain: " + plain);

        System.out.println("\n==========Sign and VerifySign==========");

        String sign = Sign.sign(plain, Constants.PWD, Constants.PRI_KEY, Constants.PUB_CER);
        System.out.println("sign: " + sign);
        return sign;
    }
    
    private static <T> void postXml(Class<?> clazz, T t)
    {
        XStream xtreamData = new XStream();
        xtreamData.alias("stream", clazz);
        String restr = xtreamData.toXML(t)
            .replace("__", "_");
        restr = XML_HEAD + restr;
        System.out.println("restr: " + restr);
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> formEntity = new HttpEntity<>(restr, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(httpsUrl, formEntity, String.class);
        System.out.println("entity: " + JsonUtil.toString(entity.getBody(), true));
    }

}
