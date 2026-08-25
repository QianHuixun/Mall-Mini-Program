package cn.tofocus.lejia.zx.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class T21000005Response
{
	private String RSP_CODE;//Ӧ����
    private String RSP_MSG;//Ӧ��������
    private String REQ_SSN;//������ˮ��
    private String PWDID;//��̬������
    private String USER_SSN;
	private String USER_TRANS_DT;
    private String USER_TRANS_TM;
    private String TRANS_ID;
    private String SIGN_INFO;//ǩ��      
    
    public String getRSP_CODE() {
        return RSP_CODE;
    }

    public void setRSP_CODE(String RSP_CODE) {
        this.RSP_CODE = RSP_CODE;
    }

    public String getRSP_MSG() {
        return RSP_MSG;
    }

    public void setRSP_MSG(String RSP_MSG) {
        this.RSP_MSG = RSP_MSG;
    }

    public String getREQ_SSN() {
        return REQ_SSN;
    }

    public void setREQ_SSN(String REQ_SSN) {
        this.REQ_SSN = REQ_SSN;
    }
    
    /**��ȡ-��̬������*/
    public java.lang.String getPWDID()
    {
        return PWDID;
    }
    /**����-��̬������*/
    public void setPWDID(java.lang.String strValue) throws Exception
    {
        PWDID = strValue;
    }
    public java.lang.String getSIGN_INFO() 
   	{
   		return SIGN_INFO;
   	}
       
   	public void setSIGN_INFO(java.lang.String strValue) 
   	{
   		SIGN_INFO = strValue;
   	}

	public String getTRANS_ID() {
		return TRANS_ID;
	}

	public void setTRANS_ID(String tRANS_ID) {
		TRANS_ID = tRANS_ID;
	}

	public String getUSER_SSN() {
		return USER_SSN;
	}

	public void setUSER_SSN(String uSER_SSN) {
		USER_SSN = uSER_SSN;
	}

	public String getUSER_TRANS_DT() {
		return USER_TRANS_DT;
	}

	public void setUSER_TRANS_DT(String uSER_TRANS_DT) {
		USER_TRANS_DT = uSER_TRANS_DT;
	}

	public String getUSER_TRANS_TM() {
		return USER_TRANS_TM;
	}

	public void setUSER_TRANS_TM(String uSER_TRANS_TM) {
		USER_TRANS_TM = uSER_TRANS_TM;
	}
   	
   	
}
