package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000010ResponseData {
    private String RSP_CODE;//应答码
    private String RSP_MSG;//应答码描述
    private String REQ_SSN;//发起方流水号
    private String STATE;//状态
    private String TRANS_DATE;//交易受理日期
    private String TRANS_TIME;//交易受理时间
    private String USER_SSN;//交易流水号
    private String RESULT_CODE;//响应码
    private String RESULT_MSG;//响应信息
    private String SIGN_INFO;//签名
}
