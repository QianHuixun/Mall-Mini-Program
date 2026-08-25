package cn.tofocus.lejia.zx.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class T21000036Response extends TResponse
{
    private String RSP_CODE;
    
    private String RSP_MSG;
    
    private String REQ_SSN;
    
    private String MCHNT_NM;
    
    private String MCHNT_CD;
    
    private String DEAL_ACC_NAME;
    
    private String DEAL_ACC_NO;
    
    private BigDecimal PRE_AMOUNT;
    
    private BigDecimal CUR_AMOUNT;
    
    private String RSP_RESERVED;
    
}