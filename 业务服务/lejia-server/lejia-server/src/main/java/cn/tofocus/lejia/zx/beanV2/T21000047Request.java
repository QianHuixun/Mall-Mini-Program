package cn.tofocus.lejia.zx.beanV2;

import java.math.BigDecimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000047Request 
{
	private String TRANS_CODE;
	private String REQ_SSN;
	private String MCHNT_ID;
	
	private String DEAL_TYPE;
	private String USER_C_ID;
	private String USER_C_NM;
	private String BUSS_ID;
	private String BUSS_SUB_ID;
	private String TRANS_DT;
	private String TRANS_TM;
	private BigDecimal AMOUNT;
	private String FUND_TP;
	private String CONTRACT_ID;
	private String MEMO;

	private String REQ_RESERVED;
	private String SIGN_INFO;
}