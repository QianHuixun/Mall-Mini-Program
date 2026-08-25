package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000031Request {
	private String TRANS_CODE;
	private String REQ_SSN;
	private String MCHNT_ID;
	private String FILE_COUNT;
	private String FILE_TYPE;
	private String FILE_NAME;
	private String TRANS_TYPE;
	private String FILE_CONTENT;
	private String ENCRYPTION_FLAG;
	private String ENDPOINT;//OSS访问地址
	private String ACCESS_KEY_ID;//OSS 标识
	private String ACCESS_KEY_SECRET;//OSS秘钥
	private String BUCKET_NAME;//OSS 类型
	private String FILE_PATH;//OSS 文件路径
	private String REQ_RESERVED;
	private String SIGN_INFO;
}