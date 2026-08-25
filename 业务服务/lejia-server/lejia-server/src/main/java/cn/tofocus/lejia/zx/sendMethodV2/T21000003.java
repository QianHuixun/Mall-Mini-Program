package cn.tofocus.lejia.zx.sendMethodV2;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.zx.beanV2.T21000003Request;
import cn.tofocus.lejia.zx.beanV2.T21000003Response;
import cn.tofocus.lejia.zx.utilV2.*;

public class T21000003 extends BaseSendMethod
{
    public static void main(String[] args)
    {
        /*
         * 请求url 测试及生产环境地址请以商户接入规范中为准，demo中地址仅作参考
        **/
        String httpsUrl = "https://apitest.zyynm.com/api/public/";
        
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000003Request request = new T21000003Request();
            request.setTRANS_CODE("21000003");
            Random r = new Random();
            String reqSsn = "J04059100000000" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID("J04059100000000");//平台商户编号
            
            // 企业用户
            request.setUSER_ID("J04059100000051");// 用户编号
            request.setUSER_NM("北京国尚腾龙商贸中心（个体工商户）");//用户变更姓名
            request.setUSER_CARD_TP("03");//用户证件类型  03-统一社会信用代码
            request.setUSER_CARD_NO("92110112MA00HADJ5P");//用户证件号码
            request.setUSER_PHONE("17886078617");//用户手机号
            request.setUSER_ROLE(Constants.USER_ROLE);//用户角色
            
            // 个人用户
//            request.setUSER_ID("J04059100000004");// 用户编号
//            request.setUSER_NM("王晓盈");//用户变更姓名
//            request.setUSER_CARD_TP("01");//用户证件类型
//            request.setUSER_CARD_NO("522731196902115610");//用户证件号码
//            request.setUSER_PHONE("18648594068");//用户手机号
//            request.setUSER_ROLE(Constants.USER_ROLE);//用户角色
            
            
            //        	request.setUSER_ADD("北京市青年路");//用户地址
            //        	request.setCORP_NM("");//企业法人姓名
            //        	request.setCORP_ID_TYPE("");//企业法人身份证号码
            //        	request.setREQ_RESERVED("");//发起方保留域
            
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
            String resStr = HttpsPost.post(httpsUrl, restr, "J04059100000000", "21000003");
            //把xml为转换为实体对象
            T21000003Response resData = XstreamUtils.toBean(resStr, T21000003Response.class);
            
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(sortSignInfo(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
