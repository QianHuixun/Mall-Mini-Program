package cn.tofocus.lejia.zx.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class T21000005Request {
	private String TRANS_CODE;
	private String SIGN_INFO;
	private String MCHNT_ID;
	private String MEM_ID;
	private String BUSS_ID;
	private String TRANS_DT;
	private String TRANS_TM;
	private BigDecimal WITH_AMT;
	private String USER_ID;
	private String WITH_TYPE;
	private String FEE_TYPE;
	private String MEMO;
	private String REQ_RESERVED;

	private String REQ_SSN;

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getWITH_TYPE() {
		return WITH_TYPE;
	}

	public void setWITH_TYPE(String wITH_TYPE) {
		WITH_TYPE = wITH_TYPE;
	}

	public String getFEE_TYPE() {
		return FEE_TYPE;
	}

	public void setFEE_TYPE(String fEE_TYPE) {
		FEE_TYPE = fEE_TYPE;
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
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

	public String getMEM_ID() {
		return MEM_ID;
	}

	public void setMEM_ID(String mEM_ID) {
		MEM_ID = mEM_ID;
	}

	public String getBUSS_ID() {
		return BUSS_ID;
	}

	public void setBUSS_ID(String bUSS_ID) {
		BUSS_ID = bUSS_ID;
	}

	public String getTRANS_DT() {
		return TRANS_DT;
	}

	public void setTRANS_DT(String tRANS_DT) {
		TRANS_DT = tRANS_DT;
	}

	public String getTRANS_TM() {
		return TRANS_TM;
	}

	public void setTRANS_TM(String tRANS_TM) {
		TRANS_TM = tRANS_TM;
	}

	public BigDecimal getWITH_AMT() {
		return WITH_AMT;
	}

	public void setWITH_AMT(BigDecimal wITH_AMT) {
		WITH_AMT = wITH_AMT;
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