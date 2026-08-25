package cn.tofocus.lejia.zx.bean;

public class T21000011Response {

	private String RSP_CODE;//Ӧ����
    private String RSP_MSG;//Ӧ��������
    private String REQ_SSN;//������ˮ��
    private String MCHNT_ID;//�̻����
    private String FILE_CONTENT;//�ļ�����
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
    public java.lang.String getSIGN_INFO() 
   	{
   		return SIGN_INFO;
   	}
       
   	public void setSIGN_INFO(java.lang.String strValue) 
   	{
   		SIGN_INFO = strValue;
   	}
   	
    public String getMCHNT_ID() {
        return MCHNT_ID;
    }
    public void setMCHNT_ID(String MCHNT_ID) {
        this.MCHNT_ID = MCHNT_ID;
    }

    public String getFILE_CONTENT() {
        return FILE_CONTENT;
    }
    public void setFILE_CONTENT(String FILE_CONTENT) {
        this.FILE_CONTENT = FILE_CONTENT;
    }


}
