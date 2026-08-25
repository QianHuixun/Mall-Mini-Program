package cn.tofocus.lejia.domain.app;

import org.dom4j.io.SAXReader;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.zx.beanV2.T21000001Request;
import cn.tofocus.lejia.zx.beanV2.T21000001Response;
import cn.tofocus.lejia.zx.util.Constants;
import cn.tofocus.lejia.zx.util.HttpsPost;
import cn.tofocus.lejia.zx.util.SignUtil;
import cn.tofocus.lejia.zx.util.Utils;
import cn.tofocus.lejia.zx.util.XstreamUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class ZxManager
{
    public static void main(String[] args) {
        /*
         * 请求url 测试及生产环境地址请以商户接入规范中为准，demo中地址仅作参考
        **/
        String httpsUrl = "https://tseb.laastg.test.citicbank.com:38443/api/public";

        try {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000001Request request = new T21000001Request();
            request.setTRANS_CODE("21000001");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID("J04059100000000");//平台商户编号
            request.setMCHNT_USER_ID("test1");
            request.setUSER_TYPE("1");
            request.setUSER_NM("联调测试");
            request.setUSER_ROLE("001001");
            request.setSIGN_TYPE("00");//签约类型
//            request.setUSER_ID_TYPE("01");
//            request.setUSER_ID_NO("000013477");
            request.setUSER_PHONE("15562632654");
            request.setCORP_NM("法人姓名");
            request.setCORP_ID_NO("123456");
            request.setUSER_ADD("用户地址");
            request.setREQ_RESERVED("123456");//发起方保留域      
            request.setCORP_ID_TYPE("");

            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());

            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();

            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());

            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD,Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(httpsUrl, restr, "J04059100000000", "21000001");
            //把xml为转换为实体对象
            T21000001Response resData = XstreamUtils.toBean(resStr, T21000001Response.class);

            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getReqSsn()
    {
        Random r = new Random();
        return "J04059100000000" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
            + String.valueOf(r.nextLong()).substring(1, 8 + 1);
    }
    
    //获取商户入驻接收报文中的验签字段
    public static String sortSignInfo(String xml){
        List<String> signList = new ArrayList<String>();
        try {
            org.dom4j.Document document = DocumentHelper.parseText(xml);
            Element dataElement = document.getRootElement().element("DATA");
            for(Iterator iter = dataElement.elementIterator();iter.hasNext();){
                Element element = (Element)iter.next();
                if(element.getName().equals("SIGN_INFO")){
                    element.detach();
                    continue;
                }
                String tagName = element.getText();
                signList.add(tagName);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //排序
        Collections.sort(signList);

        StringBuffer signInfo = new StringBuffer();

        for (String sign  : signList)
        {
            signInfo = signInfo.append(sign);
        }
        //若验证银行侧响应报文签名未通过，可与银行侧对比该串内容是否一致
        System.out.println("响应字段排序串："+signInfo);
        return signInfo.toString();
    }
}
