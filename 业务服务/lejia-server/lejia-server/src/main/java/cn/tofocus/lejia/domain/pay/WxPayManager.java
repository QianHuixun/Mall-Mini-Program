package cn.tofocus.lejia.domain.pay;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.tencent.common.Configure;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.dao.market.MktPayLineDao;
import cn.tofocus.lejia.util.wx.PayJs;
import cn.tofocus.lejia.util.wx.PayReqData;
import cn.tofocus.lejia.util.wx.PayRes;
import cn.tofocus.lejia.util.wx.PayService;


@Component
public class WxPayManager
{
    @Value("${wx.pay.test:false}")
    private Boolean wxPayTest;
    
    @Autowired
    private MktPayLineDao payDao;
    
    public PayJs topayIvc(String spbill_create_ip, String openid, String tradeNo, BigDecimal amt, WeixinConfig wxc)
        throws Exception
    {
        Configure.setCertLocalPath(wxc.getConfigLocalpath());
        Configure.setKey(wxc.getConfigKey());
        Configure.setCertPassword(wxc.getMCH_ID());
        int money = amt.multiply(new BigDecimal(100)).intValue();
        System.out.println("调用wxpay_topay");
        String out_trade_no = tradeNo;
        System.out.println("步骤1");
        PayReqData req = new PayReqData(money, wxc.getAB_NAME(), wxc.getPkey().toString(), out_trade_no, spbill_create_ip,
            wxc.getRE_URL(), openid, "", wxc);
        System.out.println("步骤2");
        PayService service = new PayService();
        String res = service.request(req);
        System.out.println("步骤3");
        System.out.println("res:" + res);
        if(StringUtils.isBlank(res) && Boolean.TRUE.equals(wxPayTest))
        {
            PayJs js = new PayJs("prepay_id=wx0709074921331747c6c8f15f03896e0000", "wxf8e8ea188dd9d52c",
                "1783386469772", "a2sw258r5tet0zcu318np6f52yryrz4x");
            return js;
        }
        //		PayRes xmlres = (PayRes) Util.getObjectFromXML(res, PayRes.class);
        PayRes xmlres = objectFromXml(res);
        PayJs js = new PayJs(xmlres);
        System.out.println("步骤4");
        System.out.println(xmlres.getPrepay_id());
        return js;
    }
    
    private PayRes objectFromXml(String xmlStr)
    {
        Class<PayRes> clazz = PayRes.class;
        Field[] fields = clazz.getDeclaredFields();
        Document xml = Jsoup.parse(xmlStr);
        Elements elements = xml.getElementsByTag("xml");
        Element element = elements.get(0);
        JSONObject dto = new JSONObject();
        for (Field field : fields)
        {
            Elements el = element.select(field.getName());
            if (!el.isEmpty() && StringUtil.isNotEmpty(el.text())) dto.put(field.getName(), el.text());
        }
        PayRes bean = JsonUtil.getBean(dto.toString(), PayRes.class);
        return bean;
    }
    
    public void payLine(Map<String, Object> res)
    {
        try
        {
            Integer ascription = 2;
            if(res.containsKey("attach"))
                ascription = Integer.valueOf((String)res.get("attach"));
            payLine((String)res.get("out_trade_no"),
                (String)res.get("transaction_id"),
                (String)res.get("time_end"),
                (String)res.get("cash_fee"),
                (String)res.get("result_code"),
                ascription);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void payLine(String out_trade_no, String transaction_id, String time_end, String cash_fee,
        String result_code, Integer ascription)
    {
        List<MktPayLine> exec = payDao.select().eq("orderNumber", out_trade_no).exec();
        if (!exec.isEmpty()) return;
        MktPayLine line = new MktPayLine();
        line.setAmt(cash_fee);
        line.setCode(transaction_id);
        line.setOrderNumber(out_trade_no);
        line.setPayTime(time_end);
        line.setPayType(PayType.ORDER_WEIXIN);
        line.setStatus(result_code);
        line.setAscription(ascription);
        payDao.add(line);
    }
    
    public static void main(String[] args)
    {
        String mchid = "1575634231";
        String nonce_str = RandomStringGenerator.getRandomStringByLength(32);
        // 商户API证书序列号
        String serial_no = "123RdPG2lFj9PMzVniYgqlE6JCY55678";
        String timestamp = String.valueOf(System.currentTimeMillis());
        Map<String, Object> map = new HashMap<>();
        map.put("mchid", mchid);
        map.put("nonce_str", nonce_str);
        map.put("serial_no", serial_no);
        map.put("timestamp", timestamp);
        String signature = Signature.getSign(map);
        System.out.println("signature: " + signature);
        String s = "Authorization: WECHATPAY2-SHA256-RSA2048 mchid=\"" + mchid + "\",nonce_str=\"" 
            + nonce_str + "\",signature=\"" + signature + "\",timestam=\"" + timestamp + "\",serial_no=\""
            + serial_no +"\"";
        System.out.println(s);
    }
//    WECHATPAY2-SHA256-RSA2048 mchid="1575634231", 
    
    
   
  
}

