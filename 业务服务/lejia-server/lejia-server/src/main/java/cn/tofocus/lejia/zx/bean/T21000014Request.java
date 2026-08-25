package cn.tofocus.lejia.zx.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class T21000014Request extends TRequest
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID; // 商户编号
    
    private String USER_ID; // 用户编号
    
    private String WITH_TYPE;
    
    private String BUSS_ID;
    
    private String TRANS_DT;
    
    private String TRANS_TM;
    
    private String FEE_TYPE;
    
    private BigDecimal WITH_AMT;
    
    private String MEMO;
    
    private String REQ_RESERVED;
    
    private String WITH_ACCOUNT;
    
    private String WITH_ACCNAME;
    
}
