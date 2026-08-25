package cn.tofocus.lejia.app.v1.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.lejia.bean.dto.app.MktZxPayLineDto;
import cn.tofocus.lejia.bean.entity.market.MktZxPayLine;
import cn.tofocus.lejia.dao.market.MktZxPayLineDao;
import cn.tofocus.lejia.domain.app.AppMemberManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.util.XMLUtil;
import cn.tofocus.lejia.zx.pay.constants.Constants;
import cn.tofocus.lejia.zx.pay.util.Sign;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/zx/pay")
@RestController
public class ZxApiImpl
{
    
    @Autowired
    private MktZxPayLineDao zxPayLineDao;
    
    @Autowired
    private AppMemberManager appMemberManager;
    
    @Autowired
    private AppOrderManager orderManager;
    
    @Autowired
    private RedisCounter counter;
    
    @PostMapping("/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response)
        throws UnsupportedEncodingException, IOException
    {
        System.out.println("中信返回");
        String r1 = "<stream><return_code>SUCCESS</return_code></stream>";
        String r2 = "<stream><return_code>FAIL</return_code></stream>";
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String buffer = null;
        // 存放请求内容
        StringBuffer xml = new StringBuffer();
        while ((buffer = br.readLine()) != null)
        {
            // 在页面中显示读取到的请求参数
            xml.append(buffer);
        }
        String callbackMessage = xml.toString();
        log.info("callbackMessage: {}", callbackMessage);
        int of = callbackMessage.indexOf("<sign>");
        int of2 = callbackMessage.indexOf("</sign>");
        if (of == -1 || of2 == -1)
        {
            log.info("验签失败");
            response.setContentType("text/xml");
            response.getWriter().write(r2);
            return;
        }
        String sign = callbackMessage.substring(of + 6, of2);
        
        MktZxPayLineDto object = (MktZxPayLineDto)XMLUtil.convertXmlStrToObject(MktZxPayLineDto.class, callbackMessage);
        
        MktZxPayLine beanFrom = BeanUtil.beanFrom(MktZxPayLine.class, object);
        String out_trade_no = beanFrom.getOut_trade_no();
        log.info("out_trade_no: {}", out_trade_no);
        Long increment = counter.increment("zyysc", "order", out_trade_no);
        counter.expire("zyysc", "order", out_trade_no, 86400);
        if (increment != 1)
        {
            log.info("订单回调重复：{}", out_trade_no);
            response.setContentType("text/xml");
            response.getWriter().write(r1);
            return;
        }
        zxPayLineDao.add(beanFrom);
        
        try
        {
            // 验签 
            @SuppressWarnings("unchecked")
            Map<String, Object> infoObject =
                (Map<String, Object>)JSON.parseObject(JSON.toJSONString(object), Map.class);
            Map<String, String> info = new HashMap<>();
            for (String key : infoObject.keySet())
            {
                info.put(key, infoObject.get(key) + "");
            }
            String plain = Sign.sortByASCII(info);
            System.out.println("plain: " + plain);
            System.out.println("sign: " + sign);
            Boolean verifySign = Sign.verifySign(plain, sign, Constants.ZX_PUB_CER);
            if (!verifySign)
            {
                log.info("验签失败");
                response.setContentType("text/xml");
                response.getWriter().write(r2);
                counter.set("zyysc", "order", out_trade_no, 0);
                return;
            }
            
            if (beanFrom.getReturn_code().equals("SUCCESS"))
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
            }
            
            response.getWriter().write(r1);
        }
        catch (Exception e)
        {
            counter.set("zyysc", "order", out_trade_no, 0);
            log.info("支付回调异常： ", e);
        }
        
    }
    
}
