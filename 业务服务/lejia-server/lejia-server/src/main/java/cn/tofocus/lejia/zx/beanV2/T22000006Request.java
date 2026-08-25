package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T22000006Request
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    // 用户编号（如果查公共登记簿，则传商户编号）
    private String USER_ID;
    
    // 登记簿类型
    // (1)银行为商户交易资金账户开立的公共登记簿标识、发起方输入标识进行查询：
    //  00-公共计息收费登记薄
    //  12-自有资金登记薄
    //  13-担保登记薄
    //  17-待结算手续费登记簿
    //
    // (2)用户登记簿标识：
    //  14-子商户/用户登记薄
    //
    // (3)交易资金账户标识：
    //  TA-交易资金账户
    //
    // 平台剩余透支额度标识
    //  RO-平台剩余透支额度
    private String REGISTER_ATTR;
    
    private String REQ_RESERVED;
    
    private String SIGN_INFO;
}
