package cn.tofocus.lejia.zx.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class T21000035Response extends TResponse
{
    private String RSP_CODE;
    
    private String RSP_MSG;
    
    private String REQ_SSN;
    
    private BigDecimal AMOUNT;
    
}