package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000031ResponseData
{
    private String RSP_CODE;//应答码
    
    private String RSP_MSG;//应答码描述
    
    private String REQ_SSN;//发起方流水号
    
    private String FILE_NAME;//文件名称
    
    private String ENCRYPTION_FLAG;//加密标志
    
    private String RESULT_CODE;
    
    private String RESULT_MSG;
    
    private String SIGN_INFO;//签名
}