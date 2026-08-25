package cn.tofocus.lejia.zx.sendMethod;

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

import com.thoughtworks.xstream.XStream;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.zx.bean.T21000024Request;
import cn.tofocus.lejia.zx.bean.T21000024Response;
import cn.tofocus.lejia.zx.util.HttpsPost;
import cn.tofocus.lejia.zx.util.SignUtil;
import cn.tofocus.lejia.zx.util.Utils;

public class T21000024
{
    /** 根目录*/
    private static String ROOTPATH = "";
    static
    {
        ROOTPATH = System.getProperty("user.dir");
    }
    
    private static final String PTNRTESTCER = ROOTPATH + "/PTNR/PTNRtest.cer";
    
    /** https私钥库*/
    private static final String KEYSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";
    
    /** https信任库*/
    private static final String TRUSTSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";
    
    /** 报文头*/
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  
    public static void main(String[] args)
    {
        //私钥库 密码
        String password = "111111";
        
        String httpsUrl = "https://202.108.57.27:30466/dsgj/";
        
        try {

            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------

            XStream xtreamData = new XStream();

            //--------------- 新增商户信息测试数据 ---------------

            T21000024Request request = new T21000024Request();
            request.setTRANS_CODE("21000024");
            Random r = new Random();
            String reqSsn = "J00024000000000" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS") + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID("J00024000000000");//平台商户编号              
            request.setUSER_ID_TYPE("01"); // 01-个人身份证
            request.setUSER_ID("J00024000000002");
            request.setOP_TYPE("1");
            request.setACCT_NM("在市谋");
            request.setPAN_NUM("004001");
            request.setPAN("6217730700880774");
            request.setBANK_CARD_NO("371329198804011179");
            request.setBANK_PHONE("15096230421");
            
            //实体对象转换为XML
            xtreamData.alias("ROOT", T21000024Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            System.out.println("restr: " + restr);
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(httpsUrl, restr);
            resStr = resStr.replace("ROOT", T21000024Response.class.getName());

            //把xml为转换为实体对象
            T21000024Response resData =
                    (T21000024Response) Utils.getObjectFromXML(resStr, T21000024Response.class);

            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(sortSignInfo(resStr).getBytes(), sigStr, PTNRTESTCER);
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
            Element rootElement = document.getRootElement();
            for(Iterator iter = rootElement.elementIterator();iter.hasNext();){
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
        return signInfo.toString();
    }
}
