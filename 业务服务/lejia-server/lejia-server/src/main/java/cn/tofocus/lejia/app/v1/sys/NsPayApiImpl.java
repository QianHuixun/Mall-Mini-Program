package cn.tofocus.lejia.app.v1.sys;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.lejia.bean.entity.ns.MktNsPayLine;
import cn.tofocus.lejia.dao.market.MktOrderCodeDao;
import cn.tofocus.lejia.dao.ns.MktNsPayLineDao;
import cn.tofocus.lejia.domain.app.AppMemberManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/ns/pay")
@RestController
public class NsPayApiImpl
{
    
    @Autowired
    private MktNsPayLineDao nsPayLineDao;
    
    @Autowired
    private AppMemberManager appMemberManager;
    
    @Autowired
    private AppOrderManager orderManager;
    
    @Autowired
    private MktOrderCodeDao orderCodeDao;
    
    @Autowired
    private RedisCounter counter;
    
    @RequestMapping(value = "/reurl")
    @ResponseBody
    public void reurl(HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        System.out.println("*************农商支付回调*************");
        String r1 = "{\"code\":\"00000000\",\"messga\":\"success\"}";
        String r2 = "{\"code\":\"error\",\"messga\":\"notice error\"}";
        
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
        JSONObject object = JSON.parseObject(callbackMessage);
        int of = callbackMessage.indexOf("sign");
        if (of == -1)
        {
            log.info("验签失败");
            response.getWriter().write(r2);
            return;
        }
        JSONObject jo = JSON.parseObject(object.get("bizContent").toString());
        MktNsPayLine nsPayLine = assembleNsPay(jo);
        String outTradeNo = nsPayLine.getOutTradeNo();
        Long increment = counter.increment("zyysc", "order", outTradeNo);
        counter.expire("zyysc", "order", outTradeNo, 86400);
        if (increment != 1)
        {
            log.info("订单回调重复：{}", outTradeNo);
            response.getWriter().write(r1);
            return;
        }
        nsPayLineDao.add(nsPayLine);
        
        if (nsPayLine.getReturnCode().equals("S"))
        {
            if (outTradeNo.startsWith("91"))
            {//订单支付成功回调
                orderManager.payOrder(outTradeNo);
            }
            else if (outTradeNo.startsWith("92"))
            {//会员年费支付成功回调
                appMemberManager.payOrder(outTradeNo, true);
            }
            else if (outTradeNo.startsWith("93"))
            {//会员充值支付成功回调
                appMemberManager.payOrder(outTradeNo, true);
            }
        }
        
        response.getWriter().write(r1);
    }
    
    private MktNsPayLine assembleNsPay(JSONObject object)
    {
        MktNsPayLine np = new MktNsPayLine();
        np.setReturnCode(object.get("returnCode").toString());
        np.setReturnMsg(object.get("returnMsg").toString());
        np.setErrCode(object.get("subCode").toString());
        np.setErrMsg(object.get("subMsg").toString());
        np.setNoticeType(object.get("noticeType").toString());
        np.setTransactionId(object.get("orderId").toString());
        np.setOutTradeNo(object.get("bizId").toString());
        np.setTotalFee(object.get("payCtAmount").toString());
        np.setFeeType(object.get("feeType").toString());
        np.setTimeEnd(object.get("timeEnd").toString());
        np.setBankType(object.get("bankType").toString());
        np.setCashFee(object.get("cashFee").toString());
        np.setCouponFee(object.get("couponFee").toString());
        np.setSettlementTotalFee(object.get("settlementTotalFee").toString());
        np.setAttach(object.get("attach").toString());
        np.setRout(object.get("rout").toString());
        return np;
    }
    
}
