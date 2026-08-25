package cn.tofocus.lejia.api.pub;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.domain.TjZxManager;
import cn.tofocus.lejia.domain.zx.ZxUserManager;
import cn.tofocus.lejia.utils.DateUtil;
import cn.tofocus.lejia.zx.beanV2.ZxNotify;
import cn.tofocus.lejia.zx.beanV2.ZxNotifyData;
import cn.tofocus.lejia.zx.utilV2.Constants;
import cn.tofocus.lejia.zx.utilV2.XstreamUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/pub/citicbank")
@RestController
public class PubZxCallbackApiImpl
{
    @Autowired
    private TjZxManager tjZxManager;
    @Autowired
    private ZxUserManager zxUserManager;
    
    @PostMapping(value = "/dsgj/notify")
    public String callback(HttpServletRequest request)
    {
        log.info("中信银行e管家回调：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        try
        {
            //接收回调数据
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setExpandEntityReferences(false);
            String result = null;
            while ((result = reader.readLine()) != null)
            {
                buffer.append(result);
            }
            result = buffer.toString();
            reader.close();
            log.info("中信银行e管家回调数据：{}", result);
            ZxNotify resData = XstreamUtils.toBean(result, ZxNotify.class);
            // 05-入金通知
            if("05".equals(resData.getNOTIFY_TP()) || "01".equals(resData.getNOTIFY_TP()))
            {
                log.info("中信回调,进入入金处理");
                ZxNotifyData znd = resData.getNOTIFY_DATA();
                ZxFileType zft = ZxFileType.QUDAO_RUJIN;
                if("C".equals(znd.getC_D_FLAG()))
                {
                    if("03".equals(znd.getTRANS_TP()))
                        zft = ZxFileType.PINGTAI_RUJIN;
                    if("02".equals(znd.getTRANS_TP()))
                        zft = ZxFileType.QIYE_RUJIN;
                    if("01".equals(znd.getTRANS_TP()))
                        zft = ZxFileType.GREN_RUJIN;
                    if("平台入金".equals(znd.getTRANS_TP()))
                        zft = ZxFileType.PINGTAI_RUJIN;
                    if("企业用户入金".equals(znd.getTRANS_TP()))
                        zft = ZxFileType.QIYE_RUJIN;
                    if("个人用户入金".equals(znd.getTRANS_TP()))
                        zft = ZxFileType.GREN_RUJIN;
                    if("入金".equals(znd.getTRANS_TP()))
                    {
                        // 线下转账传过来是 入金,其他转账是否是入金 目前不确定 直接调 21000047接口 然后结束
                        tjZxManager.t21000047(new BigDecimal(znd.getTRANS_AMT()), null, null, null, null, null, null);
                        return "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><ROOT><RSP_CODE>00000</RSP_CODE><RSP_MSG>成功</RSP_MSG></ROOT>";
                    }
                }
                String trans_DT = znd.getTRANS_DT();
                Calendar cal = Calendar.getInstance();
                String formatDate = DateUtil.formatDate(cal.getTime());
                formatDate = formatDate.substring(formatDate.length( )- 9, formatDate.length());
                Date date = DateUtil.formatDateStr(trans_DT + formatDate, "yyyyMMdd HH:mm:ss");
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                Date previousDate = cal.getTime();
                // 钱划到 担保登记簿   
                tjZxManager.runGuarantee(new BigDecimal(znd.getTRANS_AMT()), resData.getNOTIFY_SSN(), 
                    previousDate, zft, znd.getUSER_ID(), Constants.ascription);
            }
            // 02-文件处理状态
            if("02".equals(resData.getNOTIFY_TP()))
            {
                log.info("中信回调,进入文件处理");
                ZxNotifyData znd = resData.getNOTIFY_DATA();
                if("Z".equals(znd.getFILE_ST()))
                {
                    String fn = znd.getFILE_NAME().replace(".ZIP", "");
                    int indexOf = fn.indexOf("830");
                    StringBuilder sb = new StringBuilder(fn);
                    sb.replace(indexOf, indexOf + "830".length(), "616");
                    // 处理文件 
                    tjZxManager.handle616File(sb.toString());
                }
            }
        }
        catch (Exception e)
        {
            log.error("中信银行e管家回调异常", e);
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ROOT><RSP_CODE>00000</RSP_CODE><RSP_MSG>成功</RSP_MSG></ROOT>";
    }
    
    @PostMapping(value = "/test")
    public void test(@RequestParam(value = "date", required = false)String date, 
    		@RequestParam(value = "ascription", required = false, defaultValue = "13")Integer ascription, 
    		@RequestParam(value = "flag", required = false)Boolean flag)
    {
        tjZxManager.runSettle(date, ascription, flag);
    }
    
    @PostMapping(value = "/test2")
    public void test2(@RequestParam(value = "fn", required = false)String fn)
    {
        tjZxManager.handle616File(fn);
    }
    
    @PostMapping(value = "/appointOrder")
    public void appointOrder(Integer pkey)
    {
        tjZxManager.appointOrder(pkey);
    }
    
}
