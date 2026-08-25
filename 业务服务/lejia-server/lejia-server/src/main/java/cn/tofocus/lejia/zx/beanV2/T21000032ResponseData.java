package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000032ResponseData
{
    private String RSP_CODE;
    
    private String RSP_MSG;
    
    private String REQ_SSN;
    
    private String RESULT_CODE;
    
    private String RESULT_MSG;
    
    private String FILE_ST;
    
    private String SIGN_INFO;
}
