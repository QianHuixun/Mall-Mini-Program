//// BEGIN OF MODIFIABLE SEGMENT - package
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
import cn.tofocus.lejia.zx.bean.T21000008Request;
import cn.tofocus.lejia.zx.bean.T21000008Response;
import cn.tofocus.lejia.zx.util.HttpsPost;
import cn.tofocus.lejia.zx.util.SignUtil;
import cn.tofocus.lejia.zx.util.Utils;


public class T21000008
{
	/** 根目录*/
    private static String ROOTPATH = "";
    static {
        ROOTPATH = System.getProperty("user.dir");
    }

  
    private static final String PTNRTESTCER = ROOTPATH + "/PTNR/PTNRtest.cer";
    /** https私钥库*/
    private static final String KEYSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";

    /** https信任库*/
    private static final String TRUSTSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";

    /** 报文头*/
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    /** 上传文件路径**/
    private static final String FILE_PATH = ROOTPATH + "/file/";
    public static void main(String[] args) {

        //私钥库 密码
        String password = "111111";

        /*
         * 请求url 
         * 商户测试要修改为外网地址    202.108.57.43 30466
        **/
//        String httpsUrl = "https://22.5.65.150:30466/dsgj";
//        String httpsUrl = "https://202.108.57.27:30466/dsgj";
        String httpsUrl = "https://custtest.xinanshizu.com/dsgj/";

        try {

            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------

            XStream xtreamData = new XStream();

            //--------------- 新增商户信息测试数据 ---------------

            T21000008Request request = new T21000008Request();
            request.setTRANS_CODE("21000008");
            Random r = new Random();
            String reqSsn = "J00024000000000" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS") + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            System.out.println("reqSsn.length(): " + reqSsn.length());
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID("J00024000000000");
            request.setFILE_TYPE("001");
            request.setFILE_COUNT("1");
            String burl = "src/main/resources/file/z/";
            request.setFILE_NAME("J0002400000000010010012021121401.ZIP");
            request.setTRANS_TYPE("MSG");
//            System.out.println("FILE_PATH: " + FILE_PATH);
            String fileContent = Utils.encodeBase64File(burl + "J0002400000000010010012021121401.ZIP").replace("\r\n", "");
//            request.setREQ_RESERVED("123456");
            
//            "UEsDBBQAAAAIAPd8eFPQyGLuXAAAAJACAAAgAAAASjAwMDI0MDAwMDAwMDAwMTAwMTAwMTIwMjExMTIzMDHzMjAwMDIxgAEvFK6RkYGRoaGhkbEhkAlkGBgYnli9Y9vZFQokA0tDIzMDSyNDU2NLU1NTI7rrR/WYgYZBhYGxJgmGUmq/IQgaGiADfDziAVAf0FuGhiS7aHACAFBLAQIUABQAAAAIAPd8eFPQyGLuXAAAAJACAAAgAAAAAAAAAAAAAAC2gQAAAABKMDAwMjQwMDAwMDAwMDAxMDAxMDAxMjAyMTExMjMwMVBLBQYAAAAAAQABAE4AAACaAAAAAAA=\r\n" + 
            
            //实体对象转换为XML
            xtreamData.alias("ROOT", T21000008Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setFILE_CONTENT(fileContent);
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            System.out.println(restr);
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(httpsUrl, restr);
//            String resStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ROOT><RSP_CODE>00000</RSP_CODE><REQ_SSN>BECP20180327163652919JM296TX4D4ZIY69OQ8K</REQ_SSN><MEM_ID>H00000700000001</MEM_ID><SIGN_INFO>MIAGCSqGSIb3DQEHAqCAMIACAQExCzAJBgUrDgMCGgUAMIAGCSqGSIb3DQEHAQAAMYIBtjCCAbICAQEwMDArMQswCQYDVQQGEwJDTjENMAsGA1UECwwEUFROUjENMAsGA1UEAwwEdGVzdAIBMDAJBgUrDgMCGgUAoF0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTgwNjIxMDEyODQ5WjAjBgkqhkiG9w0BCQQxFgQUaI+Qbk1PCekOHUKR8IaSgdVFbGUwDQYJKoZIhvcNAQEBBQAEggEAFI2F44ssSfuC6sjS1II8Nb+psteIaoOVpo98m4XyfX9FM6ZxI/i8y7gqH7UZI3Lsh+HgyRkIzm4GxcvrHqH04RdqRL36A64+vM2PHnbqbhsrFOxsv98h8qNdr/qYw470mENC3McK4xUyznSxFsLD/yHHuhnGq9p0LZdaOEjBnpq6PNZLv87xn93Dt1te13k+uGUWjQNfOKElc+nh+i0WXGYMcaVPWlEXHbjWOIXWM293BakuK4RJoroTmNxrgix6+SSiArQPChw5GYCKcY0GMDzOYDUy7i3QgZjnX8AoH9r1GA/PfdJSFsf+KzQe8WEV/jLgidhpg2ZDnhv77ETLUAAAAAAAAA==</SIGN_INFO></ROOT>";
            resStr = resStr.replace("ROOT", T21000008Response.class.getName());

            //把xml为转换为实体对象
            T21000008Response resData =
                    (T21000008Response) Utils.getObjectFromXML(resStr, T21000008Response.class);

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
				if(element.getName().equals("SIGN_INFO") || element.getName().equals("RESULT_CODE") || element.getName().equals("RESULT_MSG")){
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
