package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T22000007Request
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    private String TRANS_TYPE;
    
    // 用户编号（如果查公共登记簿，则传商户编号）
    private String USER_ID;
    
    // 用户名称
    private String USER_NM;
    
    private String BUSS_ID;
    
    private String BUSS_SUB_ID;
    
    private String TRANS_DT;
    
    private String ORI_USER_TRANS_DT;

    private String ORI_BUSS_ID;
    
    private String ORI_BUSS_SUB_ID;
    
    private String TRANS_TM;
    
    private String AMOUNT;
    
    private String FUND_TP;
    
    private String SIGN_INFO;
}
