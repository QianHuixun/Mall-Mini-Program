package cn.tofocus.lejia.zx.bean;

import lombok.Data;

@Data
public class T21000024Request extends TRequest
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID; // 商户编号
    
    private String USER_ID; // 用户编号
    
    private String OP_TYPE; // 绑卡类型 1绑定 2解绑
    
    private String PAN_NUM; // 开户银行联行号
    
    private String ACCT_NM; // 账户名称
    
    private String PAN; // 银行账号
    
    private String USER_ID_TYPE; // 用户证件类型
    
    private String BANK_CARD_NO; // 用户证件号码
    
    private String ACCT_TYPE; // 账户类型
    
    private String BANK_PHONE; // 银行预留手机号
    
    private String REQ_RESERVED; // 发起方保留域
    
    private String AUTH_PROTOCOL_VERSION; //用户授权协议版本号
    
    private String AUTH_PROTOCOL_NO; // 用户授权协议流水号
    
}
