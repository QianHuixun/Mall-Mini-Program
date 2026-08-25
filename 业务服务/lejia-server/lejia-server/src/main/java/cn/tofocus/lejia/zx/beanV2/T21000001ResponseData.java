package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000001ResponseData {
    private String RSP_CODE;//电商管家应答码
    private String RSP_MSG;//电商管家应答码描述
    private String REQ_SSN;//发起方流水号
    private String USER_ID;//用户编号
    private String PWDID;//动态密码句柄
    private String TRANS_ID;
    private String IS_NEED_CHECK;//是否需要审核
    private String SIGN_INFO;//签名
}
