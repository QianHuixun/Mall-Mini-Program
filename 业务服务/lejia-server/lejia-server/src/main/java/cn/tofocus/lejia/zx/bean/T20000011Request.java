package cn.tofocus.lejia.zx.bean;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class T20000011Request {
	private String TRANS_CODE;
	private String REQ_SSN;
	private String MCHNT_ID;
	private String FILE_TYPE;
	private String SETTLE_DT;
	private String REQ_RESERVED;
	private String SIGN_INFO;

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

	public String getFILE_TYPE() {
		return FILE_TYPE;
	}

	public void setFILE_TYPE(String fILE_TYPE) {
		FILE_TYPE = fILE_TYPE;
	}

	public String getSETTLE_DT() {
		return SETTLE_DT;
	}

	public void setSETTLE_DT(String sETTLE_DT) {
		SETTLE_DT = sETTLE_DT;
	}
}