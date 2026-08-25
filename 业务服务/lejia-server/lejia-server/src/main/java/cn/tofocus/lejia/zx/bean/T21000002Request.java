package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;


public class T21000002Request
{
	private String TRANS_CODE;
	private String SIGN_INFO;
	private String MCHNT_ID;// �̻����
	private String USER_ID;// �û����
	private String ACCT_NM;// �˻�����
	private String PAN;// ���п���
	private String PAN_NUM;// �������к�
	private String OP_TYPE;// ��������
	private String BANK_PHONE;// ����Ԥ���ֻ���
	private String REQ_RESERVED;// ���𷽱�����
	private String REQ_SSN;
  

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


	public String getPAN_NUM() {
		return PAN_NUM;
	}


	public void setPAN_NUM(String pAN_NUM) {
		PAN_NUM = pAN_NUM;
	}


	public String getTRANS_CODE() {
		return TRANS_CODE;
	}


	public void setTRANS_CODE(String tRANS_CODE) {
		TRANS_CODE = tRANS_CODE;
	}


	public String getSIGN_INFO() {
		return SIGN_INFO;
	}


	public void setSIGN_INFO(String sIGN_INFO) {
		SIGN_INFO = sIGN_INFO;
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


	public String getACCT_NM() {
		return ACCT_NM;
	}


	public void setACCT_NM(String aCCT_NM) {
		ACCT_NM = aCCT_NM;
	}


	public String getPAN() {
		return PAN;
	}


	public void setPAN(String pAN) {
		PAN = pAN;
	}


	public String getOP_TYPE() {
		return OP_TYPE;
	}


	public void setOP_TYPE(String oP_TYPE) {
		OP_TYPE = oP_TYPE;
	}


	public String getBANK_PHONE() {
		return BANK_PHONE;
	}


	public void setBANK_PHONE(String bANK_PHONE) {
		BANK_PHONE = bANK_PHONE;
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