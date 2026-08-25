package cn.tofocus.lejia.zx.bean;

public class TResponse
{
    private String SIGN_INFO;
    
    private String RSP_CODE;
    
    private String RSP_MSG;
    
    boolean isSucc;
    
    public String getRSP_MSG()
    {
        return RSP_MSG;
    }
    
    public void setRSP_MSG(String RSP_MSG)
    {
        this.RSP_MSG = RSP_MSG;
    }
    
    public String getRSP_CODE()
    {
        return RSP_CODE;
    }
    
    public void setRSP_CODE(String RSP_CODE)
    {
        this.RSP_CODE = RSP_CODE;
    }
    
    public boolean isSucc()
    {
        return isSucc;
    }
    
    public void setSucc(boolean isSucc)
    {
        this.isSucc = isSucc;
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
