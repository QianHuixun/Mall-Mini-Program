package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000011ResponseData {
    private String RSP_CODE;
    private String RSP_MSG;
    private String REQ_SSN;
    private String MCHNT_ID;
    private String FILE_CONTENT;
    private String FILE_NAME;
    private String SIGN_INFO;
}
