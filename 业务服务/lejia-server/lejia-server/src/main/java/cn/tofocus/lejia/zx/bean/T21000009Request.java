package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class T21000009Request extends TRequest
{

	private String TRANS_CODE;
	private String REQ_SSN;
	private String MCHNT_ID;
	private String USER_ID;
	private String REQ_RESERVED;

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
