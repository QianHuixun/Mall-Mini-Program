package cn.tofocus.lejia.app.v1.sys;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.security.MD5;
import cn.tofocus.common.util.security.SHA;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.lejia.bean.entity.zx.ThirdPayLineEntity;
import cn.tofocus.lejia.dao.zx.ThirdPayLineDao;
import cn.tofocus.lejia.domain.app.AppActivityManager;
import cn.tofocus.lejia.domain.app.AppMemberManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/chinaums/pay")
@RestController
public class ChinaUmsPayApiImpl
{

    private final static String KEY = "zka64dtS5aAnBwYWHJhYYtwCMjmf5c6nW8nDr2jGhMxA5ewF";
    
    @Autowired
    private ThirdPayLineDao thirdPayLineDao;
    
    @Autowired
    private AppOrderManager orderManager;
    
    @Autowired
    private AppActivityManager appActivityManager;
    
    @Autowired
    private AppMemberManager appMemberManager;
    
    @Autowired
    private RedisCounter counter;

    @PostMapping("/notify")
    public String notify(HttpServletRequest request)
    {
        log.info("通联商务支付回调：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        try
        {
            Map<String, String[]> map = request.getParameterMap();
            Map<String, String> paramMap = new LinkedHashMap<>();
            
            map.forEach((String key, String[] values) -> {
                paramMap.put(key, values[0]);
            });
            log.info("通联商务支付回调数据：{}", JsonUtil.toString(paramMap));
            verifySign(paramMap);
            ThirdPayLineEntity tol = new ThirdPayLineEntity();
            BeanUtils.copyProperties(tol, paramMap);
            
            // TODO 上线后确认 merOrderId 是否是 我们这边传过去的订单号
            String merOrderId = tol.getMerOrderId();
            merOrderId = merOrderId.replace("3EY5", "");
            Long increment = counter.increment("zyysc", "order", merOrderId);
            counter.expire("zyysc", "order", merOrderId, 86400);
            if(increment != 1)
            {
                log.info("订单回调重复：{}", merOrderId);
                return "FAIL";
            }
            thirdPayLineDao.add(tol);
            System.out.println("merOrderId: " + merOrderId);
            try
            {
                if(paramMap.containsKey("status"))
                {
                    if(paramMap.get("status").equals("TRADE_SUCCESS"))
                    {
                        if (merOrderId.startsWith("91"))
                        {
                            //订单支付成功回调
                            orderManager.payOrder(merOrderId);
                        }
                        else if (merOrderId.startsWith("92"))
                        {
                            //会员年费支付成功回调
                            appMemberManager.payOrder(merOrderId, true);
                        }
                        else if (merOrderId.startsWith("93"))
                        {
                            //会员充值支付成功回调
                            appMemberManager.payOrder(merOrderId, true);
                        }
                        else if(merOrderId.startsWith("95"))
                        {
                            // 活动参与支付成功回调
                            appActivityManager.payOrder(merOrderId);
                        }
                    }
                }
                return "SUCCESS";
            }
            catch (Exception e)
            {
                counter.set("zyysc", "order", merOrderId, 0);
                log.info("支付回调异常： ", e);
            }
        }
        catch (Exception e)
        {
            log.error("通联商务支付回调异常", e);
        }
        return "SUCCESS";
    }
    
    private static void verifySign(Map<String, String> paramMap)
        throws Exception
    {
        // 先取出sign及signType字段
        String sign = paramMap.get("sign");
        if (StringUtil.isBlank(sign))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "缺少sign参数");
        sign = sign.toUpperCase();
        String signType = paramMap.get("signType");
        // 再将Map中所有参数（不包括sign）按照字段名的ASCII码从小到大排序（字典序）
        Map<String, String> sortedMap = new TreeMap<>(paramMap);
        sortedMap.remove("sign");
        // 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedMap.entrySet())
        {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // 去掉最后多余的&
        sb.deleteCharAt(sb.length() - 1);
        // 最后在stringA最后拼接上key得到stringSignTemp字符串，判断signType，如果是MD5，则进行MD5编码，否则进行SHA256编码
        String stringSignTemp = sb.append(KEY).toString();
        String mySign = null;
        if ("MD5".equals(signType))
        {
            mySign = MD5.getMD5(stringSignTemp).toUpperCase();
        }
        else
        {
            mySign = SHA.getSHA(SHA.SHA256, stringSignTemp).toUpperCase();
        }
        System.out.println("sign: " + sign);
        System.out.println("mySign: " + mySign);
        if (!mySign.equals(sign))
            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, "签名验证失败");
    }
    
}
