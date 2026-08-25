package cn.tofocus.lejia.zx.bean;

import lombok.Data;

@Data
public class T21000014Response extends TResponse
{
    private String RSP_CODE;//应答码
    
    private String RSP_MSG;//应答码描述
    
    private String REQ_SSN;//发起方流水号
    
    private String USER_SSN;
    
    private String USER_TRANS_DT;
    
    private String USER_TRANS_TM;
    
    private String WITH_CHANNEL;
    
    private String PWDID;//动态密码句柄
    
    private String TRANS_ID;//交易标识
    
}
