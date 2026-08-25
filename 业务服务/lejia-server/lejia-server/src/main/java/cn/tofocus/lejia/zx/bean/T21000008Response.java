package cn.tofocus.lejia.zx.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class T21000008Response extends TResponse
{
    
    private String REQ_SSN;
    
    private String RESULT_CODE;
    
    private String RESULT_MSG;
    
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
    
    public String getREQ_SSN()
    {
        return REQ_SSN;
    }
    
    public void setREQ_SSN(String REQ_SSN)
    {
        this.REQ_SSN = REQ_SSN;
    }
    
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
}