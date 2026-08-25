package cn.tofocus.lejia.zx.sendMethodV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public abstract class BaseSendMethod
{
    //获取商户入驻接收报文中的验签字段
    public static String sortSignInfo(String xml)
    {
        List<String> signList = new ArrayList<String>();
        try
        {
            org.dom4j.Document document = DocumentHelper.parseText(xml);
            Element dataElement = document.getRootElement().element("DATA");
            for (Iterator iter = dataElement.elementIterator(); iter.hasNext();)
            {
                Element element = (Element)iter.next();
                if (element.getName().equals("SIGN_INFO"))
                {
                    element.detach();
                    continue;
                }
                String tagName = element.getText();
                signList.add(tagName);
            }
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        //排序
        Collections.sort(signList);
        
        StringBuffer signInfo = new StringBuffer();
        
        for (String sign : signList)
        {
            signInfo = signInfo.append(sign);
        }
        //若验证银行侧响应报文签名未通过，可与银行侧对比该串内容是否一致
        System.out.println("响应字段排序串：" + signInfo);
        return signInfo.toString();
    }
    
    //获取商户入驻接收报文中的验签字段
    public static String sortSignInfoNoR(String xml)
    {
        List<String> signList = new ArrayList<String>();
        try
        {
            org.dom4j.Document document = DocumentHelper.parseText(xml);
            Element dataElement = document.getRootElement().element("DATA");
            for (Iterator iter = dataElement.elementIterator(); iter.hasNext();)
            {
                Element element = (Element)iter.next();
                if (element.getName().equals("SIGN_INFO") || element.getName().equals("RESULT_CODE") || element.getName().equals("RESULT_MSG"))
                {
                    element.detach();
                    continue;
                }
                String tagName = element.getText();
                signList.add(tagName);
            }
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        //排序
        Collections.sort(signList);
        
        StringBuffer signInfo = new StringBuffer();
        
        for (String sign : signList)
        {
            signInfo = signInfo.append(sign);
        }
        //若验证银行侧响应报文签名未通过，可与银行侧对比该串内容是否一致
        System.out.println("响应字段排序串：" + signInfo);
        return signInfo.toString();
    }
}
