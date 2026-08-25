package cn.tofocus.lejia.zx.beanV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import org.dom4j.Element;

@Data
@XStreamAlias("ROOT")
public class T21000010Request {
	private String TRANS_CODE;//交易码
	private String REQ_SSN;//发起方流水号
	private String MCHNT_ID;//商户编号
	private String USER_ID;//用户编号
	private String ORI_USER_SSN;//中信侧交易流水号
	private String ORI_REQ_SSN;//商户发起方流水号
	private String BUSS_ID;//订单号
	private String BUSS_SUB_ID;//子订单号
	private String USER_TRANS_DT;//交易日期
	private String REQ_RESERVED;//发起方保留域
	private String SIGN_INFO;//签名
}
