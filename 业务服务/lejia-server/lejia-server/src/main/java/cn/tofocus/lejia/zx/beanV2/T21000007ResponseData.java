package cn.tofocus.lejia.zx.beanV2;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T21000007ResponseData {
	private String RSP_CODE;//应答码
	private String RSP_MSG;//应答码描述
	private String REQ_SSN;//发起方流水号
	private String MCHNT_ID;
	private String FILE_TYPE;
	private String SETTLE_DT;
	private String RESULT_CODE;//业务响应码，RSP_CODE返回00000时，该字段返回。成功返回：00000
	private String RESULT_MSG;//业务响应信息
	private String FILE_CONTENT;
	private String SIGN_INFO;//签名
	
}