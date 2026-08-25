package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class TRequest
{
    private String SIGN_INFO;
    
    public String getSIGN_INFO()
    {
        return SIGN_INFO;
    }
    
    public void setSIGN_INFO(String sIGN_INFO)
    {
        SIGN_INFO = sIGN_INFO;
    }
    
    public String sortSignInfo(Element root)
    {
        List<Element> list = root.elements();
        List<java.lang.String> signList = new ArrayList<java.lang.String>();
        
        for (Element e : list)
        {
            if (e.getText() != null)
            {
                signList.add(e.getText());
            }
        }
        //排序
        Collections.sort(signList);
        
        StringBuffer signInfo = new StringBuffer();
        
        for (java.lang.String sign : signList)
        {
            signInfo = signInfo.append(sign);
        }
        return signInfo.toString();
    }
}
