package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;


public class T21000003Request extends TRequest
{
	private String TRANS_CODE;
	private String MCHNT_ID;//�̻����
	private String USER_ID;//�û����
	private String USER_NM;//�û��������
	private String USER_CARD_TP;//�û�֤������
	private String USER_CARD_NO;//�û�֤������
	private String USER_PHONE;//�û��ֻ���
	private String USER_ADD;//�û���ַ
	private String CORP_NM;//��ҵ��������
	private String CORP_ID_TYPE;//��ҵ�������֤����
	private String USER_ROLE;//�û���ɫ
	private String REQ_RESERVED;//���𷽱�����
	private String REQ_SSN;//����������ˮ��
  

	public String getUSER_NM() {
		return USER_NM;
	}


	public void setUSER_NM(String uSER_NM) {
		USER_NM = uSER_NM;
	}


	public String getUSER_CARD_TP() {
		return USER_CARD_TP;
	}


	public void setUSER_CARD_TP(String uSER_CARD_TP) {
		USER_CARD_TP = uSER_CARD_TP;
	}


	public String getUSER_CARD_NO() {
		return USER_CARD_NO;
	}


	public void setUSER_CARD_NO(String uSER_CARD_NO) {
		USER_CARD_NO = uSER_CARD_NO;
	}


	public String getUSER_PHONE() {
		return USER_PHONE;
	}


	public void setUSER_PHONE(String uSER_PHONE) {
		USER_PHONE = uSER_PHONE;
	}


	public String getUSER_ADD() {
		return USER_ADD;
	}


	public void setUSER_ADD(String uSER_ADD) {
		USER_ADD = uSER_ADD;
	}


	public String getCORP_NM() {
		return CORP_NM;
	}


	public void setCORP_NM(String cORP_NM) {
		CORP_NM = cORP_NM;
	}


	public String getCORP_ID_TYPE() {
		return CORP_ID_TYPE;
	}


	public void setCORP_ID_TYPE(String cORP_ID_TYPE) {
		CORP_ID_TYPE = cORP_ID_TYPE;
	}


	public String getUSER_ROLE() {
		return USER_ROLE;
	}


	public void setUSER_ROLE(String uSER_ROLE) {
		USER_ROLE = uSER_ROLE;
	}


	public String sortSignInfo(Element root)
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
		//����
		Collections.sort(signList);
		
		StringBuffer signInfo = new StringBuffer();
		
		for (java.lang.String sign  : signList)
		{
			signInfo = signInfo.append(sign);
		}
		return signInfo.toString();
	}

	public String getTRANS_CODE() {
		return TRANS_CODE;
	}


	public void setTRANS_CODE(String tRANS_CODE) {
		TRANS_CODE = tRANS_CODE;
	}


	public String getMCHNT_ID() {
		return MCHNT_ID;
	}


	public void setMCHNT_ID(String mCHNT_ID) {
		MCHNT_ID = mCHNT_ID;
	}


	public String getUSER_ID() {
		return USER_ID;
	}


	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getREQ_RESERVED() {
		return REQ_RESERVED;
	}


	public void setREQ_RESERVED(String rEQ_RESERVED) {
		REQ_RESERVED = rEQ_RESERVED;
	}


	public String getREQ_SSN() {
		return REQ_SSN;
	}


	public void setREQ_SSN(String rEQ_SSN) {
		REQ_SSN = rEQ_SSN;
	}
}