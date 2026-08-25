package cn.tofocus.lejia.zx.bean;

import lombok.Data;

@Data
public class T21000007Request extends TRequest
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    private String FILE_NAME;
    
    private String FILE_TYPE;
    
    private String SETTLE_DT;
    
    private String TRANS_TYPE;
    
    private String REQ_RESERVED;
    
    public String getTRANS_CODE()
    {
        return TRANS_CODE;
    }
    
    public void setTRANS_CODE(String tRANS_CODE)
    {
        TRANS_CODE = tRANS_CODE;
    }
    
    public String getMCHNT_ID()
    {
        return MCHNT_ID;
    }
    
    public void setMCHNT_ID(String mCHNT_ID)
    {
        MCHNT_ID = mCHNT_ID;
    }
    
    public String getREQ_SSN()
    {
        return REQ_SSN;
    }
    
    public void setREQ_SSN(String rEQ_SSN)
    {
        REQ_SSN = rEQ_SSN;
    }
    
    public String getFILE_NAME()
    {
        return FILE_NAME;
    }
    
    public void setFILE_NAME(String fILE_NAME)
    {
        FILE_NAME = fILE_NAME;
    }
    
    public String getTRANS_TYPE()
    {
        return TRANS_TYPE;
    }
    
    public void setTRANS_TYPE(String tRANS_TYPE)
    {
        TRANS_TYPE = tRANS_TYPE;
    }
    
    public String getREQ_RESERVED()
    {
        return REQ_RESERVED;
    }
    
    public void setREQ_RESERVED(String rEQ_RESERVED)
    {
        REQ_RESERVED = rEQ_RESERVED;
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
}