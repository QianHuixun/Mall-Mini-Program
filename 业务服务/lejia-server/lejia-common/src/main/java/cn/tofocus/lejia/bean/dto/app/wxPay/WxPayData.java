package cn.tofocus.lejia.bean.dto.app.wxPay;

import lombok.Data;

@Data
public class WxPayData
{
    private String appId;
    
    private String timeStamp;
    
    private String nonceStr;
    
    private String pack;
    
    private String signType;
    
    private String paySign;
    
    private String packageVal;
}
