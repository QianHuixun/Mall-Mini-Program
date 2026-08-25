package cn.tofocus.lejia.zx.util;

/**
 * <p> Description: 签名验签工具</p>
 * <p> Copyright: Copyright (c) 2018 </p>
 * <p> Create Date: 2018-3-21 </p>
 * <p> Company: CITIC BANK </p> 
 * @author wangmengjie
 * @version 1.0.0
 */

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
//		StringBuffer pri_key_file_nm = new StringBuffer(new StringBuffer("config").append(File.separator).toString() + "PTNR" + File.separator + "PTNRtest.key");
//		StringBuffer cer_file_nm =  new StringBuffer(new StringBuffer("config").append(File.separator).toString() + "PTNR" + File.separator + "PTNRtest.cer");
	    String property = System.getProperty("user.dir");
		StringBuffer pri_key_file_nm = new StringBuffer(new StringBuffer(property).append(File.separator).toString() + "PTNR" + File.separator + "PTNRtest.key");
		StringBuffer cer_file_nm =  new StringBuffer(new StringBuffer(property).append(File.separator).toString() + "PTNR" + File.separator + "PTNRtest.cer");
		System.out.println("pri_key_file_nm: " + pri_key_file_nm);
		System.out.println("cer_file_nm: " + cer_file_nm);
		return OLPcrypt.sign(byte_msg, "Mima001", pri_key_file_nm.toString(), cer_file_nm.toString());
	}

	
	/***
	 * 验签
	 * @param msg 待验签数据
	 * @param sign 发送方生成的签名信息
	 * @param 发送方cer证书文件名
	 * @return 验签结果
	 */
	public static Boolean verifySign(byte[] msg,String sign, String sender_cer)
	{
		return OLPdecrypt.verifySign(msg, sign, sender_cer);
	}
	
	/***
	 * 签名字段排序
	 * @param 报文节点
	 * @return 排序结果
	 */
	public static java.lang.String sortSignInfo(Element root)
	{
		List<Element> list = root.elements();
		List<java.lang.String> signList = new ArrayList<java.lang.String>();

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
		
		for (java.lang.String sign  : signList)
		{
			signInfo = signInfo.append(sign);
		}
		return signInfo.toString();
	}
	
	/***
	 * 文件签名字段排序
	 * @param 报文节点
	 * @return 排序结果
	 */
	
	public static java.lang.String sortFileSignInfo(Element root)
	{
		List<Element> list = root.elements();
		List<java.lang.String> signList = new ArrayList<java.lang.String>();
		
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
		
		for (java.lang.String sign  : signList)
		{
			signInfo = signInfo.append(sign);
		}
		return signInfo.toString();
	}


}
