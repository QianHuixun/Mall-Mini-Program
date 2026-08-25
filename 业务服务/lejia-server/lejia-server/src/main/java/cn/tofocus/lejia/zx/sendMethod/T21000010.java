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

import cn.tofocus.lejia.zx.bean.T21000010Request;
import cn.tofocus.lejia.zx.bean.T21000010Response;
import cn.tofocus.lejia.zx.util.HttpsPost;
import cn.tofocus.lejia.zx.util.SignUtil;
import cn.tofocus.lejia.zx.util.Utils;

public class T21000010 {


	/** ��Ŀ¼*/
    private static String ROOTPATH = "";
    static {
        ROOTPATH = System.getProperty("user.dir");
    }

  
    private static final String PTNRTESTCER = ROOTPATH + "/PTNR/PTNRtest.cer";
    /** https˽Կ��*/
    private static final String KEYSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";

    /** https���ο�*/
    private static final String TRUSTSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";

    /** ����ͷ*/
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    /** �ϴ��ļ�·��**/
    private static final String FILE_PATH = ROOTPATH + "/file/";
    public static void main(String[] args) {

        //˽Կ�� ����
        String password = "111111";

        /*
         * ����url 
         * �̻�����Ҫ�޸�Ϊ������ַ    202.108.57.43 30466
        **/
//        String httpsUrl = "https://22.5.227.200:30466/dsgj/";
        String httpsUrl = "https://202.108.57.27:30466/dsgj/";

        try {

            //-----------  ��д��������  ������ֻ��Ҫ�޸����е����ݼ��ɣ�------------

            XStream xtreamData = new XStream();

            //--------------- ��������ѯ�������� ---------------

            T21000010Request request = new T21000010Request();
            request.setTRANS_CODE("21000010"); // 交易码
            request.setREQ_SSN("J000240000000002020121715131000012300169"); //发起方流水号
            request.setMCHNT_ID("J00024000000000");// 商户编号
            request.setUSER_ID("J00024300000000");
//            request.setORI_REQ_SSN("J0002400000000020190415164422363IY69O497");// 中信侧交易流水号
            request.setORI_USER_SSN("EMSSJUJ20200622160039245Kv1cdWKE");// 商户发起方流水号
            request.setUSER_TRANS_DT("20200618");
            request.setREQ_RESERVED("2");
            
            //ʵ�����ת��ΪXML
            xtreamData.alias("ROOT", T21000010Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------��ǩ��------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            System.out.println(restr);
            //---------- ������������ ------//
            //��ʼ��https�������
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //������������Ӧ����
            String resStr = HttpsPost.post(httpsUrl, restr);
//            String resStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ROOT><RSP_CODE>00000</RSP_CODE><REQ_SSN>BECP20180327163652919JM296TX4D4ZIY69OQ8K</REQ_SSN><MEM_ID>H00000700000001</MEM_ID><SIGN_INFO>MIAGCSqGSIb3DQEHAqCAMIACAQExCzAJBgUrDgMCGgUAMIAGCSqGSIb3DQEHAQAAMYIBtjCCAbICAQEwMDArMQswCQYDVQQGEwJDTjENMAsGA1UECwwEUFROUjENMAsGA1UEAwwEdGVzdAIBMDAJBgUrDgMCGgUAoF0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTgwNjIxMDEyODQ5WjAjBgkqhkiG9w0BCQQxFgQUaI+Qbk1PCekOHUKR8IaSgdVFbGUwDQYJKoZIhvcNAQEBBQAEggEAFI2F44ssSfuC6sjS1II8Nb+psteIaoOVpo98m4XyfX9FM6ZxI/i8y7gqH7UZI3Lsh+HgyRkIzm4GxcvrHqH04RdqRL36A64+vM2PHnbqbhsrFOxsv98h8qNdr/qYw470mENC3McK4xUyznSxFsLD/yHHuhnGq9p0LZdaOEjBnpq6PNZLv87xn93Dt1te13k+uGUWjQNfOKElc+nh+i0WXGYMcaVPWlEXHbjWOIXWM293BakuK4RJoroTmNxrgix6+SSiArQPChw5GYCKcY0GMDzOYDUy7i3QgZjnX8AoH9r1GA/PfdJSFsf+KzQe8WEV/jLgidhpg2ZDnhv77ETLUAAAAAAAAA==</SIGN_INFO></ROOT>";
            resStr = resStr.replace("ROOT", T21000010Response.class.getName());

            //��xmlΪת��Ϊʵ�����
            T21000010Response resData =
                    (T21000010Response) Utils.getObjectFromXML(resStr, T21000010Response.class);

            //-------- ��ǩ --------------
            //��ȡǩ����Ϣ
            String sigStr = resData.getSIGN_INFO();
            //��ǩ�Ƿ�ɹ�
            boolean isSucc = SignUtil.verifySign(sortSignInfo(resStr).getBytes(), sigStr, PTNRTESTCER);
            System.out.println("��Ӧ��Ϣ��ǩ��" + (isSucc == true ? "��ǩ�ɹ���" : "��ǩʧ�ܣ�����ǩ����"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //��ȡ�̻���פ���ձ����е���ǩ�ֶ�
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
    	//����
		Collections.sort(signList);
		
		StringBuffer signInfo = new StringBuffer();
		
		for (String sign  : signList)
		{
			signInfo = signInfo.append(sign);
		}
		return signInfo.toString();
    }


}
