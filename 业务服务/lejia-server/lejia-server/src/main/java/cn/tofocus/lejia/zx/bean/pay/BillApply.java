package cn.tofocus.lejia.zx.bean.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户对账账单申请
 * @author  29701
 * @version  [版本号, 2021年10月22日]
 */

@Data
public class BillApply
{
    @Schema(description = "内部商户号")
    private String MCHNO;
    
    @Schema(description = "清算日期")
    private String SETTLEDATE;
    
    @Schema(description = "支付通道")
    private String PAYCHANNEL;
    
    @Schema(description = "签名")
    private String SIGN;
    
    @Schema(description = "版本")
    private String VERSION;
}
