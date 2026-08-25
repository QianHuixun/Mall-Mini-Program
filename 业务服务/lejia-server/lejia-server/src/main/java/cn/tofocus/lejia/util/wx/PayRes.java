package cn.tofocus.lejia.util.wx;

public class PayRes
{
    
    private String sign;
    
    private String prepay_id;
    
    private String appid;
    
    private String trade_state;
    
    private String total_fee;
    
    public String getTotal_fee()
    {
        return total_fee;
    }
    
    public void setTotal_fee(String total_fee)
    {
        this.total_fee = total_fee;
    }
    
    public String getTime_end()
    {
        return time_end;
    }
    
    public void setTime_end(String time_end)
    {
        this.time_end = time_end;
    }
    
    private String time_end;
    
    public String getAppid()
    {
        return appid;
    }
    
    public void setAppid(String appid)
    {
        this.appid = appid;
    }
    
    public String getSign()
    {
        return sign;
    }
    
    public void setSign(String sign)
    {
        this.sign = sign;
    }
    
    public String getPrepay_id()
    {
        return prepay_id;
    }
    
    public void setPrepay_id(String prepay_id)
    {
        this.prepay_id = prepay_id;
    }
    
    public String getTrade_state()
    {
        return trade_state;
    }
    
    public void setTrade_state(String trade_state)
    {
        this.trade_state = trade_state;
    }
    
}
