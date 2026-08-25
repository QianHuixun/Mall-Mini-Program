package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000003Request
{
    private String TRANS_CODE;
    
    private String REQ_SSN;//发起方请求流水号
    
    private String MCHNT_ID;//商户编号
    
    private String USER_ID;//用户编号
    
    private String USER_ROLE;//用户角色
    
    private String USER_NM;//用户变更姓名

    private String USER_TYPE;//用户类型
    
    private String USER_CARD_TP;//用户证件类型
    
    private String USER_CARD_NO;//用户证件号码
    
    private String USER_PHONE;//用户手机号
    
    private String USER_ADD;//用户地址
    
    private String CORP_NM;//企业法人姓名
    
    private String CORP_ID_TYPE_NEW;//企业法人证件类型
    
    private String CORP_ID_NUM_NEW;//企业法人身份证号码
    
    private String REQ_RESERVED;//发起方保留域
    
    private String SIGN_INFO;
    
}