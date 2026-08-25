package cn.tofocus.lejia.zx.bean;

public class T21000007Response extends TResponse
{
    private String RSP_CODE; 
    
    private String RSP_MSG; 
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    public String getMCHNT_ID()
    {
        return MCHNT_ID;
    }
    
    public void setMCHNT_ID(String mCHNT_ID)
    {
        MCHNT_ID = mCHNT_ID;
    }
    
    public String getFILE_TYPE()
    {
        return FILE_TYPE;
    }
    
    public void setFILE_TYPE(String fILE_TYPE)
    {
        FILE_TYPE = fILE_TYPE;
    }
    
    public String getSETTLE_DT()
    {
        return SETTLE_DT;
    }
    
    public void setSETTLE_DT(String sETTLE_DT)
    {
        SETTLE_DT = sETTLE_DT;
    }
    
    public String getFILE_CONTENT()
    {
        return FILE_CONTENT;
    }
    
    public void setFILE_CONTENT(String fILE_CONTENT)
    {
        FILE_CONTENT = fILE_CONTENT;
    }
    
    private String FILE_TYPE;
    
    private String SETTLE_DT;
    
    private String FILE_CONTENT;
    
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