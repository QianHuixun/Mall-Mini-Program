//// BEGIN OF MODIFIABLE SEGMENT - package
package cn.tofocus.lejia.zx.sendMethod;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;

import cn.tofocus.lejia.zx.bean.T21000006Request;
import cn.tofocus.lejia.zx.bean.T21000006Response;
import cn.tofocus.lejia.zx.util.HttpsPost;
import cn.tofocus.lejia.zx.util.SignUtil;
import cn.tofocus.lejia.zx.util.Utils;


public class T21000006
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

    public static void main(String[] args) {

        //私钥库 密码
        String password = "111111";

        /*
         * 请求url 
         * 商户测试要修改为外网地址    202.108.57.43 30466
        **/
        String httpsUrl = "https://22.5.227.200:30466/dsgj/";

        try {

            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------

            XStream xtreamData = new XStream();

            //--------------- 新增商户信息测试数据 ---------------

            T21000006Request request = new T21000006Request();
            request.setTRANS_CODE("21000006");
            request.setREQ_SSN("J100364000000002019121715131000012300157");
            request.setMCHNT_ID("J10036400000000");
            request.setUSER_ID("J10036400000001");
            request.setTRANS_TYPE("02");
            request.setVERI_CD("356829");
            request.setPWDID("1000000007917461");
            request.setTRANS_ID("EMSSJUJ20190420180152007GAf2x9TM");
            request.setREQ_RESERVED("2");
            
            //实体对象转换为XML
            xtreamData.alias("ROOT", T21000006Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            System.out.println(restr);
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(httpsUrl, restr);
//            String resStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ROOT><RSP_CODE>00000</RSP_CODE><REQ_SSN>BECP20180327163652919JM296TX4D4ZIY69OQ8K</REQ_SSN><MEM_ID>H00000700000001</MEM_ID><SIGN_INFO>MIAGCSqGSIb3DQEHAqCAMIACAQExCzAJBgUrDgMCGgUAMIAGCSqGSIb3DQEHAQAAMYIBtjCCAbICAQEwMDArMQswCQYDVQQGEwJDTjENMAsGA1UECwwEUFROUjENMAsGA1UEAwwEdGVzdAIBMDAJBgUrDgMCGgUAoF0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTgwNjIxMDEyODQ5WjAjBgkqhkiG9w0BCQQxFgQUaI+Qbk1PCekOHUKR8IaSgdVFbGUwDQYJKoZIhvcNAQEBBQAEggEAFI2F44ssSfuC6sjS1II8Nb+psteIaoOVpo98m4XyfX9FM6ZxI/i8y7gqH7UZI3Lsh+HgyRkIzm4GxcvrHqH04RdqRL36A64+vM2PHnbqbhsrFOxsv98h8qNdr/qYw470mENC3McK4xUyznSxFsLD/yHHuhnGq9p0LZdaOEjBnpq6PNZLv87xn93Dt1te13k+uGUWjQNfOKElc+nh+i0WXGYMcaVPWlEXHbjWOIXWM293BakuK4RJoroTmNxrgix6+SSiArQPChw5GYCKcY0GMDzOYDUy7i3QgZjnX8AoH9r1GA/PfdJSFsf+KzQe8WEV/jLgidhpg2ZDnhv77ETLUAAAAAAAAA==</SIGN_INFO></ROOT>";
            resStr = resStr.replace("ROOT", T21000006Response.class.getName());

            //把xml为转换为实体对象
            T21000006Response resData =
                    (T21000006Response) Utils.getObjectFromXML(resStr, T21000006Response.class);

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
