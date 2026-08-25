package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class T21000011Request {

	private String TRANS_CODE;//������
	private String REQ_SSN;//������ˮ��
	private String MCHNT_ID;//�̻����
	private String USER_SSN;//���Ž�����ˮ��
	private String USER_TRANS_DT;//���Ž�������
	private String TRANS_TYPE;//��������
	private String REQ_RESERVED;//���𷽱�����
	private String SIGN_INFO;//ǩ��

	public String getUSER_SSN() {
		return USER_SSN;
	}

	public void setUSER_SSN(String mUSER_SSN) {
		USER_SSN = mUSER_SSN;
	}
	
	public String getTRANS_TYPE() {
		return TRANS_TYPE;
	}

	public void setTRANS_TYPE(String mTRANS_TYPE) {
		TRANS_TYPE = mTRANS_TYPE;
	}
	
	public String getUSER_TRANS_DT() {
		return USER_TRANS_DT;
	}

	public void setUSER_TRANS_DT(String mUSER_TRANS_DT) {
		USER_TRANS_DT = mUSER_TRANS_DT;
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
