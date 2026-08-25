package cn.tofocus.lejia.zx.bean;

public class T21000003Response extends TResponse
{
    private String RSP_CODE;//Ӧ����
    
    private String RSP_MSG;//Ӧ��������
    
    private String REQ_SSN;//������ˮ��
    
    private String PWDID;//��̬������
    
    private String TRANS_ID;
    
    public String getRSP_CODE()
    {
        return RSP_CODE;
    }
    
    public void setRSP_CODE(String RSP_CODE)
    {
        this.RSP_CODE = RSP_CODE;
    }
    
    public String getRSP_MSG()
    {
        return RSP_MSG;
    }
    
    public void setRSP_MSG(String RSP_MSG)
    {
        this.RSP_MSG = RSP_MSG;
    }
    
    public String getREQ_SSN()
    {
        return REQ_SSN;
    }
    
    public void setREQ_SSN(String REQ_SSN)
    {
        this.REQ_SSN = REQ_SSN;
    }
    
    /**��ȡ-��̬������*/
    public java.lang.String getPWDID()
    {
        return PWDID;
    }
    
    /**����-��̬������*/
    public void setPWDID(java.lang.String strValue)
        throws Exception
    {
        PWDID = strValue;
    }
    
    public String getTRANS_ID()
    {
        return TRANS_ID;
    }
    
    public void setTRANS_ID(String tRANS_ID)
    {
        TRANS_ID = tRANS_ID;
    }
    
}
