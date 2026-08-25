package cn.tofocus.lejia.zx.bean;

import lombok.Data;

@Data
public class T21000035Request extends TRequest
{
    private String TRANS_CODE;
    
    private String REQ_SSN;
    
    private String MCHNT_ID;
    
    /*
     * 登记簿类型
                  银行为商户交易资金账户开立的公共登记簿标识、发起方输入标识进行查询：
        00-公共计息收费登记薄
        12-自有资金登记薄
        13-担保登记薄
        17-待结算手续费登记簿
     */
    private String REGISTER_ATTR;
    
}