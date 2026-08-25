package cn.tofocus.lejia.zx.beanV2;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.Data;

@Data
@XStreamAlias("ROOT")
public class T21000029Request
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    // 用户编号（如果查公共登记簿，则传商户编号）
    private String USER_ID;
    
    private String TRANS_DATE;
    private String PAGE;
    /*
     *  01-入金分账
        02-交易划转
        03-提现
        04-提现手续费
        05-提现退汇
        06-渠道来账
        07-支付交易
        08-退款交易
        09-平台商户预付交易
        11-平台扣罚
        12-平台补贴
        13-实时预清分
        98-所有（返回明细类型）
        99-所有（返回汇总类型）
        （备注：入担保白名单商户查询支付渠道入金时使用01类型）
     */
    private String TRANS_TYPE;
    
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
