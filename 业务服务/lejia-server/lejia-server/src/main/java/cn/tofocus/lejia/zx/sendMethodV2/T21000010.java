package cn.tofocus.lejia.zx.sendMethodV2;


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

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.zx.beanV2.T21000010Request;
import cn.tofocus.lejia.zx.beanV2.T21000010Response;
import cn.tofocus.lejia.zx.utilV2.Constants;
import cn.tofocus.lejia.zx.utilV2.HttpsPost;
import cn.tofocus.lejia.zx.utilV2.SignUtil;
import cn.tofocus.lejia.zx.utilV2.Utils;
import cn.tofocus.lejia.zx.utilV2.XstreamUtils;


public class T21000010
{
    public static void main(String[] args) {
        /*
         * 请求url 测试及生产环境地址请以商户接入规范中为准，demo中地址仅作参考
         **/
//        String httpsUrl = "https://tseb.laastg.test.citicbank.com:38443/api/public";

        try {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000010Request request = new T21000010Request();
            request.setTRANS_CODE("21000010");
            Random r = new Random();
            String reqSsn = Constants.MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
            + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setORI_USER_SSN("EMSSBPG2506034701290912024786977");
            request.setUSER_TRANS_DT("20250603");

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
            String resStr = HttpsPost.post(Constants.httpsUrl, restr, Constants.MCHNT_ID, "21000010");
            //把xml为转换为实体对象
            T21000010Response resData = XstreamUtils.toBean(resStr, T21000010Response.class);

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

    //获取商户入驻接收报文中的验签字段
    public static String sortSignInfo(String xml){
        List<String> signList = new ArrayList<String>();
        System.out.println(xml);
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
