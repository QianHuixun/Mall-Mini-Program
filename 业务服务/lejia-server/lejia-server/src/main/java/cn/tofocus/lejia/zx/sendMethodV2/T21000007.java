package cn.tofocus.lejia.zx.sendMethodV2;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.zx.beanV2.T21000007Request;
import cn.tofocus.lejia.zx.beanV2.T21000007Response;
import cn.tofocus.lejia.zx.utilV2.*;

public class T21000007 extends BaseSendMethod
{
    public static void main(String[] args)
    {
        /*
         * 请求url 测试及生产环境地址请以商户接入规范中为准，demo中地址仅作参考
        **/
        String httpsUrl = "https://apitest.zyynm.com/dsgj/";
        
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000007Request request = new T21000007Request();
            request.setTRANS_CODE("21000007");
            Random r = new Random();
            String reqSsn = "J04059100000000" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID("J04059100000000");
            // J0002400000000010010012021112501
            request.setFILE_NAME("J0405910000000010336162025052605");
            request.setFILE_TYPE("616");
//            request.setSETTLE_DT("20211126");
            request.setTRANS_TYPE("MSG");
            //            request.setREQ_RESERVED("2");
            
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
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据

            String resStr = HttpsPost.post(httpsUrl, restr, "J04059100000000", "21000007");
            //把xml为转换为实体对象
            T21000007Response resData = XstreamUtils.toBean(resStr, T21000007Response.class);
            
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(sortSignInfoNoR(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
