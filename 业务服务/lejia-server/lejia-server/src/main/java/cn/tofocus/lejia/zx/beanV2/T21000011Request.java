package cn.tofocus.lejia.zx.beanV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import org.dom4j.Element;
@Data
@XStreamAlias("ROOT")
public class T21000011Request {
	private String TRANS_CODE;//交易码
	private String REQ_SSN;//发起方流水号
	private String MCHNT_ID;//商户编号
	private String USER_SSN;//中信交易流水号
	private String USER_TRANS_DT;//中信交易日期
	private String TRANS_TYPE;//交易类型
	private String OPERATE_TYPE;//是否重新打印回执信息
	private String REQ_RESERVED;//发起方保留域
	private String ENDPOINT;//OSS访问地址
	private String ACCESS_KEY_ID;//OSS 标识
	private String ACCESS_KEY_SECRET;//OSS秘钥
	private String BUCKET_NAME;//OSS 类型
	private String FILE_PATH;//OSS 文件路径
	private String SIGN_INFO;//签名
}
