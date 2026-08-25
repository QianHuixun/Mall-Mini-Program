package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000014ResponseData
{
    private String RSP_CODE;//应答码
    
    private String RSP_MSG;//应答码描述
    
    private String REQ_SSN;//发起方流水号
    
    private String USER_SSN;
    
    private String USER_TRANS_DT;
    
    private String USER_TRANS_TM;
    
    private String PWDID;//动态密码句柄
    
    private String TRANS_ID;//交易标识

    private String WITH_CHANNEL;
    
    private String SIGN_INFO;
}
