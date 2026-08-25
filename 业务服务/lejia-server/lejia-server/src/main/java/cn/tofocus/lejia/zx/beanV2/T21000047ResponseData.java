package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.util.List;

@Data
@XStreamAlias("DATA")
public class T21000047ResponseData 
{
    private String RSP_CODE;
    private String RSP_MSG;
    private String REQ_SSN;

    private String USER_SSN;
    private String USER_TRANS_DT;
    private String USER_TRANS_TM;
    
    
    private String SIGN_INFO;
}
