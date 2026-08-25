package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000028Request
{
    
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    //    00-预付交易支付
    //    01-预付交易撤销
    //    02-预付交易完成
    //    03-预付完成撤销
    private String TRANS_TYPE;
    
    private String USER_ID;
    
    private String USER_NM;
    
    private String BUSS_ID;
    
    private String BUSS_SUB_ID;
    
    private String TRANS_DT;
    
    private String TRANS_TM;
    
    private String AMOUNT;
    
    private String DISCOUNT_AMT;
    
    private String DEVIDE_AMT;
    
    private String FUND_TP;
    
    private String CONTRACT_ID;
    
    private String MEMO;
    
    private String REQ_RESERVED;
    
    private String SIGN_INFO;
}