package cn.tofocus.lejia.zx.sendMethodV2;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.zx.beanV2.T21000001Request;
import cn.tofocus.lejia.zx.beanV2.T21000001Response;
import cn.tofocus.lejia.zx.beanV2.T21000029Request;
import cn.tofocus.lejia.zx.utilV2.Constants;
import cn.tofocus.lejia.zx.utilV2.HttpsPost;
import cn.tofocus.lejia.zx.utilV2.SignUtil;
import cn.tofocus.lejia.zx.utilV2.Utils;
import cn.tofocus.lejia.zx.utilV2.XstreamUtils;

public class T21000029 extends BaseSendMethod
{
    public static void main(String[] args)
    {
        String httpsUrl = "https://apitest.zyynm.com/api/public/";
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000029Request request = new T21000029Request();
            request.setTRANS_CODE("21000029");
            Random r = new Random();
            String reqSsn = "J04059100000000" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID("J04059100000000");//平台商户编号              
            request.setUSER_ID("J04059100000000");
            request.setREGISTER_ATTR("12");
            request.setTRANS_DATE("20250620");
            request.setPAGE("1");
            request.setTRANS_TYPE("98");
            
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
            String resStr = HttpsPost.post(httpsUrl, restr, "J04059100000000", "21000029");
            //把xml为转换为实体对象
            
            //-------- 验签 --------------
            //获取签名信息
//            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
//            boolean isSucc = SignUtil.verifySign(sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
//            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            // 测试返回的 <USER_ID>J04059100000002</USER_ID>
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
