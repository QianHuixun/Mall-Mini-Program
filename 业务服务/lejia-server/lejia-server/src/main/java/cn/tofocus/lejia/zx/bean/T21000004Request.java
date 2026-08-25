package cn.tofocus.lejia.zx.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;


public class T21000004Request
{
	private String TRANS_CODE;
	private String SIGN_INFO;
	private String MCHNT_ID;
    private String USER_D_ID;
    private String USER_D_NM;
    private String USER_C_ID;
    private String USER_C_NM;
    private String BUSS_ID;
    private String BUSS_SUB_ID;
    private String TRANS_DT;
    private String TRANS_TM;
    private BigDecimal AMOUNT;
    private String FUND_TP;
    private String CONTRACT_ID;
    private String MEMO;
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

	public String getUSER_D_ID() {
		return USER_D_ID;
	}

	public void setUSER_D_ID(String uSER_D_ID) {
		USER_D_ID = uSER_D_ID;
	}

	public String getUSER_D_NM() {
		return USER_D_NM;
	}

	public void setUSER_D_NM(String uSER_D_NM) {
		USER_D_NM = uSER_D_NM;
	}

	public String getUSER_C_ID() {
		return USER_C_ID;
	}

	public void setUSER_C_ID(String uSER_C_ID) {
		USER_C_ID = uSER_C_ID;
	}

	public String getUSER_C_NM() {
		return USER_C_NM;
	}

	public void setUSER_C_NM(String uSER_C_NM) {
		USER_C_NM = uSER_C_NM;
	}

	public String getBUSS_ID() {
		return BUSS_ID;
	}

	public void setBUSS_ID(String bUSS_ID) {
		BUSS_ID = bUSS_ID;
	}

	public String getBUSS_SUB_ID() {
		return BUSS_SUB_ID;
	}

	public void setBUSS_SUB_ID(String bUSS_SUB_ID) {
		BUSS_SUB_ID = bUSS_SUB_ID;
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

	public BigDecimal getAMOUNT() {
		return AMOUNT;
	}

	public void setAMOUNT(BigDecimal aMOUNT) {
		AMOUNT = aMOUNT;
	}

	public String getFUND_TP() {
		return FUND_TP;
	}

	public void setFUND_TP(String fUND_TP) {
		FUND_TP = fUND_TP;
	}

	public String getCONTRACT_ID() {
		return CONTRACT_ID;
	}

	public void setCONTRACT_ID(String cONTRACT_ID) {
		CONTRACT_ID = cONTRACT_ID;
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
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

	private String REQ_RESERVED;//���𷽱�����
	private String REQ_SSN;//����������ˮ��

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
}