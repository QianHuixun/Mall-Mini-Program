package cn.tofocus.lejia.zx.bean;

public class T21000010Response {


	private String RSP_CODE;//Ӧ����
    private String RSP_MSG;//Ӧ��������
    private String REQ_SSN;//������ˮ��
    private String STATE;//״̬
    private String USER_SSN;//������ˮ��
    private String RESULT_CODE;//��Ӧ��
    private String RESULT_MSG;//��Ӧ��Ϣ
    private String SIGN_INFO;//ǩ��      
    
	
    public String getRESULT_MSG() {
        return RESULT_MSG;
    }

    public void setRESULT_MSG(String RESULT_MSG) {
        this.RESULT_MSG = RESULT_MSG;
    }
    
    public String getRESULT_CODE() {
        return RESULT_CODE;
    }

    public void setRESULT_CODE(String RESULT_CODE) {
        this.RESULT_CODE = RESULT_CODE;
    }
    
    public String getUSER_SSN() {
        return USER_SSN;
    }

    public void setUSER_SSN(String USER_SSN) {
        this.USER_SSN = USER_SSN;
    }
    
    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }
    
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
    public java.lang.String getSIGN_INFO() 
   	{
   		return SIGN_INFO;
   	}
       
   	public void setSIGN_INFO(java.lang.String strValue) 
   	{
   		SIGN_INFO = strValue;
   	}
   	



}
