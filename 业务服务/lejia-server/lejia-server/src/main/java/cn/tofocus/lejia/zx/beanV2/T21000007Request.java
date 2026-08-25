package cn.tofocus.lejia.zx.beanV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import org.dom4j.Element;

@Data
@XStreamAlias("ROOT")
public class T21000007Request {
	private String TRANS_CODE;
	private String REQ_SSN;
	private String MCHNT_ID;//商户编号
	private String FILE_NAME;//文件名称
	private String FILE_TYPE;//文件类型
	private String SETTLE_DT;//清算日期
	private String TRANS_TYPE;//文件传输类型
	private String ENDPOINT;//OSS访问地址
	private String ACCESS_KEY_ID;//OSS 标识
	private String ACCESS_KEY_SECRET;//OSS秘钥
	private String BUCKET_NAME;//OSS 类型
	private String FILE_PATH;//OSS 文件路径
	private String REQ_RESERVED;
	private String SIGN_INFO;
}