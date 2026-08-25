package cn.tofocus.lejia.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpUtils;

import org.springframework.stereotype.Component;

import com.tencent.common.Util;

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.lejia.util.wx.PayRes;

@Component
public class PayManager
{
//    PayRes xmlres = (PayRes) Util.getObjectFromXML(res, PayRes.class);
    
//    public static void main(String[] args)
//    {
////        Document document1;
////        Element root = DocumentHelper.createElement("data");
////        document1 = DocumentHelper.createDocument(root);
////
////        Element head = root.addElement("head");
////
////        Element log_id = head.addElement("log_id");//接口调用轨迹id
////
////       //setText里面的值是子节点<log_id>的值
////
////        log_id.setText(UUID.randomUUID().toString());
////
////        Element wsid = head.addElement("wsid");//接口id
////
////        wsid.setText("SLIS_NB_20150417_01");
////
////        Element sendtime = head.addElement("sendtime");//发送时间
////
////        sendtime.setText("2020-01-01");
////
////        Element body = root.addElement("body");
////
////        Element agentcode = body.addElement("prtno");
////
////        agentcode.setText("prtno");
////
////        Element appntname = body.addElement("type");
////
////        appntname.setText("type");
////
////        Element paycount = body.addElement("salechnlflag");
////
////        paycount.setText("salechnlflag");
////
////        String requestXml = document1.asXML();
////        System.out.println(requestXml);
//        
//        StringBuilder sb = new StringBuilder();
//        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//        sb.append("<ROOT>");
//        sb.append("<TRANS_CODE>21000001</TRANS_CODE>");
//        sb.append("<REQ_SSN>J100330000000002018121715130000012300000</REQ_SSN>");
//        sb.append("<MCHNT_ID>J10033000000000</MCHNT_ID>");
//        sb.append("<MCHNT_USER_ID>test1</MCHNT_USER_ID>");
//        sb.append("<USER_TYPE>1</USER_TYPE>");
//        sb.append("<USER_NM>联调测试</USER_NM>");
//        sb.append("<USER_ROLE>001001</USER_ROLE>");
//        sb.append("<SIGN_TYPE>00</SIGN_TYPE>");
//        sb.append("<USER_ID_TYPE>01</USER_ID_TYPE>");
//        sb.append("<USER_ID_NO>000013477</USER_ID_NO>");
//        sb.append("<USER_PHONE>15562632654</USER_PHONE>");
//        sb.append("<CORP_NM>李先生</CORP_NM>");
//        sb.append("<CORP_ID_NO>123456</CORP_ID_NO>");
//        sb.append("<USER_ADD>北京市青年路</USER_ADD>");
//        sb.append("<REQ_RESERVED></REQ_RESERVED>");
//        sb.append("<SIGN_INFO>00</SIGN_INFO>");
//        sb.append("</ROOT>");
//        System.out.println(sb.toString());
////        HttpUtil.postBodyString("", sb);
//        
//        /**
//<?xml version="1.0" encoding="UTF-8"?>
//<ROOT>
//    <TRANS_CODE>21000001</TRANS_CODE>
//    <REQ_SSN>J100330000000002018121715130000012300000</REQ_SSN>
//    <MCHNT_ID>J10033000000000</MCHNT_ID>
//    <MCHNT_USER_ID>test1</MCHNT_USER_ID>
//    <USER_TYPE>1</USER_TYPE>
//    <USER_NM>联调测试</USER_NM>
//    <USER_ROLE>001001</USER_ROLE>
//    <SIGN_TYPE>00</SIGN_TYPE>
//    <USER_ID_TYPE>01</USER_ID_TYPE>
//    <USER_ID_NO>000013477</USER_ID_NO>
//    <USER_PHONE>15562632654</USER_PHONE>
//    <CORP_NM>李先生</CORP_NM>
//    <CORP_ID_NO>123456</CORP_ID_NO>
//    <USER_ADD>北京市青年路</USER_ADD>
//    <REQ_RESERVED></REQ_RESERVED>
//    <SIGN_INFO>00</SIGN_INFO>
//</ROOT>
//
//         */
//        t();
//    }
    

    private static void t()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("bbb", 1);
        map.put("aaa", "hello");
        map.put("abc", "abc");
        map.put("u1", "u1");
        System.out.println("排序前：" + map);
        Map<String, Object> treeMap = new TreeMap<>(String::compareTo);
        treeMap.putAll(map);
        System.out.println("排序后：" + treeMap);
    }
    
    
}
