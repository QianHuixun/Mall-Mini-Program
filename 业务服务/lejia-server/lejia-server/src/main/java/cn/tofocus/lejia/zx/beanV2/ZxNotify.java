package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class ZxNotify
{
    private String NOTIFY_URL;
    private String NOTIFY_SSN;
    private String MCHNT_ID;
    private String NOTIFY_TP; 
    private String SIGN_INFO;
    private ZxNotifyData NOTIFY_DATA;
}
