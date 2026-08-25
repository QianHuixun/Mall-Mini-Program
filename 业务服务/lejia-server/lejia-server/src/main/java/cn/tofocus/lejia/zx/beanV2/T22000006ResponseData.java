package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("DATA")
public class T22000006ResponseData
{
    private String RSP_CODE;
    
    private String RSP_MSG;
    
    private String REQ_SSN;
    
    // 上一日可提现金额（单位：元）
    private String PRE_AMOUNT;
    
    // 可用余额（单位：元）
    private String AVL_AMOUNT;
    
    // 可提现金额（单位：元）
    private String AMOUNT;
    
    // 平台剩余透支金额（单位：元）
    private String PLFM_SPLS_OVDF_AMT;
    
    // 签名
    private String SIGN_INFO;
    
    // 冻结金额（单位：元）
    private String REMARK1;
    
    // 备用字段2
    private String REMARK2;
    
    // 备用字段3
    private String REMARK3;
}
