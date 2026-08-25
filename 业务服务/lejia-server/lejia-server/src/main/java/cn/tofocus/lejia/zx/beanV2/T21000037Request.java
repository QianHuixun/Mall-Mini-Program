package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000037Request {
	private String TRANS_CODE;//交易码
	private String REQ_SSN;//发起方流水号
	private String MCHNT_ID;//商户编号
	private String USER_ID;//用户编号
	private String PAN;//卡号
	private String SIGN_INFO;//签名
}
