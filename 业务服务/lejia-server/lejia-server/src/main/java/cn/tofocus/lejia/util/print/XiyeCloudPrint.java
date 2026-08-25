package cn.tofocus.lejia.util.print;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.util.print.bean.CustomerPrintBean;
import cn.tofocus.lejia.util.print.bean.PrintOriInfo;
import cn.tofocus.lejia.util.print.bean.XiyeDeliveryMerhantGoodsBean;
import cn.tofocus.lejia.util.print.bean.XiyePrintCustomerBean;
import cn.tofocus.lejia.util.print.bean.XiyePrintDeliveryBean;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class XiyeCloudPrint
{
    /**
     * <开发文档地址:  https://www.xpyun.net/open/index.html>
     * <打印机官方后台地址:  https://admin.xpyun.net/>
     * <账户及密码:  103335249@qq.com  zoujincheng2002@>
     * <添加新的打印机,需要在官方后台先添加对应的SN,后续在代码里调用>
     * <后续修改成多个打印机时,可接官方添加打印机的接口>
     */
    private static final String USER = "103335249@qq.com";
    
    private static final String USERKEY = "7739f3b27dbd4e2a9c4f6e4a79911aa2";
    
    private static final String PRINTURL = "https://open.xpyun.net/api/openapi/xprinter/print";
    
    //    private static final String SN = "74HM92LFHT0CF4A";
    
    //    @Value("${zyysc.xpyun.sn:74HM92LFHT0CF4A}")
    //    private String dySn;
    
    // 客户联
    public Boolean xiyeCustomerPrint(CustomerPrintBean info, String dySn)
    {
        return printData(null, info, true, dySn);
    }
    
    private Boolean printData(XiyePrintDeliveryBean deliveryBean, CustomerPrintBean info, Boolean b, String dySn)
    {
        String content;
        XiyePrintCustomerBean bean = new XiyePrintCustomerBean();
        bean.setSn(dySn);
        long time10 = System.currentTimeMillis() / 1000;
        bean.setTimestamp(String.valueOf(time10));
        String sign = sha1(USER + USERKEY + String.valueOf(time10));
        bean.setSign(sign);
        bean.setUser(USER);
        if (b == null)
        {
            content = marketXiyeCustBean(info) + deliveryPrint(deliveryBean);
        }
        else if (Boolean.TRUE.equals(b))
            content = makeMsg(null, info, true);
        else
            content = makeMsg(deliveryBean, null, false);
        
        bean.setContent(content);
        String resp = HttpUtil.postBodyForString(PRINTURL, bean);
        if (resp != null)
        {
            JSONObject object = JSON.parseObject(resp);
            Integer code = (Integer)object.get("code");
            if (code.equals(0))
            {
                String data = (String)object.get("data");
                log.info("print order:{}", data);
                
                return true;
            }
            else
            {
                String msg = (String)object.get("msg");
                log.info("print error msg:{}", msg);
                return false;
            }
        }
        return false;
    }
    
    private Boolean printDataTj(XiyePrintDeliveryBean deliveryBean, CustomerPrintBean info, Boolean b, String dySn)
    {
        String content;
        XiyePrintCustomerBean bean = new XiyePrintCustomerBean();
        bean.setSn(dySn);
        long time10 = System.currentTimeMillis() / 1000;
        bean.setTimestamp(String.valueOf(time10));
        String sign = sha1(USER + USERKEY + String.valueOf(time10));
        bean.setSign(sign);
        bean.setUser(USER);
        if (b == null)
        {
            content = marketXiyeCustBeanTj(info) + deliveryPrint(deliveryBean);
        }
        else if (Boolean.TRUE.equals(b))
            content = makeMsg(null, info, true);
        else
            content = makeMsg(deliveryBean, null, false);
        
        bean.setContent(content);
        String resp = HttpUtil.postBodyForString(PRINTURL, bean);
        if (resp != null)
        {
            JSONObject object = JSON.parseObject(resp);
            Integer code = (Integer)object.get("code");
            if (code.equals(0))
            {
                String data = (String)object.get("data");
                log.info("print order:{}", data);
                
                return true;
            }
            else
            {
                String msg = (String)object.get("msg");
                log.info("print error msg:{}", msg);
                return false;
            }
        }
        return false;
    }
    
    private String makeMsg(XiyePrintDeliveryBean bean, CustomerPrintBean info, Boolean b)
    {
        if (Boolean.TRUE.equals(b))
            return marketXiyeCustBean(info);
        else
            return deliveryPrint(bean);
    }
    
    // 拣货联
    public Boolean xiyeDeliveryPrint(XiyePrintDeliveryBean info, String dySn)
    {
        return printData(info, null, false, dySn);
    }
    
    public Boolean xiyePrint(XiyePrintDeliveryBean bean, CustomerPrintBean info, String dySn, Boolean dineIn)
    {
        log.info("--XiyePrintDeliveryBean--{}", bean);
        log.info("--CustomerPrintBean--{}", info);
        if(Boolean.TRUE.equals(dineIn))
            return printDataDineIn(bean, info, null, dySn);
        return printDataTj(bean, info, null, dySn);
    }
    
    private String marketXiyeCustBean(CustomerPrintBean info)
    {
        info.setMarketMobile(StringUtil.isEmpty(info.getMarketMobile()) ? "" : info.getMarketMobile());
        StringBuilder buffer = new StringBuilder();
        //根据状态判断是否为退款单
        if (info.getStatus() == OrderStatus.REFUNDED_ORDER || info.getRefundAmt().signum() > 0)
        {
            if (info.getStatus() == OrderStatus.REFUNDED_ORDER)
                buffer.append("<CB>已全部退款<BR></CB>");
            else
                buffer.append("<CB>已部分退款<BR></CB>");
            buffer.append("<C>请仔细核对退款商品</C>");
            buffer.append("<BR>");
            buffer.append("--------------------------------");
            buffer.append("<BR>");
            buffer.append("<BR>");
        }
        buffer.append("<L>");
        buffer.append("<N>顾客联");
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("#");
        buffer.append("<B2>" + info.getOrderTrace());
        buffer.append("</L>");
        buffer.append("  <B>" + info.getPickUp());
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<N><L><BOLD>订单编号：" + info.getOrderNumber() + "</BOLD></L>");
        buffer.append("<BR>");
        buffer.append("<L><N>下单时间：" + info.getOrderTime() + "</L>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("<N>市场电话：" + info.getMarketMobile());
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("--------------------------------");
        buffer.append("<BR>");
        buffer.append("<CB>" + info.getDeliveryMode() + "<BR><BR></CB>");
        buffer.append("<C>" + info.getReceivedTime() + "</C>");
        buffer.append("<BR>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>【备注】 " + info.getRemarket() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------商品--------------");
        buffer.append("<BR>");
        List<PrintOriInfo> ori = info.getOri();
        
        for (int i = 0; i < ori.size(); i++)
        {
            PrintOriInfo bean = ori.get(i);
            buffer.append("<N>" + (i + 1) + "." + bean.getGoodsName() + "(" + bean.getSpecifications() + ")");
            buffer.append("<BR>");
            buffer.append(
                "  x" + bean.getCount() + "<HT> " + "<HT>" + bean.getGoodsPrice() + "<HT>" + bean.getGoodsAmt());
            if (bean.getAmt() != null && bean.getRefundAmt().signum() > 0)
            {
                buffer.append("<BR>");
                if (bean.getAmt().signum() == 0)
                    buffer.append("  已全部退款" + "<HT> " + "<HT>" + bean.getRefundAmt().negate());
                else
                    buffer.append("  已部分退款" + "<HT> " + "<HT>" + bean.getRefundAmt().negate());
            }
            buffer.append("<BR>");
        }
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>商品金额</L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getGoodsAmt());
        buffer.append("<BR>");
        buffer.append("<N><L>配送费  </L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getDeliveryFee());
        buffer.append("<BR>");
        buffer.append("<N><L>优惠金额</L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getDiscountAmt());
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<L><N>总件数：" + info.getTotalCount() + "</L>");
        buffer.append("           ");
        buffer.append("<N>合计：" + info.getTotalAmt());
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        if (StringUtil.isNotEmpty(info.getAddress()))
        {
            buffer.append("<N><BOLD>" + info.getAddress() + "</BOLD>");
            buffer.append("<BR>");
        }
        buffer.append("<B>" + info.getMobile());
        buffer.append("<BR>");
        buffer.append("<N><BOLD>" + info.getName() + "</BOLD>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        
//        buffer.append("<BR>");
//        buffer.append("<L><QRCODE s=10 e=L l=center>" + info.getQrcode() + "</QRCODE>");
//        buffer.append("<BR>");
        
        buffer.append("<C><N>" + info.getMarketName() + "</C>");
        buffer.append("<BR>");
        buffer.append("<C><N>" + "线上买菜，线下配送" + "</C>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<N>----------沿此虚线剪开----------");
        buffer.append("<BR>");
        return buffer.toString();
    }
    
    private String marketXiyeCustBeanTj(CustomerPrintBean info)
    {
        info.setMarketMobile(StringUtil.isEmpty(info.getMarketMobile()) ? "" : info.getMarketMobile());
        StringBuilder buffer = new StringBuilder();
        //根据状态判断是否为退款单
        if (info.getStatus() == OrderStatus.REFUNDED_ORDER || info.getRefundAmt().signum() > 0)
        {
            if (info.getStatus() == OrderStatus.REFUNDED_ORDER)
                buffer.append("<CB>已全部退款<BR></CB>");
            else
                buffer.append("<CB>已部分退款<BR></CB>");
            buffer.append("<C>请仔细核对退款商品</C>");
            buffer.append("<BR>");
            buffer.append("--------------------------------");
            buffer.append("<BR>");
            buffer.append("<BR>");
        }
        buffer.append("<L>");
        buffer.append("<N>顾客联");
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("#");
        buffer.append("<B2>" + info.getOrderTrace());
        buffer.append("</L>");
        buffer.append("  <B>" + info.getPickUp());
        
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<C><N>" + info.getMarketName() + "</N></C>");
        buffer.append("<BR>");
//        buffer.append("<BR>");
        

        
//        buffer.append("<BR>");
//        buffer.append("--------------------------------");
        
        buffer.append("<CB>" + info.getDeliveryMode() + "<BR><BR></CB>");
        buffer.append("<C>" + info.getReceivedTime() + "</C>");
        buffer.append("<BR>--------------------------------");
        buffer.append("<BR>");
        
        if (StringUtil.isNotEmpty(info.getAddress()))
        {
            buffer.append("<N><BOLD>" + info.getAddress() + "</BOLD>");
            buffer.append("<BR>");
        }
        buffer.append("<B>" + info.getMobile());
        buffer.append("<BR>");
        buffer.append("<N><BOLD>" + info.getName() + "</BOLD>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        
        buffer.append("<N><L>【备注】 " + info.getRemarket() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------商品--------------");
        buffer.append("<BR>");
        List<PrintOriInfo> ori = info.getOri();
        
        for (int i = 0; i < ori.size(); i++)
        {
            PrintOriInfo bean = ori.get(i);
            buffer.append("<N>" + (i + 1) + "." + bean.getGoodsName() + "(" + bean.getSpecifications() + ")");
            buffer.append("<BR>");
            buffer.append(
                "  x" + bean.getCount() + "<HT> " + "<HT>" + bean.getGoodsPrice() + "<HT>" + bean.getGoodsAmt());
            if (bean.getAmt() != null && bean.getRefundAmt().signum() > 0)
            {
                buffer.append("<BR>");
                if (bean.getAmt().signum() == 0)
                    buffer.append("  已全部退款" + "<HT> " + "<HT>" + bean.getRefundAmt().negate());
                else
                    buffer.append("  已部分退款" + "<HT> " + "<HT>" + bean.getRefundAmt().negate());
            }
            buffer.append("<BR>");
        }
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>商品金额</L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getGoodsAmt());
        buffer.append("<BR>");
        buffer.append("<N><L>配送费  </L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getDeliveryFee());
        buffer.append("<BR>");
        buffer.append("<N><L>优惠金额</L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getDiscountAmt());
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<L><N>总件数：" + info.getTotalCount() + "</L>");
        buffer.append("           ");
        buffer.append("<N>合计：" + info.getTotalAmt());
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        
        buffer.append("<N><L><BOLD>订单编号：" + info.getOrderNumber() + "</BOLD></L>");
        buffer.append("<BR>");
        buffer.append("<L><N>下单时间：" + info.getOrderTime() + "</L>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        

        buffer.append("<N><C>" + info.getContent() + "</C>");
        buffer.append("<BR>");
        
        if(StringUtils.isNotBlank(info.getPhoto1()))
        {
              buffer.append("<QRCODE s=9 e=L l=center>");
              buffer.append(info.getPhoto1());
              buffer.append("</QRCODE>");
        }
        if(StringUtils.isNotBlank(info.getPhoto1Text()))
        {
              buffer.append("<N><C>");
              buffer.append(info.getPhoto1Text());
              buffer.append("</C>");
              buffer.append("<BR><BR>");
        }
        if(StringUtils.isNotBlank(info.getPhoto2()))
        {
            buffer.append("<QRCODE s=9 e=L l=center>");
            buffer.append(info.getPhoto2());
            buffer.append("</QRCODE>");
        }
        if(StringUtils.isNotBlank(info.getPhoto2Text()))
        {
            buffer.append("<N><C>");
            buffer.append(info.getPhoto2Text());
            buffer.append("</C>");
        }
        
//        if(StringUtils.isNotBlank(info.getPhoto1()) && StringUtils.isNotBlank(info.getPhoto2()))
//        {
//            buffer.append("<TWO_QR><LQR>");
//            buffer.append(info.getPhoto1());
//            buffer.append("</LQR><RQR>");
//            buffer.append(info.getPhoto2());
//            buffer.append("</RQR></TWO_QR>");
//        }
//        else if(StringUtils.isBlank(info.getPhoto1()) && StringUtils.isBlank(info.getPhoto2()))
//        {
//            
//        }
//        else
//        {
//            buffer.append("<QRCODE s=6 e=L l=center>");
//            buffer.append(info.getPhoto1());
//            buffer.append(info.getPhoto2());
//            buffer.append("</QRCODE>");
////            buffer.append("<QRCODE s=6 e=L l=center>" + info.getPhoto1() + info.getPhoto2() + "</QRCODE>");
//        }
        
//        if(StringUtils.isNotBlank(info.getPhoto1Text()) && StringUtils.isNotBlank(info.getPhoto2Text()))
//        {
//            buffer.append("<N><L>");
//            buffer.append(addSpace(info.getPhoto1Text()));
//            buffer.append("</L>");
//            buffer.append("<N><L>");
//            buffer.append(addSpace(info.getPhoto2Text()));
//            buffer.append("</L>");
//        }
//        else if(StringUtils.isNotBlank(info.getPhoto1Text()))
//        {
//            buffer.append("<N><C>");
//            buffer.append(info.getPhoto1Text());
////            buffer.append(addSpace(info.getPhoto1Text()));
//            buffer.append("</C>");
//        }
//        else if(StringUtils.isNotBlank(info.getPhoto2Text()))
//        {
//            buffer.append("<N><C>");
//            buffer.append(info.getPhoto2Text());
////            buffer.append(addSpace(info.getPhoto2Text()));
//            buffer.append("</C>");
//        }
       
        buffer.append("<BR><BR>");
//        String c1 = "一";
//        String c2 = "一二";
//        String c3 = "一二三";
//        String c4 = "一二三四";
//        String c5 = "一二三四五";
//        String c6 = "一二三四五六";
//        String c7 = "一二三四五六七";
//        String c8 = "abcde";
//        buffer.append("<N><L>" + addSpace(c1) + "</L>");
//        buffer.append("<N><L>" + addSpace(c2) + "</L>");
//        
//        buffer.append("<BR><BR>");
//        buffer.append("<N><L>" + addSpace(c3) + "</L>");
//        buffer.append("<N><L>" + addSpace(c4) + "</L>");
//        
//        buffer.append("<BR><BR>");
//        buffer.append("<N><L>" + addSpace(c5) + "</L>");
//        buffer.append("<N><L>" + addSpace(c6) + "</L>");
//        
//        buffer.append("<BR><BR>");
//        buffer.append("<N><L>" + addSpace(c7) + "</L>");
//        buffer.append("<N><L>" + addSpace(c8) + "</L>");
        
        buffer.append("<BR>");
        buffer.append("<N>----------沿此虚线剪开----------");
        buffer.append("<BR>");
        return buffer.toString();
    }
    
    private String addSpace(String s)
    {
        String res = s;
        int i = s.length();
        int countHanzi = countHanzi(s);
        i = i - countHanzi;
        i = i + 2 * countHanzi;
        switch (i)
        {
            case 1:
                res = String.format("     %s          ", s);
                break;
            case 2:
                res = String.format("     %s         ", s);
                break;
            case 3:
                res = String.format("    %s         ", s);
                break;
            case 4:
                res = String.format("    %s        ", s);
                break;
            case 5:
                res = String.format("   %s        ", s);
                break;
            case 6:
                res = String.format("   %s       ", s);
                break;
            case 7:
                res = String.format("  %s       ", s);
                break;
            case 8:
                res = String.format("  %s      ", s);
                break;
            case 9:
                res = String.format("  %s     ", s);
                break;
            case 10:
                res = String.format(" %s     ", s);
                break;
            case 11:
                res = String.format(" %s    ", s);
                break;
            case 12:
                res = String.format("%s    ", s);
                break;
            case 13:
                res = String.format("%s   ", s);
                break;
            case 14:
                res = String.format("%s  ", s);
                break;
            case 15:
                res = String.format("%s ", s);
                break;
        }
        return res;
    }
    
    public int countHanzi(String text) {
        if (text == null) return 0;
//        // 使用正则表达式匹配所有汉字
//        int count = (int) text.chars()
//                               .filter(ch -> Character.isSupplementaryCodePoint(ch)) // 确保是扩展的Unicode码点（例如某些emoji）
//                               .filter(ch -> (ch >= 0x4e00 && ch <= 0x9fff) || (ch >= 0x3400 && ch <= 0x4DBF) || (ch >= 0x20000 && ch <= 0x2A6DF) || (ch >= 0x2A700 && ch <= 0x2B73F) || (ch >= 0x2B740 && ch <= 0x2B81F) || (ch >= 0x2B820 && ch <= 0x2CEAF) || (ch >= 0xF900 && ch <= 0xFAFF) || (ch >= 0x2F800 && ch <= 0x2FA1F)) // 包括其他扩展的汉字范围
//                               .count();
//        return count;
        return (int) text.chars()
            .filter(ch -> (ch >= 0x4e00 && ch <= 0x9fff)) // 基本汉字区间
            .count();
    }
    
    private String deliveryPrint(XiyePrintDeliveryBean info)
    
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("<N>拣货联");
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("#");
        buffer.append("<B2>" + info.getOrderTrace());
        buffer.append("</L>");
        buffer.append("  <B>" + info.getPickUp());
        buffer.append("<BR>");
        buffer.append("<BR>");
        
        buffer.append("<N><L>" + info.getName() + "   " + info.getMobile());
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<CB>" + info.getDeliveryMode() + "<BR><BR></CB>");
        buffer.append("<C>" + info.getReceivedTime() + "</C>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>【备注】 " + info.getRemarket() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------商品--------------");
        buffer.append("<BR>");
        List<XiyeDeliveryMerhantGoodsBean> merchantGoods = info.getMerchantGoods();
        
        for (XiyeDeliveryMerhantGoodsBean merGoods : merchantGoods)
        {
            buffer.append("<L><BOLD>" + merGoods.getMerchantName() + "商户 (" + merGoods.getArea() + "-"
                + merGoods.getBooth() + ")</BOLD></L>");
            buffer.append("<BR>");
            
            buffer.append("<BR>");
            List<PrintOriInfo> ori = merGoods.getOri();
            
            for (int i = 0; i < ori.size(); i++)
            {
                PrintOriInfo bean = ori.get(i);
                
                buffer.append("<N>" + (i + 1) + "." + bean.getGoodsName() + "(" + bean.getSpecifications() + ")");
                
                buffer.append("<BR>");
                buffer.append("<L>");
                
                buffer.append(
                    "  x" + bean.getCount() + "<HT> " + "<HT>" + bean.getPrice() + "<HT>" + bean.getTotalPrice());
                if (bean.getAmt() != null && bean.getRefundAmt().signum() > 0)
                {
                    buffer.append("<BR>");
                    if (bean.getAmt().signum() == 0)
                        buffer.append("  已全部退款" + "<HT> " + "<HT>" + bean.getProcureRefundAmt().negate());
                    else
                        buffer.append("  已部分退款" + "<HT> " + "<HT>" + bean.getProcureRefundAmt().negate());
                }
                buffer.append("</L>");
                buffer.append("<BR>");
                
            }
            buffer.append("<R>共<B>" + merGoods.getGoodsCount() + "</B>件<BR></R>");
            buffer.append("<BR>");
        }
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>总件数：<B> " + info.getTotalCount() + "</B></L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<L>订单编号： " + info.getOrderNumber() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N><L>下单时间： " + info.getOrderTime() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        return buffer.toString();
    }
    
    private String sha1(String text)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] messageDigest = md.digest(text.getBytes());
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest)
            {
                hexString.append(String.format("%02x", b & 0xff));
            }
            
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    private Boolean printDataDineIn(XiyePrintDeliveryBean deliveryBean, CustomerPrintBean info, Boolean b, String dySn)
    {
        String content;
        XiyePrintCustomerBean bean = new XiyePrintCustomerBean();
        bean.setSn(dySn);
        long time10 = System.currentTimeMillis() / 1000;
        bean.setTimestamp(String.valueOf(time10));
        String sign = sha1(USER + USERKEY + String.valueOf(time10));
        bean.setSign(sign);
        bean.setUser(USER);
        if (b == null)
        {
            content = marketXiyeCustBeanDineIn(info) + deliveryPrintDineIn(deliveryBean);
        }
        else if (Boolean.TRUE.equals(b))
            content = makeMsg(null, info, true);
        else
            content = makeMsg(deliveryBean, null, false);
        
        bean.setContent(content);
        String resp = HttpUtil.postBodyForString(PRINTURL, bean);
        if (resp != null)
        {
            JSONObject object = JSON.parseObject(resp);
            Integer code = (Integer)object.get("code");
            if (code.equals(0))
            {
                String data = (String)object.get("data");
                log.info("print order:{}", data);
                
                return true;
            }
            else
            {
                String msg = (String)object.get("msg");
                log.info("print error msg:{}", msg);
                return false;
            }
        }
        return false;
    }
    
    private String marketXiyeCustBeanDineIn(CustomerPrintBean info)
    {
        info.setMarketMobile(StringUtil.isEmpty(info.getMarketMobile()) ? "" : info.getMarketMobile());
        StringBuilder buffer = new StringBuilder();
        //根据状态判断是否为退款单
        if (info.getStatus() == OrderStatus.REFUNDED_ORDER || info.getRefundAmt().signum() > 0)
        {
            if (info.getStatus() == OrderStatus.REFUNDED_ORDER)
                buffer.append("<CB>已全部退款<BR></CB>");
            else
                buffer.append("<CB>已部分退款<BR></CB>");
            buffer.append("<C>请仔细核对退款商品</C>");
            buffer.append("<BR>");
            buffer.append("--------------------------------");
            buffer.append("<BR>");
            buffer.append("<BR>");
        }
        buffer.append("<L>");
        buffer.append("<N>顾客联");
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("#");
        buffer.append("<B2>" + info.getOrderTrace());
        buffer.append("</L>");
        buffer.append("  <B>" + info.getPickUp());
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<N><L><BOLD>订单编号：" + info.getOrderNumber() + "</BOLD></L>");
        buffer.append("<BR>");
        buffer.append("<L><N>下单时间：" + info.getOrderTime() + "</L>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("<N>市场电话：" + info.getMarketMobile());
        buffer.append("</L>");
        
        buffer.append("<BR>");
        buffer.append("--------------------------------");
        buffer.append("<BR>");
        
        buffer.append("<N><L>【备注】 " + info.getRemarket() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------商品--------------");
        buffer.append("<BR>");
        List<PrintOriInfo> ori = info.getOri();
        
        for (int i = 0; i < ori.size(); i++)
        {
            PrintOriInfo bean = ori.get(i);
            buffer.append("<N>" + (i + 1) + "." + bean.getGoodsName() + "(" + bean.getSpecifications() + ")");
            buffer.append("<BR>");
            buffer.append(
                "  x" + bean.getCount() + "<HT> " + "<HT>" + bean.getGoodsPrice() + "<HT>" + bean.getGoodsAmt());
            if (bean.getAmt() != null && bean.getRefundAmt().signum() > 0)
            {
                buffer.append("<BR>");
                if (bean.getAmt().signum() == 0)
                    buffer.append("  已全部退款" + "<HT> " + "<HT>" + bean.getRefundAmt().negate());
                else
                    buffer.append("  已部分退款" + "<HT> " + "<HT>" + bean.getRefundAmt().negate());
            }
            buffer.append("<BR>");
        }
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>商品金额</L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getGoodsAmt());
        buffer.append("<BR>");
        buffer.append("<N><L>配送费  </L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getDeliveryFee());
        buffer.append("<BR>");
        buffer.append("<N><L>优惠金额</L>");
        buffer.append(" <HT>" + " " + "<HT>" + info.getDiscountAmt());
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<L><N>总件数：" + info.getTotalCount() + "</L>");
        buffer.append("           ");
        buffer.append("<N>合计：" + info.getTotalAmt());
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        if (StringUtil.isNotEmpty(info.getAddress()))
        {
            
            buffer.append("<B><BOLD>" + info.getAddress() + "</BOLD>");
            buffer.append("<BR>");
        }
        buffer.append("<N>--------------------------------");
        
        buffer.append("<C><N>" + info.getMarketName() + "</C>");
        buffer.append("<BR>");
        buffer.append("<C><N>" + "线上买菜，线下配送" + "</C>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<N>----------沿此虚线剪开----------");
        buffer.append("<BR>");
        return buffer.toString();
    }

    private String deliveryPrintDineIn(XiyePrintDeliveryBean info)
    
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("<N>拣货联");
        buffer.append("</L>");
        buffer.append("<BR>");
        buffer.append("<BR>");
        buffer.append("<L>");
        buffer.append("#");
        buffer.append("<B2>" + info.getOrderTrace());
        buffer.append("</L>");
        buffer.append("  <B>" + info.getPickUp());
        buffer.append("<BR>");
        buffer.append("<BR>");
        
        buffer.append("<N><L>" + info.getName() + "   " + info.getMobile() + "   " + info.getAddress());
        buffer.append("</L>");
        buffer.append("<BR>");
        
//        buffer.append("<N>--------------------------------");
//        buffer.append("<BR>");
//        buffer.append("<CB>" + info.getDeliveryMode() + "<BR><BR></CB>");
//        buffer.append("<C>" + info.getReceivedTime() + "</C>");
//        buffer.append("<BR>");
        
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>【备注】 " + info.getRemarket() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------商品--------------");
        buffer.append("<BR>");
        List<XiyeDeliveryMerhantGoodsBean> merchantGoods = info.getMerchantGoods();
        
        for (XiyeDeliveryMerhantGoodsBean merGoods : merchantGoods)
        {
            buffer.append("<L><BOLD>" + merGoods.getMerchantName() + "商户 (" + merGoods.getArea() + "-"
                + merGoods.getBooth() + ")</BOLD></L>");
            buffer.append("<BR>");
            
            buffer.append("<BR>");
            List<PrintOriInfo> ori = merGoods.getOri();
            
            for (int i = 0; i < ori.size(); i++)
            {
                PrintOriInfo bean = ori.get(i);
                
                buffer.append("<N>" + (i + 1) + "." + bean.getGoodsName() + "(" + bean.getSpecifications() + ")");
                
                buffer.append("<BR>");
                buffer.append("<L>");
                
                buffer.append(
                    "  x" + bean.getCount() + "<HT> " + "<HT>" + bean.getPrice() + "<HT>" + bean.getTotalPrice());
                if (bean.getAmt() != null && bean.getRefundAmt().signum() > 0)
                {
                    buffer.append("<BR>");
                    if (bean.getAmt().signum() == 0)
                        buffer.append("  已全部退款" + "<HT> " + "<HT>" + bean.getProcureRefundAmt().negate());
                    else
                        buffer.append("  已部分退款" + "<HT> " + "<HT>" + bean.getProcureRefundAmt().negate());
                }
                buffer.append("</L>");
                buffer.append("<BR>");
                
            }
            buffer.append("<R>共<B>" + merGoods.getGoodsCount() + "</B>件<BR></R>");
            buffer.append("<BR>");
        }
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        buffer.append("<N><L>总件数：<B> " + info.getTotalCount() + "</B></L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<L>订单编号： " + info.getOrderNumber() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N><L>下单时间： " + info.getOrderTime() + "</L>");
        buffer.append("<BR>");
        buffer.append("<N>--------------------------------");
        buffer.append("<BR>");
        return buffer.toString();
    }
}
