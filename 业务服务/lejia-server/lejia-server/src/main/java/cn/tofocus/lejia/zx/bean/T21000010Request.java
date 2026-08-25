package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class T21000010Request {

	private String TRANS_CODE;//������
	private String REQ_SSN;//������ˮ��
	private String MCHNT_ID;//�̻����
	private String USER_ID;//�û����
	private String ORI_USER_SSN;//���Ĳཻ����ˮ��
	private String ORI_REQ_SSN;//�̻�������ˮ��
	private String BUSS_ID;//������
	private String BUSS_SUB_ID;//�Ӷ�����
	private String USER_TRANS_DT;//��������
	private String REQ_RESERVED;//���𷽱�����
	private String SIGN_INFO;//ǩ��

	public String getBUSS_ID() {
		return BUSS_ID;
	}

	public void setBUSS_ID(String mBUSS_ID) {
		BUSS_ID = mBUSS_ID;
	}
	
	public String getBUSS_SUB_ID() {
		return BUSS_SUB_ID;
	}

	public void setBUSS_SUB_ID(String mBUSS_SUB_ID) {
		BUSS_SUB_ID = mBUSS_SUB_ID;
	}
	
	public String getUSER_TRANS_DT() {
		return USER_TRANS_DT;
	}

	public void setUSER_TRANS_DT(String mUSER_TRANS_DT) {
		USER_TRANS_DT = mUSER_TRANS_DT;
	}
	
	public String getORI_REQ_SSN() {
		return ORI_REQ_SSN;
	}

	public void setORI_REQ_SSN(String mORI_REQ_SSN) {
		ORI_REQ_SSN = mORI_REQ_SSN;
	}
	
	public String getORI_USER_SSN() {
		return ORI_USER_SSN;
	}

	public void setORI_USER_SSN(String mORI_USER_SSN) {
		ORI_USER_SSN = mORI_USER_SSN;
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

	public String getREQ_SSN() {
		return REQ_SSN;
	}

	public void setREQ_SSN(String rEQ_SSN) {
		REQ_SSN = rEQ_SSN;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String rUSER_ID) {
		USER_ID = rUSER_ID;
	}
	public String getREQ_RESERVED() {
		return REQ_RESERVED;
	}
	public void setREQ_RESERVED(String rEQ_RESERVED) {
		REQ_RESERVED = rEQ_RESERVED;
	}

	public String sortSignInfo(Element root) {
		List<Element> list = root.elements();
		List<java.lang.String> signList = new ArrayList<java.lang.String>();

		for (Element e : list) {
			if (e.getText() != null) {
				signList.add(e.getText());
			}
		}
		// ����
		Collections.sort(signList);

		StringBuffer signInfo = new StringBuffer();

		for (java.lang.String sign : signList) {
			signInfo = signInfo.append(sign);
		}
		return signInfo.toString();
	}

}
