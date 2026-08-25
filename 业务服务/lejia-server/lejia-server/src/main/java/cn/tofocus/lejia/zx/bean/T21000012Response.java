package cn.tofocus.lejia.zx.bean;

public class T21000012Response extends TResponse
{
    
    private String RSP_CODE;//Ӧ����
    
    private String RSP_MSG;//Ӧ��������
    
    private String REQ_SSN;//������ˮ��
    
    private String RESULT_CODE;//��Ӧ��
    
    private String RESULT_MSG;//��Ӧ��Ϣ
    
    private String FILE_ST;
    
    
    public String getRESULT_CODE()
    {
        return RESULT_CODE;
    }
    
    public void setRESULT_CODE(String rESULT_CODE)
    {
        RESULT_CODE = rESULT_CODE;
    }
    
    public String getRESULT_MSG()
    {
        return RESULT_MSG;
    }
    
    public void setRESULT_MSG(String rESULT_MSG)
    {
        RESULT_MSG = rESULT_MSG;
    }
    
    public String getFILE_ST()
    {
        return FILE_ST;
    }
    
    public void setFILE_ST(String fILE_ST)
    {
        FILE_ST = fILE_ST;
    }
    
    
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
    
}
