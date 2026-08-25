package cn.tofocus.lejia.app.v1.sys;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.domain.app.AppActivityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.common.Configure;
import com.tencent.common.Signature;
import com.tencent.common.XMLParser;

import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.lejia.domain.app.AppMemberManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.pay.WxPayManager;
import cn.tofocus.lejia.util.wx.PayResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/wx/pay")
@RestController
public class WxPayApiImpl
{
    @Autowired
    private AppOrderManager orderManager;
    
    @Autowired
    private WxPayManager payManager;
    
    @Autowired
    private AppMemberManager appMemberManager;
    
    @Autowired
    private RedisCounter counter;
    
    @Autowired
    private AppActivityManager appActivityManager;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    private Map<String,String> appidKeyMap = new HashMap<>();
    
    @PostConstruct
    private void init() 
    {
        List<SysAscription> list = ascriptionDao.findAll();
        for(SysAscription sa : list)
        {
            if(StringUtils.isNotBlank(sa.getConfigAppid()) && StringUtils.isNotBlank(sa.getConfigKey()))
                appidKeyMap.put(sa.getConfigAppid(), sa.getConfigKey());
        }
    }
    
    @RequestMapping(value = "/reurl")
    @ResponseBody
    public void reurl(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        System.out.println("微信支付回调");
        String r1 = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        String r2 = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名失败]]></return_msg></xml>";
        BufferedReader reader = null;
        reader = request.getReader();
        String line = "";
        String xmlString = null;
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null)
        {
            inputString.append(line);
        }
        xmlString = inputString.toString();
        System.out.println("xmlString: " + xmlString);
        Map<String, Object> map = XMLParser.getMapFromXML(xmlString);
        String signFromAPIResponse = map.get("sign").toString();
        response.setContentType("text/xml");
        map.put("sign", "");
        String appkey = map.get("appid").toString();
        SysAscription sa = ascriptionDao.byAppid(appkey);
        if(sa == null || StringUtils.isBlank(sa.getConfigKey()))
        {
            response.getWriter().write(r2);
            return;
        }
//        if (!appidKeyMap.containsKey(appkey))
//        {
//            response.getWriter().write(r2);
//            return;
//        }
//        Configure.setKey(appidKeyMap.get(appkey));
        Configure.setKey(sa.getConfigKey());
        String signForAPIResponse = Signature.getSign(map);
        if (!signForAPIResponse.equals(signFromAPIResponse))
        {
            response.getWriter().write(r2);
            return;
        }
        payManager.payLine(map);
        String out_trade_no = (String)map.get("out_trade_no");
        Long increment = counter.increment("zyysc", "order", out_trade_no);
        counter.expire("zyysc", "order", out_trade_no, 86400);
        if(increment != 1)
        {
            log.info("订单回调重复：{}", out_trade_no);
            response.getWriter().write(r1);
            return;
        }
        try
        {
            if (map.get("return_code").equals("SUCCESS"))
            {
                if (out_trade_no.startsWith("91"))
                {//订单支付成功回调
                    orderManager.payOrder(out_trade_no);
                }
                else if (out_trade_no.startsWith("92"))
                {//会员年费支付成功回调
                    appMemberManager.payOrder(out_trade_no, true);
                }
                else if (out_trade_no.startsWith("93"))
                {//会员充值支付成功回调
                    appMemberManager.payOrder(out_trade_no, true);
                }
                else if(out_trade_no.startsWith("95"))
                {
                    // 活动参与支付成功回调
                    appActivityManager.payOrder(out_trade_no);
                }
            }
            response.getWriter().write(r1);
        }
        catch (Exception e)
        {
            counter.set("zyysc", "order", out_trade_no, 0);
            log.info("支付回调异常： ", e);
        }
    }
    
    // 服务商回调
    @RequestMapping(value = "/reurl/isp")
    @ResponseBody
    public void reurlISP(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        System.out.println("服务商模式-微信支付回调");
//        String r1 = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        PayResult rrr = new PayResult();
        rrr.setReturnCode("SUCCESS");
        rrr.setReturnMsg("OK");
        BufferedReader reader = null;
        reader = request.getReader();
        String line = "";
        String xmlString = null;
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null)
        {
            inputString.append(line);
        }
        xmlString = inputString.toString();
        System.out.println(xmlString);
        Map<String, Object> res = XMLParser.getMapFromXML(xmlString);
        System.out.println("res: " + JsonUtil.toString(res, true));
//        if (!Signature.checkIsSignValidFromResponseString(xmlString))
//        {
//            rrr.setReturnCode("FAIL");
//            rrr.setReturnMsg("签名失败");
//            String r2 =
//                "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名失败]]></return_msg></xml>";
//            response.setContentType("text/xml");
//            response.getWriter().write(r2);
//        }
//        payManager.payLine(res);
//        String out_trade_no = (String)res.get("out_trade_no");
//        Long increment = counter.increment("zyysc", "order", out_trade_no);
//        counter.expire("zyysc", "order", out_trade_no, 86400);
//        if(increment != 1)
//        {
//            log.info("订单回调重复：{}", out_trade_no);
//            response.setContentType("text/xml");
//            response.getWriter().write(r1);
//            return;
//        }
//        try
//        {
//            if (res.get("return_code").equals("SUCCESS"))
//            {
//                if (out_trade_no.startsWith("91"))
//                {//订单支付成功回调
//                    orderManager.payOrder(out_trade_no);
//                }
//                else if (out_trade_no.startsWith("92"))
//                {//会员年费支付成功回调
//                    appMemberManager.payOrder(out_trade_no, true);
//                }
//                else if (out_trade_no.startsWith("93"))
//                {//会员充值支付成功回调
//                    appMemberManager.payOrder(out_trade_no, true);
//                }
//            }
//            response.setContentType("text/xml");
//            response.getWriter().write(r1);
//        }
//        catch (Exception e)
//        {
//            counter.set("zyysc", "order", out_trade_no, 0);
//            log.info("支付回调异常： ", e);
//        }
      
    }
    
}
