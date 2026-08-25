package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000003ResponseData {
    private String RSP_CODE;//应答码
    private String RSP_MSG;//应答码描述
    private String REQ_SSN;//发起方流水号
    private String PWDID;//动态密码句柄
    private String TRANS_ID;
    private String SIGN_INFO;//签名
    private String IS_NEED_CHECK;//是否需要审核
}
