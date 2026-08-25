package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("NOTIFY_DATA")
public class ZxNotifyData
{
    private String TRANS_DT;
    
    private String TRANS_TM;
    
    private String TRANS_AMT;
    
    private String C_D_FLAG;
    
    private String ACCOUNT_TYPE;
    
    private String ACCOUNT;
    
    private String PAY_ACCNO;
    
    private String PAY_ACCNAME;
    
    private String OPBN;
    
    private String USER_NM;
    
    private String USER_ID;
    
    private String TRANS_TP;
    
    private String DIGEST;
    
    private String FRSC_SENUM;
    
    private String REMARK1;
    
    private String REMARK2;
    
    private String REMARK3;
    
    private String MCHNT_ID;
    
    private String FILE_TYPE;
    
    private String FILE_NAME;
    
    private String FILE_ST;
    
    private String FEE_AMT;
    
    private String REMARK;
    
    private String USERNO;
    
    private String USERNAME;
    
}
