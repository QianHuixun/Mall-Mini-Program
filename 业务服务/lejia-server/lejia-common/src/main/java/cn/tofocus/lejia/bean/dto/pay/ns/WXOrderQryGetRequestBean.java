package cn.tofocus.lejia.bean.dto.pay.ns;


public class WXOrderQryGetRequestBean
{
    private String operFlag;
    private String appTp;
    private String bizId;
    private String bizTime;
    private String merchId;
    private String orderId;
    private String txTp;
    public String getOperFlag()
    {
        return operFlag;
    }
    public void setOperFlag(String operFlag)
    {
        this.operFlag = operFlag;
    }
    public String getAppTp()
    {
        return appTp;
    }
    public void setAppTp(String appTp)
    {
        this.appTp = appTp;
    }
    public String getBizId()
    {
        return bizId;
    }
    public void setBizId(String bizId)
    {
        this.bizId = bizId;
    }
    public String getBizTime()
    {
        return bizTime;
    }
    public void setBizTime(String bizTime)
    {
        this.bizTime = bizTime;
    }
    public String getMerchId()
    {
        return merchId;
    }
    public void setMerchId(String merchId)
    {
        this.merchId = merchId;
    }
    public String getOrderId()
    {
        return orderId;
    }
    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
    public String getTxTp()
    {
        return txTp;
    }
    public void setTxTp(String txTp)
    {
        this.txTp = txTp;
    }
    
    
    
}
