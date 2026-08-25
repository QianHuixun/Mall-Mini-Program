/**
 * <p> Description: 签名验签工具</p>
 * <p> Copyright: Copyright (c) 2018 </p>
 * <p> Create Date: 2018-3-21 </p>
 * <p> Company: CITIC BANK </p> 
 * @author wangmengjie
 * @version 1.0.0
 */
package cn.tofocus.lejia.zx.utilV2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;



public class SignUtil {
	/**
	 * 发送方加签方法
	 * @param byte_msg  待加签数据
	 * @return 签名信息
	 */
	public static String sign(byte[] byte_msg) {
		StringBuffer pri_key_file_nm = new StringBuffer(new StringBuffer("PTNR").append(File.separator).toString()  + File.separator + "xinanshizu.key");
		StringBuffer cer_file_nm =  new StringBuffer(new StringBuffer("PTNR").append(File.separator).toString() + File.separator + "xinanshizu.cer");
	
		return OLPcrypt.sign(byte_msg, "Mima001", pri_key_file_nm.toString(), cer_file_nm.toString());
	}

	
	/***
	 * 验签
	 * @param msg 待验签数据
	 * @param sign 发送方生成的签名信息
	 * @param sender_cer 发送方cer证书文件名
	 * @return 验签结果
	 */
	public static Boolean verifySign(byte[] msg,String sign, String sender_cer)
	{
		return OLPdecrypt.verifySign(msg, sign, sender_cer);
	}
	
	/***
	 * 签名字段排序
	 * @param root 报文节点
	 * @return 排序结果
	 */
	public static String sortSignInfo(Element root)
	{
		List<Element> list = root.elements();
		List<String> signList = new ArrayList<String>();

		for (Element e : list)
		{
			if (e.getText() != null)
			{
				signList.add(e.getText());
			}
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
	
	/***
	 * 文件签名字段排序
	 * @param root 报文节点
	 * @return 排序结果
	 */
	
	public static String sortFileSignInfo(Element root)
	{
		List<Element> list = root.elements();
		List<String> signList = new ArrayList<String>();
		
		for (Element e : list)
		{
			if (!(e.getName().equals("RSP_CODE") || e.getName().equals("FILE_CONTENT") || e.getName().equals("RSP_MSG")))
			{
				if(e.getText() != null)
				{
					signList.add(e.getText());
				}
			}	
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
