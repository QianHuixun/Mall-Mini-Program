package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000032Request
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    private String FILE_NAME;
    
    private String REQ_RESERVED;
    
    private String SIGN_INFO;
}
